package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class VSphereDeployFromTemplateContext implements Context {
    String server
    String template
    String clone
    String cluster

    /**
     * Sets the vSphere configuration to use.
     */
    void server(String server) {
        this.server = server
    }

    /**
     * Sets the name of the vSphere template to clone and start.
     */
    void template(String template) {
        this.template = template
    }

    /**
     * Sets the name of the cloned VM.
     */
    void clone(String clone) {
        this.clone = clone
    }

    /**
     * Sets the vCenter cluster to be used by the VM.
     */
    void cluster(String cluster) {
        this.cluster = cluster
    }
}
