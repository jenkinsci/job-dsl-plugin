package javaposse.jobdsl.plugin;

import hudson.Extension;
import hudson.model.Item;
import javaposse.jobdsl.dsl.helpers.PropertiesContext;

@Extension
public class TestContextExtensionPoint extends ContextExtensionPoint {
    @DslMethod(context = PropertiesContext.class)
    public Object test() {
        return "<testNode/>";
    }

    @DslMethod(context = PropertiesContext.class)
    public Object twice() {
        return "error";
    }

    @DslMethod(context = PropertiesContext.class)
    public Object testComplexObject(String arg1, int arg2, boolean arg3) {
        return new SomeValueObject(arg1, arg2, arg3);
    }

    @Override
    public void notifyItemCreated(Item item) {
    }

    @Override
    public void notifyItemUpdated(Item item) {
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

        public String getValue1() {
            return value1;
        }

        public void setValue1(String value1) {
            this.value1 = value1;
        }

        public int getValue2() {
            return value2;
        }

        public void setValue2(int value2) {
            this.value2 = value2;
        }

        public boolean isValue3() {
            return value3;
        }

        public void setValue3(boolean value3) {
            this.value3 = value3;
        }
    }
}
