package javaposse.jobdsl.plugin.actions;

import hudson.model.Item;
import hudson.model.Job;
import hudson.model.Run;
import javaposse.jobdsl.dsl.GeneratedJob;

import java.util.LinkedHashSet;
import java.util.Set;

public class GeneratedJobsAction extends GeneratedObjectsAction<GeneratedJob, GeneratedJobsBuildAction> {
    public GeneratedJobsAction(Job<?, ?> job) {
        super(job, GeneratedJobsBuildAction.class);
    }

    public Set<Item> getItems() {
        Set<Item> result = new LinkedHashSet<Item>();
        for (Run run : job.getBuilds()) {
            GeneratedJobsBuildAction action = run.getAction(GeneratedJobsBuildAction.class);
            if (action != null) {
                result.addAll(action.getItems());
            }
        }
        return result;
    }
}
