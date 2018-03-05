package javaposse.jobdsl.dsl.helpers.parameter

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class ActiveChoiceGroovyScriptContext extends AbstractContext {
    ActiveChoiceScriptContext script
    ActiveChoiceScriptContext fallbackScript

    ActiveChoiceGroovyScriptContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Sets the script that will dynamically generate the parameter value options.
     */
    ActiveChoiceScriptContext script(String script, @DslContext(ActiveChoiceScriptContext) Closure closure = null) {
        this.script = new ActiveChoiceScriptContext(jobManagement, script)
        executeInContext(closure, this.script)
        this.script
    }

    /**
     * Provides alternate parameter value options in case the main script fails.
     */
    ActiveChoiceScriptContext fallbackScript(String fallbackScript,
                                             @DslContext(ActiveChoiceScriptContext) Closure closure = null) {
        this.fallbackScript = new ActiveChoiceScriptContext(jobManagement, fallbackScript)
        executeInContext(closure, this.fallbackScript)
        this.fallbackScript
    }
}
