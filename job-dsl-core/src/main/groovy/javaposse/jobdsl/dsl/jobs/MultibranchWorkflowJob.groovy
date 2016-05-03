package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Folder
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.triggers.MultibranchWorkflowTriggerContext
import javaposse.jobdsl.dsl.helpers.workflow.OrphanedItemStrategyContext
import javaposse.jobdsl.dsl.helpers.workflow.BranchSourcesContext

class MultibranchWorkflowJob extends Folder {
    MultibranchWorkflowJob(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Adds build triggers to the job.
     */
    void triggers(@DslContext(MultibranchWorkflowTriggerContext) Closure closure) {
        MultibranchWorkflowTriggerContext context = new MultibranchWorkflowTriggerContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        configure { Node project ->
            context.triggerNodes.each {
                project / 'triggers' << it
            }
        }
    }

    /**
     * Adds branch sources.
     */
    void branchSources(@DslContext(BranchSourcesContext) Closure sourcesClosure) {
        BranchSourcesContext context = new BranchSourcesContext(jobManagement, this)
        ContextHelper.executeInContext(sourcesClosure, context)

        configure { Node project ->
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
            configure { Node project ->
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
