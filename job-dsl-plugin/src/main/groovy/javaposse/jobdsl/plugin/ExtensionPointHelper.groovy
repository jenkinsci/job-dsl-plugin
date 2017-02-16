package javaposse.jobdsl.plugin

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.ExtensibleContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.plugin.structs.DescribableContext
import org.apache.commons.lang.ClassUtils
import org.jenkinsci.plugins.structs.describable.DescribableModel

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

import static javaposse.jobdsl.plugin.structs.DescribableHelper.findDescribableModels
import static javaposse.jobdsl.plugin.structs.DescribableHelper.isOptionalClosureArgument

class ExtensionPointHelper {
    static Set<DslExtension> findExtensionPoints(String name, Class<? extends ExtensibleContext> contextType,
                                                 Object... args) {
        Class[] parameterTypes = ClassUtils.toClass(args)
        Set<DslExtension> candidates

        // Find extensions that match any @DslExtensionMethod annotated method with the given name and parameters
        candidates = findCandidateMethods(name, contextType).findAll {
            ClassUtils.isAssignable(parameterTypes, filterParameterTypes(it.method), true)
        }

        if (candidates.empty && isOptionalClosureArgument(args)) {
            candidates = findDescribableModels(contextType, name).collect { new DescribableExtension(it) }
        }

        candidates
    }

    static Map<Method, ContextExtensionPoint> findExtensionMethods(Class<? extends ExtensibleContext> contextType) {
        Map<Method, ContextExtensionPoint> result = [:]
        ContextExtensionPoint.all().each { ContextExtensionPoint extensionPoint ->
            extensionPoint.class.methods.each { Method method ->
                DslExtensionMethod annotation = method.getAnnotation(DslExtensionMethod)
                if (annotation != null && annotation.context().isAssignableFrom(contextType)) {
                    result[method] = extensionPoint
                }
            }
        }
        result
    }

    static boolean hasIdenticalSignature(Method method1, Method method2) {
        method1.name == method2.name && Arrays.equals(filterParameterTypes(method1), filterParameterTypes(method2))
    }

    static Class<?>[] filterParameterTypes(Method method) {
        method.parameterTypes.findAll { isVisibleParameterType(it) }
    }

    static boolean isVisibleParameterType(Class parameterType) {
        !DslEnvironment.isAssignableFrom(parameterType)
    }

    private static List<ExtensionPointMethod> findCandidateMethods(String name,
                                                                   Class<? extends ExtensibleContext> contextType) {
        findExtensionMethods(contextType).findAll { it.key.name == name }.collect {
            new ExtensionPointMethod(it.value, it.key)
        }
    }

    interface DslExtension {
        Object call(DslEnvironment environment, JobManagement jobManagement, Object[] args)
                throws InvocationTargetException

        boolean isDeprecated()
    }

    static class ExtensionPointMethod implements DslExtension {
        final ContextExtensionPoint extensionPoint
        final Method method

        ExtensionPointMethod(ContextExtensionPoint extensionPoint, Method method) {
            this.extensionPoint = extensionPoint
            this.method = method
        }

        @Override
        String toString() {
            "${extensionPoint.class}.${method.name}(${method.parameterTypes*.name.join(', ')}"
        }

        @Override
        boolean isDeprecated() {
            method.getAnnotation(Deprecated) != null
        }

        @Override
        Object call(DslEnvironment environment, JobManagement jobManagement, Object[] args) {
            Class<?>[] parameterTypes = method.parameterTypes
            Object[] processedArgs = new Object[parameterTypes.length]
            int j = 0
            for (int i = 0; i < parameterTypes.length; i++) {
                processedArgs[i] = DslEnvironment.isAssignableFrom(parameterTypes[i]) ? environment : args[j++]
            }
            method.invoke(extensionPoint, processedArgs)
        }
    }

    static class DescribableExtension implements DslExtension {
        final DescribableModel describableModel

        DescribableExtension(DescribableModel describableModel) {
            this.describableModel = describableModel
        }

        @Override
        Object call(DslEnvironment environment, JobManagement jobManagement, Object[] args) {
            DescribableContext delegate = new DescribableContext(describableModel, jobManagement)
            if (args.length == 1 && args[0] instanceof Closure) {
                ContextHelper.executeInContext((Closure) args[0], delegate)
            }
            delegate.createInstance()
        }

        @Override
        boolean isDeprecated() {
            describableModel.deprecated
        }

        @Override
        String toString() {
            describableModel.type.name
        }
    }
}
