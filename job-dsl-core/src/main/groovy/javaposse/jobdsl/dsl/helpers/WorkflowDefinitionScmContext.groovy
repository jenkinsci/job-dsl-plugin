package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class WorkflowDefinitionScmContext implements Context {
    Node definitionNode

    /**
     * Defines a Groovy CPS SCM DSL definition.
     */
    void cps(@DslContext(CpsContext) Closure cpsClosure) {
        CpsContext context = new CpsContext()
        ContextHelper.executeInContext(cpsClosure, context)

        definitionNode = new NodeBuilder().'definition'(
                class: 'org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition') {
            scriptPath(context.scriptPath ?: 'Jenkinsfile')
        }
    }
}
