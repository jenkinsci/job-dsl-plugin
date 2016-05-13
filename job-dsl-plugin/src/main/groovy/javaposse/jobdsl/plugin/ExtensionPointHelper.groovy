package javaposse.jobdsl.plugin;

import groovy.lang.Closure;
import javaposse.jobdsl.dsl.ContextHelper;
import javaposse.jobdsl.dsl.ExtensibleContext;
import javaposse.jobdsl.plugin.structs.DescribableContext;
import org.apache.commons.lang.ClassUtils;
import org.jenkinsci.plugins.structs.describable.DescribableModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static javaposse.jobdsl.plugin.structs.DescribableHelper.findDescribableModels;
import static javaposse.jobdsl.plugin.structs.DescribableHelper.isOptionalClosureArgument;
import static org.apache.commons.lang.ClassUtils.convertClassesToClassNames;
import static org.apache.commons.lang.StringUtils.join;

class ExtensionPointHelper {
    static Set<DslExtension> findExtensionPoints(String name, Class<? extends ExtensibleContext> contextType,
                                                 Object... args) {
        Class[] parameterTypes = ClassUtils.toClass(args);
        Set<DslExtension> candidates = new HashSet<DslExtension>();

        // Find extensions that match any @DslExtensionMethod annotated method with the given name and parameters
        for (ExtensionPointMethod candidate : findCandidateMethods(name, contextType)) {
            if (ClassUtils.isAssignable(parameterTypes, filterParameterTypes(candidate.method), true)) {
                candidates.add(candidate);
            }
        }

        if (candidates.isEmpty() && isOptionalClosureArgument(args)) {
            for (DescribableModel candidate : findDescribableModels(contextType, name)) {
                candidates.add(new DescribableExtension(candidate));
            }
        }

        return candidates;
    }

    static Map<Method, ContextExtensionPoint> findExtensionMethods(Class<? extends ExtensibleContext> contextType) {
        Map<Method, ContextExtensionPoint> result = new HashMap<Method, ContextExtensionPoint>();
        for (ContextExtensionPoint extensionPoint : ContextExtensionPoint.all()) {
            for (Method method : extensionPoint.getClass().getMethods()) {
                DslExtensionMethod annotation = method.getAnnotation(DslExtensionMethod.class);
                if (annotation != null && annotation.context().isAssignableFrom(contextType)) {
                    result.put(method, extensionPoint);
                }
            }
        }
        return result;
    }

    static boolean hasIdenticalSignature(Method method1, Method method2) {
        return method1.getName().equals(method2.getName()) &&
                Arrays.equals(filterParameterTypes(method1), filterParameterTypes(method2));
    }

    static Class<?>[] filterParameterTypes(Method method) {
        List<Class<?>> result = new ArrayList<Class<?>>();
        for (Class<?> parameterType : method.getParameterTypes()) {
            if (isVisibleParameterType(parameterType)) {
                result.add(parameterType);
            }
        }
        return result.toArray(new Class<?>[result.size()]);
    }

    static boolean isVisibleParameterType(Class parameterType) {
        return !DslEnvironment.class.isAssignableFrom(parameterType);
    }

    private static List<ExtensionPointMethod> findCandidateMethods(String name, Class<? extends ExtensibleContext> contextType) {
        List<ExtensionPointMethod> result = new ArrayList<ExtensionPointMethod>();
        for (Map.Entry<Method, ContextExtensionPoint> entry : findExtensionMethods(contextType).entrySet()) {
            if (entry.getKey().getName().equals(name)) {
                result.add(new ExtensionPointMethod(entry.getValue(), entry.getKey()));
            }
        }
        return result;
    }

    interface DslExtension {
        Object call(DslEnvironment environment, Object[] args) throws InvocationTargetException, IllegalAccessException;
    }

    static class ExtensionPointMethod implements DslExtension {
        final ContextExtensionPoint extensionPoint;

        final Method method;

        ExtensionPointMethod(ContextExtensionPoint extensionPoint, Method method) {
            this.extensionPoint = extensionPoint;
            this.method = method;
        }

        @Override
        public String toString() {
            return extensionPoint.getClass() +
                    "." +
                    method.getName() +
                    "(" +
                    join(convertClassesToClassNames(Arrays.asList(method.getParameterTypes())), ", ") +
                    ")";
        }

        @Override
        public Object call(DslEnvironment environment, Object[] args)
                throws InvocationTargetException, IllegalAccessException {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] processedArgs = new Object[parameterTypes.length];
            int j = 0;
            for (int i = 0; i < parameterTypes.length; i++) {
                processedArgs[i] = DslEnvironment.class.isAssignableFrom(parameterTypes[i]) ? environment : args[j++];
            }
            return method.invoke(extensionPoint, processedArgs);
        }
    }

    static class DescribableExtension implements DslExtension {
        final DescribableModel describableModel;

        DescribableExtension(DescribableModel describableModel) {
            this.describableModel = describableModel;
        }

        @Override
        public Object call(DslEnvironment environment, Object[] args)
                throws InvocationTargetException, IllegalAccessException {
            DescribableContext delegate = new DescribableContext(describableModel);
            if (args.length == 1 && args[0] instanceof Closure) {
                ContextHelper.executeInContext((Closure) args[0], delegate);
            }
            return delegate.createInstance();
        }

        @Override
        public String toString() {
            return describableModel.getType().getName();
        }
    }
}
