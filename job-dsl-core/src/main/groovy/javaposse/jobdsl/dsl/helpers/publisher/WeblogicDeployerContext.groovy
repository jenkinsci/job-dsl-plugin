package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import org.apache.commons.lang.RandomStringUtils

/**
 * DSL Support for the weblogic-deployment-plugin.
 */
class WeblogicDeployerContext implements Context {

    /**
     * Enumeration of available deployment stage modes.
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
     * Fails the build if the deployment fails. Defaults to {@code false}.
     */
    void mustExitOnFailure(boolean mustExitOnFailure = true) {
        this.mustExitOnFailure = mustExitOnFailure
    }

    /**
     * Stop the job on first deployment failure. No other defined deployment task of this job will be executed.
     * Defaults to {@code false}.
     */
    void forceStopOnFirstFailure(boolean forceStopOnFirstFailure = true) {
        this.forceStopOnFirstFailure = forceStopOnFirstFailure
    }

    /**
     * Deploy only if the build was triggered by a parameterized cause AND the SCM detects changes.
     * Defaults to {@code false}.
     */
    void deployingOnlyWhenUpdates(boolean deployingOnlyWhenUpdates = true) {
        this.deployingOnlyWhenUpdates = deployingOnlyWhenUpdates
    }

    /**
     * (experimental plugin feature) Defines a dependency to other deployment jobs.
     * Defaults to {@code ''}.
     */
    void deployedProjectsDependencies(String deployedProjectsDependencies) {
        this.deployedProjectsDependencies = deployedProjectsDependencies
    }

    /**
     * Deploy only when the deployment policy is fulfilled. Defaults to {@code <Empty>}
     */
    void deploymentPolicies(@DslContext(WeblogicDeployerPolicyContext) Closure deploymentPoliciesClosure = null) {

        WeblogicDeployerPolicyContext context = new WeblogicDeployerPolicyContext()
        ContextHelper.executeInContext(deploymentPoliciesClosure, context)

        NodeBuilder nodeBuilder = NodeBuilder.newInstance()

        context.deploymentPolicies.each {
            policy -> deploymentPoliciesIdsNodes << nodeBuilder.createNode('string', policy)
        }
    }

    /**
     * Configures a Weblogic deployment task using the weblogic-deployer-plugin.
     *
     * These are the default values, which are used if they are not overridden by closure.
     * All other properties must be set via closure for each task definition, as there are no default values.
     */
    void task(@DslContext(WeblogicDeployerTaskContext) Closure taskClosure) {

        WeblogicDeployerTaskContext context = new WeblogicDeployerTaskContext()
        ContextHelper.executeInContext(taskClosure, context)

        NodeBuilder nodeBuilder = NodeBuilder.newInstance()
        Node tasksNode = nodeBuilder.'org.jenkinsci.plugins.deploy.weblogic.data.DeploymentTask' {
            id(RandomStringUtils.randomAlphanumeric(10))
            weblogicEnvironmentTargetedName(context.weblogicEnvironmentTargetedName)
            deploymentName(context.deploymentName)
            deploymentTargets(context.deploymentTargets)
            isLibrary(context.isLibrary)
            builtResourceRegexToDeploy(context.builtResourceRegexToDeploy)
            baseResourcesGeneratedDirectory(context.baseResourcesGeneratedDirectory)
            taskName(context.taskName)

            jdk {
                name(context.jdkName)
                home(context.jdkHome)
            }
            stageMode(context.stageMode.toString())
            commandLine(context.commandLine)
            deploymentPlan(context.deploymentPlan)
        }

        this.taskNodes << tasksNode
    }
}
