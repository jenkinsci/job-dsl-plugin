package javaposse.jobdsl.plugin.fixtures

import hudson.Extension
import hudson.model.Job
import hudson.triggers.Trigger
import org.jenkinsci.Symbol
import org.kohsuke.stapler.DataBoundConstructor

class SubTrigger extends Trigger<Job> {
    @DataBoundConstructor
    SubTrigger() {
    }

    @Extension
    @Symbol(['sub'])
    static class DescriptorImpl extends FooTrigger.DescriptorImpl {
    }
}
