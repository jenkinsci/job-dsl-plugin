package javaposse.jobdsl.plugin;

import hudson.Extension;
import javaposse.jobdsl.dsl.helpers.PropertiesContext;
import javaposse.jobdsl.plugin.api.ContextExtensionPoint;
import javaposse.jobdsl.plugin.api.DslMethod;

@Extension
public class TestContextExtensionPoint2 extends ContextExtensionPoint {
    @DslMethod(context = PropertiesContext.class)
    public Object twice() {
        return "error";
    }
}
