package javaposse.jobdsl.plugin.actions;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.View;
import javaposse.jobdsl.dsl.GeneratedView;

import java.util.Set;

import static com.google.common.collect.Sets.newLinkedHashSet;

public class GeneratedViewsAction extends GeneratedObjectsAction<GeneratedView, GeneratedViewsBuildAction> {
    public GeneratedViewsAction(AbstractProject<?, ?> project) {
        super(project, GeneratedViewsBuildAction.class);
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
