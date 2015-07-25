package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class SlackNotificationsContext implements Context {

    boolean notifyBuildStart = false
    boolean notifyAborted = false
    boolean notifyFailure = false
    boolean notifyNotBuilt = false
    boolean notifySuccess = false
    boolean notifyUnstable = false
    boolean notifyBackToNormal = false
    boolean notifyRepeatedFailure = false
    boolean includeTestSummary = false
    boolean showCommitList = false
    String teamDomain
    String integrationToken
    String projectChannel
    String customMessage

    void notifyBuildStart(boolean notifyBuildStart) {
        this.notifyBuildStart = notifyBuildStart
    }

    void notifyAborted(boolean notifyAborted) {
        this.notifyAborted = notifyAborted
    }

    void notifyFailure(boolean notifyFailure) {
        this.notifyFailure = notifyFailure
    }

    void notifyNotBuilt(boolean notifyNotBuilt) {
        this.notifyNotBuilt = notifyNotBuilt
    }

    void notifySuccess(boolean notifySuccess) {
        this.notifySuccess = notifySuccess
    }

    void notifyUnstable(boolean notifyUnstable) {
        this.notifyUnstable = notifyUnstable
    }

    void notifyBackToNormal(boolean notifyBackToNormal) {
        this.notifyBackToNormal = notifyBackToNormal
    }

    void notifyRepeatedFailure(boolean notifyRepeatedFailure) {
        this.notifyRepeatedFailure = notifyRepeatedFailure
    }

    void includeTestSummary(boolean includeTestSummary) {
        this.includeTestSummary = includeTestSummary
    }

    void showCommitList(boolean showCommitList) {
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
