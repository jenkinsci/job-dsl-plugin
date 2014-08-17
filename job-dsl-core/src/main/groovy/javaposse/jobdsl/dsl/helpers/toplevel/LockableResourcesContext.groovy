package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.helpers.Context

class LockableResourcesContext implements Context {
    String resourcesVariable
    Integer resourceNumber

    def resourcesVariable(String resourcesVariable) {
        this.resourcesVariable = resourcesVariable
    }

    def resourceNumber(int resourceNumber) {
        this.resourceNumber = resourceNumber
    }
}
