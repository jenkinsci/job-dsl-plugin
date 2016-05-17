package javaposse.jobdsl.plugin.structs

import hudson.model.Describable
import hudson.model.Descriptor
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.DslException
import javaposse.jobdsl.dsl.ExtensibleContext
import jenkins.model.Jenkins
import org.codehaus.groovy.syntax.Types
import org.jenkinsci.Symbol
import org.jenkinsci.plugins.structs.SymbolLookup
import org.jenkinsci.plugins.structs.describable.DescribableModel
import org.jenkinsci.plugins.structs.describable.ErrorType
import org.kohsuke.stapler.NoStaplerConstructorException

import java.util.logging.Level
import java.util.logging.Logger

/**
 * Provides utility methods for extending the DSL with any {@link Describable}.
 *
 * @since 1.46
 */
class DescribableHelper {
    private static final Logger LOGGER = Logger.getLogger(DescribableHelper.name)
    private static final Collection<String> KEYWORDS = Types.keywords

    /**
     * Finds {@link DescribableModel}s with the given name within the set of given models.
     *
     * The name can either be a symbolic name specified by a {@code org.jenkinsci.Symbol} or the uncapitalized
     * unqualified class name.
     *
     * @see #uncapitalize(java.lang.Class)
     */
    static Collection<DescribableModel> findDescribableModels(Collection<DescribableModel> models, String name) {
        Collection<DescribableModel> result = models.findAll { SymbolLookup.get().find(getTypeForLookup(it), name) }
        result ?: models.findAll { uncapitalize(it.type) == name }
    }

    /**
     * Finds {@link DescribableModel}s with the given name for the {@link ContextType} of the given {@link Context}.
     *
     * The name can either be a symbolic name specified by a {@code org.jenkinsci.Symbol} or the uncapitalized
     * unqualified class name.
     *
     * @see #uncapitalize(java.lang.Class)
     */
    static Collection<DescribableModel> findDescribableModels(Class<? extends Context> contextClass, String name) {
        Collection<Descriptor> descriptors = getDescriptors(contextClass)
        Collection<Descriptor> result = descriptors.findAll { SymbolLookup.get().find(it.class, name) }
        getDescribableModels(result ?: descriptors.findAll { uncapitalize(it.clazz) == name })
    }

    /**
     * Returns the given {@link DescribableModel}s indexed by symbolic name.
     *
     * The name can either be a symbolic name specified by a {@code org.jenkinsci.Symbol} or the uncapitalized
     * unqualified class name.
     *
     * @see #uncapitalize(java.lang.Class)
     */
    static Map<String, DescribableModel> findDescribableModels(Collection<DescribableModel> models,
                                                               Collection<String> knownSymbols = []) {
        Collection<DescribableModel> candidateModels = models.findAll { !hasError(it) }
        Collection<String> illegalSymbols = knownSymbols + KEYWORDS

        Map<String, DescribableModel> symbols = filterIllegalSymbols(findSymbols(candidateModels), illegalSymbols)
        Map<String, DescribableModel> result = findUncapitalizedClassNames(candidateModels - symbols.values())
        result.putAll(symbols)
        filterIllegalSymbols(result, illegalSymbols)
    }

    /**
     * Finds {@link DescribableModel}s for the {@link ContextType} of the given {@link Context}, indexed by symbolic
     * name.
     *
     * The name can either be a symbolic name specified by a {@code org.jenkinsci.Symbol} or the uncapitalized
     * unqualified class name.
     *
     * @see #uncapitalize(java.lang.Class)
     */
    static Map<String, DescribableModel> findDescribableModels(Class<? extends ExtensibleContext> contextClass,
                                                               Collection<String> knownSymbols = []) {
        findDescribableModels(getDescribableModels(getDescriptors(contextClass)), knownSymbols)
    }

    /**
     * Returns {@code true} if the given list of arguments is either empty or contains exactly one {@link Closure}
     * argument.
     */
    static boolean isOptionalClosureArgument(Object[] args) {
        args.length == 0 || (args.length == 1 && args[0] instanceof Closure)
    }

