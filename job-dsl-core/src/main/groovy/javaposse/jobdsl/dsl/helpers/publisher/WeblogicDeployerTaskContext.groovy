package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

import static javaposse.jobdsl.dsl.helpers.publisher.WeblogicDeployerContext.WeblogicDeploymentStageModes

/**
 * DSL Support for the weblogic-deployment-plugin's task subsection.
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

    WeblogicDeploymentStageModes stageMode = WeblogicDeploymentStageModes.BY_DEFAULT
    String commandLine = ''
    String deploymentPlan = ''

    /**
     * Server environment to deploy to.
     */
    void weblogicEnvironmentTargetedName(String weblogicEnvironmentTargetedName) {
        this.weblogicEnvironmentTargetedName = weblogicEnvironmentTargetedName
    }

    /**
     * Name of the deployed Application.
     */
    void deploymentName(String deploymentName) {
        this.deploymentName = deploymentName
    }

    /**
     * Targets to deploy to. Comma separated String of targets
     * (e.g. AdminServer, myManagedServer, myCluster). Defaults to {@code AdminServer}
     */
    void deploymentTargets(String deploymentTargets) {
        this.deploymentTargets = deploymentTargets
    }

    /**
     * Type of deployment. Defaults to {@code false}.
     */
    void isLibrary(boolean isLibrary = true) {
        this.isLibrary = isLibrary
    }

    /**
     * Regex matching the artifact to deploy.
     */
    void builtResourceRegexToDeploy(String builtResourceRegexToDeploy) {
        this.builtResourceRegexToDeploy = builtResourceRegexToDeploy
    }

    /**
     * Base directory where to search for the deployment artifact.
     * NOTE: Can only be used in FreeStyle projects!
     */
    void baseResourcesGeneratedDirectory(String baseResourcesGeneratedDirectory) {
        this.baseResourcesGeneratedDirectory = baseResourcesGeneratedDirectory
    }

    /**
     * Task name to identify deployment task (optional).
     */
    void taskName(String taskName) {
        this.taskName = taskName
    }

    /**
     * Name of the JDK to use (optional). If not specified, the default JDK defined in plugin settings will be used.
     * Defaults to {@code <empty>}.
     */
    void jdkName(String jdkName) {
        this.jdkName = jdkName
    }

    /**
     * Path to the JDK home to use.
     * Defaults to {@code <empty>}.
     */
    void jdkHome(String jdkHome) {
        this.jdkHome = jdkHome
    }

    /**
     * Staging method to use.
     * Defaults to {@code WeblogicDeploymentStageModes.BY_DEFAULT}.
     */
    void stageMode(WeblogicDeploymentStageModes stageMode) {
        this.stageMode = stageMode
    }

    /**
     * Defines custom commands to be executed instead of the default ones (undeploy/deploy).
     * Commands to be executed separated by semicolon. Defaults to {@code ''}.
     *
     * Can contain tokens which will be replaced. See plugin website.
     */
    void commandLine(String commandLine) {
        if (this.commandLine.isEmpty()) {
            this.commandLine += commandLine
        } else {
            this.commandLine += ' ' + commandLine
        }
    }

    /**
     * Path to the deployment plan to use. Must be referenced in command line
     * (See plugin website). Defaults to {@code ''}.
     */
    void deploymentPlan(String deploymentPlan) {
        this.deploymentPlan = deploymentPlan
    }
}
