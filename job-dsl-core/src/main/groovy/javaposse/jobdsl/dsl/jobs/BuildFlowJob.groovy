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
     * Sets the build flow DSL script.
     * Use {@link javaposse.jobdsl.dsl.DslFactory#readFileFromWorkspace(java.lang.String)} to read DSL scripts from
     * files.
     */
    void buildFlow(String buildFlowText) {
        withXmlActions << WithXmlAction.create { Node project ->
            project / dsl(buildFlowText)
        }
    }

    /**
     * Sets whether the build flow run needs a workspace. Defaults to {@code true}
     * @since 1.42
     */
    void buildNeedsWorkspace(boolean needsWorkspace = true) {
        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('buildNeedsWorkspace', needsWorkspace)
            project / node
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
