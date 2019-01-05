package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin

@ContextType('org.jenkinsci.plugins.workflow.flow.FlowDefinition')
class WorkflowDefinitionContext extends AbstractExtensibleContext {
    Node definitionNode

    WorkflowDefinitionContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    /**
     * Defines a Groovy CPS DSL definition.
     */
    @RequiresPlugin(id = 'workflow-cps', minimumVersion = '2.29')
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
    @RequiresPlugin(id = 'workflow-cps', minimumVersion = '2.29')
    void cpsScm(@DslContext(CpsScmContext) Closure cpsScmClosure) {
        CpsScmContext context = new CpsScmContext(jobManagement, item)
        ContextHelper.executeInContext(cpsScmClosure, context)

        Preconditions.checkState(!context.scmContext.scmNodes.empty, 'SCM must be specified')
        Preconditions.checkState(context.scmContext.scmNodes.size() == 1, 'only one SCM can be specified')

        definitionNode = new NodeBuilder().
                definition(class: 'org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition') {
            scriptPath(context.scriptPath)
            lightweight(context.lightweight)
        }
        definitionNode.children().add(context.scmContext.scmNodes[0])
    }

    @Override
    protected void addExtensionNode(Node node) {
        definitionNode = ContextHelper.toNamedNode('definition', node)
    }
}
