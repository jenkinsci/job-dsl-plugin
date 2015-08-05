package javaposse.jobdsl.dsl.helpers.parameter

import javaposse.jobdsl.dsl.Context

class ActiveChoiceScriptlerScriptContext implements Context {
    List<Node> parameters = []

    void parameter(String name, String value) {
        parameters << NodeBuilder.newInstance().'entry' {
            string(name)
            string(value)
        }
    }
}
