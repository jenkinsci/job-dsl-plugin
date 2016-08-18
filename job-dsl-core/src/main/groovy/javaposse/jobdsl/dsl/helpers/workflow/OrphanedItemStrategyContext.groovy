package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class OrphanedItemStrategyContext implements Context {
    Node orphanedItemStrategyNode

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
}
