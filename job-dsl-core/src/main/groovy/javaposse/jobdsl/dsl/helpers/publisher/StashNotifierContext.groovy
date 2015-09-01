package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

/**
 * DSL for https://wiki.jenkins-ci.org/display/JENKINS/StashNotifier+Plugin
 */
class StashNotifierContext implements Context {
    String commitSha1 = ''
    boolean keepRepeatedBuilds = false

    /**
     * Attaches the notification to a specific commit in Stash.
     */
    void commitSha1(String commitSha1) {
        this.commitSha1 = commitSha1
    }

    /**
     * If set, results of repeated builds of the same commit will show up in Stash as a list of builds. Defaults to
     * {@code false}.
     */
    void keepRepeatedBuilds(boolean keepRepeatedBuilds = true) {
        this.keepRepeatedBuilds = keepRepeatedBuilds
    }
}
