package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class SlackNotificationsContext implements Context {
    boolean notifyBuildStart
    boolean notifyAborted
    boolean notifyFailure
    boolean notifyNotBuilt
    boolean notifySuccess
    boolean notifyUnstable
    boolean notifyBackToNormal
    boolean notifyRepeatedFailure
    boolean includeTestSummary
    boolean showCommitList
    String teamDomain
    String integrationToken
    String projectChannel
    String customMessage

    /**
     * Sends a notification when the build starts. Defaults to {@code false}.
     */
    void notifyBuildStart(boolean notifyBuildStart = true) {
        this.notifyBuildStart = notifyBuildStart
    }

    /**
     * Sends a notification when the build is aborted. Defaults to {@code false}.
     */
    void notifyAborted(boolean notifyAborted = true) {
        this.notifyAborted = notifyAborted
    }

    /**
     * Sends a notification when the build is failed. Defaults to {@code false}.
     */
    void notifyFailure(boolean notifyFailure = true) {
        this.notifyFailure = notifyFailure
    }

    /**
     * Sends a notification when the build is not run. Defaults to {@code false}.
     */
    void notifyNotBuilt(boolean notifyNotBuilt = true) {
        this.notifyNotBuilt = notifyNotBuilt
    }

    /**
     * Sends a notification when the build is successful. Defaults to {@code false}.
     */
    void notifySuccess(boolean notifySuccess = true) {
        this.notifySuccess = notifySuccess
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
    void notifyBackToNormal(boolean notifyBackToNormal = true) {
        this.notifyBackToNormal = notifyBackToNormal
    }

    /**
     * Sends a notification when the build failed repeatedly. Defaults to {@code false}.
     */
    void notifyRepeatedFailure(boolean notifyRepeatedFailure = true) {
        this.notifyRepeatedFailure = notifyRepeatedFailure
    }

    /**
     * If set, includes a test summary in the message. Defaults to {@code false}.
     */
    void includeTestSummary(boolean includeTestSummary = true) {
        this.includeTestSummary = includeTestSummary
    }

    /**
     * If set, includes a list of commits in the message. Defaults to {@code false}.
     */
    void showCommitList(boolean showCommitList = true) {
        this.showCommitList = showCommitList
    }

    /**
     * Sets the Slack subdomain to which notifications should be sent.
     */
    void teamDomain(String teamDomain) {
        this.teamDomain = teamDomain
    }

    /**
     * Sets the integration token to use for sending notifications.
     *
     * For security reasons, do not use a hard-coded token. See
     * <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/Handling-Credentials">Handling Credentials</a> for
     * details about handling credentials in DSL scripts.
     */
    void integrationToken(String integrationToken) {
        this.integrationToken = integrationToken
    }

    /**
     * Specifies the channel names to which notifications should be sent.
     */
    void projectChannel(String projectChannel) {
        this.projectChannel = projectChannel
    }

    /**
     * Specifies a custom message that will be included with the notifications.
     */
    void customMessage(String customMessage) {
        this.customMessage = customMessage
    }
}
