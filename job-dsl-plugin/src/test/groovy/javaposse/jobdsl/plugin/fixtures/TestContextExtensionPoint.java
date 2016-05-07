package javaposse.jobdsl.plugin.fixtures;

import hudson.Extension;
import hudson.model.Item;
import javaposse.jobdsl.dsl.Context;
import javaposse.jobdsl.dsl.helpers.step.StepContext;
import javaposse.jobdsl.dsl.helpers.triggers.TriggerContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslEnvironment;
import javaposse.jobdsl.plugin.DslExtensionMethod;
import org.spockframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Extension(optional = true)
public class TestContextExtensionPoint extends ContextExtensionPoint {
    private List<String> createdItems = new ArrayList<String>();
    private List<String> updatedItems = new ArrayList<String>();

    @DslExtensionMethod(context = StepContext.class)
    public Object test() {
        return new SomeValueObject("foo", 42, true);
    }

    @DslExtensionMethod(context = StepContext.class)
    public Object twice() {
        return "error";
    }

    @DslExtensionMethod(context = StepContext.class)
    public Object testComplexObject(String arg1, int arg2, boolean arg3) {
        return new SomeValueObject(arg1, arg2, arg3);
    }

    @DslExtensionMethod(context = StepContext.class)
    public Object withNestedContext(Runnable closure) {
        TestContext context = new TestContext();
        executeInContext(closure, context);

        return new SomeValueObject(context.value1, context.value2, context.value3);
    }

    @DslExtensionMethod(context = StepContext.class)
    public Object withEnvironment(DslEnvironment environment, String arg1, int arg2, boolean arg3) {
        Assert.that(environment != null);
        return new SomeValueObject(arg1, arg2, arg3);
    }

    @DslExtensionMethod(context = StepContext.class)
    public Object withNoValue() {
        return null;
    }

    /**
     * Hides {@link SomeTrigger}.
     */
    @DslExtensionMethod(context = TriggerContext.class)
    public Object someTrigger(DslEnvironment environment, int foo, String bar, Runnable closure) {
        return null;
    }

    @DslExtensionMethod(context = TriggerContext.class)
    public Object someTrigger(DslEnvironment environment, int foo, Runnable closure) {
        return null;
    }

    @Override
    public void notifyItemCreated(Item item, DslEnvironment dslEnvironment) {
        createdItems.add(item.getFullName());
    }

    @Override
    public void notifyItemUpdated(Item item, DslEnvironment dslEnvironment) {
        updatedItems.add(item.getFullName());
    }

    boolean isItemCreated(String name) {
        return createdItems.contains(name);
    }

    boolean isItemUpdated(String name) {
        return updatedItems.contains(name);
    }

    public static class SomeValueObject {
        private String value1;
        private int value2;
        private boolean value3;

        public SomeValueObject(String value1, int value2, boolean value3) {
            this.value1 = value1;
            this.value2 = value2;
            this.value3 = value3;
        }
    }

    public static class TestContext implements Context {
        private String value1;
        private int value2;
        private boolean value3;

        public void value1(String value) {
            this.value1 = value;
        }

        public void value2(int value) {
            this.value2 = value;
        }

        public void value3(boolean value) {
            this.value3 = value;
        }
    }
}
