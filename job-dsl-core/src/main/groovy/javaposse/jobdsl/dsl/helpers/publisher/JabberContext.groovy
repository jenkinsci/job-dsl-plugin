package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class JabberContext extends AbstractContext {
    private static final Set<String> VALID_STRATEGY_NAMES = [
            'ALL', 'FAILURE_AND_FIXED', 'ANY_FAILURE', 'STATECHANGE_ONLY', 'NEW_FAILURE_AND_FIXED'
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

    JabberContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Specifies when to send notifications. Must be one of {@code 'ALL'} (default), {@code 'FAILURE_AND_FIXED'},
     * {@code 'ANY_FAILURE'}, {@code 'NEW_FAILURE_AND_FIXED'} or {@code 'STATECHANGE_ONLY'}.
     */
    void strategyName(String strategyName) {
        checkArgument(
                VALID_STRATEGY_NAMES.contains(strategyName),
                "Jabber Strategy needs to be one of these values: ${VALID_STRATEGY_NAMES.join(',')}"
        )
        if (strategyName == 'NEW_FAILURE_AND_FIXED') {
            jobManagement.requireMinimumPluginVersion('instant-messaging', '1.26')
        }
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
        checkArgument(
                VALID_CHANNEL_NOTIFICATION_NAMES.contains(channelNotificationName),
                'Jabber Channel Notification name needs to be one of these values: ' +
                        VALID_CHANNEL_NOTIFICATION_NAMES.join(',')
        )

        this.channelNotificationName = channelNotificationName
    }
}
