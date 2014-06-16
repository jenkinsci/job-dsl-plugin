package javaposse.jobdsl.dsl.helpers.toplevel

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.AbstractHelper

import static javaposse.jobdsl.dsl.helpers.AbstractContextHelper.executeInContext

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
        Preconditions.checkState(!labelAlreadyAdded, 'Label can only be appplied once')
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
                envContext.addInfoToBuilder(delegate)
                on(true)
                keepJenkinsSystemVariables(envContext.keepSystemVariables)
                keepBuildVariables(envContext.keepBuildVariables)
                contributors()
            }
        }
    }

    /**
     * <pre>
     * {@code
     * <project>
     *     <properties>
     *         <hudson.plugins.throttleconcurrents.ThrottleJobProperty>
     *             <maxConcurrentPerNode>0</maxConcurrentPerNode>
     *             <maxConcurrentTotal>0</maxConcurrentTotal>
     *             <categories>
     *                 <string>CDH5-repo-update</string>
     *             </categories>
     *             <throttleEnabled>true</throttleEnabled>
     *             <throttleOption>category</throttleOption>
     *         </hudson.plugins.throttleconcurrents.ThrottleJobProperty>
     *     <properties>
     * </project>
     * }
     * </pre>
     */
    def throttleConcurrentBuilds(Closure throttleClosure) {
        ThrottleConcurrentBuildsContext throttleContext = new ThrottleConcurrentBuildsContext()
        AbstractContextHelper.executeInContext(throttleClosure, throttleContext)

        execute {
            it / 'properties' / 'hudson.plugins.throttleconcurrents.ThrottleJobProperty' {
                maxConcurrentPerNode throttleContext.maxConcurrentPerNode
                maxConcurrentTotal throttleContext.maxConcurrentTotal
                throttleEnabled throttleContext.throttleDisabled ? 'false' : 'true'
                if (throttleContext.categories.isEmpty()) {
                    throttleOption 'project'
                } else {
                    throttleOption 'category'
                }
                categories {
                    throttleContext.categories.each { c ->
                        string c
                    }
                }
            }
        }
    }

    /*
    <disabled>true</disabled>
     */

    def disabled(boolean shouldDisable = true) {
        execute {
            it / disabled(shouldDisable ? 'true' : 'false')
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
    def logRotator(int daysToKeepInt = -1, int numToKeepInt = -1,
                   int artifactDaysToKeepInt = -1, int artifactNumToKeepInt = -1) {
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
     * Priority of this job. Requires the
     * <a href="https://wiki.jenkins-ci.org/display/JENKINS/Priority+Sorter+Plugin">Priority Sorter Plugin</a>.
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
        def workspace = Preconditions.checkNotNull(workspacePath, 'Workspace path must not be null')
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

    /**
     * Configures the Notification Plugin
     *
     * <properties>
     *     <com.tikal.hudson.plugins.notification.HudsonNotificationProperty>
     *         <endpoints>
     *             <com.tikal.hudson.plugins.notification.Endpoint>
     *                 <protocol>HTTP</protocol>
     *                 <format>JSON</format>
     *                 <url />
     *                 <event>all</event>
     *                 <timeout>30000</timeout>
     *             </com.tikal.hudson.plugins.notification.Endpoint>
     *         </endpoints>
     *     </com.tikal.hudson.plugins.notification.HudsonNotificationProperty>
     * </properties>
     */
    def notification(Closure notificationClosure) {
        NotificationContext notificationContext = new NotificationContext()
        executeInContext(notificationClosure, notificationContext)

        execute {
            it / 'properties' / 'com.tikal.hudson.plugins.notification.HudsonNotificationProperty' {
                endpoints notificationContext.endpoints
            }
        }
    }

    /**
     * <properties>
     *     <hudson.plugins.batch__task.BatchTaskProperty>
     *         <tasks>
     *             <hudson.plugins.batch__task.BatchTask>
     *                 <name>Hello World</name>
     *                 <script>echo Hello World</script>
     *             </hudson.plugins.batch__task.BatchTask>
     *         </tasks>
     *     </hudson.plugins.batch__task.BatchTaskProperty>
     * </properties>
     */
    def batchTask(String name, String script) {
        execute {
            def batchTaskProperty = it / 'properties' / 'hudson.plugins.batch__task.BatchTaskProperty'
            batchTaskProperty / 'tasks' << 'hudson.plugins.batch__task.BatchTask' {
                delegate.name name
                delegate.script script
            }
        }
    }
}
