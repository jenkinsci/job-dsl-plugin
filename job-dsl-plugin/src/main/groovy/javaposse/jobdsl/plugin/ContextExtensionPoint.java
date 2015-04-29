package javaposse.jobdsl.plugin;

import com.google.common.base.Preconditions;
import groovy.lang.Closure;
import hudson.ExtensionList;
import hudson.ExtensionPoint;
import javaposse.jobdsl.dsl.Context;
import javaposse.jobdsl.dsl.ContextHelper;
import jenkins.model.Jenkins;

/**
 * An ExtensionPoint for the job-dsl-plugin to extend it with new DSL methods.
 */
public abstract class ContextExtensionPoint implements ExtensionPoint {
    /**
     * Call the {@link Runnable}, which must be a Groovy closure, in the given {@link Context}.
     *
     * @param runnable the Groovy closure
     * @param context  the {@link Context} for the {@link Runnable}
     */
    public static void executeInContext(Runnable runnable, Context context) {
        if (runnable != null) {
            Preconditions.checkArgument(runnable instanceof Closure, "runnable must be a Groovy closure");

            ContextHelper.executeInContext((Closure) runnable, context);
        }
    }

    /**
     * Returns all registered JobDslContextExtensionPoints.
     *
     * @return a list of all registered JobDslContextExtensionPoints.
     */
    public static ExtensionList<ContextExtensionPoint> all() {
        return Jenkins.getInstance().getExtensionList(ContextExtensionPoint.class);
    }
}
