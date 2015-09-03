package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context

class CustomToolsContext implements Context {
    boolean skipMasterInstallation = false
    boolean convertHomesToUppercase = false

    /**
     * Skips installation of tools at the matrix master job. Defaults to {@code false}.
     */
    void skipMasterInstallation(boolean skipMasterInstallation = true) {
        this.skipMasterInstallation = skipMasterInstallation
    }

    /**
     * Converts {@code #ToolName_HOME} variables to the upper-case. Defaults to {@code false}.
     */
    void convertHomesToUppercase(boolean convertHomesToUppercase = true) {
        this.convertHomesToUppercase = convertHomesToUppercase
    }
}
