package javaposse.jobdsl.dsl.helpers.parameter

import javaposse.jobdsl.dsl.Context

class ActiveChoiceScriptlerScriptContext implements Context {
    Map<String, String> parameters = [:]

    /**
     * Adds parameter values for the Scriptler script.
     */
    void parameter(String name, String value) {
        parameters[name] = value
    }
}
