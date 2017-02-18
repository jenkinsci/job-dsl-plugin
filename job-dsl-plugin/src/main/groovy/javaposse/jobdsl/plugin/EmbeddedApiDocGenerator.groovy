package javaposse.jobdsl.plugin

import groovy.transform.PackageScope
import hudson.PluginWrapper
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ExtensibleContext
import javaposse.jobdsl.plugin.structs.DescribableHelper
import jenkins.model.Jenkins
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import org.jenkinsci.plugins.structs.describable.ArrayType
import org.jenkinsci.plugins.structs.describable.AtomicType
import org.jenkinsci.plugins.structs.describable.DescribableModel
import org.jenkinsci.plugins.structs.describable.DescribableParameter
import org.jenkinsci.plugins.structs.describable.EnumType
import org.jenkinsci.plugins.structs.describable.HeterogeneousObjectType
import org.jenkinsci.plugins.structs.describable.HomogeneousObjectType
import org.jenkinsci.plugins.structs.describable.ParameterType
import org.springframework.core.LocalVariableTableParameterNameDiscoverer
import org.springframework.core.ParameterNameDiscoverer

import java.lang.reflect.Method
import java.text.BreakIterator
import java.text.StringCharacterIterator

import static javaposse.jobdsl.plugin.ExtensionPointHelper.findExtensionMethods
import static javaposse.jobdsl.plugin.ExtensionPointHelper.hasIdenticalSignature
import static javaposse.jobdsl.plugin.ExtensionPointHelper.isVisibleParameterType
import static javaposse.jobdsl.plugin.structs.DescribableHelper.findDescribableModels
import static org.apache.commons.lang.StringEscapeUtils.unescapeHtml

class EmbeddedApiDocGenerator {
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER =
            new LocalVariableTableParameterNameDiscoverer()

    private final List<DescribableModel> newContexts = []
    private final List<ParameterType> newListContexts = []
    private final Set<String> knownContexts = []

    String generateApi() throws Exception {
        JSONObject api = JSONObject.fromObject(Context.getResource('dsl.json').getText('UTF-8'))

        JSONObject contexts = api.getJSONObject('contexts')
        contexts.values().each { JSONObject context -> generateExtensionMethods(context) }

        while (newContexts || newListContexts) {
            while (newContexts) {
                DescribableModel model = newContexts.remove(0)
                contexts[getContextClassName(model)] = generateContext(model)
            }
            while (newListContexts) {
                ParameterType parameterType = newListContexts.remove(0)
                contexts[getListContextClassName(parameterType)] = generateListContext(parameterType)
            }
        }

        api.toString()
    }

    private void addContext(DescribableModel model) {
        String contextClassName = getContextClassName(model)
        if (!knownContexts.contains(contextClassName)) {
            knownContexts.add(contextClassName)
            newContexts << model
        }
    }

    private void addListContext(ParameterType parameterType) {
        String contextClassName = getListContextClassName(parameterType)
        if (!knownContexts.contains(contextClassName)) {
            knownContexts.add(contextClassName)
            newListContexts << parameterType
        }
    }

    /**
     * Generates extension methods into built-in contexts.
     */
    private void generateExtensionMethods(JSONObject context) {
        String type = context.getString('type')
        JSONArray methods = context.getJSONArray('methods')

        Class<?> contextClass = Context.classLoader.loadClass(type)
        if (ExtensibleContext.isAssignableFrom(contextClass)) {
            Set<String> knownMethods = methods*.getString('name')

            Class<? extends ExtensibleContext> extensibleContextClass = contextClass.asSubclass(ExtensibleContext)
            Set<Method> extensions = findExtensionMethods(extensibleContextClass).keySet()
            findUniqueMethods(extensions.findAll { !knownMethods.contains(it.name) }).each {
                methods << generateMethod(it.key, it.value)
            }
            knownMethods.addAll(extensions*.name)

            methods.each { JSONObject method ->
                if (!hasOptionalClosureSignature(method)) {
                    knownMethods.remove(method.getString('name'))
                }
            }

            Map<String, DescribableModel> symbols = findDescribableModels(extensibleContextClass, knownMethods)
            symbols.sort().each { String symbol, DescribableModel model ->
                JSONObject method = methods.find { it.getString('name') == symbol } as JSONObject
                if (method) {
                    method.getJSONArray('signatures').add(generateSignature(model))
                } else {
                    methods << generateMethod(symbol, model)
                }
            }
        }
    }

    private static boolean hasOptionalClosureSignature(JSONObject method) {
        method.getJSONArray('signatures').any { JSONObject signature ->
            isOptionalClosureSignature(signature)
        }
    }

