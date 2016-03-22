package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class EnvInjectPasswordsContext extends AbstractContext {
    boolean injectGlobalPasswords
    boolean maskPasswordParameters = true

    EnvInjectPasswordsContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Injects global passwords provided by Jenkins configuration. Defaults to {@code false}.
     */
    void injectGlobalPasswords(boolean injectGlobalPasswords = true) {
        this.injectGlobalPasswords = injectGlobalPasswords
    }

    /**
     * Masks passwords provided by build parameters. Defaults to {@code true}.
     */
    void maskPasswordParameters(boolean maskPasswordParameters = true) {
        this.maskPasswordParameters = maskPasswordParameters
    }
}
