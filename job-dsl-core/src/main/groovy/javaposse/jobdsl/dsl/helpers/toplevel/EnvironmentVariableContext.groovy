package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.wrapper.WrapperEnvironmentVariableContext

class EnvironmentVariableContext extends WrapperEnvironmentVariableContext {
    boolean keepSystemVariables = true
    boolean keepBuildVariables = true
    boolean overrideBuildParameters = false
    EnvironmentVariableContributorsContext contributorsContext
    private final JobManagement jobManagement

    EnvironmentVariableContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
        this.contributorsContext = new EnvironmentVariableContributorsContext(jobManagement)
    }

    void loadFilesFromMaster(boolean loadFilesFromMaster) {
        this.loadFilesFromMaster = loadFilesFromMaster
    }

    void keepSystemVariables(boolean keepSystemVariables) {
        this.keepSystemVariables = keepSystemVariables
    }

    void keepBuildVariables(boolean keepBuildVariables) {
        this.keepBuildVariables = keepBuildVariables
    }

    void overrideBuildParameters(boolean overrideBuildParameters = true) {
        this.overrideBuildParameters = overrideBuildParameters
    }

    void contributors(@DslContext(EnvironmentVariableContributorsContext) Closure contributorsClosure) {
        ContextHelper.executeInContext(contributorsClosure, contributorsContext)
    }
}
