package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

@ContextType('jenkins.branch.MultiBranchProjectFactory')
class MultiBranchProjectFactoryContext extends AbstractExtensibleContext {
    final List<Node> projectFactoryNodes = []

    MultiBranchProjectFactoryContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    /**
     * Configure the default workflow branch project factory.
     */
    void workflowBranchProjectFactory(
        @DslContext(WorkflowBranchProjectFactoryContext)
        Closure workflowBranchProjectFactoryClosure) {
        WorkflowBranchProjectFactoryContext context = new WorkflowBranchProjectFactoryContext()
        ContextHelper.executeInContext(workflowBranchProjectFactoryClosure, context)

        this.projectFactoryNodes <<
            new NodeBuilder().'org.jenkinsci.plugins.workflow.multibranch.WorkflowBranchProjectFactory' {
                owner(class: 'org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject',
                    reference: '../..')

                scriptPath(context.scriptPath)
            }
    }

    @Override
    protected void addExtensionNode(final Node node) {
        projectFactoryNodes << node
    }
}
