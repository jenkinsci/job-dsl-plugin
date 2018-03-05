package javaposse.jobdsl.dsl.helpers.parameter

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class ActiveChoiceScriptContext extends AbstractContext {
    String script
    Boolean useSandbox

    ActiveChoiceScriptContext(JobManagement jobManagement, String script) {
        super(jobManagement)
        this.script = script
    }

    /**
     * True if this Groovy script is to be run in a sandbox with limited abilities.
     *
     * @since 1.69
     */
    @RequiresPlugin(id = 'uno-choice', minimumVersion = '2.0')
    void useSandbox(boolean useSandbox = true) {
        this.useSandbox = useSandbox
    }
}
