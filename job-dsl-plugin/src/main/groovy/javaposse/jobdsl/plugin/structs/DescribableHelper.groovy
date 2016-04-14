package javaposse.jobdsl.plugin.structs

import hudson.model.Describable
import hudson.model.Descriptor
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.DslException
import jenkins.model.Jenkins
import org.jenkinsci.plugins.structs.SymbolLookup
import org.jenkinsci.plugins.structs.describable.DescribableModel

/**
 * Provides utility methods for extending the DSL with any {@link Describable}.
 *
 * @since 1.46
 */
class DescribableHelper {
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
        result = result ?: descriptors.findAll { uncapitalize(it.clazz) == name }
        result.collect { new DescribableModel(it.clazz) }
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
        } catch (ClassNotFoundException e) {
            throw new DslException("Can not load context type $className for $contextType.name", e)
        } catch (ClassCastException e) {
            throw new DslException("Context type $className in $contextType must be a subclass of $Describable.name", e)
        }
    }
}
