package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Context

class BranchSourceContext implements Context {
    String id = ''
    String remote = ''
    String credentialsId = ''
    String includes = ''
    String excludes = ''
    boolean ignoreOnPushNotifications = false

    /**
     * Sets id.
     */
    void id(String id) {
        this.id = id
    }

    /**
     * Sets remote.
     */
    void remote(String remote) {
        this.remote = remote
    }

    /**
     * Sets credentialsId for remote.
     */
    void credentialsId(String credentialsId) {
        this.credentialsId = credentialsId
    }

    /**
     * Sets branch include spattern.
     */
    void includes(String includes) {
        this.includes = includes
    }

    /**
     * Sets branch exclude pattern.
     */
    void excludes(String excludes) {
        this.excludes = excludes
    }

    /**
     * Ignore push notificatoins.
     */
    void ignoreOnPushNotifications(boolean ignoreOnPushNotifications = true) {
        this.ignoreOnPushNotifications = ignoreOnPushNotifications
    }
}
