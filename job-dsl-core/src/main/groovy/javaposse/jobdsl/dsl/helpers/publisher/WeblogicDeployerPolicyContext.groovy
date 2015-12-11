package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

/**
 * DSL Support for the weblogic-deployment-plugin's deployment policies subsection.
 */
class WeblogicDeployerPolicyContext implements Context {

    /**
     * Deployment policy.
     */
    List<String> deploymentPolicies = []

    /**
     * Legacy code started this job.  No cause information is available.
     */
    void legacyCode() {
        this.deploymentPolicies.add('hudson.model.Cause\\\\\$LegacyCodeCause')
    }

    /**
     * Started by user.
     */
    void user() {
        this.deploymentPolicies.add('hudson.model.Cause\\\\\$UserCause')
    }

    /**
     * Started by user.
     */
    void userId() {
        this.deploymentPolicies.add('hudson.model.Cause\\\\\$UserIdCause')
    }

    /**
     * Started by remote host.
     */
    void remoteHost() {
        this.deploymentPolicies.add('hudson.model.Cause\\\\\$RemoteCause')
    }

    /**
     * Built after other projects are built or whenever a SNAPSHOT dependency is built.
     */
    void upstream() {
        this.deploymentPolicies.add('hudson.model.Cause\\\\\$UpstreamCause')
    }

    /**
     * Started by deployment timer.
     */
    void deploymentTimer() {
        this.deploymentPolicies.add('org.jenkinsci.plugins.deploy.weblogic.trigger.DeploymentTrigger' +
                '\\\\\$DeploymentTriggerCause')
    }

    /**
     * Started by an SCM change.
     */
    void scmChange() {
        this.deploymentPolicies.add('hudson.triggers.SCMTrigger\\\\\$SCMTriggerCause')
    }
}
