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
        <!-- Missing from DSL Call, Elastic and Likely stuck are radio buttons to Absolute -->
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

    /**
     * Add environment variables to the build.
     *
     * <project>
     *   <properties>
     *     <EnvInjectJobProperty>
     *       <info>
     *         <propertiesContent>TEST=foo BAR=123</propertiesContent>
     *         <loadFilesFromMaster>false</loadFilesFromMaster>
     *       </info>
     *       <on>true</on>
     *       <keepJenkinsSystemVariables>true</keepJenkinsSystemVariables>
     *       <keepBuildVariables>true</keepBuildVariables>
     *       <contributors/>
     *     </EnvInjectJobProperty>
     */
    def environmentVariables(Closure envClosure) {
        environmentVariables(null, envClosure)
    }

    def environmentVariables(Map<Object,Object> vars, Closure envClosure = null) {
        EnvironmentVariableContext envContext = new EnvironmentVariableContext()
        if (vars) {
            envContext.envs(vars)
        }
        AbstractContextHelper.executeInContext(envClosure, envContext)

        execute {
            it / 'properties' / 'EnvInjectJobProperty' {
                info {
                    propertiesContent(envContext.props.join('\n'))
                    loadFilesFromMaster(false)
                }
                on(true)
                keepJenkinsSystemVariables(true)
                keepBuildVariables(true)
                contributors()
            }
        }
    }

    def static class EnvironmentVariableContext implements Context {
        def props = []

        def env(Object key, Object value) {
            props << "${key}=${value}"
        }

        def envs(Map<Object, Object> map) {
            map.entrySet().each {
                env(it.key, it.value)
            }
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

    /**
     * Name of the JDK installation to use for this job.
     * @param jdkArg name of the JDK installation to use for this job.
     */
    def jdk(String jdkArg) {
        execute {
            def jdkNode = methodMissing('jdk', jdkArg)
            it / jdkNode
        }
    }

    /**
     * Priority of this job.
     * Requires the <a href="https://wiki.jenkins-ci.org/display/JENKINS/Priority+Sorter+Plugin">Priority Sorter Plugin</a>.
     * Default value is 100.
     *
     * <properties>
     *   <hudson.queueSorter.PrioritySorterJobProperty plugin="PrioritySorter@1.3">
     *     <priority>100</priority>
     *   </hudson.queueSorter.PrioritySorterJobProperty>
     * </properties>
     */
    def priority(int value) {
        execute {
            def node = new Node(it / 'properties', 'hudson.queueSorter.PrioritySorterJobProperty')
            node.appendNode('priority', value)
        }
    }
}