package javaposse.jobdsl.plugin;

import hudson.model.AbstractProject;
import javaposse.jobdsl.dsl.GeneratedConfigFile;

import java.util.Set;

public class GeneratedConfigFilesAction
        extends GeneratedObjectsAction<GeneratedConfigFile, GeneratedConfigFilesBuildAction> {
    public GeneratedConfigFilesAction(AbstractProject<?, ?> project) {
        super(project, GeneratedConfigFilesBuildAction.class);
    }

    public String getUrlName() {
        return "generatedConfigFiles";
    }
}
