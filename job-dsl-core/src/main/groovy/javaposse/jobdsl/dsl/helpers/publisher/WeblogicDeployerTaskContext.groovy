package javaposse.jobdsl.dsl.helpers.publisher;

import javaposse.jobdsl.dsl.helpers.Context;

import static javaposse.jobdsl.dsl.helpers.publisher.WeblogicDeployerContext.WeblogicDeploymentStageModes

/**
 * <p>DSL Support for the weblogic-deployment-plugin's task subsection.</p>
 * <p><a href="https://wiki.jenkins-ci.org/display/JENKINS/WebLogic+Deployer+Plugin">WebLogic Deployer Plugin</a></p>
 */
class WeblogicDeployerTaskContext implements Context {

    String weblogicEnvironmentTargetedName
    String deploymentName
    String deploymentTargets = 'AdminServer'
    boolean isLibrary = false
    String builtResourceRegexToDeploy
    String baseResourcesGeneratedDirectory
    String taskName

    String jdkName = ''
    String jdkHome = ''
    String jdkProperties = ''

    WeblogicDeploymentStageModes stageMode = WeblogicDeploymentStageModes.BY_DEFAULT
    String commandLine = ''
    String deploymentPlan = ''

    /**
     * <p>Server environment to deploy to</p>
     * @param weblogicEnvironmentTargetedName
     */
    def weblogicEnvironmentTargetedName(String weblogicEnvironmentTargetedName) {
        this.weblogicEnvironmentTargetedName = weblogicEnvironmentTargetedName
    }

    /**
     * <p>Name of the deployed Application</p>
     * @param deploymentName application name
     */
    def deploymentName(String deploymentName) {
        this.deploymentName = deploymentName
    }

    /**
     * <p>Targets to deploy to</p>
     * @param deploymentTargets Comma separated String of targets
     * (e.g. AdminServer, myManagedServer, myCluster)
     */
    def deploymentTargets(String deploymentTargets) {
        this.deploymentTargets = deploymentTargets
    }

    /**
     * <p>Type of deployment</p>
     *
     * @param isLibrary true, false (default)
     *  <ul>
     *    <li><strong>true</strong>: deploy as shared library</li>
     *    <li><strong>false</strong>: deploy as application</li>
     * </ul>
     */
    def isLibrary(boolean isLibrary = true) {
        this.isLibrary = isLibrary
    }

    /**
     * <p>Regex matching the artifact to deploy</p>
     * @param builtResourceRegexToDeploy e.g. myApp\.ear
     */
    def builtResourceRegexToDeploy(String builtResourceRegexToDeploy) {
        this.builtResourceRegexToDeploy = builtResourceRegexToDeploy
    }

    /**
     * <p>Base directory where to search for the deployment artifact.</p>
     * <p><strong>NOTE: Can only be used in FreeStyle projects!</strong></p>
     * @param baseResourcesGeneratedDirectory
     */
    def baseResourcesGeneratedDirectory(String baseResourcesGeneratedDirectory) {
        this.baseResourcesGeneratedDirectory = baseResourcesGeneratedDirectory
    }

    /**
     * <p>Task name to identify deployment task (optional)</p>
     * @param taskName optional task name
     */
    def taskName(String taskName) {
        this.taskName = taskName
    }

    /**
     * <p>Name of the JDK to use (optional). If not specified,
     * the default JDK defined in plugin settings will be used.</p>
     * @param jdkName Name of the JDK to use
     */
    def jdkName(String jdkName) {
        this.jdkName = jdkName
    }

    /**
     * <p>Path to the JDK home to use</p>
     * @param jdkHome JDK home
     */
    def jdkHome(String jdkHome) {
        this.jdkHome = jdkHome
    }

    def jdkProperties(String jdkProperties) {
        this.jdkProperties = jdkProperties
    }

    /**
     * <p>Staging method to use</p>
     * @param stageMode one of {@link WeblogicDeploymentStageModes}
     */
    def stageMode(WeblogicDeploymentStageModes stageMode) {
        this.stageMode = stageMode
    }

    /**
     * <p>Defines custom commands to be executed instead of the default ones (undeploy/deploy).
     * Can contain tokens which will be replaced:
     * <a href="https://wiki.jenkins-ci.org/display/JENKINS/WebLogic+Deployer+Plugin">WebLogic Deployer Plugin</a></p>
     * @param commandLine commands to be executed separated by semicolon
     */
    def commandLine(String commandLine) {
        this.commandLine = commandLine
    }

    def deploymentPlan(String deploymentPlan) {
        this.deploymentPlan = deploymentPlan
    }
}