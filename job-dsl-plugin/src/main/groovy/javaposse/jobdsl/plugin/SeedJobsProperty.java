package javaposse.jobdsl.plugin;

import com.google.common.collect.Maps;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import jenkins.YesNoMaybe;

import java.util.Map;

/**
 * Seed Jobs which reference this template.
 */
class SeedJobsProperty extends JobProperty<AbstractProject<?,?>> {
    public final Map<String,String> seedJobs;

    public SeedJobsProperty(Map<String,String> seedJobs) {
        this.seedJobs = Maps.newHashMap(seedJobs);
    }

    public SeedJobsProperty() {
        this.seedJobs = Maps.newHashMap();
    }

@Extension(dynamicLoadable = YesNoMaybe.YES)
public static final class DescriptorImpl extends JobPropertyDescriptor {
        public String getDisplayName() {
            return "Seed Jobs Being Referenced";
        }
    }
}
