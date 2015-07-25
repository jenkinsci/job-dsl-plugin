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

    void notifyBuildStart(boolean notifyBuildStart = true) {
        this.notifyBuildStart = notifyBuildStart
    }

    void notifyAborted(boolean notifyAborted = true) {
        this.notifyAborted = notifyAborted
    }

    void notifyFailure(boolean notifyFailure = true) {
        this.notifyFailure = notifyFailure
    }

    void notifyNotBuilt(boolean notifyNotBuilt = true) {
        this.notifyNotBuilt = notifyNotBuilt
    }

    void notifySuccess(boolean notifySuccess = true) {
        this.notifySuccess = notifySuccess
    }

    void notifyUnstable(boolean notifyUnstable = true) {
        this.notifyUnstable = notifyUnstable
    }

    void notifyBackToNormal(boolean notifyBackToNormal = true) {
        this.notifyBackToNormal = notifyBackToNormal
    }

    void notifyRepeatedFailure(boolean notifyRepeatedFailure = true) {
        this.notifyRepeatedFailure = notifyRepeatedFailure
    }

    void includeTestSummary(boolean includeTestSummary = true) {
        this.includeTestSummary = includeTestSummary
    }

    void showCommitList(boolean showCommitList = true) {
        this.showCommitList = showCommitList
    }

    void teamDomain(String teamDomain) {
        this.teamDomain = teamDomain
    }

    void integrationToken(String integrationToken) {
        this.integrationToken = integrationToken
    }

    void projectChannel(String projectChannel) {
        this.projectChannel = projectChannel
    }

    void customMessage(String customMessage) {
        this.customMessage = customMessage
    }
}
