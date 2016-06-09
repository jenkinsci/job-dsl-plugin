package javaposse.jobdsl.plugin.actions;

import hudson.model.Job;
import javaposse.jobdsl.dsl.GeneratedUserContent;

public class GeneratedUserContentsAction
        extends GeneratedObjectsAction<GeneratedUserContent, GeneratedUserContentsBuildAction> {
    public GeneratedUserContentsAction(Job<?, ?> job) {
        super(job, GeneratedUserContentsBuildAction.class);
    }
}
