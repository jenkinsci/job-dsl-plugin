package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class MattermostPublisherContext implements Context {
    String endpoint
    String room
    String icon
    String customMessage

    boolean startNotification
    boolean notifySuccess
    boolean notifyAborted
    boolean notifyNotBuilt
    boolean notifyUnstable
    boolean notifyFailure
    boolean notifyBackToNormal
    boolean notifyRepeatedFailure
    boolean includeTestSummary
    boolean showCommitList
    boolean includeCustomMessage

    /**
     * Sends a notification when the build starts. Defaults to {@code false}.
     */
    void startNotification(boolean startNotification = true) {
        this.startNotification = startNotification
    }

    /**
     * Sends a notification when the build is successful. Defaults to {@code false}.
     */
    void notifySuccess(boolean notifySuccess = true) {
        this.notifySuccess = notifySuccess
    }

    /**
     * Sends a notification when the build is aborted. Defaults to {@code false}.
     */
    void notifyAborted(boolean notifyAborted = true) {
        this.notifyAborted = notifyAborted
    }

    /**
     * Sends a notification when the build is not build. Defaults to {@code false}.
     */
    void notifyNotBuilt(boolean notifyNotBuilt = true) {
        this.notifyNotBuilt = notifyNotBuilt
    }

    /**
     * Sends a notification when the build is unstable. Defaults to {@code false}.
     */
    void notifyUnstable(boolean notifyUnstable = true) {
        this.notifyUnstable = notifyUnstable
    }

    /**
     * Sends a notification when the build is failed. Defaults to {@code false}.
     */
    void notifyFailure(boolean notifyFailure = true) {
        this.notifyFailure = notifyFailure
    }

    /**
     * Sends a notification when the build is back to normal. Defaults to {@code false}.
     */
    void notifyBackToNormal(boolean notifyBackToNormal = true) {
        this.notifyBackToNormal = notifyBackToNormal
    }

    /**
     * Sends a notification when the build repeats to fail. Defaults to {@code false}.
     */
    void notifyRepeatedFailure(boolean notifyRepeatedFailure = true) {
        this.notifyRepeatedFailure = notifyRepeatedFailure
    }

    /**
     * Include the test summary in the build message. Defaults to {@code false}.
     */
    void includeTestSummary(boolean includeTestSummary = true) {
        this.includeTestSummary = includeTestSummary
    }

    /**
     * Include the commit list with titles and authors in the build message. Defaults to {@code false}.
     */
    void showCommitList(boolean showCommitList = true) {
        this.showCommitList = showCommitList
    }

    /**
     * Include a custom message in the build notification. Defaults to {@code false}.
     */
    void includeCustomMessage(boolean includeCustomMessage = true) {
        this.includeCustomMessage = includeCustomMessage
    }

    /**
     * Configures your Mattermost incoming webhook url.
     */
    void endpoint(String endpoint) {
        this.endpoint = endpoint
    }

    /**
     * Configures the channel names to which notifications should be sent.
     */
    void room(String room) {
        this.room = room
    }

    /**
     * Configures the URL to use as avatar for the message.
     */
    void icon(String icon) {
        this.icon = icon
    }

    /**
     * Configures custom message that will be included with the notifications displayed.
     */
    void customMessage(String customMessage) {
        this.customMessage = customMessage
    }
}
