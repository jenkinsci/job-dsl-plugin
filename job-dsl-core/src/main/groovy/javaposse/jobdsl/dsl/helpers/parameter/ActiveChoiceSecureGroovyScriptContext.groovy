package javaposse.jobdsl.dsl.helpers.parameter

import javaposse.jobdsl.dsl.Context

class ActiveChoiceSecureGroovyScriptContext implements Context {
    String script
    boolean sandbox

    /**
     * Sets the script to dynamically generate the value options.
     */
    void script(String script) {
        this.script = script
    }

    /**
     * Enables or disables the Groovy sandbox for the provided script. Will default to false when omitted.
     */
    void sandbox(boolean sandbox) {
        this.sandbox = sandbox
    }
}
