package javaposse.jobdsl.plugin;

import javaposse.jobdsl.dsl.helpers.ExtensibleContext;
import org.apache.commons.lang.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang.ClassUtils.convertClassesToClassNames;
import static org.apache.commons.lang.StringUtils.join;

class ExtensionPointHelper {
    static Set<ExtensionPointMethod> findExtensionPoints(String name, Class<? extends ExtensibleContext> contextType,
                                                         Object... args) {
        Class[] parameterTypes = ClassUtils.toClass(args);
        Set<ExtensionPointMethod> candidates = new HashSet<ExtensionPointMethod>();

        // Find extensions that match any @DslExtensionMethod annotated method with the given name and parameters
        for (ExtensionPointMethod candidate : findCandidateMethods(name, contextType)) {
            if (ClassUtils.isAssignable(parameterTypes, candidate.getFilteredParameterTypes(), true)) {
                candidates.add(candidate);
            }
        }
        return candidates;
    }

    private static List<ExtensionPointMethod> findCandidateMethods(String name, Class<? extends ExtensibleContext> contextType) {
        List<ExtensionPointMethod> result = new ArrayList<ExtensionPointMethod>();
        for (ContextExtensionPoint extensionPoint : ContextExtensionPoint.all()) {
            for (Method method : extensionPoint.getClass().getMethods()) {
                if (method.getName().equals(name)) {
                    DslExtensionMethod annotation = method.getAnnotation(DslExtensionMethod.class);
                    if (annotation != null && annotation.context().isAssignableFrom(contextType)) {
                        result.add(new ExtensionPointMethod(extensionPoint, method));
                    }
                }
            }
        }
        return result;
    }

    static class ExtensionPointMethod {
        private final ContextExtensionPoint extensionPoint;
        private final Method method;

        ExtensionPointMethod(ContextExtensionPoint extensionPoint, Method method) {
            this.extensionPoint = extensionPoint;
            this.method = method;
        }

        public Class<?>[] getFilteredParameterTypes() {
            List<Class<?>> result = new ArrayList<Class<?>>();
            for (Class<?> parameterType : method.getParameterTypes()) {
                if (!DslEnvironment.class.isAssignableFrom(parameterType)) {
                    result.add(parameterType);
                }
            }
            return result.toArray(new Class<?>[result.size()]);
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
}
