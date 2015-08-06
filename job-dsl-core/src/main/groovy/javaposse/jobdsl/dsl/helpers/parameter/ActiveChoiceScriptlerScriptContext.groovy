package javaposse.jobdsl.dsl.helpers.parameter

import javaposse.jobdsl.dsl.Context

class ActiveChoiceScriptlerScriptContext implements Context {
    Map<String, String> parameters = [:]

    void parameter(String name, String value) {
        parameters[name] = value
    }
}
