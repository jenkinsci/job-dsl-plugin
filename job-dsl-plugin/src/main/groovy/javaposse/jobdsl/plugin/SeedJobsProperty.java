package javaposse.jobdsl.plugin;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import hudson.Extension;
import hudson.model.JobProperty;
import hudson.model.AbstractProject;
import hudson.model.JobPropertyDescriptor;

import com.google.common.collect.Sets;
import jenkins.YesNoMaybe;

/**
 * Seed Jobs which reference this template.
 * @author jryan
 *
 */
class SeedJobsProperty extends JobProperty<AbstractProject<?,?>> {
    public final Map<String,String> seedJobs;

    public SeedJobsProperty(Map<String,String> seedJobs) {
        this.seedJobs = Maps.newHashMap(seedJobs);
    }

    public SeedJobsProperty() {
        this.seedJobs = Maps.newHashMap();
    }

//    public Collection<String> getSeedJobs() {
//        return seedJobs.keySet();
//    }
//
@Extension(dynamicLoadable = YesNoMaybe.YES)
public static final class DescriptorImpl extends JobPropertyDescriptor {
        public String getDisplayName() {
            return "Seed Jobs Being Referenced";
        }
    }
}
