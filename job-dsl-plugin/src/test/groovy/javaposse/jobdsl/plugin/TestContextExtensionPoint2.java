package javaposse.jobdsl.plugin;

import hudson.Extension;
import hudson.model.Item;
import javaposse.jobdsl.dsl.helpers.PropertiesContext;

@Extension
public class TestContextExtensionPoint2 extends ContextExtensionPoint {
    @DslMethod(context = PropertiesContext.class)
    public Object twice() {
        return "error";
    }

    @Override
    public void notifyItemCreated(Item item) {
    }

    @Override
    public void notifyItemUpdated(Item item) {
    }
}
