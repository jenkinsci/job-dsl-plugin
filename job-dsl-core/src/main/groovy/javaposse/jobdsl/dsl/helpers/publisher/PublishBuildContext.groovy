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

    /**
     * If set, publishes unstable builds. Defaults to {@code true}.
     */
    void publishUnstable(boolean publishUnstable) {
        this.publishUnstable = publishUnstable
    }

    /**
     * If set, publishes failed builds. Defaults to {@code true}.
     */
    void publishFailed(boolean publishFailed) {
        this.publishFailed = publishFailed
    }

    /**
     * Manages how long to keep records of the builds.
     */
    void discardOldBuilds(int daysToKeep = -1, int numToKeep = -1,
                          int artifactDaysToKeep = -1, int artifactNumToKeep = -1) {
        discardOldBuilds = true
        this.daysToKeep = daysToKeep
        this.numToKeep = numToKeep
        this.artifactDaysToKeep = artifactDaysToKeep
        this.artifactNumToKeep = artifactNumToKeep
    }
}
