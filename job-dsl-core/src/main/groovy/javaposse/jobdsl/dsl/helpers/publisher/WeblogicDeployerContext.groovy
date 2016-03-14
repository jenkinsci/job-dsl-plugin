package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

import java.security.SecureRandom

class WeblogicDeployerContext implements Context {
    private final Random random = new SecureRandom()

    List<Node> taskNodes = []
    WeblogicDeployerPolicyContext weblogicDeployerPolicyContext = new WeblogicDeployerPolicyContext()

    boolean mustExitOnFailure
    boolean forceStopOnFirstFailure
    boolean deployingOnlyWhenUpdates
    String deployedProjectsDependencies

    /**
     * Fails the build if the deployment fails. Defaults to {@code false}.
     */
    void mustExitOnFailure(boolean mustExitOnFailure = true) {
        this.mustExitOnFailure = mustExitOnFailure
    }

    /**
     * Stops the job on first deployment failure. No other defined deployment task of this job will be executed.
     * Defaults to {@code false}.
     */
    void forceStopOnFirstFailure(boolean forceStopOnFirstFailure = true) {
        this.forceStopOnFirstFailure = forceStopOnFirstFailure
    }

    /**
     * Deploys only if the build was triggered by a parameterized cause and the SCM detects changes.
     * Defaults to {@code false}.
     */
    void deployingOnlyWhenUpdates(boolean deployingOnlyWhenUpdates = true) {
        this.deployingOnlyWhenUpdates = deployingOnlyWhenUpdates
    }

    /**
     * Defines a dependency to other deployment jobs.
     */
    void deployedProjectsDependencies(String deployedProjectsDependencies) {
        this.deployedProjectsDependencies = deployedProjectsDependencies
    }

    /**
     * Deploys only when the deployment policy is fulfilled.
     */
    void deploymentPolicies(@DslContext(WeblogicDeployerPolicyContext) Closure deploymentPoliciesClosure = null) {
        ContextHelper.executeInContext(deploymentPoliciesClosure, weblogicDeployerPolicyContext)
    }

    /**
     * Adds a deployment tasks. Can be called multiple times to add more tasks.
     */
    void task(@DslContext(WeblogicDeployerTaskContext) Closure taskClosure) {
        WeblogicDeployerTaskContext context = new WeblogicDeployerTaskContext()
        ContextHelper.executeInContext(taskClosure, context)

        this.taskNodes << new NodeBuilder().'org.jenkinsci.plugins.deploy.weblogic.data.DeploymentTask' {
            id(new BigInteger(50, random).toString(32))
            weblogicEnvironmentTargetedName(context.weblogicEnvironmentTargetedName ?: '')
            deploymentName(context.deploymentName ?: '')
            deploymentTargets(context.deploymentTargets ?: '')
            isLibrary(context.isLibrary)
            builtResourceRegexToDeploy(context.builtResourceRegexToDeploy ?: '')
            baseResourcesGeneratedDirectory(context.baseResourcesGeneratedDirectory ?: '')
            taskName(context.taskName ?: '')
            jdk {
                name(context.jdkName ?: '')
                home(context.jdkHome ?: '')
            }
            stageMode(context.stageMode.mode)
            commandLine(context.commandLine.join('\n'))
            deploymentPlan(context.deploymentPlan ?: '')
        }
    }

    /**
     * Enumeration of available deployment stage modes.
     */
    static enum WeblogicDeploymentStageModes {
        BY_DEFAULT('bydefault'),
        STAGE('stage'),
        NO_STAGE('nostage'),
        EXTERNAL_STAGE('external_stage')

        final String mode

        WeblogicDeploymentStageModes(String mode) {
            this.mode = mode
        }
    }
}
