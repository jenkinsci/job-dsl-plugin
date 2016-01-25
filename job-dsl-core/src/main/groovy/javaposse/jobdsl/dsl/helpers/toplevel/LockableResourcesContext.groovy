package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.Context

class LockableResourcesContext implements Context {
    String resourcesVariable
    Integer resourceNumber
    String labelName

    /**
     * Name for the Jenkins variable to store the reserved resources in.
     */
    void resourcesVariable(String resourcesVariable) {
        this.resourcesVariable = resourcesVariable
    }

    /**
     * Number of resources to request. By default all resources are requested.
     */
    void resourceNumber(int resourceNumber) {
        this.resourceNumber = resourceNumber
    }

    /**
     * Label assigned to group of lockable resources
     */
    void labelName(String labelName) {
        this.labelName = labelName
    }
}