    /**
     * Returns the uncapitalized unqualified name for the given class.
     *
     * <p>
     * Examples:
     * <ul>
     *     <li>{@code describableHelper} for {@link DescribableHelper}
     *     <li>{@code url} for {@link URL}
     *     <li>{@code urlClassLoader} for {@link URLClassLoader}
     *     <li>{@code unicodeBlock} for {@link Character.UnicodeBlock}
     * </ul>
     */
    static String uncapitalize(Class clazz) {
        String simpleName = clazz.simpleName
        int firstLowerCase = simpleName.chars.findIndexOf { Character.isLowerCase(it) }
        if (firstLowerCase == -1) {
            simpleName.toLowerCase(Locale.ENGLISH)
        } else {
            if (firstLowerCase < 2) {
                firstLowerCase = 2
            }
            new StringBuilder(simpleName.length())
                    .append(simpleName[0..firstLowerCase - 2].toLowerCase())
                    .append(simpleName[(firstLowerCase - 1)..-1])
                    .toString()
        }
    }

    private static Class getTypeForLookup(DescribableModel model) {
        Describable.isAssignableFrom(model.type) ? Jenkins.instance.getDescriptorOrDie(model.type).class : model.type
    }

    private static List<Descriptor> getDescriptors(Class<?> contextType) {
        ContextType contextTypeAnnotation = contextType.getAnnotation(ContextType)
        if (contextTypeAnnotation == null) {
            return []
        }

        String className = contextTypeAnnotation.value()
        try {
            ClassLoader classLoader = Jenkins.instance.pluginManager.uberClassLoader
            Class<? extends Describable> type = classLoader.loadClass(className).asSubclass(Describable)
            return Jenkins.instance.getDescriptorList(type)
        } catch (ClassNotFoundException ignore) {
            LOGGER.fine("can not get descriptors for '$className', class not found")
            return []
        } catch (ClassCastException e) {
            throw new DslException("Context type $className in $contextType must be a subclass of $Describable.name", e)
        }
    }

    /**
     * Returns a {@link DescribableModel}s for each valid {@link Descriptor}. A valid descriptor must have a
     * {@link org.kohsuke.stapler.DataBoundConstructor}.
     */
    private static Collection<DescribableModel> getDescribableModels(Collection<Descriptor> descriptors) {
        List<DescribableModel> result = []
        descriptors.each {
            try {
                result << new DescribableModel(it.clazz)
            } catch (NoStaplerConstructorException e) {
                LOGGER.fine("can not introspect ${it.clazz}: ${e.message}")
            } catch (NoClassDefFoundError e) {
                LOGGER.fine("can not introspect ${it.clazz}: ${e.message}")
            }
        }
        result
    }

    private static Map<String, DescribableModel> findSymbols(Collection<DescribableModel> models) {
        Map<String, DescribableModel> result = [:]
        Set<String> duplicateSymbols = []
        models.each { DescribableModel model ->
            Symbol symbol = getTypeForLookup(model).getAnnotation(Symbol)
            symbol?.value()?.each {
                if (!duplicateSymbols.contains(it)) {
                    if (result.containsKey(it)) {
                        duplicateSymbols << it
                        result.remove(it)
                    } else {
                        result[it] = model
                    }
                }
            }
        }
        result
    }

    private static Map<String, DescribableModel> findUncapitalizedClassNames(Collection<DescribableModel> models) {
        Map<String, DescribableModel> result = [:]
        Set<String> duplicateSymbols = []
        models.each { DescribableModel model ->
            String symbol = uncapitalize(model.type)
            if (!duplicateSymbols.contains(symbol)) {
                if (result.containsKey(symbol)) {
                    duplicateSymbols << symbol
                    result.remove(symbol)
                } else {
                    result[symbol] = model
                }
            }
        }
        result
    }

    private static boolean hasError(DescribableModel model) {
        if (LOGGER.isLoggable(Level.FINE) && model.parameters.any { it.type instanceof ErrorType }) {
            LOGGER.fine("model has errors: $model")
        }
        model.parameters.any { it.required && it.type instanceof ErrorType }
    }

    private static Map<String, DescribableModel> filterIllegalSymbols(Map<String, DescribableModel> symbols,
                                                                      Collection<String> illegalSymbols) {
        symbols.findAll { !illegalSymbols.contains(it.key) }
    }
}
