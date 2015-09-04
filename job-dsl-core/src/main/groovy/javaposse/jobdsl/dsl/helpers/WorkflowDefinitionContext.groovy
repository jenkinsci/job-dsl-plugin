package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class WorkflowDefinitionContext implements Context {
    Node definitionNode

    /**
     * Defines a Groovy CPS DSL definition.
     */
    void cps(@DslContext(CpsContext) Closure cpsClosure) {
        CpsContext context = new CpsContext()
        ContextHelper.executeInContext(cpsClosure, context)

        definitionNode = new NodeBuilder().'definition'(class: 'org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition') {
            script(context.script ?: '')
            sandbox(context.sandbox)
        }
    }
}
