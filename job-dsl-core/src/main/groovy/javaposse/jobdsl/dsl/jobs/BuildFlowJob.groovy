package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.helpers.publisher.BuildFlowPublisherContext

class BuildFlowJob extends Job {
    BuildFlowJob(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    /**
     * Sets the build flow DSL script.
     * Use {@link javaposse.jobdsl.dsl.DslFactory#readFileFromWorkspace(java.lang.String)} to read DSL scripts from
     * files.
     */
    void buildFlow(String buildFlowText) {
        configure { Node project ->
            project / dsl(buildFlowText)
        }
    }

    /**
     * Sets whether the build flow run needs a workspace. Defaults to {@code false}.
     *
     * @since 1.42
     */
    @RequiresPlugin(id = 'build-flow-plugin', minimumVersion = '0.12')
    void buildNeedsWorkspace(boolean needsWorkspace = true) {
        configure { Node project ->
            Node node = methodMissing('buildNeedsWorkspace', needsWorkspace)
            project / node
        }
    }

    /**
     * Specifies a file path relative to the workspace from where the DSL will be read. Also sets
     * {@code buildNeedsWorkspace} to {@code true}.
     *
     * @since 1.42
     * @see #buildNeedsWorkspace(boolean)
     */
    @RequiresPlugin(id = 'build-flow-plugin', minimumVersion = '0.12')
    void dslFile(String fileName) {
        buildNeedsWorkspace()

        configure { Node project ->
            Node node = methodMissing('dslFile', fileName)
            project / node
        }
    }

    @Override
    void publishers(@DslContext(BuildFlowPublisherContext) Closure closure) {
        BuildFlowPublisherContext context = new BuildFlowPublisherContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        configure { Node project ->
            context.publisherNodes.each {
                project / 'publishers' << it
            }
        }
    }
}
