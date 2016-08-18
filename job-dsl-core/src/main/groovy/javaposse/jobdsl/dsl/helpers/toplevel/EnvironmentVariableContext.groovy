package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.wrapper.WrapperEnvironmentVariableContext

class EnvironmentVariableContext extends WrapperEnvironmentVariableContext {
    boolean keepSystemVariables = true
    boolean keepBuildVariables = true
    boolean overrideBuildParameters = false
    EnvironmentVariableContributorsContext contributorsContext

    EnvironmentVariableContext(JobManagement jobManagement, Item item) {
        super(jobManagement)
        this.contributorsContext = new EnvironmentVariableContributorsContext(jobManagement, item)
    }

    /**
     * Load files (properties or scripts) from the master node.
     *
     * @since 1.21
     */
    void loadFilesFromMaster(boolean loadFilesFromMaster) {
        this.loadFilesFromMaster = loadFilesFromMaster
    }

    /**
     * Injects Jenkins system variables and environment variables defined as global properties and as node properties.
     *
     * @since 1.21
     */
    void keepSystemVariables(boolean keepSystemVariables) {
        this.keepSystemVariables = keepSystemVariables
    }

    /**
     * Inject Jenkins build variables and also environment contributors and build variable contributors provided by
     * other plugins.
     *
     * @since 1.21
     */
    void keepBuildVariables(boolean keepBuildVariables) {
        this.keepBuildVariables = keepBuildVariables
    }

    /**
     * Allows environment variables to override build parameters.
     *
     * @since 1.30
     */
    void overrideBuildParameters(boolean overrideBuildParameters = true) {
        this.overrideBuildParameters = overrideBuildParameters
    }

    /**
     * Add environment and build variable contributors provided by other plugins.
     *
     * @since 1.30
     */
    void contributors(@DslContext(EnvironmentVariableContributorsContext) Closure contributorsClosure) {
        ContextHelper.executeInContext(contributorsClosure, contributorsContext)
    }
}
