package javaposse.jobdsl.plugin.actions;

import hudson.model.Job;
import javaposse.jobdsl.dsl.GeneratedConfigFile;

public class GeneratedConfigFilesAction
        extends GeneratedObjectsAction<GeneratedConfigFile, GeneratedConfigFilesBuildAction> {
    public GeneratedConfigFilesAction(Job<?, ?> job) {
        super(job, GeneratedConfigFilesBuildAction.class);
    }
}
