package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

/**
 * DSL Support for the weblogic-deployment-plugin's deployment policies subsection.
 *
 * https://wiki.jenkins-ci.org/display/JENKINS/WebLogic+Deployer+Plugin
 */
class WeblogicDeployerPolicyContext implements Context {

    /**
     * Enumeration of available deployment policies.
     */
    static enum WeblogicDeploymentPolicies {

        /**
         * Legacy code started this job.  No cause information is available.
         */
        LEGACY_CODE('hudson.model.Cause\\\\\$LegacyCodeCause'),

        /**
         * Started by user.
         */
        USER('hudson.model.Cause\\\\\$UserCause'),

        /**
         * Started by user.
         */
        USER_ID('hudson.model.Cause\\\\\$UserIdCause'),

        /**
         * Started by remote host.
         */
        REMOTE_HOST('hudson.model.Cause\\\\\$RemoteCause'),

        /**
         * Built after other projects are built or whenever a SNAPSHOT dependency is built.
         */
        UPSTREAM('hudson.model.Cause\\\\\$UpstreamCause'),

        /**
         * Started by deployment timer.
         */
        DEPLOYMENT_TRIGGER(
                'org.jenkinsci.plugins.deploy.weblogic.trigger.DeploymentTrigger\\\\\$DeploymentTriggerCause'),

        /**
         * Started by an SCM change.
         */
        SCM_CHANGE('hudson.triggers.SCMTrigger\\\\\$SCMTriggerCause'),

        private final String stringRepresentation

        WeblogicDeploymentPolicies(String stringRepresentation) {
            this.stringRepresentation = stringRepresentation
        }

        @Override
        String toString() {
            this.stringRepresentation
        }
    }

    /**
     * Deployment policy.
     */
    List<String> deploymentPolicies = []

    /**
     * Define a deployment policy.
     *
     * @param deploymentPolicy one of: {@link WeblogicDeploymentPolicies}.
     */
    void policy(WeblogicDeploymentPolicies deploymentPolicy) {
        this.deploymentPolicies.add(deploymentPolicy.toString())
    }
}
