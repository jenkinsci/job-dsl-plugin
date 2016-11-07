package javaposse.jobdsl.plugin.fixtures

import hudson.Extension
import hudson.model.Item
import hudson.model.Job
import hudson.triggers.Trigger
import hudson.triggers.TriggerDescriptor
import org.jenkinsci.Symbol
import org.kohsuke.stapler.DataBoundConstructor

class IntegerTrigger extends Trigger<Job> {
    @DataBoundConstructor
    IntegerTrigger() {
    }

    @Extension
    @Symbol('int')
    static class DescriptorImpl extends TriggerDescriptor {
        final String displayName = null

        @Override
        boolean isApplicable(Item item) {
            false
        }
    }
}
