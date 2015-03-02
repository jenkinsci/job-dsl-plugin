package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.WorkflowDefinitionContext

class WorkflowJob extends Job {

    static final String TEMPLATE = '''
        <?xml version='1.0' encoding='UTF-8'?>
        <flow-definition>
          <actions/>
          <description/>
          <keepDependencies>false</keepDependencies>
          <properties/>
          <definition class="org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition">
            <script/>
            <sandbox>false</sandbox>
          </definition>
          <triggers/>
        </flow-definition>
    '''.stripIndent().trim()

    final String template = TEMPLATE

    WorkflowJob(JobManagement jobManagement) {
        super(jobManagement)
    }

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
}
