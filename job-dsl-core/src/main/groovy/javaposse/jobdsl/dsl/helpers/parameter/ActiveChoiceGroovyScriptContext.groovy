package javaposse.jobdsl.dsl.helpers.parameter

import javaposse.jobdsl.dsl.Context

class ActiveChoiceGroovyScriptContext implements Context {
    String script
    String fallbackScript

    /**
     * Sets the script that will dynamically generate the parameter value options.
     */
    void script(String script) {
        this.script = script
    }

    /**
     * Provides alternate parameter value options in case the main script fails.
     */
    void fallbackScript(String fallbackScript) {
        this.fallbackScript = fallbackScript
    }
}
