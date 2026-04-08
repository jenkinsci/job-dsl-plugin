package javaposse.jobdsl.dsl.helpers.parameter

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.DslContext

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class ActiveChoiceGroovyScriptContext implements Context {
    String script
    String fallbackScript

    Node secureScriptNode
    Node secureFallbackScriptNode

    /**
     * Sets the script that will dynamically generate the parameter value options, sandbox is turned off,
     * won't be used if secureScript or secureFallbackScript is specified.
     */
    void script(String script) {
        this.script = script
    }

    /**
     * Provides alternate parameter value options in case the main script fails, sandbox is turned off,
     * won't be used if secureScript or secureFallbackScript is specified.
     */
    void fallbackScript(String fallbackScript) {
        this.fallbackScript = fallbackScript
    }

    /**
     * Configure the script that will dynamically generate the parameter value options.
     */
    void secureScript(@DslContext(ActiveChoiceSecureGroovyScriptContext) Closure closure) {
        ActiveChoiceSecureGroovyScriptContext context = new ActiveChoiceSecureGroovyScriptContext()
        executeInContext(closure, context)

        secureScriptNode = new NodeBuilder().secureScript {
            delegate.script(context.script ?: '')
            delegate.sandbox(context.sandbox ?: false)
        }
    }

    /**
     * Configure the fallback script in case the main script fails.
     */
    void secureFallbackScript(@DslContext(ActiveChoiceSecureGroovyScriptContext) Closure closure) {
        ActiveChoiceSecureGroovyScriptContext context = new ActiveChoiceSecureGroovyScriptContext()
        executeInContext(closure, context)

        secureFallbackScriptNode = new NodeBuilder().secureFallbackScript {
            delegate.script(context.script ?: '')
            delegate.sandbox(context.sandbox ?: false)
        }
    }
}
