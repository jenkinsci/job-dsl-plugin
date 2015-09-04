package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context

class ConfigFileContext implements Context {
    final String configFileId

    String targetLocation
    String variable

    ConfigFileContext(String configFileId) {
        this.configFileId = configFileId
    }

    /**
     * Defines where the file should be created on the slave.  If it is blank, a temporary file will be created.
     */
    void targetLocation(String targetLocation) {
        this.targetLocation = targetLocation
    }

    /**
     * Allows to define an environment variable with which refers to the file location.
     */
    void variable(String variable) {
        this.variable = variable
    }
}
