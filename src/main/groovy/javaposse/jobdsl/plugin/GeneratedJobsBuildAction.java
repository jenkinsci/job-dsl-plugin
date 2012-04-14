package javaposse.jobdsl.plugin;

import java.util.Collection;

import javaposse.jobdsl.dsl.GeneratedJob;

import hudson.model.Action;
import com.google.common.collect.ImmutableList;

class GeneratedJobsBuildAction implements Action {
    public final Collection<GeneratedJob> modifiedJobs;

    public GeneratedJobsBuildAction(Collection<GeneratedJob> modifiedJobs) {
        this.modifiedJobs = ImmutableList.copyOf(modifiedJobs); // TODO Make this a tuple with job name and if it was created or updated
    }

    /**
     * No task list item.
     */
    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return "Generated Jobs";
    }

    public String getUrlName() {
        return "generatedJobs";
    }

    public Collection<GeneratedJob> getModifiedJobs() {
        return modifiedJobs;
    }

}
