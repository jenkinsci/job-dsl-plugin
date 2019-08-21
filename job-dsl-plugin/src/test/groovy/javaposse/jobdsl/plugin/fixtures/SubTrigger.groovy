package javaposse.jobdsl.plugin.fixtures

import hudson.Extension
import org.jenkinsci.Symbol
import org.kohsuke.stapler.DataBoundConstructor

class SubTrigger extends FooTrigger {
    @DataBoundConstructor
    SubTrigger() {
    }

    @Extension
    @Symbol(['sub'])
    static class DescriptorImpl extends FooTrigger.DescriptorImpl {
    }
}
