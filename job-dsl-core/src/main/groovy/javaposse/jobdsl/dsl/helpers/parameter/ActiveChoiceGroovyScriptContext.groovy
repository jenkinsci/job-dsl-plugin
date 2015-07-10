package javaposse.jobdsl.dsl.helpers.parameter

import javaposse.jobdsl.dsl.Context

class ActiveChoiceGroovyScriptContext implements Context {
    String script
    String fallbackScript

    void script(String script) {
        this.script = script
    }

    void fallbackScript(String fallbackScript) {
        this.fallbackScript = fallbackScript
    }
}
