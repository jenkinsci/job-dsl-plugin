package javaposse.jobdsl.plugin.actions

import hudson.model.Job
import hudson.model.Run
import hudson.model.View
import javaposse.jobdsl.dsl.GeneratedView

class GeneratedViewsAction extends GeneratedObjectsAction<GeneratedView, GeneratedViewsBuildAction> {
    GeneratedViewsAction(Job job) {
        super(job, GeneratedViewsBuildAction)
    }

    Set<View> getViews() {
        Set<View> result = new TreeSet<>(Comparators.VIEW_COMPARATOR)
        for (Run run : job.builds) {
            GeneratedViewsBuildAction action = run.getAction(GeneratedViewsBuildAction)
            if (action != null) {
                result.addAll(action.views)
            }
        }
        result
    }
}
