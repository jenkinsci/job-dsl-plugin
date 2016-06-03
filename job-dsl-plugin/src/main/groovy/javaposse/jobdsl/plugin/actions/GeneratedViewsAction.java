package javaposse.jobdsl.plugin.actions;

import hudson.model.Job;
import hudson.model.Run;
import hudson.model.View;
import javaposse.jobdsl.dsl.GeneratedView;

import java.util.LinkedHashSet;
import java.util.Set;

public class GeneratedViewsAction extends GeneratedObjectsAction<GeneratedView, GeneratedViewsBuildAction> {
    public GeneratedViewsAction(Job<?, ?> job) {
        super(job, GeneratedViewsBuildAction.class);
    }

    public Set<View> getViews() {
        Set<View> result = new LinkedHashSet<View>();
        for (Run run : job.getBuilds()) {
            GeneratedViewsBuildAction action = run.getAction(GeneratedViewsBuildAction.class);
            if (action != null) {
                result.addAll(action.getViews());
            }
        }
        return result;
    }
}
