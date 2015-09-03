package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class JabberContext implements Context {
    String strategyName = 'ALL' // ALL,  FAILURE_AND_FIXED, ANY_FAILURE, STATECHANGE_ONLY
    boolean notifyOnBuildStart = false
    boolean notifySuspects = false
    boolean notifyCulprits = false
    boolean notifyFixers = false
    boolean notifyUpstreamCommitters = false
    String channelNotificationName = 'Default' // Default, SummaryOnly, BuildParameters, PrintFailingTests

    /**
     * Specifies when to send notifications. Must be one of {@code 'ALL'} (default), {@code 'FAILURE_AND_FIXED'},
     * {@code 'ANY_FAILURE'} or {@code 'STATECHANGE_ONLY'}.
     */
    void strategyName(String strategyName) {
        this.strategyName = strategyName
    }

    /**
     * Sends a notification when the build starts. Defaults to {@code false}.
     */
    void notifyOnBuildStart(boolean notifyOnBuildStart = true) {
        this.notifyOnBuildStart = notifyOnBuildStart
    }

    /**
     * Sends notifications to the users that are suspected of having broken this build. Defaults to {@code false}.
     */
    void notifySuspects(boolean notifySuspects = true) {
        this.notifySuspects = notifySuspects
    }

    /**
     * Sends notifications to users from previous unstable/failed builds. Defaults to {@code false}.
     */
    void notifyCulprits(boolean notifyCulprits = true) {
        this.notifyCulprits = notifyCulprits
    }

    /**
     * Sends notifications to the users that have fixed a broken build. Defaults to {@code false}.
     */
    void notifyFixers(boolean notifyFixers = true) {
        this.notifyFixers = notifyFixers
    }

    /**
     * Sends notifications to upstream committers if no committers were found for a broken build. Defaults to
     * {@code false}.
     */
    void notifyUpstreamCommitters(boolean notifyUpstreamCommitters = true) {
        this.notifyUpstreamCommitters = notifyUpstreamCommitters
    }

    /**
     * Specifies the message type. Must be one of {@code 'Default'} (default), {@code 'SummaryOnly'},
     * {@code 'BuildParameters'} or {@code 'PrintFailingTests'}.
     */
    void channelNotificationName(String channelNotificationName) {
        this.channelNotificationName = channelNotificationName
    }
}
