package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

/**
 * DSL for https://wiki.jenkins-ci.org/display/JENKINS/StashNotifier+Plugin
 */
class StashNotifierContext implements Context {
    String commitSha1 = ''
    String userName = ''
    String password = ''
    String serverBaseUrl = ''

    boolean keepRepeatedBuilds = false

    void commitSha1(String commitSha1) {
        this.commitSha1 = commitSha1
    }

    void keepRepeatedBuilds(boolean keepRepeatedBuilds = true) {
        this.keepRepeatedBuilds = keepRepeatedBuilds
    }

    void userName(String userName) {
        this.userName = userName
    }

    void password(String password) {
        this.password = password
    }

    void serverBaseUrl(String serverBaseUrl) {
        this.serverBaseUrl = serverBaseUrl
    }
}
