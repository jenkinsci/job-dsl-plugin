package javaposse.jobdsl.plugin;

import com.google.common.collect.Sets;
import hudson.model.Action;
import hudson.model.View;
import hudson.model.ViewGroup;
import javaposse.jobdsl.dsl.GeneratedView;

import java.util.Collection;
import java.util.Set;

import static javaposse.jobdsl.plugin.JenkinsJobManagement.getItemNameFromFullName;
import static javaposse.jobdsl.plugin.JenkinsJobManagement.getViewGroup;

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

    public Set<View> getViews() {
        Set<View> allGeneratedViews = Sets.newLinkedHashSet();
        if (modifiedViews != null) {
            for (GeneratedView generatedView : modifiedViews) {
                ViewGroup viewGroup = getViewGroup(generatedView.getName());
                if (viewGroup != null) {
                    View view = viewGroup.getView(getItemNameFromFullName(generatedView.getName()));
                    if (view != null) {
                        allGeneratedViews.add(view);
                    }
                }
            }
        }
        return allGeneratedViews;
    }
}
