package javaposse.jobdsl.plugin.fixtures

import hudson.Extension
import hudson.model.Item
import hudson.model.Job
import hudson.triggers.Trigger
import hudson.triggers.TriggerDescriptor
import org.jenkinsci.Symbol
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

@Deprecated
class DeprecatedTrigger extends Trigger<Job> {
    @DataBoundConstructor
    DeprecatedTrigger() {
    }

    @DataBoundSetter
    @Deprecated
    @SuppressWarnings(['EmptyMethod', 'UnusedMethodParameter'])
    void setDeprecatedOption(String value) {
    }

    @Extension
    @Symbol('old')
    static class DescriptorImpl extends TriggerDescriptor {
        final String displayName = null

        @Override
        boolean isApplicable(Item item) {
            false
        }
    }
}
