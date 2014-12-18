package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.Context

class LockableResourcesContext implements Context {
    String resourcesVariable
    Integer resourceNumber

    void resourcesVariable(String resourcesVariable) {
        this.resourcesVariable = resourcesVariable
    }

    void resourceNumber(int resourceNumber) {
        this.resourceNumber = resourceNumber
    }
}
