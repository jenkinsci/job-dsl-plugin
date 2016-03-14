package javaposse.jobdsl.dsl.helpers.publisher

import groovy.transform.Canonical
import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

class IrcContext extends AbstractContext {
    List<IrcPublisherChannel> channels = []

    List<String> strategies = ['ALL', 'ANY_FAILURE', 'FAILURE_AND_FIXED', 'NEW_FAILURE_AND_FIXED', 'STATECHANGE_ONLY']

    List<String> notificationMessages = ['Default', 'SummaryOnly', 'BuildParameters', 'PrintFailingTests']

    String strategy

    String notificationMessage

    boolean notifyOnBuildStarts = false

    boolean notifyScmCommitters = false

    boolean notifyScmCulprits = false

    boolean notifyUpstreamCommitters = false

    boolean notifyScmFixers = false

    IrcContext(JobManagement jobManagement) {
        super(jobManagement)
        strategy = strategies[0]
        notificationMessage = notificationMessages[0]
    }

    /**
     * Adds a channel to notify. Can be called multiple times to add notify more channels.
     *
     * For security reasons, do not use a hard-coded password. See
     * <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/Handling-Credentials">Handling Credentials</a> for
     * details about handling credentials in DSL scripts.
     */
    void channel(String name, String password = '', boolean notificationOnly = false) {
        checkNotNullOrEmpty(name, 'Channel name for irc channel is required!')

        channels << new IrcPublisherChannel(
            name: name,
            password: password,
            notificationOnly: notificationOnly
        )
    }

    /**
     * Adds a channel to notify. Can be called multiple times to add notify more channels.
     *
     * The map can contain one or more of the following keys: {@code name}, {@code password} and
     * {@code notificationOnly}.
     *
     * For security reasons, do not use a hard-coded password. See
     * <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/Handling-Credentials">Handling Credentials</a> for
     * details about handling credentials in DSL scripts.
     */
    void channel(Map args) {
        channel(args.name, args.password, args.notificationOnly)
    }

    /**
     * Specifies when to send notifications. Must be one of {@code 'ALL'}, {@code 'FAILURE_AND_FIXED'},
     * {@code 'ANY_FAILURE'}, {@code 'NEW_FAILURE_AND_FIXED'} or {@code 'STATECHANGE_ONLY'}.
     */
    void strategy(String strategy) {
        checkArgument(strategies.contains(strategy), "Possible values: ${strategies.join(',')}")
        if (strategy == 'NEW_FAILURE_AND_FIXED') {
            jobManagement.requireMinimumPluginVersion('instant-messaging', '1.26')
        }

        this.strategy = strategy
    }

    /**
     * Specifies the message type. Must be one of {@code 'Default'}, {@code 'SummaryOnly'},
     * {@code 'BuildParameters'} or {@code 'PrintFailingTests'}.
     */
    void notificationMessage(String notificationMessage) {
        checkArgument(
            notificationMessages.contains(notificationMessage),
            "Possible values: ${notificationMessages.join(',')}"
        )

        this.notificationMessage = notificationMessage
    }

    /**
     * Sends notifications to the users that are suspected of having broken this build. Defaults to {@code false}.
     */
    void notifyScmCommitters(boolean value = true) {
        notifyScmCommitters = value
    }

    /**
     * Sends notifications to users from previous unstable/failed builds. Defaults to {@code false}.
     */
    void notifyScmCulprits(boolean value = true) {
        notifyScmCulprits = value
    }

    /**
     * Sends notifications to upstream committers if no committers were found for a broken build. Defaults to
     * {@code false}.
     */
    void notifyUpstreamCommitters(boolean value = true) {
        notifyUpstreamCommitters = value
    }

    /**
     * Sends notifications to the users that have fixed a broken build. Defaults to {@code false}.
     */
    void notifyScmFixers(boolean value = true) {
        notifyScmFixers = value
    }

    @Canonical
    static class IrcPublisherChannel {
        String name
        String password
        boolean notificationOnly
    }
}

