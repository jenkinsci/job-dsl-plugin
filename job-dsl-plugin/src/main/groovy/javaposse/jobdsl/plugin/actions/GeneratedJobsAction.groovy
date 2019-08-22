package javaposse.jobdsl.plugin.actions

import hudson.model.Item
import hudson.model.Job
import hudson.model.Run
import javaposse.jobdsl.dsl.GeneratedJob

class GeneratedJobsAction extends GeneratedObjectsAction<GeneratedJob, GeneratedJobsBuildAction> {
    GeneratedJobsAction(Job job) {
        super(job, GeneratedJobsBuildAction)
    }

    Iterable<Item> getItems() {
        Set<Item> result = []
        for (Run run : job.builds) {
            GeneratedJobsBuildAction action = run.getAction(GeneratedJobsBuildAction)
            if (action != null) {
                result.addAll(action.items)
            }
        }
        result.toSorted(Comparators.ITEM_COMPARATOR)
    }
}
