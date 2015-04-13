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

    EnvironmentVariableContext(JobManagement jobManagement) {
        super(jobManagement)
        this.contributorsContext = new EnvironmentVariableContributorsContext(jobManagement)
    }

    /**
     * @since 1.21
     */
    void loadFilesFromMaster(boolean loadFilesFromMaster) {
        this.loadFilesFromMaster = loadFilesFromMaster
    }

    /**
     * @since 1.21
     */
    void keepSystemVariables(boolean keepSystemVariables) {
        this.keepSystemVariables = keepSystemVariables
    }

    /**
     * @since 1.21
     */
    void keepBuildVariables(boolean keepBuildVariables) {
        this.keepBuildVariables = keepBuildVariables
    }

    /**
     * @since 1.30
     */
    void overrideBuildParameters(boolean overrideBuildParameters = true) {
        this.overrideBuildParameters = overrideBuildParameters
    }

    /**
     * @since 1.30
     */
    void contributors(@DslContext(EnvironmentVariableContributorsContext) Closure contributorsClosure) {
        ContextHelper.executeInContext(contributorsClosure, contributorsContext)
    }
}
