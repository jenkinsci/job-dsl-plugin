package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ComputedFolder
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.workflow.BranchSourcesContext

class MultibranchWorkflowJob extends ComputedFolder {
    MultibranchWorkflowJob(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    @Deprecated
    MultibranchWorkflowJob(JobManagement jobManagement) {
        super(jobManagement)
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
}
