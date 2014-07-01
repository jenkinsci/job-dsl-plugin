package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction

import static com.google.common.base.Preconditions.checkState

class MatrixHelper extends AbstractContextHelper<AxisContext> {
    MatrixHelper(List<WithXmlAction> withXmlActions, JobType jobType) {
        super(withXmlActions, jobType)
    }

    def axes(Closure closure) {
        checkState(type == JobType.Matrix, 'axes can only be applied for Matrix jobs')

        execute(closure, new AxisContext())
    }

    Closure generateWithXmlClosure(AxisContext context) {
        return { Node project ->
            def axesNode
            if (project.axes.isEmpty()) {
                axesNode = project.appendNode('axes')
            } else {
                axesNode = project.axes[0]
            }
            context.axisNodes.each {
                axesNode << it
            }
            context.configureBlocks.each {
                new WithXmlAction(it).execute(axesNode)
            }
        }
    }

    /**
     * <combinationFilter>axis_label=='a'||axis_label=='b'</combinationFilter>
     */
    def combinationFilter(String filterExpression) {
        checkState(type == JobType.Matrix, 'combinationFilter can only be applied for Matrix jobs')

        execute {
            def node = methodMissing('combinationFilter', filterExpression)
            it / node
        }
    }

    /**
     * <executionStrategy>
     *     <runSequentially>false</runSequentially>
     * </executionStrategy>
     */
    def runSequentially(boolean sequentially = true) {
        checkState(type == JobType.Matrix, 'runSequentially can only be applied for Matrix jobs')

        execute {
            def node = methodMissing('runSequentially', sequentially)
            it / 'executionStrategy' / node
        }
    }

    /**
     * <executionStrategy>
     *     <touchStoneCombinationFilter>axis_label=='a'||axis_label=='b'</touchStoneCombinationFilter>
     *     <touchStoneResultCondition>
     *         <name>UNSTABLE</name>
     *         <ordinal>1</ordinal>
     *         <color>YELLOW</color>
     *         <completeBuild>true</completeBuild>
     *     </touchStoneResultCondition>
     * </executionStrategy>
     */
    def touchStoneFilter(String filter, boolean continueOnUnstable = false) {
        checkState(type == JobType.Matrix, 'touchStoneFilter can only be applied for Matrix jobs')

        execute {
            it / 'executionStrategy' / 'touchStoneCombinationFilter'(filter)
            it / 'executionStrategy' / 'touchStoneResultCondition' {
                name continueOnUnstable ? 'UNSTABLE' : 'STABLE'
                color continueOnUnstable ? 'YELLOW' : 'BLUE'
                ordinal continueOnUnstable ? 1 : 0
            }
        }
    }
}
