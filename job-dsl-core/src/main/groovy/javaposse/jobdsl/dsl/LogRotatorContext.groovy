package javaposse.jobdsl.dsl

class LogRotatorContext implements Context {
    int daysToKeep = -1
    int numToKeep = -1
    int artifactDaysToKeep = -1
    int artifactNumToKeep = -1

    void daysToKeep(int daysToKeep) {
        this.daysToKeep = daysToKeep
    }

    void numToKeep(int numToKeep) {
        this.numToKeep = numToKeep
    }

    void artifactDaysToKeep(int artifactDaysToKeep) {
        this.artifactDaysToKeep = artifactDaysToKeep
    }

    void artifactNumToKeep(int artifactNumToKeep) {
        this.artifactNumToKeep = artifactNumToKeep
    }
}
