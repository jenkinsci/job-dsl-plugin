package javaposse.jobdsl.plugin.actions;

import hudson.model.AbstractProject;
import javaposse.jobdsl.dsl.GeneratedConfigFile;

public class GeneratedConfigFilesAction
        extends GeneratedObjectsAction<GeneratedConfigFile, GeneratedConfigFilesBuildAction> {
    public GeneratedConfigFilesAction(AbstractProject<?, ?> project) {
        super(project, GeneratedConfigFilesBuildAction.class);
    }
}
