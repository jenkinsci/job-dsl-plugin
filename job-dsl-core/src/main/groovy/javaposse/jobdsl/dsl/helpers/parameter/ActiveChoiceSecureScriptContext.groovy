package javaposse.jobdsl.dsl.helpers.parameter

import javaposse.jobdsl.dsl.Context

class ActiveChoiceSecureScriptContext implements Context {
    boolean sandbox

    /**
     * Sets the sandbox field for secure scripts.
     * Defaults to {@code false}.
     *
     * @since 1.77
     */
    void sandbox(boolean sandbox = true) {
        this.sandbox = sandbox
    }
}
