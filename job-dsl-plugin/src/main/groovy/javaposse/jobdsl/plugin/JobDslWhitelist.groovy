package javaposse.jobdsl.plugin

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.plugin.structs.DescribableContext
import javaposse.jobdsl.plugin.structs.DescribableListContext
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.AbstractWhitelist

import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * Allows methods defined in {@link Context}.
 */
class JobDslWhitelist extends AbstractWhitelist {
    private static final Method INVOKE_METHOD = GroovyObject.getDeclaredMethod('invokeMethod', String, Object)
    private static final Set<Class> DYNAMIC_CONTEXTS = [
            AbstractExtensibleContext, DescribableContext, DescribableListContext
    ]

    @Override
    boolean permitsConstructor(Constructor<?> constructor, Object[] args) {
        constructor.declaringClass == JenkinsJobParent
    }

    @Override
    boolean permitsMethod(Method method, Object receiver, Object[] args) {
        Context.isAssignableFrom(method.declaringClass) ||
                (method == INVOKE_METHOD && receiver.class.classLoader == JobDslWhitelist.classLoader &&
                        DYNAMIC_CONTEXTS.any { context -> context.isInstance(receiver) })
    }
}
