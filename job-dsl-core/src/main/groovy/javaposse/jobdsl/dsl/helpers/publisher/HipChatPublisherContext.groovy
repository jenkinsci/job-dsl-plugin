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

    void token(String token) {
        this.token = token
    }

    void rooms(String... rooms) {
        this.rooms.addAll(rooms)
    }

    void notifyBuildStart(boolean notifyBuildStart = true) {
        this.notifyBuildStart = notifyBuildStart
    }

    void notifySuccess(boolean notifySuccess = true) {
        this.notifySuccess = notifySuccess
    }

    void notifyAborted(boolean notifyAborted = true) {
        this.notifyAborted = notifyAborted
    }

    void notifyNotBuilt(boolean notifyNotBuilt = true) {
        this.notifyNotBuilt  = notifyNotBuilt
    }

    void notifyUnstable(boolean notifyUnstable = true) {
        this.notifyUnstable = notifyUnstable
    }

    void notifyFailure(boolean notifyFailure = true) {
        this.notifyFailure = notifyFailure
    }

    void notifyBackToNormal(boolean notifyBackToNormal = true) {
        this.notifyBackToNormal = notifyBackToNormal
    }

    void startJobMessage(String startJobMessage) {
        this.startJobMessage = startJobMessage
    }

    void completeJobMessage(String completeJobMessage) {
        this.completeJobMessage = completeJobMessage
    }
}
