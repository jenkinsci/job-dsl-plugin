package javaposse.jobdsl.dsl.helpers.parameter

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.DslContext

import static com.google.common.base.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class ActiveChoiceContext implements Context {

    private static final Set<String> VALID_CHOICE_TYPES = [
        'SINGLE_SELECT', 'MULTI_SELECT', 'CHECKBOX', 'RADIO'
    ]

    String description
    int visibleItemCount = 1
    boolean filterable = false
    boolean supportsFilterable = true
    String choiceType = 'SINGLE_SELECT'
    Node script

    void description(String description) {
        this.description = description
    }

    void visibleItemCount(int visibleItemCount) {
        this.visibleItemCount = visibleItemCount
    }

    void filterable(boolean filterable) {
        this.filterable = filterable
    }

    void choiceType(String choiceType) {
      checkArgument(VALID_CHOICE_TYPES.contains(choiceType),
        "choiceType must be one of ${VALID_CHOICE_TYPES.join(', ')}")
      this.choiceType = choiceType
    }

    void groovyScript(@DslContext(ActiveChoiceGroovyScriptContext) Closure closure = null) {
        checkArgument(script == null, 'script already defined')
        ActiveChoiceGroovyScriptContext context = new ActiveChoiceGroovyScriptContext()
        executeInContext(closure, context)

        script = NodeBuilder.newInstance().script(class: 'org.biouno.unochoice.model.GroovyScript') {
            if (context.script) {
                script(context.script)
            }
            if (context.fallbackScript) {
                fallbackScript(context.fallbackScript)
            }
        }
    }

    static Node createActiveChoiceNode(String type, String paramName, ActiveChoiceContext context, String
            choiceTypePrefix, Closure additionalParams = {}) {
        Node newNode = NodeBuilder.newInstance().
                "${type}" additionalParams << {
            name(paramName)
            description(context.description ?: '')
            randomName('choice-parameter-' + System.nanoTime())
            visibleItemCount(context.visibleItemCount)
            parameters(class: 'linked-hash-map')
            choiceType(choiceTypePrefix + context.choiceType)
            if (context.supportsFilterable) {
                filterable(context.filterable)
            }
        }
        if (context.script) {
            newNode.children().add(context.script)
        }
        newNode
    }
}
