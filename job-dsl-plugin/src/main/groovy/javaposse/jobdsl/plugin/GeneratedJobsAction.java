package javaposse.jobdsl.plugin;

import com.google.common.collect.Sets;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import javaposse.jobdsl.dsl.GeneratedJob;

import java.util.Set;


public class GeneratedJobsAction implements Action {

    AbstractProject<?,?> project;
    public GeneratedJobsAction(AbstractProject<?,?> project) {
        this.project = project;
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public String getUrlName() {
        return "generatedJobs";
    }

    /**
     * Search for all jobs which were created by the child builds
     */
    public Set<GeneratedJob> findLastGeneratedJobs() {

        AbstractBuild<?, ?> b;
        for (b = project.getLastBuild(); b != null; b = b.getPreviousBuild()) {
            GeneratedJobsBuildAction ret = b.getAction(GeneratedJobsBuildAction.class);
            if (ret != null && ret.getModifiedJobs() != null) {
                return Sets.newLinkedHashSet(ret.getModifiedJobs());
            }
        }
        return null;
    }

    /**
     * Search for all jobs which were created by the child builds
     */
    public Set<GeneratedJob> findAllGeneratedJobs() {

        AbstractBuild<?, ?> b;
        Set<GeneratedJob> allGeneratedJobs = Sets.newLinkedHashSet();
        for(AbstractBuild build: project.getBuilds()) {
            GeneratedJobsBuildAction ret = build.getAction(GeneratedJobsBuildAction.class);
            if (ret != null && ret.getModifiedJobs() != null) {
                allGeneratedJobs.addAll(ret.getModifiedJobs());
            }
        }
        return allGeneratedJobs;
    }
}
