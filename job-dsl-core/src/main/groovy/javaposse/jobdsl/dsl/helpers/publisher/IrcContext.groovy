package javaposse.jobdsl.dsl.helpers.publisher

import com.google.common.base.Preconditions
import groovy.transform.Canonical
import javaposse.jobdsl.dsl.helpers.Context

/**
 * @author Zsolt Takacs <zsolt@takacs.cc>
 */
class IrcContext implements Context {
    def channels = []

    def strategies = [all: 'ALL', failure: 'ANY_FAILURE', 'failure and fixed': 'FAILURE_AND_FIXED',
            change: 'STATECHANGE_ONLY']

    def notificationMessages = [
        'Summary + SCM changes': 'hudson.plugins.im.build_notify.DefaultBuildToChatNotifier',
        'Just summary': 'hudson.plugins.im.build_notify.SummaryOnlyBuildToChatNotifier',
        'Summary and build parameters': 'hudson.plugins.im.build_notify.BuildParametersBuildToChatNotifier',
        'Summary, SCM changes and failed tests': 'hudson.plugins.im.build_notify.PrintFailingTestsBuildToChatNotifier'
    ]

    def strategy

    def notificationMessage

    def notifyOnBuildStarts = false

    def notifyScmCommitters = false

    def notifyScmCulprits = false

    def notifyUpstreamCommitters = false

    def notifyScmFixers = false

    public IrcContext() {
        strategy = strategies[0];
        notificationMessage = notificationMessages[0];
    }


    def channel(String name, String password = '', boolean notificationOnly = false) {
        if (!name) {
            throw new RuntimeException("Channel name for irc channel is required!")
        }

        channels << new IrcPublisherChannel(
            name: name,
            password: password,
            notificationOnly: notificationOnly
        )
    }

    def channel(Map args) {
        channel(args.name, args.password, args.notificationOnly)
    }

    def strategy(String strategy) {
        Preconditions.checkArgument(
            strategies.keySet().contains(strategy), "Possible values: ${strategies.keySet().join(',')}"
        )

        this.strategy = strategy
    }

    def notificationMessage(String notificationMessage) {
        Preconditions.checkArgument(
            notificationMessages.keySet().contains(notificationMessage),
            "Possible values: ${notificationMessages.keySet().join(',')}"
        )

        this.notificationMessage = notificationMessage
    }

    def notifyScmCommitters(boolean value) {
        notifyScmCommitters = value
    }

    def notifyScmCulprits(boolean value) {
        notifyScmCulprits = value
    }

    def notifyUpstreamCommitters(boolean value) {
        notifyUpstreamCommitters = value
    }

    def notifyScmFixers(boolean value) {
        notifyScmFixers = value
    }

    @Canonical
    static class IrcPublisherChannel {
        String name
        String password
        boolean notificationOnly
    }
}
