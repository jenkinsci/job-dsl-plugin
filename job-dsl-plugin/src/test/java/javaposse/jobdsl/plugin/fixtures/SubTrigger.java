package javaposse.jobdsl.plugin.fixtures;

import hudson.Extension;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

public class SubTrigger extends FooTrigger {
    @DataBoundConstructor
    public SubTrigger() {}

    @Extension
    @Symbol("sub")
    public static class DescriptorImpl extends FooTrigger.DescriptorImpl {}
}
