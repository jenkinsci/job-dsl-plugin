package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.helpers.common.WorkspaceCleanupContext

class PreBuildCleanupContext extends WorkspaceCleanupContext {
    String cleanupParameter

    void cleanupParameter(String cleanupParameter) {
        this.cleanupParameter = cleanupParameter
    }
}