    private static boolean isOptionalClosureSignature(JSONObject signature) {
        if (!signature.has('parameters')) {
            return true
        }
        JSONArray parameters = signature.getJSONArray('parameters')
        if (parameters.size() > 1) {
            return false
        } else if (parameters.empty) {
            return true
        }
        parameters[0].getString('type') == 'Closure'
    }

    /**
     * Generates an extension method for an {@link ContextExtensionPoint} of a built-in context.
     */
    private static JSONObject generateMethod(String name, List<Method> methods) {
        new JSONObject()
                .element('name', name)
                .element('signatures', methods.sort { it.toString() }.collect { generateSignature(it) })
    }

    /**
     * Generates a method signature for a {@link javaposse.jobdsl.plugin.DslExtensionMethod}.
     */
    private static JSONObject generateSignature(Method method) {
        JSONObject signature = new JSONObject()
                .element('parameters', generateParameters(method))
                .element('extension', true)

        PluginWrapper plugin = Jenkins.instance.pluginManager.whichPlugin(method.declaringClass)
        if (plugin) {
            signature.element('plugin', [id: plugin.shortName])
        }

        signature
    }

    private static JSONArray generateParameters(Method method) {
        JSONArray result = new JSONArray()
        String[] names = PARAMETER_NAME_DISCOVERER.getParameterNames(method)
        method.parameterTypes.eachWithIndex { Class type, int index ->
            if (isVisibleParameterType(type)) {
                String name = names ? names[index] : 'arg' + index
                String simpleName = type.simpleName
                if (simpleName == 'Runnable') {
                    simpleName = 'Closure'
                }
                result.add(JSONObject.fromObject([name: name, type: simpleName]))
            }
        }
        result
    }

    /**
     * Generates an extension method for a built-in context.
     */
    private JSONObject generateMethod(String symbol, DescribableModel model) {
        new JSONObject()
                .element('name', symbol)
                .element('signatures', [generateSignature(model)])
    }

    /**
     * Generates a method signature for an extension method.
     */
    private JSONObject generateSignature(DescribableModel model) {
        JSONObject signature = new JSONObject()
                .element('parameters', model.parameters ? [generateOptionalClosureParameter()] : [])
                .element('generated', true)

        if (model.deprecated) {
            signature.element('deprecated', true)
        }
        if (model.parameters) {
            signature.element('contextClass', getContextClassName(model))
            addContext(model)
        }

        generateHelp(signature, model.help)

        PluginWrapper plugin = Jenkins.instance.pluginManager.whichPlugin(model.type)
        if (plugin) {
            signature.element('plugin', [id: plugin.shortName])
        }

        signature
    }

    /**
     * Generates a new context matching a {@link javaposse.jobdsl.plugin.structs.DescribableContext}.
     */
    private JSONObject generateContext(DescribableModel model) {
        JSONArray methods = new JSONArray()
        model.parameters.each {
            try {
                methods << generateMethod(it)
            } catch (UnsupportedParameterType ignore) {
            }
        }

        new JSONObject()
                .element('type', getContextClassName(model))
                .element('methods', methods)
    }

    /**
     * Generates a new context matching a {@link javaposse.jobdsl.plugin.structs.DescribableListContext}.
     */
    private JSONObject generateListContext(ParameterType parameterType) {
        new JSONObject()
                .element('type', getListContextClassName(parameterType))
                .element('methods', getTypes(parameterType).sort().collect { generateMethod(it.key, it.value) })
    }

    /**
     * Generates a method matching a method in a {@link javaposse.jobdsl.plugin.structs.DescribableContext}.
     */
    private JSONObject generateMethod(DescribableParameter parameter) {
        new JSONObject()
                .element('name', parameter.name)
                .element('signatures', [generateSignature(parameter)])
    }

    /**
     * Generates a method signature matching a signature of a method in a
     * {@link javaposse.jobdsl.plugin.structs.DescribableContext}.
     */
    private JSONObject generateSignature(DescribableParameter parameter) {
        JSONObject signature = new JSONObject()
                .element('parameters', [generateParameter(parameter.type)])
                .element('deprecated', parameter.deprecated)
                .element('generated', true)

        if (isContextParameter(parameter.type)) {
            if (isListContext(parameter.type)) {
                signature.element('contextClass', getListContextClassName(parameter.type))
                addListContext(parameter.type)
            } else {
                HomogeneousObjectType homogeneousObjectType = (HomogeneousObjectType) parameter.type
                signature.element('contextClass', getContextClassName(homogeneousObjectType.schemaType))
                addContext(homogeneousObjectType.schemaType)
            }
        }
        if (parameter.required && !(parameter.type instanceof ArrayType)) {
            signature.element('required', true)
        }

        generateHelp(signature, parameter.help)

        signature
    }

    private static boolean isContextParameter(ParameterType parameterType) {
        parameterType instanceof HomogeneousObjectType ||
                parameterType instanceof HeterogeneousObjectType ||
                (parameterType instanceof ArrayType && isContextParameter(parameterType.elementType))
    }

