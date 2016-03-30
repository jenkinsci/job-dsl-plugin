package javaposse.jobdsl.plugin.fixtures;

import hudson.Extension;
import javaposse.jobdsl.dsl.helpers.step.StepContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslExtensionMethod;

@Extension(optional = true)
public class TestContextExtensionPoint2 extends ContextExtensionPoint {
    @DslExtensionMethod(context = StepContext.class)
    public Object twice() {
        return "error";
    }
}
