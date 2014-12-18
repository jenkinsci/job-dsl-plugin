package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context

class ConfigFileContext implements Context {
    final String configFileId

    String targetLocation
    String variable

    ConfigFileContext(String configFileId) {
        this.configFileId = configFileId
    }

    void targetLocation(String targetLocation) {
        this.targetLocation = targetLocation
    }

    void variable(String variable) {
        this.variable = variable
    }
}
