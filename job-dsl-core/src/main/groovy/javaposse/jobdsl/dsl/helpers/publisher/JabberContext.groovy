package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

class JabberContext implements Context {
    String strategyName = 'ALL' // ALL,  FAILURE_AND_FIXED, ANY_FAILURE, STATECHANGE_ONLY
    boolean notifyOnBuildStart = false
    boolean notifySuspects = false
    boolean notifyCulprits = false
    boolean notifyFixers = false
    boolean notifyUpstreamCommitters = false
    String channelNotificationName = 'Default' // Default, SummaryOnly, BuildParameters, PrintFailingTests

    void strategyName(String strategyName) {
        this.strategyName = strategyName
    }

    void notifyOnBuildStart(boolean notifyOnBuildStart) {
        this.notifyOnBuildStart = notifyOnBuildStart
    }

    void notifySuspects(boolean notifySuspects) {
        this.notifySuspects = notifySuspects
    }

    void notifyCulprits(boolean notifyCulprits) {
        this.notifyCulprits = notifyCulprits
    }

    void notifyFixers(boolean notifyFixers) {
        this.notifyFixers = notifyFixers
    }

    void notifyUpstreamCommitters(boolean notifyUpstreamCommitters) {
        this.notifyUpstreamCommitters = notifyUpstreamCommitters
    }

    void channelNotificationName(String channelNotificationName) {
        this.channelNotificationName = channelNotificationName
    }
}
