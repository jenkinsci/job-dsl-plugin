package javaposse.jobdsl.plugin.actions

import hudson.model.Job
import javaposse.jobdsl.dsl.GeneratedConfigFile

class GeneratedConfigFilesAction extends GeneratedObjectsAction<GeneratedConfigFile, GeneratedConfigFilesBuildAction> {
    GeneratedConfigFilesAction(Job job) {
        super(job, GeneratedConfigFilesBuildAction)
    }
}
