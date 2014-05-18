package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.helpers.common.WorkspaceCleanupContext

class PreBuildCleanupContext extends WorkspaceCleanupContext implements Context {
    String cleanupParameter

    void cleanupParameter(String cleanupParameter) {
        this.cleanupParameter = cleanupParameter
    }
}
