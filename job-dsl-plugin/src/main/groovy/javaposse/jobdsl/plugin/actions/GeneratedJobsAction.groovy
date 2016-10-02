package javaposse.jobdsl.plugin.actions

import hudson.model.Item
import hudson.model.Job
import hudson.model.Run
import javaposse.jobdsl.dsl.GeneratedJob

class GeneratedJobsAction extends GeneratedObjectsAction<GeneratedJob, GeneratedJobsBuildAction> {
    GeneratedJobsAction(Job job) {
        super(job, GeneratedJobsBuildAction)
    }

    List <Item> getItems() {
        List<Item> result = []
        for (Run run : job.builds) {
            GeneratedJobsBuildAction action = run.getAction(GeneratedJobsBuildAction)
            if (action != null) {
                result.addAll(action.items)
            }
        }
        result.sort { it.name }
    }

}
