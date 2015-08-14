package javaposse.jobdsl.dsl.helpers.parameter

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.DslContext

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext
import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class ActiveChoiceContext implements Context {
    private static final Set<String> VALID_CHOICE_TYPES = [
        'SINGLE_SELECT', 'MULTI_SELECT', 'CHECKBOX', 'RADIO'
    ]

    boolean supportsFilterable = true
    String choiceTypePrefix = 'PT_'

    String description
    boolean filterable
    String choiceType = 'SINGLE_SELECT'
    Node script

    void description(String description) {
        this.description = description
    }

    void filterable(boolean filterable = true) {
        this.filterable = filterable
    }

    void choiceType(String choiceType) {
        checkArgument(
                VALID_CHOICE_TYPES.contains(choiceType),
                "choiceType must be one of ${VALID_CHOICE_TYPES.join(', ')}"
        )
      this.choiceType = choiceType
    }

    void groovyScript(@DslContext(ActiveChoiceGroovyScriptContext) Closure closure) {
        ActiveChoiceGroovyScriptContext context = new ActiveChoiceGroovyScriptContext()
        executeInContext(closure, context)

        script = new NodeBuilder().script(class: 'org.biouno.unochoice.model.GroovyScript') {
            delegate.script(context.script ?: '')
            delegate.fallbackScript(context.fallbackScript ?: '')
        }
    }

    void scriptlerScript(String scriptId, @DslContext(ActiveChoiceScriptlerScriptContext) Closure closure = null) {
        ActiveChoiceScriptlerScriptContext context = new ActiveChoiceScriptlerScriptContext()
        executeInContext(closure, context)

        script = new NodeBuilder().script(class: 'org.biouno.unochoice.model.ScriptlerScript') {
            scriptlerScriptId(scriptId)
            parameters {
                context.parameters.each { String name, String value ->
                    entry {
                        string(name)
                        string(value)
                    }
                }
            }
        }
    }
}
