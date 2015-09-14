package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import org.apache.commons.lang.RandomStringUtils

/**
 * <p>DSL Support for the weblogic-deployment-plugin.</p>
 * <p><a href="https://wiki.jenkins-ci.org/display/JENKINS/WebLogic+Deployer+Plugin">WebLogic Deployer Plugin</a></p>
 */
class WeblogicDeployerContext implements Context {

    /**
     * <p>Enumeration of available deployment stage modes.</p>
     */
    static enum WeblogicDeploymentStageModes {

        BY_DEFAULT('bydefault'),

        STAGE('stage'),

        NO_STAGE('nostage'),

        EXTERNAL_STAGE('external_stage')

        private final String stringRepresentation

        WeblogicDeploymentStageModes(String stringRepresentation) {
            this.stringRepresentation = stringRepresentation
        }

        @Override
        String toString() {
            this.stringRepresentation
        }
    }

    List<Node> taskNodes = []
    List<Node> deploymentPoliciesIdsNodes = []

    boolean mustExitOnFailure = false
    boolean forceStopOnFirstFailure = false
    boolean deployingOnlyWhenUpdates = false
    String deployedProjectsDependencies = ''

    /**
     * <p>Fails the build if the deployment fails.</p>
     *
     * @param mustExitOnFailure true, false (default)
     */
    void mustExitOnFailure(boolean mustExitOnFailure = true) {
        this.mustExitOnFailure = mustExitOnFailure
    }

    /**
     * <p>Stop the job on first deployment failure. No other defined
     * deployment task of this job will be executed.</p>
     * @param forceStopOnFirstFailure true, false (default)
     */
    void forceStopOnFirstFailure(boolean forceStopOnFirstFailure = true) {
        this.forceStopOnFirstFailure = forceStopOnFirstFailure
    }

    /**
     * <p>Deploy only if the build was triggered by a parameterized
     * cause AND the SCM detects changes.</p>
     * @param deployingOnlyWhenUpdates true, false (default)
     */
    void deployingOnlyWhenUpdates(boolean deployingOnlyWhenUpdates = true) {
        this.deployingOnlyWhenUpdates = deployingOnlyWhenUpdates
    }

    /**
     * <p>(experimental plugin feature) Defines a dependency to other deployment jobs.</p>
     * @param deployedProjectsDependencies job name of an other deployment job
     */
    void deployedProjectsDependencies(String deployedProjectsDependencies) {
        this.deployedProjectsDependencies = deployedProjectsDependencies
    }

    void deploymentPolicies(Closure deploymentPoliciesClosure = null) {

        WeblogicDeployerPolicyContext context = new WeblogicDeployerPolicyContext()
        ContextHelper.executeInContext(deploymentPoliciesClosure, context)

        NodeBuilder nodeBuilder = NodeBuilder.newInstance()

        context.deploymentPolicies.each {
            policy -> deploymentPoliciesIdsNodes << nodeBuilder.createNode('string', policy)
        }
    }

    /**
     * <p>Configures a Weblogic deployment task using the weblogic-deployer-plugin</p>
     * <p>These are the default values, which are used if they are not overridden by closure.
     * All other properties must be set via closure for each task definition, as there are no default values.</p>
     *
     * <pre>
     * {@code
     * <org.jenkinsci.plugins.deploy.weblogic.data.DeploymentTask>
     *     <id>_generated_</id>
     *
     *     <deploymentTargets>AdminServer</deploymentTargets>
     *     <isLibrary>false</isLibrary>
     *
     *     <jdk>
     *         <!-- When leaving these tags empty, the default JDK should be used.
     *              Otherwise name and home must be set. -->
     *         <name></name>
     *         <home></home>
     *         <properties></properties>
     *     </jdk>
     *
     *     <stageMode>bydefault</stageMode>
     *     <commandLine></commandLine>
     *     <deploymentPlan></deploymentPlan>
     * </org.jenkinsci.plugins.deploy.weblogic.data.DeploymentTask>
     *}
     * </pre>
     *
     * @see <a href="https://wiki.jenkins-ci.org/display/JENKINS/WebLogic+Deployer+Plugin">WebLogic Deployer Plugin</a>
     */
    void task(Closure taskClosure) {

        WeblogicDeployerTaskContext context = new WeblogicDeployerTaskContext()
        ContextHelper.executeInContext(taskClosure, context)

        NodeBuilder nodeBuilder = NodeBuilder.newInstance()
        Node tasksNode = nodeBuilder.'org.jenkinsci.plugins.deploy.weblogic.data.DeploymentTask' {
            id RandomStringUtils.randomAlphanumeric(10)
            weblogicEnvironmentTargetedName context.weblogicEnvironmentTargetedName
            deploymentName context.deploymentName
            deploymentTargets context.deploymentTargets
            isLibrary context.isLibrary
            builtResourceRegexToDeploy context.builtResourceRegexToDeploy
            baseResourcesGeneratedDirectory context.baseResourcesGeneratedDirectory
            taskName context.taskName

            jdk {
                name context.jdkName
                home context.jdkHome
                properties context.jdkProperties
            }
            stageMode context.stageMode.toString()
            commandLine context.commandLine
            deploymentPlan context.deploymentPlan
        }

        this.taskNodes << tasksNode
    }
}
