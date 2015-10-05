package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class HipChatPublisherContext implements Context {
    String token
    List<String> rooms = []
    boolean notifyBuildStart
    boolean notifySuccess
    boolean notifyAborted
    boolean notifyNotBuilt
    boolean notifyUnstable
    boolean notifyFailure
    boolean notifyBackToNormal
    String startJobMessage
    String completeJobMessage

    /**
     * Sets either a v1 admin/notification API token, or a v2 access token with send_notification scope.
     *
     * For security reasons, do not use a hard-coded token. See
     * <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/Handling-Credentials">Handling Credentials</a> for
     * details about handling credentials in DSL scripts.
     */
    void token(String token) {
        this.token = token
    }

    /**
     * Specifies the room names to which notifications should be sent.
     */
    void rooms(String... rooms) {
        this.rooms.addAll(rooms)
    }

    /**
     * Sends a notification when the build starts. Defaults to {@code false}.
     */
    void notifyBuildStart(boolean notifyBuildStart = true) {
        this.notifyBuildStart = notifyBuildStart
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
     * Sends a notification when the build is not run. Defaults to {@code false}.
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
     * Configures the message that will be displayed in the room when the build starts.
     */
    void startJobMessage(String startJobMessage) {
        this.startJobMessage = startJobMessage
    }

    /**
     * Configures the message that will be displayed in the room when the build is completed.
     */
    void completeJobMessage(String completeJobMessage) {
        this.completeJobMessage = completeJobMessage
    }
}
