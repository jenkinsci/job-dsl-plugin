package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.WithXmlAction

class TopLevelHelper extends AbstractHelper {

    TopLevelHelper(List<WithXmlAction> withXmlActions) {
        super(withXmlActions)
    }

    def description(String descriptionString) {
        execute {
            def descNode = methodMissing('description', descriptionString)
            it / descNode
        }
    }

    /**
     * "Restrict where this project can be run"
     * <assignedNode>FullTools&amp;&amp;RPM&amp;&amp;DC</assignedNode>
     * @param labelExpression Label of node to use, if null is passed in, the label is cleared out and it can roam
     * @return
     */
    boolean labelAlreadyAdded = false
    def label(String labelExpression = null) {
        Preconditions.checkState(!labelAlreadyAdded, "Label can only be appplied once")
        labelAlreadyAdded = true
        execute {
            if (labelExpression) {
                it / assignedNode(labelExpression)
                it / canRoam('false') // If canRoam is true, the label will not be used
            } else {
                it / assignedNode('')
                it / canRoam('true')
            }
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
            it / 'hudson.plugins.chucknorris.CordellWalkerRecorder' / factGenerator
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

    /**
     * Block build if certain jobs are running
     <hudson.plugins.buildblocker.BuildBlockerProperty>
        <useBuildBlocker>true</useBuildBlocker> <!-- Always true -->
        <blockingJobs>API-SmokeTests-TestBranchAPI-NightlyTests-TestBranchAPI-Sync-Instances</blockingJobs>
     </hudson.plugins.buildblocker.BuildBlockerProperty>
     */
    def blockOn(String projectNames) {
        execute {
            it / 'hudson.plugins.buildblocker.BuildBlockerProperty' {
                useBuildBlocker 'true'
                blockingJobs projectNames
            }
        }
    }
}