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
    String jdkProperties = ''

    WeblogicDeploymentStageModes stageMode = WeblogicDeploymentStageModes.BY_DEFAULT
    String commandLine = ''
    String deploymentPlan = ''

    /**
     * Server environment to deploy to.
     *
     * @param weblogicEnvironmentTargetedName.
     */
    void weblogicEnvironmentTargetedName(String weblogicEnvironmentTargetedName) {
        this.weblogicEnvironmentTargetedName = weblogicEnvironmentTargetedName
    }

    /**
     * Name of the deployed Application.
     *
     * @param deploymentName application name.
     */
    void deploymentName(String deploymentName) {
        this.deploymentName = deploymentName
    }

    /**
     * Targets to deploy to.
     *
     * @param deploymentTargets Comma separated String of targets
     * (e.g. AdminServer, myManagedServer, myCluster).
     */
    void deploymentTargets(String deploymentTargets) {
        this.deploymentTargets = deploymentTargets
    }

    /**
     * Type of deployment. Defaults to {@code false}.
     *
     * @param isLibrary true, false (default).
     */
    void isLibrary(boolean isLibrary = true) {
        this.isLibrary = isLibrary
    }

    /**
     * Regex matching the artifact to deploy.
     *
     * @param builtResourceRegexToDeploy e.g. myApp\.ear
     */
    void builtResourceRegexToDeploy(String builtResourceRegexToDeploy) {
        this.builtResourceRegexToDeploy = builtResourceRegexToDeploy
    }

    /**
     * Base directory where to search for the deployment artifact.
     * NOTE: Can only be used in FreeStyle projects!
     *
     * @param baseResourcesGeneratedDirectory
     */
    void baseResourcesGeneratedDirectory(String baseResourcesGeneratedDirectory) {
        this.baseResourcesGeneratedDirectory = baseResourcesGeneratedDirectory
    }

    /**
     * Task name to identify deployment task (optional).
     *
     * @param taskName optional task name.
     */
    void taskName(String taskName) {
        this.taskName = taskName
    }

    /**
     * Name of the JDK to use (optional). If not specified, the default JDK defined in plugin settings will be used.
     * Defaults to {@code ''}.
     *
     * @param jdkName Name of the JDK to use.
     */
    void jdkName(String jdkName) {
        this.jdkName = jdkName
    }

    /**
     * Path to the JDK home to use.
     * Defaults to {@code ''}.
     *
     * @param jdkHome JDK home.
     */
    void jdkHome(String jdkHome) {
        this.jdkHome = jdkHome
    }

    /**
     * JDK Properties to use.
     * Defaults to {@code ''}.
     *
     * @param jdkProperties JDK Properties.
     */
    void jdkProperties(String jdkProperties) {
        this.jdkProperties = jdkProperties
    }

    /**
     * Staging method to use.
     * Defaults to {@code WeblogicDeploymentStageModes.BY_DEFAULT}.
     *
     * @param stageMode one of {@link WeblogicDeploymentStageModes}.
     */
    void stageMode(WeblogicDeploymentStageModes stageMode) {
        this.stageMode = stageMode
    }

    /**
     * Defines custom commands to be executed instead of the default ones (undeploy/deploy).
     * Defaults to {@code ''}.
     *
     * Can contain tokens which will be replaced. See plugin website.
     *
     * @param commandLine commands to be executed separated by semicolon.
     */
    void commandLine(String commandLine) {
        this.commandLine = commandLine
    }

    /**
     * Path to the deployment plan to use. Must be referenced in command line
     * (See plugin website). Defaults to {@code ''}.
     *
     * @param deploymentPlan deployment plan to use.
     */
    void deploymentPlan(String deploymentPlan) {
        this.deploymentPlan = deploymentPlan
    }
}
