package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.publisher.BuildFlowPublisherContext

class BuildFlowJob extends Job {
    BuildFlowJob(JobManagement jobManagement) {
        super(jobManagement)
    }
   
    /**
     * Controls the "Build Flow Needs Workspace" checkbox, see Build Flow Plugin for more details.
     */
    void buildFlowNeedsWorkspace(boolean needsWorkspace) {
        withXmlActions << WithXmlAction.create { Node project ->
            project / buildNeedsWorkspace(Boolean.valueOf(needsWorkspace).toString())
        }
    }

    /**
     * Causes the Build Flow Plugin to process a DSL script from a file within the job's WORKSPACE
     * Enables {@link #buildFlowNeedsWorkspace} automatically.
     * @param fileName The DSL file to load from the job's workspace.
     */
    void buildFlowFile(String fileName) {
        buildFlowNeedsWorkspace(true) //File requires a workspace
        withXmlActions << WithXmlAction.create { Node project ->
            project / dslFile(fileName)
        }
    }

    /**
     * Sets the build flow DSL script.
     * Use {@link javaposse.jobdsl.dsl.DslFactory#readFileFromWorkspace(java.lang.String)} to read DSL scripts from
     * files.
     */
    void buildFlow(String buildFlowText) {
        withXmlActions << WithXmlAction.create { Node project ->
            project / dsl(buildFlowText)
        }
    }

    @Override
    void publishers(@DslContext(BuildFlowPublisherContext) Closure closure) {
        BuildFlowPublisherContext context = new BuildFlowPublisherContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            context.publisherNodes.each {
                project / 'publishers' << it
            }
        }
    }
}
