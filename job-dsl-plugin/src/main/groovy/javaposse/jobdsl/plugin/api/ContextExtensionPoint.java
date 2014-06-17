package javaposse.jobdsl.plugin.api;

import com.google.common.base.Preconditions;
import groovy.lang.Closure;
import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.Item;
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper;
import javaposse.jobdsl.dsl.helpers.Context;
import jenkins.model.Jenkins;

/**
 * An ExtensionPoint for the job-dsl-plugin to extend it with new DSL methods.
 */
public abstract class ContextExtensionPoint implements ExtensionPoint {
    /**
     * Internally shared object.
     */
    private Context context;

    /**
     * Set the context to use it later inside the notification methods.
     *
     * @param context The context object.
     */
    protected void setContext(Context context) {
        this.context = context;
    }

    /**
     * The previously stored context.
     *
     * @return The context object.
     */
    protected Context getContext() {
        return context;
    }

    /**
     * Notifies the ExtensionPoint if an item has been created. Implementations should override this to get notified.
     * The default implementation is empty.
     *
     * @param item the newly created item
     */
    public void notifyItemCreated(Item item) {
    }

    /**
     * Notifies the ExtensionPoint if an item has been updated. Implementations should override this to get notified.
     * The default implementation is empty.
     *
     * @param item the updated item
     */
    public void notifyItemUpdated(Item item) {
    }

    /**
     * Call the {@link Runnable}, which must be a Groovy closure, in the given {@link Context}.
     *
     * @param runnable the Groovy closure
     * @param context  the {@link Context} for the {@link Runnable}
     */
    public static void executeInContext(Runnable runnable, Context context) {
        if (runnable != null) {
            Preconditions.checkArgument(runnable instanceof Closure, "runnable must be a Groovy closure");

            AbstractContextHelper.executeInContext((Closure) runnable, context);
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
