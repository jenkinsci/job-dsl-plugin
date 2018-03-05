package javaposse.jobdsl.dsl.helpers.parameter

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

abstract class AbstractActiveChoiceContext extends AbstractContext {
    String description
    Node script

    AbstractActiveChoiceContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Sets a description for the parameter.
     */
    void description(String description) {
        this.description = description
    }

    /**
     * Use a Groovy script to generate value options.
     */
    void groovyScript(@DslContext(ActiveChoiceGroovyScriptContext) Closure closure) {
        ActiveChoiceGroovyScriptContext context = new ActiveChoiceGroovyScriptContext(jobManagement)
        executeInContext(closure, context)

        script = new NodeBuilder().script(class: 'org.biouno.unochoice.model.GroovyScript') {
            if (context.script.useSandbox == null) {
                delegate.script(context.script.script ?: '')
            } else {
                delegate.secureScript(plugin: 'script-security@1.24') {
                    delegate.script(context.script.script ?: '')
                    delegate.sandbox(context.script.useSandbox == true ? 'true' : 'false')
                }
            }

            if (context.fallbackScript.useSandbox == null) {
                delegate.fallbackScript(context.fallbackScript.script ?: '')
            } else {
                delegate.secureFallbackScript(plugin: 'script-security@1.24') {
                    delegate.script(context.fallbackScript.script ?: '')
                    delegate.sandbox(context.fallbackScript.useSandbox == true ? 'true' : 'false')
                }
            }
        }
    }
}
