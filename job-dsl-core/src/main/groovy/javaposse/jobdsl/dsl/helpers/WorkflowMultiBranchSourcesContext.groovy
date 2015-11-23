package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class WorkflowMultiBranchSourcesContext implements Context {
    Node branchSourceNode

    /**
     * Defines a Groovy BranchSource DSL definition.
     */
    void git(@DslContext(BranchSourceContext) Closure branchSourceClosure) {
        BranchSourceContext context = new BranchSourceContext()
        ContextHelper.executeInContext(branchSourceClosure, context)

        Node sourceNode = new NodeBuilder().'source'(class: 'jenkins.plugins.git.GitSCMSource') {
            id(context.id ?: UUID.randomUUID().toString())
            remote(context.remote ?: '')
            credentialsId(context.credentialsId ?: '')
            includes(context.includes ?: '*')
            excludes(context.excludes ?: '')
            ignoreOnPushNotifications(context.ignoreOnPushNotifications)
        }

        Node strategyNode = new NodeBuilder().'strategy'(class: 'jenkins.branch.DefaultBranchPropertyStrategy') {
            'properties'(class: 'empty-list')
        }

        branchSourceNode = new NodeBuilder().'jenkins.branch.BranchSource'()
        branchSourceNode.append(sourceNode)
        branchSourceNode.append(strategyNode)

    }
}
