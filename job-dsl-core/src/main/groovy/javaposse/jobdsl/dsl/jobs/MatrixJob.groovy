package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.AxisContext

import static javaposse.jobdsl.dsl.Preconditions.checkNotNull

class MatrixJob extends Job {
    MatrixJob(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Specifies the axes for the matrix job.
     */
    void axes(@DslContext(AxisContext) Closure closure) {
        AxisContext context = new AxisContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            Node axesNode = project / 'axes'
            context.axisNodes.each {
                axesNode << it
            }
            context.configureBlocks.each {
                new WithXmlAction(it).execute(axesNode)
            }
        }
    }

    /**
     * Specifies a custom workspace name for individual child workspaces created for individual axes.
     *
     * @param workspacePath workspace path to use
     * @since 1.36
     */
    void childCustomWorkspace(String workspacePath) {
        checkNotNull(workspacePath, 'Workspace path must not be null')

        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('childCustomWorkspace', workspacePath)
            project / node
        }
    }

    /**
     * Set an expression to limit which combinations can be run.
     */
    void combinationFilter(String filterExpression) {
        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('combinationFilter', filterExpression)
            project / node
        }
    }

    /**
     * Runs each matrix combination in sequence. Defaults to {@code false}.
     */
    void runSequentially(boolean sequentially = true) {
        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('runSequentially', sequentially)
            project / 'executionStrategy' / node
        }
    }

    /**
     * Sets an expression of which combination to run first.
     */
    void touchStoneFilter(String filter, boolean continueOnUnstable = false) {
        withXmlActions << WithXmlAction.create { Node project ->
            project / 'executionStrategy' / 'touchStoneCombinationFilter'(filter)
            project / 'executionStrategy' / 'touchStoneResultCondition' {
                name continueOnUnstable ? 'UNSTABLE' : 'STABLE'
                color continueOnUnstable ? 'YELLOW' : 'BLUE'
                ordinal continueOnUnstable ? 1 : 0
            }
        }
    }
}
