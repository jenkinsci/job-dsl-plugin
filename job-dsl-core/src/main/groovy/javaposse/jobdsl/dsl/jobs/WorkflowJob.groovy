package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.workflow.WorkflowDefinitionContext

class WorkflowJob extends Job {
    WorkflowJob(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    /**
     * Adds a workflow definition.
     */
    void definition(@DslContext(WorkflowDefinitionContext) Closure definitionClosure) {
        WorkflowDefinitionContext context = new WorkflowDefinitionContext(jobManagement, this)
        ContextHelper.executeInContext(definitionClosure, context)

        configure { Node project ->
            Node definition = project / definition
            if (definition) {
                project.remove(definition)
            }
            project << context.definitionNode
        }
    }
}
