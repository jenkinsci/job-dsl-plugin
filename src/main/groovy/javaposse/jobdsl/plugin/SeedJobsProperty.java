package javaposse.jobdsl.plugin;

import java.util.Collection;
import java.util.Set;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.JobProperty;
import hudson.model.AbstractProject;
import hudson.tasks.Builder;

import com.google.common.collect.Sets;

/**
 * Seed Jobs which reference this template.
 * @author jryan
 *
 */
class SeedJobsProperty extends JobProperty<AbstractProject<?,?>> {
    public final Set<String> seedJobs;

    public SeedJobsProperty(Collection<String> seedJobs) {
        this.seedJobs = Sets.newHashSet(seedJobs);
    }

    public SeedJobsProperty() {
        this.seedJobs = Sets.newHashSet();
    }

    public Collection<String> getSeedJobs() {
        return seedJobs;
    }


    @Extension
    public static final class DescriptorImpl extends Descriptor<Builder> {
        public String getDisplayName() {
            return "Seed Jobs Being Referenced";
        }
    }
}
