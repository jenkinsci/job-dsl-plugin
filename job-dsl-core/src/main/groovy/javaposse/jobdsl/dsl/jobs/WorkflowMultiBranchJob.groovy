package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.OrphanedItemStrategyContext
import javaposse.jobdsl.dsl.helpers.WorkflowMultiBranchSourcesContext

class WorkflowMultiBranchJob extends Job {
    WorkflowMultiBranchJob(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Adds a workflow source.
     */
    void branchSource(@DslContext(WorkflowMultiBranchSourcesContext) Closure sourcesClosure) {
        WorkflowMultiBranchSourcesContext context = new WorkflowMultiBranchSourcesContext()
        ContextHelper.executeInContext(sourcesClosure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            project / sources / data << context.branchSourceNode
        }
    }

    /**
     * Adds a orphaned branch strategy
     */
    void orphanedItemStrategy(@DslContext(OrphanedItemStrategyContext) Closure orphanedItemStrategyClosure) {
        OrphanedItemStrategyContext context = new OrphanedItemStrategyContext()
        ContextHelper.executeInContext(orphanedItemStrategyClosure, context)
        Node orphanedItem = new NodeBuilder().'orphanedItemStrategy'(
                class: 'com.cloudbees.hudson.plugins.folder.computed.DefaultOrphanedItemStrategy') {
            pruneDeadBranches(context.pruneDeadBranches ?: true)
            daysToKeep(context.daysToKeep ?: 0)
            numToKeep(context.numToKeep ?: 0)
        }

        withXmlActions << WithXmlAction.create { Node project ->
            Node orphanedItemStrategy = project / 'orphanedItemStrategy'
            if (orphanedItemStrategy) {
                // There can only be only one orphanedItemStrategy, so remove if there
                project.remove(orphanedItemStrategy)
            }

            project << orphanedItem
        }
    }
}
