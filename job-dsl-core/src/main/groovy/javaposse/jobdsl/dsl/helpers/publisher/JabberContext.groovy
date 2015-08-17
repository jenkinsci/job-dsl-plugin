package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class JabberContext implements Context {
    private static final Set<String> VALID_STRATEGY_NAMES = [
            'ALL', 'FAILURE_AND_FIXED', 'ANY_FAILURE', 'STATECHANGE_ONLY'
    ]
    private static final Set<String> VALID_CHANNEL_NOTIFICATION_NAMES = [
            'Default', 'SummaryOnly', 'BuildParameters', 'PrintFailingTests'
    ]

    String strategyName = 'ALL'
    boolean notifyOnBuildStart = false
    boolean notifySuspects = false
    boolean notifyCulprits = false
    boolean notifyFixers = false
    boolean notifyUpstreamCommitters = false
    String channelNotificationName = 'Default'

    void strategyName(String strategyName) {
        checkArgument(
                VALID_STRATEGY_NAMES.contains(strategyName),
                "Jabber Strategy needs to be one of these values: ${VALID_STRATEGY_NAMES.join(',')}"
        )
        this.strategyName = strategyName
    }

    void notifyOnBuildStart(boolean notifyOnBuildStart = true) {
        this.notifyOnBuildStart = notifyOnBuildStart
    }

    void notifySuspects(boolean notifySuspects = true) {
        this.notifySuspects = notifySuspects
    }

    void notifyCulprits(boolean notifyCulprits = true) {
        this.notifyCulprits = notifyCulprits
    }

    void notifyFixers(boolean notifyFixers = true) {
        this.notifyFixers = notifyFixers
    }

    void notifyUpstreamCommitters(boolean notifyUpstreamCommitters = true) {
        this.notifyUpstreamCommitters = notifyUpstreamCommitters
    }

    void channelNotificationName(String channelNotificationName) {
        checkArgument(
                VALID_CHANNEL_NOTIFICATION_NAMES.contains(channelNotificationName),
                'Jabber Channel Notification name needs to be one of these values: ' +
                        VALID_CHANNEL_NOTIFICATION_NAMES.join(',')
        )

        this.channelNotificationName = channelNotificationName
    }
}
