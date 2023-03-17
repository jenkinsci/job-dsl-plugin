package javaposse.jobdsl.plugin.fixtures;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.Job;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

public class FooTrigger extends Trigger<Job> {
    @DataBoundConstructor
    public FooTrigger() {}

    @Extension
    @Symbol({"foo", "bar"})
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
