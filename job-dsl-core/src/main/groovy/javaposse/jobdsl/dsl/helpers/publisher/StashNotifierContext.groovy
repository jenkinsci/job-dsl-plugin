package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

/**
 * DSL for https://wiki.jenkins-ci.org/display/JENKINS/StashNotifier+Plugin
 */
class StashNotifierContext implements Context {
    String stashServerBaseUrl = ''
    String stashUserName = ''
    String stashUserPassword = ''
    String commitSha1 = ''
    boolean ignoreUnverifiedSSLPeer = false
    boolean keepRepeatedBuilds = false

    void stashServerBaseUrl(String stashServerBaseUrl) {
        this.stashServerBaseUrl = stashServerBaseUrl
    }

    void stashUserName(String stashUserName) {
        this.stashUserName = stashUserName
    }

    void stashUserPassword(String stashUserPassword) {
        this.stashUserPassword = stashUserPassword
    }

    void commitSha1(String commitSha1) {
        this.commitSha1 = commitSha1
    }

    void ignoreUnverifiedSSLPeer(boolean ignoreUnverifiedSSLPeer = false) {
        this.ignoreUnverifiedSSLPeer = ignoreUnverifiedSSLPeer
    }

    void keepRepeatedBuilds(boolean keepRepeatedBuilds = true) {
        this.keepRepeatedBuilds = keepRepeatedBuilds
    }
}
