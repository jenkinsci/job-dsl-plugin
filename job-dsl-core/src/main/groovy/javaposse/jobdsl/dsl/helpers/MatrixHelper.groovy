package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Preconditions
import groovy.transform.Canonical
import javaposse.jobdsl.dsl.AxisType
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction

class MatrixHelper extends AbstractHelper {
    JobManagement jobManagement

    MatrixHelper(List<WithXmlAction> withXmlActions, JobType jobType, JobManagement jobManagement) {
        super(withXmlActions, jobType)
        this.jobManagement = jobManagement
    }

     /**
     * <project>
     *     <axes>
     *       <hudson.matrix.LabelAxis>
     *           <name>label</name>
     *           <values>
     *               <string>linux</string>
     *               <string>mac</string>
     *               <string>lamp</string>
     *               <string>master</string>
     *           </values>
     *       </hudson.matrix.LabelAxis>
     *       <hudson.matrix.LabelExpAxis>
     *           <name>label_exp</name>
     *           <values>
     *               <string>linux</string>
     *               <string>mac</string>
     *           </values>
     *       </hudson.matrix.LabelExpAxis>
     *       <hudson.matrix.TextAxis>
     *           <name>aaa</name>
     *           <values>
     *               <string>a</string>
     *               <string>b</string>
     *               <string>c</string>
     *           </values>
     *       </hudson.matrix.TextAxis>
     *     </axes>
     *  </project>
     * Provide axis for matrix (multi configuration job)
     */
    def matrixAxis( String axis, String n, Iterable<String> v) {
        AxisType a = axis as AxisType
        //def an = axis.getAxisName()
        def an = a.axisName
        execute {
            it / axes << "${an}" {
                name n
                values {
                    v.each(){ string it}
                }
            }
        }
    }

    /*
     * <project>
     *   <executionStrategy class='hudson.matrix.DefaultMatrixExecutionStrategyImpl'>
     *          <runSequentially>false</runSequentially>
     *   </executionStrategy>
     * </project>
     */

    def matrixSequential(boolean mustRunSequentially = false) {
        execute {
            it / executionStrategy / runSequentially (mustRunSequentially?'true':'false')
        }
    }

    /*
     * <project>
     *   <combinationFilter>axis_label=='a'||axis_label=='b'</combinationFilter>
     * </project>
     */

    def matrixCombinationFilter(String filter = '') {
        execute {
            it / combinationFilter ( filter)
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
    //So UNSTABLE is 1 and YELLOW  and STABLE is BLUE and 0 and completeBuild is always true

    def matrixTouchstoneFilter(String filter = '', Boolean onSuccess = true) {
        def nameVal = 'STABLE'
        def colorVal = 'BLUE'
        def ordinalVal = 0

        if(onSuccess == false){
            nameVal = 'UNSTABLE'
            colorVal = 'YELLOW'
            ordinalVal = 1
        }
        execute {
            it / executionStrategy /  touchStoneCombinationFilter (filter)
            it /executionStrategy /  touchStoneResultCondition  {
                name nameVal
                color colorVal
                ordinal ordinalVal
                completeBuild 'true'
            }
        }
    }


}