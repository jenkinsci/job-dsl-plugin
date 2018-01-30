package javaposse.jobdsl.dsl.helpers.parameter

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.DslContext

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

abstract class AbstractActiveChoiceContext implements Context {
    String description
    Node script

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
        ActiveChoiceGroovyScriptContext context = new ActiveChoiceGroovyScriptContext()
        executeInContext(closure, context)

        script = new NodeBuilder().script(class: 'org.biouno.unochoice.model.GroovyScript') {
            delegate.script(context.script ?: '')
            delegate.fallbackScript(context.fallbackScript ?: '')
        }
    }
}
