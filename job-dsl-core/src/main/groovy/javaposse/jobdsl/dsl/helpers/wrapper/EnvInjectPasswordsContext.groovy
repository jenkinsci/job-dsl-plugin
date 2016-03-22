package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class EnvInjectPasswordsContext extends AbstractContext {
    boolean injectGlobalPasswords = false
    boolean maskPasswordParameters = true

    EnvInjectPasswordsContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Injects global passwords provided by Jenkins configuration. Defaults to {@code false}.
     *
     * @since 1.45
     */
    void injectGlobalPasswords(boolean injectGlobalPasswords = true) {
        this.injectGlobalPasswords = injectGlobalPasswords
    }

    /**
     * Masks passwords provided by build parameters. Defaults to {@code true}.
     *
     * @since 1.45
     */
    @RequiresPlugin(id = 'envinject', minimumVersion = '1.90')
    void maskPasswordParameters(boolean maskPasswordParameters = true) {
        this.maskPasswordParameters = maskPasswordParameters
    }
}
