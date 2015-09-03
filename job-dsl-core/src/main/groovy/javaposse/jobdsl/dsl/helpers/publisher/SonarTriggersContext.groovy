package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class SonarTriggersContext implements Context {
    String skipIfEnvironmentVariable

    /**
     * Skip SonarQube analysis when the value of the given variable is set to {@code true}.
     */
    void skipIfEnvironmentVariable(String environmentVariable) {
        this.skipIfEnvironmentVariable = environmentVariable
    }
}
