package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.helpers.Context

class ConfigFileContext implements Context {
    final String configFileId

    String targetLocation
    String variable

    ConfigFileContext(String configFileId) {
        this.configFileId = configFileId
    }

    def targetLocation(String targetLocation) {
        this.targetLocation = targetLocation
    }

    def variable(String variable) {
        this.variable = variable
    }
}
