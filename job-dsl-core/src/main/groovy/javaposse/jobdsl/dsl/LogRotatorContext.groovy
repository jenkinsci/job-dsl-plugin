package javaposse.jobdsl.dsl

class LogRotatorContext implements Context {
    int daysToKeep = -1
    int numToKeep = -1
    int artifactDaysToKeep = -1
    int artifactNumToKeep = -1

    /**
     * If specified, build records are only kept up to this number of days.
     */
    void daysToKeep(int daysToKeep) {
        this.daysToKeep = daysToKeep
    }

    /**
     * If specified, only up to this number of build records are kept.
     */
    void numToKeep(int numToKeep) {
        this.numToKeep = numToKeep
    }

    /**
     * If specified, artifacts from builds older than this number of days will be deleted, but the logs, history,
     * reports, etc for the build will be kept.
     */
    void artifactDaysToKeep(int artifactDaysToKeep) {
        this.artifactDaysToKeep = artifactDaysToKeep
    }

    /**
     * If specified, only up to this number of builds have their artifacts retained.
     */
    void artifactNumToKeep(int artifactNumToKeep) {
        this.artifactNumToKeep = artifactNumToKeep
    }
}
