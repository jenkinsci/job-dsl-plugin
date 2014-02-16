package javaposse.jobdsl.plugin;

import com.google.common.collect.Sets;
import hudson.model.Action;
import javaposse.jobdsl.dsl.GeneratedJob;
import javaposse.jobdsl.dsl.GeneratedView;

import java.util.Collection;
import java.util.Set;

public class GeneratedViewsBuildAction implements Action {
    public final Set<GeneratedView> modifiedViews;

    public GeneratedViewsBuildAction(Collection<GeneratedView> modifiedJobs) {
        this.modifiedViews = Sets.newLinkedHashSet(modifiedJobs);
    }

    /**
     * No task list item.
     */
    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return "Generated Views";
    }

    public String getUrlName() {
        return "generatedViews";
    }

    public Collection<GeneratedView> getModifiedViews() {
        return modifiedViews;
    }
}
