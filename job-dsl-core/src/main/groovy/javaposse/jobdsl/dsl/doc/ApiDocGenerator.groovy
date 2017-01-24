package javaposse.jobdsl.dsl.doc

import groovy.json.JsonBuilder
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.NoDoc
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.RequiresPlugins
import org.codehaus.groovy.groovydoc.GroovyAnnotationRef
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.groovydoc.GroovyMethodDoc
import org.codehaus.groovy.groovydoc.GroovyParameter
import org.codehaus.groovy.groovydoc.GroovyTag
import org.codehaus.groovy.tools.groovydoc.ArrayClassDocWrapper

import java.lang.annotation.Annotation
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ApiDocGenerator {

    final private GroovyDocHelper docHelper = new GroovyDocHelper('src/main/groovy/')
    final private String commandDocsPath = 'src/main/docs'
    final private Class rootClass = DslFactory
    final private Map allContextClasses = [:]
    final private List allContextClassesList = []

    static void main(String[] args) {
        String version = args[0]
        String outputPath = args[1]

        JsonBuilder builder = new ApiDocGenerator().generateApi(version)

        File file = new File(outputPath)
        file.parentFile.mkdirs()

        file.withWriter('UTF-8') { it << builder.toPrettyString() }
    }

    JsonBuilder generateApi(String version) {
        allContextClassesList << rootClass.name
        allContextClasses[rootClass.name] = processClass(rootClass)
        Map map = [
            version: version,
            root: [
                name: 'Jenkins Job DSL API',
                contextClass: rootClass.name
            ],
            contexts: allContextClasses
        ]

        JsonBuilder builder = new JsonBuilder()
        builder map

        builder
    }

    private Map processClass(Class clazz) {
        Map map = [type: clazz.name]

        map.methods = getMethodsForClass(clazz)
        getDelegateClasses(clazz).each { Class delegateClass ->
            map.methods.addAll getMethodsForClass(delegateClass)
        }
        map.methods = map.methods.sort { it.name }
        map
    }

    private List<Class> getDelegateClasses(Class clazz) {
        GroovyClassDoc classDoc = docHelper.getGroovyClassDoc(clazz)
        List delegateNames = classDoc.properties().findAll {
            it.annotations().any { GroovyAnnotationRef a -> a.name == 'Delegate' }
        }*.name()

        Field[] declaredFields = clazz.declaredFields.findAll { delegateNames.contains it.name }
        declaredFields*.type
    }

    private List getMethodsForClass(Class clazz) {
        List<String> methodNames = clazz.methods.findAll {
            !it.name.startsWith('get') &&
                (!it.name.startsWith('set') || it.name == 'setBuildResult') &&
                !it.name.startsWith('is') &&
                !(it.declaringClass in [Object, Script]) &&
                Modifier.isPublic(it.modifiers) &&
                !it.name.contains('$') &&
                (!it.getAnnotation(NoDoc) || it.getAnnotation(NoDoc).embeddedOnly()) &&
                !(it.name in ['invokeMethod', 'executeWithXmlActions', 'methodMissing'])
        }*.name.unique().sort()
        methodNames.collect { processMethodName it, clazz }
    }

    private Map processMethodName(String methodName, Class clazz) {
        Map methodMap = [
            name      : methodName,
            signatures: []
        ]
        GroovyMethodDoc[] methodDocs = docHelper.getAllMethods(clazz).findAll { it.name() == methodName }

        methodDocs.each { GroovyMethodDoc methodDoc ->
            Method method = GroovyDocHelper.getMethodFromGroovyMethodDoc(methodDoc, clazz)
            if (method) {
                Map signature = processMethod(method, methodDoc)
                methodMap.signatures << signature

                Class contextClass = getContextClass(method, methodDoc)
                if (contextClass) {
                    signature.contextClass = contextClass.name
                    if (!allContextClassesList.contains(contextClass.name)) {
                        allContextClassesList << contextClass.name
                        allContextClasses[contextClass.name] = processClass(contextClass)
                    }
                }
            }
        }

        String examples = getExamples(clazz, methodName)
        if (examples) {
            methodMap.examples = examples.trim()
        }

        methodMap
    }

    private String getExamples(Class clazz, String methodName) {
        String path = "${clazz.name.replaceAll('\\.', '/')}/$methodName"
        File file = new File("${commandDocsPath}/examples/${path}.groovy")
        if (file.exists()) {
            file.text
        } else if (clazz.superclass) {
            getExamples(clazz.superclass, methodName)
        } else {
            null
        }
    }

    private Class getContextClass(Method method, GroovyMethodDoc methodDoc) {
        Class clazz = null
        GroovyParameter[] groovyParameters = methodDoc.parameters()
        if (groovyParameters.length) {
            Annotation[][] parameterAnnotations = method.parameterAnnotations
            clazz = getContextClass(groovyParameters[-1], parameterAnnotations[-1])
        }
        clazz
    }

    private Class getContextClass(GroovyParameter parameter, Annotation[] annotations) {
        Class clazz = null
        if (parameter.typeName() == 'groovy.lang.Closure') {
            DelegatesTo annotation = annotations.find { it.annotationType() == DelegatesTo } as DelegatesTo
            if (annotation) {
                clazz = annotation.value()
            }
        }
        clazz
    }

    private Map processMethod(Method method, GroovyMethodDoc methodDoc) {
        Map map = [parameters: []]
        Type[] types = method.genericParameterTypes
        GroovyTag[] tags = methodDoc.tags()
        methodDoc.parameters().eachWithIndex { GroovyParameter parameter, int index ->
            map.parameters << processParameter(parameter, types[index])
        }

        if (method.getAnnotation(Deprecated) || tags.any { it.name() == 'deprecated' }) {
            map.deprecated = true
            String deprecatedText = tags.find { it.name() == 'deprecated' }?.text()?.trim()
            if (deprecatedText) {
                map.deprecatedText = stripTags(deprecatedText)
                map.deprecatedHtml = deprecatedText
            }
        }

        if (method.getAnnotation(NoDoc) && method.getAnnotation(NoDoc).embeddedOnly()) {
            map.embeddedOnly = true
        }

        String availableSince = tags.find { it.name() == 'since' }?.text()
        if (availableSince) {
            map.availableSince = availableSince.trim()
        }

        List plugins = []
        RequiresPlugin requiresPluginAnnotation = method.getAnnotation(RequiresPlugin)
        if (requiresPluginAnnotation) {
            plugins << createPlugin(requiresPluginAnnotation)
        }
        RequiresPlugins requiresPluginsAnnotation = method.getAnnotation(RequiresPlugins)
        if (requiresPluginsAnnotation) {
            requiresPluginsAnnotation.value().each { plugins << createPlugin(it) }
        }
        if (plugins) {
            map.plugins = plugins
        }

        GroovyMethodDoc methodDocWithComment = getMethodHierarchy(method, methodDoc).find {
            it.commentText().trim()
        }
        if (methodDocWithComment) {
            String comment = methodDocWithComment.commentText().trim()
            int defListIndex = comment.indexOf('<DL>')
            if (defListIndex != -1) {
                comment = comment[0..<defListIndex]
            }
            if (comment) {
                map.html = comment.trim()
            }

            String firstSentenceCommentText = methodDocWithComment.firstSentenceCommentText()
            int annotationIndex = firstSentenceCommentText.indexOf('@')
            if (annotationIndex != -1) {
                firstSentenceCommentText = firstSentenceCommentText[0..<annotationIndex]
            }
            if (firstSentenceCommentText) {
                firstSentenceCommentText = stripTags(firstSentenceCommentText)
                map.firstSentenceCommentText = firstSentenceCommentText
            }
        }

        map
    }

    /**
     * Returns the method and all methods that it overrides.
     */
    private List<GroovyMethodDoc> getMethodHierarchy(Method method, GroovyMethodDoc groovyMethodDoc) {
        List<GroovyMethodDoc> result = []

        Class clazz = method.declaringClass
        while (clazz) {
            result.addAll(docHelper.getAllMethods(clazz).findAll {
                it.name() == groovyMethodDoc.name() &&
                        it.parameters()*.typeName() == groovyMethodDoc.parameters()*.typeName()
            })
            clazz = clazz.superclass
        }

        result
    }

    private Map processParameter(GroovyParameter parameter, Type type) {
        Map map = [name: parameter.name()]
        Class clazz
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (type as ParameterizedType)
            clazz = parameterizedType.rawType as Class
            map.type = getSimpleClassName(clazz) + '<' + parameterizedType.actualTypeArguments.collect {
                getSimpleClassName it
            }.join(', ') + '>'
        } else {
            clazz = type as Class
            if (parameter.vararg()) {
                map.type = getSimpleClassName(clazz.componentType) + '...'
            } else if (parameter.type() && parameter.type() instanceof ArrayClassDocWrapper) {
                map.type = getSimpleClassName(clazz.componentType) + '[]'
            } else {
                map.type = getSimpleClassName(clazz)
            }
            map.type = map.type.replaceAll('\\$', '.') // fix inner class names
        }

        if (clazz.isEnum()) {
            map.enumConstants = clazz.enumConstants*.toString()
        }

        if (parameter.defaultValue()) {
            map.defaultValue = parameter.defaultValue()
        }
        map
    }

    private String getSimpleClassName(Class clazz) {
        String name = clazz.name
        List prefixes = [
            'java.lang.',
            'java.util.',
            'groovy.lang.',
        ]
        for (String prefix in prefixes) {
            if (name.startsWith(prefix)) {
                name = name[prefix.length()..-1]
                break
            }
        }
        name
    }

    private static Map createPlugin(RequiresPlugin requiresPluginAnnotation) {
        Map plugin = [id: requiresPluginAnnotation.id()]
        if (requiresPluginAnnotation.minimumVersion()) {
            plugin.minimumVersion = requiresPluginAnnotation.minimumVersion()
        }
        plugin
    }

    private static String stripTags(String text) {
        text.replaceAll('<[^>]*>', '')
    }
}
