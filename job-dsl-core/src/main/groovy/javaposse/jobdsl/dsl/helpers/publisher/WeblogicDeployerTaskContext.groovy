package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

import static javaposse.jobdsl.dsl.helpers.publisher.WeblogicDeployerContext.WeblogicDeploymentStageModes

class WeblogicDeployerTaskContext implements Context {
    String weblogicEnvironmentTargetedName
    String deploymentName
    String deploymentTargets = 'AdminServer'
    boolean isLibrary
    String builtResourceRegexToDeploy
    String baseResourcesGeneratedDirectory
    String taskName
    String jdkName
    String jdkHome
    WeblogicDeploymentStageModes stageMode = WeblogicDeploymentStageModes.BY_DEFAULT
    List<String> commandLine = []
    String deploymentPlan

    /**
     * Specifies the server environment to deploy to.
     */
    void weblogicEnvironmentTargetedName(String weblogicEnvironmentTargetedName) {
        this.weblogicEnvironmentTargetedName = weblogicEnvironmentTargetedName
    }

    /**
     * Sets the name of the deployed application.
     */
    void deploymentName(String deploymentName) {
        this.deploymentName = deploymentName
    }

    /**
     * Specifies the targets to deploy to. Comma separated String of targets
     * (e.g. AdminServer, myManagedServer, myCluster). Defaults to {@code 'AdminServer'}.
     */
    void deploymentTargets(String deploymentTargets) {
        this.deploymentTargets = deploymentTargets
    }

    /**
     * Specifies the type of deployment. Defaults to {@code false}.
     */
    void isLibrary(boolean isLibrary = true) {
        this.isLibrary = isLibrary
    }

    /**
     * Sets a regex matching the artifact to deploy.
     */
    void builtResourceRegexToDeploy(String builtResourceRegexToDeploy) {
        this.builtResourceRegexToDeploy = builtResourceRegexToDeploy
    }

    /**
     * Sets the base directory where to search for the deployment artifact.
     * Can only be used in free style projects.
     */
    void baseResourcesGeneratedDirectory(String baseResourcesGeneratedDirectory) {
        this.baseResourcesGeneratedDirectory = baseResourcesGeneratedDirectory
    }

    /**
     * Specifies the task name to identify the deployment task.
     */
    void taskName(String taskName) {
        this.taskName = taskName
    }

    /**
     * Sets the name of the JDK to use. If not specified, the default JDK defined in plugin settings will be used.
     */
    void jdkName(String jdkName) {
        this.jdkName = jdkName
    }

    /**
     * Sets the path to the JDK to use.
     */
    void jdkHome(String jdkHome) {
        this.jdkHome = jdkHome
    }

    /**
     * Defines the staging method to use.
     * Defaults to {@code WeblogicDeploymentStageModes.BY_DEFAULT}.
     */
    void stageMode(WeblogicDeploymentStageModes stageMode) {
        this.stageMode = stageMode
    }

    /**
     * Defines custom commands to be executed instead of the default ones (undeploy/deploy).
     * Each command line has to be ended by a semicolon.
     */
    void commandLine(String commandLine) {
        this.commandLine << commandLine
    }

    /**
     * Sets the path to the deployment plan to use.
     */
    void deploymentPlan(String deploymentPlan) {
        this.deploymentPlan = deploymentPlan
    }
}
