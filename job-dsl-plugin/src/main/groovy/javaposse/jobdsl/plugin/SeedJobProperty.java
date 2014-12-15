package javaposse.jobdsl.plugin;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Collection;
import java.util.Collections;

public class SeedJobProperty extends JobProperty<AbstractProject<?, ?>> {

    String templateJobName;
    String seedJobName;

    @DataBoundConstructor
    public SeedJobProperty(String seedJobName, String templateJobName) {
        this.seedJobName = seedJobName;
        this.templateJobName = templateJobName;
    }

    @Override
    public Collection<? extends Action> getJobActions(AbstractProject<?, ?> project) {
        return Collections.singletonList(new SeedJobAction(seedJobName, templateJobName));
    }

    @Extension
    public static class DescriptorImpl extends JobPropertyDescriptor {
        public String getDisplayName() {
            return "";
        }
    }
}
