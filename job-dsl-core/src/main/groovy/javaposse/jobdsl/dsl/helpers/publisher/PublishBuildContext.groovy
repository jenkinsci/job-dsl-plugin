package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class PublishBuildContext implements Context {
    boolean discardOldBuilds
    int daysToKeep
    int numToKeep

    void discardOldBuilds(int daysToKeep, int numToKeep) {
        discardOldBuilds = true
        this.daysToKeep = daysToKeep
        this.numToKeep = numToKeep
    }
}
