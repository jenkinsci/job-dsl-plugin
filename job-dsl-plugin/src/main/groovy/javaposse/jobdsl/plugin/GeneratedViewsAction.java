package javaposse.jobdsl.plugin;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.View;
import javaposse.jobdsl.dsl.GeneratedView;

import java.util.Set;

import static com.google.common.collect.Sets.newLinkedHashSet;


public class GeneratedViewsAction implements Action {
    AbstractProject<?, ?> project;

    public GeneratedViewsAction(AbstractProject<?, ?> project) {
        this.project = project;
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public String getUrlName() {
        return "generatedViews";
    }

    /**
     * Search for all views which were created by the child builds
     */
    public Set<GeneratedView> findLastGeneratedViews() {
        for (AbstractBuild<?, ?> b = project.getLastBuild(); b != null; b = b.getPreviousBuild()) {
            GeneratedViewsBuildAction action = b.getAction(GeneratedViewsBuildAction.class);
            if (action != null) {
                return newLinkedHashSet(action.getModifiedViews());
            }
        }
        return null;
    }

    /**
     * Search for all jobs which were created by the child builds
     */
    @Deprecated
    public Set<GeneratedView> findAllGeneratedViews() {
        Set<GeneratedView> allGeneratedViews = newLinkedHashSet();
        for (AbstractBuild build : project.getBuilds()) {
            GeneratedViewsBuildAction action = build.getAction(GeneratedViewsBuildAction.class);
            if (action != null) {
                allGeneratedViews.addAll(action.getModifiedViews());
            }
        }
        return allGeneratedViews;
    }

    public Set<View> getViews() {
        Set<View> result = newLinkedHashSet();
        for (AbstractBuild build : project.getBuilds()) {
            GeneratedViewsBuildAction action = build.getAction(GeneratedViewsBuildAction.class);
            if (action != null) {
                result.addAll(action.getViews());
            }
        }
        return result;
    }
}
