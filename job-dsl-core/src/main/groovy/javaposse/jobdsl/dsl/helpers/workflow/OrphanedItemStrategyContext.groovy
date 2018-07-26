package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

@ContextType('com.cloudbees.hudson.plugins.folder.computed.OrphanedItemStrategy')
class OrphanedItemStrategyContext extends AbstractExtensibleContext {
    Node orphanedItemStrategyNode

    OrphanedItemStrategyContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    /**
     * Trims dead items by the number of days or the number of items.
     */
    void discardOldItems(@DslContext(DefaultOrphanedItemStrategyContext) Closure closure) {
        DefaultOrphanedItemStrategyContext context = new DefaultOrphanedItemStrategyContext()
        ContextHelper.executeInContext(closure, context)

        orphanedItemStrategyNode = new NodeBuilder().'orphanedItemStrategy'(
                class: 'com.cloudbees.hudson.plugins.folder.computed.DefaultOrphanedItemStrategy',
        ) {
            pruneDeadBranches(true)
            daysToKeep(context.daysToKeep)
            numToKeep(context.numToKeep)
        }
    }

    @Override
    protected void addExtensionNode(Node node) {
        orphanedItemStrategyNode = ContextHelper.toNamedNode('orphanedItemStrategy', node)
    }
}
