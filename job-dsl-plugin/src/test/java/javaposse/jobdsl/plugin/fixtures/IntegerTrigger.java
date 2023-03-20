package javaposse.jobdsl.plugin.fixtures;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.Job;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

public class IntegerTrigger extends Trigger<Job> {
    @DataBoundConstructor
    public IntegerTrigger() {}

    @Extension
    @Symbol("int")
    public static class DescriptorImpl extends TriggerDescriptor {
        @Override
        public boolean isApplicable(Item item) {
            return false;
        }

        @Override
        public final String getDisplayName() {
            return "";
        }
    }
}
