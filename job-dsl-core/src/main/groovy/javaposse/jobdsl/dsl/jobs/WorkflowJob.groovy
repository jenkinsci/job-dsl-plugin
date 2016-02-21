package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.workflow.WorkflowDefinitionContext

class WorkflowJob extends Job {
    WorkflowJob(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Adds a workflow definition.
     */
    void definition(@DslContext(WorkflowDefinitionContext) Closure definitionClosure) {
        WorkflowDefinitionContext context = new WorkflowDefinitionContext(jobManagement, this)
        ContextHelper.executeInContext(definitionClosure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            Node definition = project / definition
            if (definition) {
                project.remove(definition)
            }
            project << context.definitionNode
        }
    }
}
