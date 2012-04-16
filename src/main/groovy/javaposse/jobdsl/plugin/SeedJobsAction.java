package javaposse.jobdsl.plugin;

import java.util.Collection;
import java.util.Set;

import hudson.model.Action;
import com.google.common.collect.Sets;

/**
 * Seed Jobs which reference this template.
 * @author jryan
 *
 */
class SeedJobsAction implements Action {
    public final Set<String> seedJobs;

    public SeedJobsAction(Collection<String> seedJobs) {
        this.seedJobs = Sets.newHashSet(seedJobs);
    }

    public SeedJobsAction() {
        this.seedJobs = Sets.newHashSet();
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return "Seed Jobs";
    }

    public String getUrlName() {
        return "seedJobs";
    }

    public Collection<String> getSeedJobs() {
        return seedJobs;
    }

}
