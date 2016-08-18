package javaposse.jobdsl.plugin.fixtures

import hudson.Extension
import hudson.model.Item
import hudson.model.Job
import hudson.triggers.Trigger
import hudson.triggers.TriggerDescriptor
import org.kohsuke.stapler.DataBoundConstructor

class Boolean extends Trigger<Job> {
    @SuppressWarnings('UnnecessaryConstructor')
    @DataBoundConstructor
    Boolean() {
    }

    @Extension
    static class DescriptorImpl extends TriggerDescriptor {
        final String displayName = null

        @Override
        boolean isApplicable(Item item) {
            false
        }
    }
}
