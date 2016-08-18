package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin

class WorkflowDefinitionContext extends AbstractContext {
    protected final Item item

    Node definitionNode

    WorkflowDefinitionContext(JobManagement jobManagement, Item item) {
        super(jobManagement)
        this.item = item
    }

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

    /**
     * Loads a pipeline script from SCM.
     *
     * @since 1.44
     */
    @RequiresPlugin(id = 'workflow-cps', minimumVersion = '1.2')
    void cpsScm(@DslContext(CpsScmContext) Closure cpsScmClosure) {
        CpsScmContext context = new CpsScmContext(jobManagement, item)
        ContextHelper.executeInContext(cpsScmClosure, context)

        Preconditions.checkState(!context.scmContext.scmNodes.empty, 'SCM must be specified')
        Preconditions.checkState(context.scmContext.scmNodes.size() == 1, 'only one SCM can be specified')

        definitionNode = new NodeBuilder().
                definition(class: 'org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition') {
            scriptPath(context.scriptPath)
        }
        definitionNode.children().add(context.scmContext.scmNodes[0])
    }
}
