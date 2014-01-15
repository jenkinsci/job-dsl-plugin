package javaposse.jobdsl.dsl.helpers.toplevel
import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.AbstractHelper

class TopLevelHelper extends AbstractHelper {

    TopLevelHelper(List<WithXmlAction> withXmlActions, JobType jobType) {
        super(withXmlActions, jobType)
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

    def environmentVariables(Map<Object, Object> vars, Closure envClosure = null) {
        EnvironmentVariableContext envContext = new EnvironmentVariableContext()
        if (vars) {
            envContext.envs(vars)
        }
        AbstractContextHelper.executeInContext(envClosure, envContext)

        execute {
            it / 'properties' / 'EnvInjectJobProperty' {
                info {
                    propertiesContent(envContext.props.join('\n'))
                    if (envContext.groovyScript) {
                        groovyScriptContent(envContext.groovyScript)
                    }
                    loadFilesFromMaster(false)
                }
                on(true)
                keepJenkinsSystemVariables(true)
                keepBuildVariables(true)
                contributors()
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
     <properties>
         <hudson.plugins.buildblocker.BuildBlockerProperty>
             <useBuildBlocker>true</useBuildBlocker>  <!-- Always true -->
             <blockingJobs>JobA</blockingJobs>
         </hudson.plugins.buildblocker.BuildBlockerProperty>
     </properties>
     */
    def blockOn(Iterable<String> projectNames) {
        blockOn(projectNames.join('\n'))
    }

    /**
     * Block build if certain jobs are running.
     * @param projectName Can be regular expressions. Newline delimited.
     * @return
     */
    def blockOn(String projectName) {
        execute {
            it / 'properties' / 'hudson.plugins.buildblocker.BuildBlockerProperty' {
                useBuildBlocker 'true'
                blockingJobs projectName
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

    /**
     * Adds a quiet period to the project.
     *
     * @param seconds number of seconds to wait
     */
    def quietPeriod(int seconds = 5) {
        execute {
            def node = methodMissing('quietPeriod', seconds)
            it / node
        }
    }

    /**
     * Sets the number of times the SCM checkout is retried on errors.
     *
     * @param times number of attempts
     */
    def checkoutRetryCount(int times = 3) {
        execute {
            def node = methodMissing('scmCheckoutRetryCount', times)
            it / node
        }
    }

    /**
     * Sets a display name for the project.
     *
     * @param displayName name to display
     */
    def displayName(String displayName) {
        def name = Preconditions.checkNotNull(displayName, 'Display name must not be null.')
        execute {
            def node = methodMissing('displayName', name)
            it / node
        }

    }

    /**
     * Configures a custom workspace for the project.
     *
     * @param workspacePath workspace path to use
     */
    def customWorkspace(String workspacePath) {
        def workspace = Preconditions.checkNotNull(workspacePath,"Workspace path must not be null")
        execute {
            def node = methodMissing('customWorkspace', workspace)
            it / node
        }

    }

    /**
     * Configures the job to block when upstream projects are building.
     *
     * @return
     */
    def blockOnUpstreamProjects() {
        execute {
            it / blockBuildWhenUpstreamBuilding(true)
        }
    }

    /**
     * Configures the job to block when downstream projects are building.
     * @return
     */
    def blockOnDownstreamProjects() {
        execute {
            it / blockBuildWhenDownstreamBuilding(true)
        }
    }

    /**
     * Configures the keep Dependencies Flag which can be set in the Fingerprinting action
     *
     * <keepDependencies>true</keepDependencies>
     */
    def keepDependencies(boolean keep = true) {
        execute {
            def node = methodMissing('keepDependencies', keep)
            it / node
        }
    }
}


