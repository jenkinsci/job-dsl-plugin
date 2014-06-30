package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import static com.google.common.base.Preconditions.checkState

class MatrixContextHelper extends AbstractContextHelper<axis.AxisContext> {
    List<Node> touchStoneResultConditionNode  = []
    boolean combinationFilterAlreadyAdded = false
    boolean runSequentiallyAlreadyAdded = false
    boolean touchStoneAlreadyAdded = false

    MatrixContextHelper(List<WithXmlAction> withXmlActions, JobType jobType) {
        super(withXmlActions, jobType)
    }

    def axis(Closure closure) {
        checkState type == JobType.MatrixJob, 'axis can only be applied for Matrix jobs'

        execute(closure, new axis.AxisContext())
    }

    Closure generateWithXmlClosure(axis.AxisContext context) {
        return { Node project ->

            //there might not be any actual axes defined...
            if ( !context.axisNodes.isEmpty() ) {
                def axisNode
                if (project.axes.isEmpty()) {
                    axisNode = project.appendNode('axes')
                } else {
                    axisNode = project.axes[0]
                }

                context.axisNodes.each {
                    axisNode << it
                }
            }
        }
    }

    /*
  * <project>
  *   <combinationFilter>axis_label=='a'||axis_label=='b'</combinationFilter>
  * </project>
  */
    def combinationFilter(String filterExpression = '') {
        Preconditions.checkState(!combinationFilterAlreadyAdded, 'combinationFilter can only be applied once')
        combinationFilterAlreadyAdded = true

        execute {
            def node = methodMissing('combinationFilter', filterExpression)
            it / node
        }
    }

    /* <project>
     *   <executionStrategy class='hudson.matrix.DefaultMatrixExecutionStrategyImpl'>
     *          <runSequentially>false</runSequentially>
     *   </executionStrategy>
     * </project>
     */
    def sequential(Boolean runInSequence = true) {
      Preconditions.checkState(!runSequentiallyAlreadyAdded, 'sequential can only be applied once')
        runSequentiallyAlreadyAdded = true

        execute {
            it / 'executionStrategy' / 'runSequentially' ( runInSequence ? 'true' : 'false')
        }
    }

    /*
     * <project>
     *   <executionStrategy class="hudson.matrix.DefaultMatrixExecutionStrategyImpl">
     *     <touchStoneCombinationFilter>axis_label=='a'||axis_label=='b'</touchStoneCombinationFilter>
     *     <touchStoneResultCondition>
     *       <name>UNSTABLE|STABLE</name>
     *       <ordinal>1|0</ordinal>
     *       <color>YELLOW|BLUE</color>
     *       <completeBuild>true</completeBuild>
     *     </touchStoneResultCondition>
     *   </executionStrategy>
     * </project>
     */
    def touchStoneFilter( String filter = '', Boolean continueOnUnstable = false ) {
        def nameVal = 'STABLE'
        def colorVal = 'BLUE'
        def ordinalVal = 0

        Preconditions.checkState( !touchStoneAlreadyAdded, 'touchStone can only be applied once' )
        touchStoneAlreadyAdded = true

        if ( continueOnUnstable ) {
            nameVal = 'UNSTABLE'
            colorVal = 'YELLOW'
            ordinalVal = 1
        }

        execute {
            it / 'executionStrategy' /  'touchStoneCombinationFilter' (filter)
            it / 'executionStrategy' /  'touchStoneResultCondition'  {
                name nameVal
                color colorVal
                ordinal ordinalVal
            }
        }
    }
}
