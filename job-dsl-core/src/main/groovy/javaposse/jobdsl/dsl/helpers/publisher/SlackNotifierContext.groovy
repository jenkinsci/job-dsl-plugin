package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class SlackNotifierContext implements Context {
    String authToken
    String authTokenCredentialId
    boolean botUser
    String commitInfoChoice
    String customMessage
    boolean includeCustomMessage
    boolean includeTestSummary
    boolean notifyAborted
    boolean notifyBackToNormal
    boolean notifyFailure
    boolean notifyNotBuilt
    boolean notifyRegression
    boolean notifyRepeatedFailure
    boolean notifySuccess
    boolean notifyUnstable
    String room
    String sendAs
    boolean startNotification
    String teamDomain

    /**
     * Sets the integration token to be used to send notifications to Slack.
     */
    void authToken(String authToken) {
        this.authToken = authToken
    }

    /**
     * Sets the ID for the integration token from the Credentials plugin to be used to send notifications to Slack.
     */
    void authTokenCredentialId(String authTokenCredentialId) {
        this.authTokenCredentialId = authTokenCredentialId
    }

    /**
     * Indicates the token belongs to a bot user in Slack.
     */
    void botUser(boolean botUser) {
        this.botUser = botUser
    }

    void commitInfoChoice(String commitInfoChoice) {
        this.commitInfoChoice = commitInfoChoice
    }

    /**
     * Sets a custom message that will be included in the notifications.
     */
    void customMessage(String customMessage) {
        this.customMessage = customMessage
    }

    void includeCustomMessage(boolean includeCustomMessage) {
        this.includeCustomMessage = includeCustomMessage
    }

    void includeTestSummary(boolean includeTestSummary) {
        this.includeTestSummary = includeTestSummary
    }

    void notifyAborted(boolean notifyAborted) {
        this.notifyAborted = notifyAborted
    }

    void notifyBackToNormal(boolean notifyBackToNormal) {
        this.notifyBackToNormal = notifyBackToNormal
    }

    void notifyFailure(boolean notifyFailure) {
        this.notifyFailure = notifyFailure
    }

    void notifyNotBuilt(boolean notifyNotBuilt) {
        this.notifyNotBuilt = notifyNotBuilt
    }

    void notifyRegression(boolean notifyRegression) {
        this.notifyRegression = notifyRegression
    }

    void notifyRepeatedFailure(boolean notifyRepeatedFailure) {
        this.notifyRepeatedFailure = notifyRepeatedFailure
    }

    void notifySuccess(boolean notifySuccess) {
        this.notifySuccess = notifySuccess
    }

    void notifyUnstable(boolean notifyUnstable) {
        this.notifyUnstable = notifyUnstable
    }

    /**
     * Sets the channel names to which notifications should be sent.
     */
    void room(String room) {
        this.room = room
    }

    void sendAs(String sendAs) {
        this.sendAs = sendAs
    }

    void startNotification(boolean startNotification) {
        this.startNotification = startNotification
    }

    /**
     * Sets the user's team Slack subdomain.
     */
    void teamDomain(String teamDomain) {
        this.teamDomain = teamDomain
    }
}
