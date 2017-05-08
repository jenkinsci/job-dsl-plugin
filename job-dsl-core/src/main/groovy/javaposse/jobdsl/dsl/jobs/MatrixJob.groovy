package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.AxisContext

import static javaposse.jobdsl.dsl.Preconditions.checkNotNull

class MatrixJob extends Job {
    MatrixJob(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    /**
     * Specifies the axes for the matrix job.
     */
    void axes(@DslContext(AxisContext) Closure closure) {
        AxisContext context = new AxisContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        configure { Node project ->
            Node axesNode = project / 'axes'
            context.axisNodes.each {
                axesNode << it
            }
            ContextHelper.executeConfigureBlocks(axesNode, context.configureBlocks)
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

        configure { Node project ->
            Node node = methodMissing('childCustomWorkspace', workspacePath)
            project / node
        }
    }

    /**
     * Set an expression to limit which combinations can be run.
     */
    void combinationFilter(String filterExpression) {
        configure { Node project ->
            Node node = methodMissing('combinationFilter', filterExpression)
            project / node
        }
    }

    /**
     * Runs each matrix combination in sequence. Defaults to {@code false}.
     */
    void runSequentially(boolean sequentially = true) {
        configure { Node project ->
            Node node = methodMissing('runSequentially', sequentially)
            project / 'executionStrategy' / node
        }
    }

    /**
     * Sets an expression of which combination to run first.
     */
    void touchStoneFilter(String filter, boolean continueOnUnstable = false) {
        configure { Node project ->
            project / 'executionStrategy' / 'touchStoneCombinationFilter'(filter)
            project / 'executionStrategy' / 'touchStoneResultCondition' {
                name continueOnUnstable ? 'UNSTABLE' : 'STABLE'
                color continueOnUnstable ? 'YELLOW' : 'BLUE'
                ordinal continueOnUnstable ? 1 : 0
            }
        }
    }
}