    /**
     * Generates a method parameter for a method in a {@link javaposse.jobdsl.plugin.structs.DescribableContext}.
     */
    private static JSONObject generateParameter(ParameterType parameterType) {
        if (parameterType instanceof AtomicType) {
            return JSONObject.fromObject([name: 'value', type: ((AtomicType) parameterType).type.simpleName])
        } else if (parameterType instanceof EnumType) {
            return JSONObject.fromObject([name: 'value', type: 'String'])
        } else if (parameterType instanceof HomogeneousObjectType || parameterType instanceof HeterogeneousObjectType) {
            return generateOptionalClosureParameter()
        } else if (parameterType instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) parameterType
            ParameterType elementType = arrayType.elementType
            if (elementType instanceof HomogeneousObjectType || elementType instanceof HeterogeneousObjectType) {
                return generateOptionalClosureParameter()
            } else if (elementType instanceof AtomicType) {
                // using toString here to avoid this log message:
                // Property 'value' of class org.codehaus.groovy.runtime.GStringImpl has no read method. SKIPPED
                return JSONObject.fromObject([
                        name: 'value',
                        type: "Iterable<${elementType.type.simpleName}>".toString()
                ])
            } else if (elementType instanceof EnumType) {
                return JSONObject.fromObject([
                        name: 'value',
                        type: 'Iterable<String>'
                ])
            }
        }
        throw new UnsupportedParameterType()
    }

    private static DescribableModel getContextModel(ParameterType parameterType) {
        if (parameterType instanceof HomogeneousObjectType) {
            return parameterType.schemaType
        } else if (parameterType instanceof HeterogeneousObjectType) {
            return new DescribableModel(parameterType.type)
        } else if (parameterType instanceof ArrayType) {
            return getContextModel(parameterType.elementType)
        }
        null
    }

    private static boolean isListContext(ParameterType parameterType) {
        parameterType instanceof ArrayType || parameterType instanceof HeterogeneousObjectType
    }

    private static String getContextClassName(DescribableModel model) {
        model.type.name
    }

    private static String getListContextClassName(ParameterType parameterType) {
        getContextClassName(parameterType) + '$$List'
    }

    private static String getContextClassName(ParameterType parameterType) {
        if (parameterType instanceof HomogeneousObjectType) {
            return getContextClassName(parameterType.schemaType)
        } else if (parameterType instanceof HeterogeneousObjectType) {
            return parameterType.type.name
        } else if (parameterType instanceof ArrayType) {
            return getContextClassName(parameterType.elementType)
        }
        null
    }

    private static Map<String, DescribableModel> getTypes(ParameterType parameterType) {
        if (parameterType instanceof HomogeneousObjectType) {
            return findDescribableModels([parameterType.schemaType])
        } else if (parameterType instanceof HeterogeneousObjectType) {
            return DescribableHelper.findDescribableModels(parameterType.types.values())
        } else if (parameterType instanceof ArrayType) {
            return getTypes(parameterType.elementType)
        }
        [:]
    }

    private static JSONObject generateOptionalClosureParameter() {
        JSONObject.fromObject([name: 'closure', type: 'Closure', defaultValue: 'null'])
    }

    private static void generateHelp(JSONObject object, String text) {
        if (text) {
            String normalizedText = text.replaceAll(/\r?\n/, '\n').trim()
            if (normalizedText) {
                object.element('html', normalizedText)
                String firstSentence = firstSentence(normalizedText)
                if (firstSentence) {
                    object.element('firstSentenceCommentText', firstSentence)
                }
            }
        }
    }

    @PackageScope
    static String firstSentence(String text) {
        // remove HTML comments
        String cleanText = text.replaceAll(/(?s)<!--.*?-->/, '')
        // remove HTML tags
        cleanText = cleanText.replaceAll(/<.*?>/, '')
        // unescape HTML and remove leading/trailing spaces
        cleanText = unescapeHtml(cleanText).trim()

        if (cleanText.empty) {
            return ''
        }

        BreakIterator iterator = BreakIterator.sentenceInstance
        iterator.text = new StringCharacterIterator(cleanText)
        cleanText[0..iterator.next() - 1].trim()
    }

    private static Map<String, List<Method>> findUniqueMethods(Set<Method> methods) {
        List<Method> result = []
        List<Method> duplicateMethods = []
        methods.each { Method method ->
            if (!duplicateMethods.any { hasIdenticalSignature(it, method) }) {
                Method identicalMethod = result.find { hasIdenticalSignature(it, method) }
                if (identicalMethod) {
                    duplicateMethods << method
                    result.remove(identicalMethod)
                } else {
                    result << method
                }
            }
        }
        result.groupBy { it.name }
    }

    private static class UnsupportedParameterType extends RuntimeException {
    }
}
