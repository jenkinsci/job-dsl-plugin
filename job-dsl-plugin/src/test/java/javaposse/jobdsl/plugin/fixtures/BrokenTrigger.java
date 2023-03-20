package javaposse.jobdsl.plugin.fixtures;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.Job;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;

public class BrokenTrigger extends Trigger<Job> {
    @Extension
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
