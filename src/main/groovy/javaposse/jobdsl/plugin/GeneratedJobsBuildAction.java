package javaposse.jobdsl.plugin;

import java.util.Collection;

import hudson.model.Action;
import com.google.common.collect.ImmutableList;

class GeneratedJobsBuildAction implements Action {
    public final Collection<String> modifiedJobs;

    public GeneratedJobsBuildAction(Collection<String> modifiedJobs) {
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

    public Collection<String> getModifiedJobs() {
        return modifiedJobs;
    }

}
