package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class WeblogicDeployerPolicyContext implements Context {
    List<String> deploymentPolicies = []

    /**
     * Legacy code started this job.  No cause information is available.
     */
    void legacyCode() {
        this.deploymentPolicies << 'hudson.model.Cause\\\\$LegacyCodeCause'
    }

    /**
     * Started by user.
     */
    void user() {
        this.deploymentPolicies << 'hudson.model.Cause\\\\$UserCause'
    }

    /**
     * Started by user.
     */
    void userId() {
        this.deploymentPolicies << 'hudson.model.Cause\\\\$UserIdCause'
    }

    /**
     * Started by remote host.
     */
    void remoteHost() {
        this.deploymentPolicies << 'hudson.model.Cause\\\\$RemoteCause'
    }

    /**
     * Built after other projects are built or whenever a SNAPSHOT dependency is built.
     */
    void upstream() {
        this.deploymentPolicies << 'hudson.model.Cause\\\\$UpstreamCause'
    }

    /**
     * Started by deployment timer.
     */
    void deploymentTimer() {
        this.deploymentPolicies <<
                'org.jenkinsci.plugins.deploy.weblogic.trigger.DeploymentTrigger\\\\$DeploymentTriggerCause'
    }

    /**
     * Started by an SCM change.
     */
    void scmChange() {
        this.deploymentPolicies << 'hudson.triggers.SCMTrigger\\\\$SCMTriggerCause'
    }
}
