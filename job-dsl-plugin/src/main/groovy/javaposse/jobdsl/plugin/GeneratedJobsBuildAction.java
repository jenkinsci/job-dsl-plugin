package javaposse.jobdsl.plugin;

import com.google.common.collect.Sets;
import hudson.model.Action;
import javaposse.jobdsl.dsl.GeneratedJob;

import java.util.Collection;
import java.util.Set;

public class GeneratedJobsBuildAction implements Action {
    public final Set<GeneratedJob> modifiedJobs;

    public GeneratedJobsBuildAction(Collection<GeneratedJob> modifiedJobs) {
        this.modifiedJobs = Sets.newHashSet(modifiedJobs);
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
