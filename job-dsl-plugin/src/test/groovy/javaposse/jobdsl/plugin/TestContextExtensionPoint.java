package javaposse.jobdsl.plugin;

import hudson.Extension;
import javaposse.jobdsl.dsl.Context;
import javaposse.jobdsl.dsl.DslExtensionMethod;
import javaposse.jobdsl.dsl.helpers.step.StepContext;

@Extension
public class TestContextExtensionPoint extends ContextExtensionPoint {
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
