package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class SonarTriggersContext implements Context {
    String skipIfEnvironmentVariable

    void skipIfEnvironmentVariable(String environmentVariable) {
        this.skipIfEnvironmentVariable = environmentVariable
    }
}
