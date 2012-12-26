package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.WithXmlAction

class TopLevelHelper extends AbstractHelper {

    TopLevelHelper(List<WithXmlAction> withXmlActions) {
        super(withXmlActions)
    }

    /**
     * "Restrict where this project can be run"
     * <assignedNode>FullTools&amp;&amp;RPM&amp;&amp;DC</assignedNode>
     * @param labelExpression
     * @return
     */
    boolean labelAlreadyAdded = false
    def label(String labelExpression) {
        Preconditions.checkState(!labelAlreadyAdded, "Label can only be appplied once")
        labelAlreadyAdded = true
        execute {
            it / assignedNode(labelExpression)
            it / canRoam('false') // If canRoam is true, the label will not be used
        }
    }

    /*
    <buildWrappers>
      <hudson.plugins.build__timeout.BuildTimeoutWrapper>
        <timeoutMinutes>15</timeoutMinutes>
        <failBuild>true</failBuild>
        <!-- Missing from DSL Call -->
        <writingDescription>false</writingDescription>
        <timeoutPercentage>0</timeoutPercentage>
        <timeoutType>absolute</timeoutType>
        <timeoutMinutesElasticDefault>3</timeoutMinutesElasticDefault>
      </hudson.plugins.build__timeout.BuildTimeoutWrapper>
    </buildWrappers>
    */

    def timeout(Integer timeoutInMinutes, Boolean shouldFailBuild = true) {
        execute {
            def pluginNode = it / buildWrappers / 'hudson.plugins.build__timeout.BuildTimeoutWrapper'
            pluginNode / timeoutMinutes(Integer.toString(timeoutInMinutes))
            pluginNode / failBuild(shoudFailBuild?'true':'false')
        }
    }
    /*
    <hudson.plugins.chucknorris.CordellWalkerRecorder>
      <factGenerator/>
    </hudson.plugins.chucknorris.CordellWalkerRecorder>
     */
    def chucknorris() {
        execute {
            def factGenerator = it / 'hudson.plugins.chucknorris.CordellWalkerRecorder' / factGenerator
        }
    }

    /*
    <disabled>true</disabled>
     */
    def disabled(boolean shouldDisable = true) {
        execute {
            it / disabled(shouldDisable?'true':'false')
        }
    }


    /**
     <logRotator>
     <daysToKeep>14</daysToKeep>
     <numToKeep>50</numToKeep>
     <artifactDaysToKeep>5</artifactDaysToKeep>
     <artifactNumToKeep>20</artifactNumToKeep>
     </logRotator>

     TODO - Let them specify a closure to fill a context object, I think it would nicer than a bunch of int args
     */

    def logRotator(int daysToKeepInt = -1, int numToKeepInt = -1, int artifactDaysToKeepInt = -1, int artifactNumToKeepInt = -1) {
        execute {
            it / logRotator {
                daysToKeep daysToKeepInt.toString()
                numToKeep numToKeepInt.toString()
                artifactDaysToKeep artifactDaysToKeepInt.toString()
                artifactNumToKeep artifactNumToKeepInt.toString()
            }
        }
    }

}