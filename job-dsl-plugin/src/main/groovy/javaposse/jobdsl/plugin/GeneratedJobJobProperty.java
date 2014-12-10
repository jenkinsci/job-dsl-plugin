package javaposse.jobdsl.plugin;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Collection;
import java.util.Collections;

/**
 * @author ceilfors
 */
public class GeneratedJobJobProperty extends JobProperty<Job<?, ?>> {

    Job<?, ?> templateJob;
    Job<?, ?> seedJob;

    @DataBoundConstructor
    public GeneratedJobJobProperty(Job<?, ?> templateJob, Job<?, ?> seedJob) {
        this.templateJob = templateJob;
        this.seedJob = seedJob;
    }

    @Override
    public Collection<? extends Action> getJobActions(Job<?, ?> job) {
        return Collections.singletonList(new GeneratedJobAction(templateJob, seedJob));
    }

    @Extension
    public static class DescriptorImpl extends JobPropertyDescriptor {
        public String getDisplayName() {
            return "";
        }
    }
}
