package javaposse.jobdsl.plugin;

import groovy.lang.Closure;
import javaposse.jobdsl.dsl.ContextHelper;
import javaposse.jobdsl.dsl.ExtensibleContext;
import javaposse.jobdsl.plugin.structs.DescribableContext;
import jenkins.model.Jenkins;
import org.apache.commons.lang.ClassUtils;
import org.jenkinsci.plugins.structs.describable.DescribableModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
            if (ClassUtils.isAssignable(parameterTypes, candidate.getFilteredParameterTypes(), true)) {
                candidates.add(candidate);
            }
        }

        if (candidates.isEmpty() && Jenkins.getInstance().getPluginManager().getPlugin("structs") != null &&
                isOptionalClosureArgument(args)) {
            for (DescribableModel candidate : findDescribableModels(contextType, name)) {
                candidates.add(new DescribableExtension(candidate));
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

    interface DslExtension {
        Object call(DslEnvironment environment, Object[] args) throws InvocationTargetException, IllegalAccessException;
    }

    private static class ExtensionPointMethod implements DslExtension {
        private final ContextExtensionPoint extensionPoint;
        private final Method method;

        ExtensionPointMethod(ContextExtensionPoint extensionPoint, Method method) {
            this.extensionPoint = extensionPoint;
            this.method = method;
        }

        Class<?>[] getFilteredParameterTypes() {
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

    private static class DescribableExtension implements DslExtension {
        private final DescribableModel describableModel;

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
