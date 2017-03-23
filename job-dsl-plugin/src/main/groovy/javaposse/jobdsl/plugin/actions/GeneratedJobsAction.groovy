package javaposse.jobdsl.plugin.actions

import hudson.model.Item
import hudson.model.Job
import hudson.model.Run
import javaposse.jobdsl.dsl.GeneratedJob

class GeneratedJobsAction extends GeneratedObjectsAction<GeneratedJob, GeneratedJobsBuildAction> {
    GeneratedJobsAction(Job job) {
        super(job, GeneratedJobsBuildAction)
    }

    Set<Item> getItems() {
        Set<Item> result = new TreeSet<>(Comparators.ITEM_COMPARATOR)
        for (Run run : job.builds) {
            GeneratedJobsBuildAction action = run.getAction(GeneratedJobsBuildAction)
            if (action != null) {
                result.addAll(action.items)
            }
        }
        result
    }
}
