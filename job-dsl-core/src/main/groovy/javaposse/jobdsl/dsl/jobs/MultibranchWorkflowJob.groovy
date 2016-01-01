package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.workflow.OrphanedItemStrategyContext
import javaposse.jobdsl.dsl.helpers.workflow.BranchSourcesContext

class MultibranchWorkflowJob extends Job {
    MultibranchWorkflowJob(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Adds branch sources.
     */
    void branchSources(@DslContext(BranchSourcesContext) Closure sourcesClosure) {
        BranchSourcesContext context = new BranchSourcesContext()
        ContextHelper.executeInContext(sourcesClosure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            context.branchSourceNodes.each {
                project / sources / data << it
            }
        }
    }

    /**
     * Sets the orphaned branch strategy.
     */
    void orphanedItemStrategy(@DslContext(OrphanedItemStrategyContext) Closure closure) {
        OrphanedItemStrategyContext context = new OrphanedItemStrategyContext()
        ContextHelper.executeInContext(closure, context)

        if (context.orphanedItemStrategyNode != null) {
            withXmlActions << WithXmlAction.create { Node project ->
                Node orphanedItemStrategy = project / 'orphanedItemStrategy'
                if (orphanedItemStrategy) {
                    // there can only be only one orphanedItemStrategy, so remove if there
                    project.remove(orphanedItemStrategy)
                }

                project << context.orphanedItemStrategyNode
            }
        }
    }
}
