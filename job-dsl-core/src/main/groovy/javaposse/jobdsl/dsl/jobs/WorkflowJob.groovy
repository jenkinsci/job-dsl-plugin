package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.ScmContext
import javaposse.jobdsl.dsl.helpers.WorkflowDefinitionContext
import javaposse.jobdsl.dsl.helpers.WorkflowDefinitionScmContext

import static javaposse.jobdsl.dsl.Preconditions.checkState

class WorkflowJob extends Job {
    WorkflowJob(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Adds a workflow definition.
     */
    void definition(@DslContext(WorkflowDefinitionContext) Closure definitionClosure) {
        WorkflowDefinitionContext context = new WorkflowDefinitionContext()
        ContextHelper.executeInContext(definitionClosure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            Node definition = project / definition
            if (definition) {
                project.remove(definition)
            }
            project << context.definitionNode
        }
    }

    /**
     * Adds a workflow definition.
     */
    void definitionScm(@DslContext(WorkflowDefinitionScmContext) Closure definitionClosure) {
        WorkflowDefinitionScmContext context = new WorkflowDefinitionScmContext()
        ContextHelper.executeInContext(definitionClosure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            Node definition = project / definition
            if (definition) {
                project.remove(definition)
            }
            project << context.definitionNode
        }
    }

    /**
     * Allows a job to check out sources from an SCM provider.
     */
    void scm(@DslContext(ScmContext) Closure closure) {
        ScmContext context = new ScmContext(withXmlActions, jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        if (!context.scmNodes.empty) {
            checkState(context.scmNodes.size() == 1, 'Outside "multiscm", only one SCM can be specified')
            withXmlActions << WithXmlAction.create { Node project ->
                Node definitionNode = project / definition
                Node scm = definitionNode / scm
                if (scm) {
                    definitionNode.remove(scm)
                }
                project / definition << context.scmNodes[0]
            }
        }
    }
}
