package javaposse.jobdsl.plugin.actions

import hudson.model.Job
import javaposse.jobdsl.dsl.GeneratedUserContent

public class GeneratedUserContentsAction
        extends GeneratedObjectsAction<GeneratedUserContent, GeneratedUserContentsBuildAction> {
    GeneratedUserContentsAction(Job job) {
        super(job, GeneratedUserContentsBuildAction)
    }
}
