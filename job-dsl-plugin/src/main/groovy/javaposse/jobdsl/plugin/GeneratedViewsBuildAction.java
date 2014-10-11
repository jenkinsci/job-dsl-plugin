package javaposse.jobdsl.plugin;

import com.google.common.collect.Sets;
import hudson.model.AbstractBuild;
import hudson.model.ItemGroup;
import hudson.model.Run;
import hudson.model.View;
import hudson.model.ViewGroup;
import javaposse.jobdsl.dsl.GeneratedView;
import jenkins.model.RunAction2;

import java.util.Collection;
import java.util.Set;

import static javaposse.jobdsl.plugin.JenkinsJobManagement.getItemNameFromPath;

public class GeneratedViewsBuildAction implements RunAction2 {
    public final Set<GeneratedView> modifiedViews;

    private transient AbstractBuild owner;
    private LookupStrategy lookupStrategy = LookupStrategy.JENKINS_ROOT;

    public GeneratedViewsBuildAction(Collection<GeneratedView> modifiedViews, LookupStrategy lookupStrategy) {
        this.modifiedViews = Sets.newLinkedHashSet(modifiedViews);
        this.lookupStrategy = lookupStrategy;
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

    @Override
    public void onLoad(Run<?, ?> run) {
        onAttached(run);
    }

    @Override
    public void onAttached(Run run) {
        if (run instanceof AbstractBuild) {
            owner = (AbstractBuild) run;
        }
    }

    public LookupStrategy getLookupStrategy() {
        return lookupStrategy == null ? LookupStrategy.JENKINS_ROOT : lookupStrategy;
    }

    public Collection<GeneratedView> getModifiedViews() {
        return modifiedViews;
    }

    public Set<View> getViews() {
        Set<View> allGeneratedViews = Sets.newLinkedHashSet();
        if (modifiedViews != null) {
            for (GeneratedView generatedView : modifiedViews) {
                ItemGroup itemGroup = getLookupStrategy().getParent(owner.getProject(), generatedView.getName());
                if (itemGroup instanceof ViewGroup) {
                    View view = ((ViewGroup) itemGroup).getView(getItemNameFromPath(generatedView.getName()));
                    if (view != null) {
                        allGeneratedViews.add(view);
                    }
                }
            }
        }
        return allGeneratedViews;
    }
}
