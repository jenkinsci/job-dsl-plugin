package javaposse.jobdsl.plugin.fixtures;

import hudson.Extension;
import javaposse.jobdsl.dsl.helpers.step.StepContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslEnvironment;
import javaposse.jobdsl.plugin.DslExtensionMethod;

@Extension(optional = true)
public class TestContextExtensionPoint2 extends ContextExtensionPoint {
    @DslExtensionMethod(context = StepContext.class)
    public Object twice() {
        return "error";
    }

    @DslExtensionMethod(context = StepContext.class)
    public Object testSignatureOne(DslEnvironment environment) {
        return null;
    }

    @DslExtensionMethod(context = StepContext.class)
    public Object testSignatureOne(DslEnvironment environment, String foo) {
        return null;
    }

    @DslExtensionMethod(context = StepContext.class)
    public Object testSignatureOne(String foo) {
        return null;
    }

    @DslExtensionMethod(context = StepContext.class)
    public Object testSignatureTwo() {
        return null;
    }

    @DslExtensionMethod(context = StepContext.class)
    public Object testSignatureTwo(String foo) {
        return null;
    }
}
