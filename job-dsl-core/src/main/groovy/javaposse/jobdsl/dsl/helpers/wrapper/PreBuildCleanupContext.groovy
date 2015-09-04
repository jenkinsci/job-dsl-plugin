package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.helpers.common.WorkspaceCleanupContext

class PreBuildCleanupContext extends WorkspaceCleanupContext {
    String cleanupParameter

    /**
     * Set this field to a boolean environment variable and if the variables value is set to true than the workspace
     * will be cleaned up.
     */
    void cleanupParameter(String cleanupParameter) {
        this.cleanupParameter = cleanupParameter
    }
}
