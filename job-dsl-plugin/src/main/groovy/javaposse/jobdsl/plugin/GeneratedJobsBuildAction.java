package javaposse.jobdsl.plugin;

import com.google.common.collect.Sets;
import hudson.model.Action;
import hudson.model.Item;
import javaposse.jobdsl.dsl.GeneratedJob;
import jenkins.model.Jenkins;

import java.util.Collection;
import java.util.Set;

public class GeneratedJobsBuildAction implements Action {
    public final Set<GeneratedJob> modifiedJobs;

    public GeneratedJobsBuildAction(Collection<GeneratedJob> modifiedJobs) {
        this.modifiedJobs = Sets.newLinkedHashSet(modifiedJobs);
    }

    /**
     * No task list item.
     */
    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return "Generated Items";
    }

    public String getUrlName() {
        return "generatedJobs";
    }

    public Collection<GeneratedJob> getModifiedJobs() {
        return modifiedJobs;
    }

    public Set<Item> getItems() {
        Set<Item> result = Sets.newLinkedHashSet();
        if (modifiedJobs != null) {
            for (GeneratedJob job : modifiedJobs) {
                Item item = Jenkins.getInstance().getItemByFullName(job.getJobName());
                if (item != null) {
                    result.add(item);
                }
            }
        }
        return result;
    }
}
