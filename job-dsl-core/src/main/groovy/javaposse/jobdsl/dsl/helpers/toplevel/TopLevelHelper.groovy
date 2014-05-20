package javaposse.jobdsl.dsl.helpers.toplevel
import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.AbstractHelper
import javaposse.jobdsl.dsl.helpers.properties.PropertiesContextHelper

class TopLevelHelper extends AbstractHelper {
    JobManagement jobManagement
    PropertiesContextHelper propertiesContextHelper

    TopLevelHelper(List<WithXmlAction> withXmlActions, JobType jobType,
                   JobManagement jobManagement, PropertiesContextHelper propertiesContextHelper) {
        super(withXmlActions, jobType)
        this.jobManagement = jobManagement
        this.propertiesContextHelper = propertiesContextHelper
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

    def environmentVariables(Closure envClosure) {
        jobManagement.logDeprecationWarning()
        propertiesContextHelper.properties {
            environmentVariables(envClosure)
        }
    }

    def environmentVariables(Map<Object, Object> vars, Closure envClosure = null) {
        jobManagement.logDeprecationWarning()
        propertiesContextHelper.properties {
            environmentVariables(vars, envClosure)
        }
    }

    def throttleConcurrentBuilds(Closure throttleClosure) {
        jobManagement.logDeprecationWarning()
        propertiesContextHelper.properties {
            throttleConcurrentBuilds(throttleClosure)
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

    def blockOn(Iterable<String> projectNames) {
        jobManagement.logDeprecationWarning()
        propertiesContextHelper.properties {
            blockOn(projectNames)
        }
    }

    def blockOn(String projectName) {
        jobManagement.logDeprecationWarning()
        propertiesContextHelper.properties {
            blockOn(projectName)
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

    def priority(int value) {
        propertiesContextHelper.properties {
            priority(value)
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

    /**
     * Configures the 'Execute concurrent builds if necessary' flag
     *
     * <concurrentBuild>true</concurrentBuild>
     */
    def concurrentBuild(boolean allowConcurrentBuild = true) {
        execute {
            it / concurrentBuild(allowConcurrentBuild ? 'true' : 'false')
        }
    }
}


