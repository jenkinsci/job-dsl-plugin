package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.helpers.Context

class ParameterizedRemoteTriggerContext implements Context {
    Map<String, String> parameters = [:]

    void parameter(String name, String value) {
        this.parameters[name] = value
    }

    void parameters(Map<String, String> parameters) {
        this.parameters.putAll(parameters)
    }
}
