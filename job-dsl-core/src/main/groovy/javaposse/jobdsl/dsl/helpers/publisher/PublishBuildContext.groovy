package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class PublishBuildContext implements Context {
    boolean publishUnstable = true
    boolean publishFailed = true
    boolean discardOldBuilds
    int daysToKeep
    int numToKeep
    int artifactDaysToKeep
    int artifactNumToKeep

    void publishUnstable(boolean publishUnstable) {
        this.publishUnstable = publishUnstable
    }

    void publishFailed(boolean publishFailed) {
        this.publishFailed = publishFailed
    }

    void discardOldBuilds(int daysToKeep = -1, int numToKeep = -1,
                          int artifactDaysToKeep = -1, int artifactNumToKeep = -1) {
        discardOldBuilds = true
        this.daysToKeep = daysToKeep
        this.numToKeep = numToKeep
        this.artifactDaysToKeep = artifactDaysToKeep
        this.artifactNumToKeep = artifactNumToKeep
    }
}
