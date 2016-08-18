package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.Context

class GitBranchSourceContext implements Context {
    String remote
    String credentialsId
    String includes = '*'
    String excludes
    boolean ignoreOnPushNotifications

    /**
     * Sets the Git remote repository URL.
     */
    void remote(String remote) {
        this.remote = remote
    }

    /**
     * Sets credentials for authentication with the remote repository.
     */
    void credentialsId(String credentialsId) {
        this.credentialsId = credentialsId
    }

    /**
     * Sets a pattern for branches to include.
     */
    void includes(String includes) {
        this.includes = includes
    }

    /**
     * Sets a pattern for branches to exclude.
     */
    void excludes(String excludes) {
        this.excludes = excludes
    }

    /**
     * If set, ignores push notifications. Defaults to {@code false}.
     */
    void ignoreOnPushNotifications(boolean ignoreOnPushNotifications = true) {
        this.ignoreOnPushNotifications = ignoreOnPushNotifications
    }
}
