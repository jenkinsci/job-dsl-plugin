package javaposse.jobdsl.dsl.helpers.parameter

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.DslContext

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class ActiveChoiceGroovyScriptContext implements Context {
    Node script = new NodeBuilder().createNode('script', '')
    Node fallbackScript = new NodeBuilder().createNode('fallbackScript', '')

    /**
     * Sets the script that will dynamically generate the parameter value options.
     */
    void script(String script, @DslContext(ActiveChoiceSecureScriptContext) Closure closure = null) {
        this.script = createNode('script', script, closure)
    }

    /**
     * Provides alternate parameter value options in case the main script fails.
     */
    void fallbackScript(String fallbackScript, @DslContext(ActiveChoiceSecureScriptContext) Closure closure = null) {
        this.fallbackScript = createNode('fallbackScript', fallbackScript, closure)
    }

    /**
     * Creates a node to be attached to a Groovy script.
     */
    private Node createNode(String name, String script, Closure closure) {
        if (!closure) {
            return new NodeBuilder().createNode(name, script)
        }

        ActiveChoiceSecureScriptContext context = new ActiveChoiceSecureScriptContext()
        executeInContext(closure, context)

        new NodeBuilder().createNode(secureName(name)).with {
            appendNode('script', script)
            appendNode('sandbox', context.sandbox)
            it
        }
    }

    /*
     * Appends 'secure' to parameter 'name' and makes it camelCase.
     */
    private String secureName(String name) {
        "secure${name.capitalize()}"
    }
}
