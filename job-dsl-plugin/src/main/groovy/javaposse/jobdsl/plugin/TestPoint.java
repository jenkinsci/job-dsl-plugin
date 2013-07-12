package javaposse.jobdsl.plugin;

import hudson.Extension;
import hudson.model.Item;
import javaposse.jobdsl.dsl.helpers.PropertiesContext;

@Extension
public class TestPoint extends ContextExtensionPoint {
    @DslMethod(context = PropertiesContext.class)
    public Object xxx(Runnable closure) {
        return "<hudson.plugins.timestamper.TimestamperBuildWrapper/>";
    }

    @Override
    public void notifyItemCreated(Item item) {
        System.out.println(item);
        System.out.println(getContext());
    }

    @Override
    public void notifyItemUpdated(Item item) {
        System.out.println(item);
        System.out.println(getContext());
    }
}
