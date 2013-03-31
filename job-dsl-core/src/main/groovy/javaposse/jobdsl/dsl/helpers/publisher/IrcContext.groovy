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
        'Summary + SCM changes': 'Default',
        'Just summary': 'SummaryOnly',
        'Summary and build parameters': 'BuildParameters',
        'Summary, SCM changes and failed tests': 'PrintFailingTests'
    ]

    def strategy

    def notificationMessage

    def notifyOnBuildStarts = false

    def notifyScmCommitters = false

    def notifyScmCulprits = false

    def notifyUpstreamCommitters = false

    def notifyScmFixers = false

    public IrcContext() {
        strategy = strategies.keySet().toArray()[0];
        notificationMessage = notificationMessages.keySet().toArray()[0];
    }


    def channel(String name, String password = '', boolean notificationOnly = false) {
        Preconditions.checkArgument(name != null && name.length() > 0, "Channel name for irc channel is required!")

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

    def notifyScmCommitters(boolean value = true) {
        notifyScmCommitters = value
    }

    def notifyScmCulprits(boolean value = true) {
        notifyScmCulprits = value
    }

    def notifyUpstreamCommitters(boolean value = true) {
        notifyUpstreamCommitters = value
    }

    def notifyScmFixers(boolean value = true) {
        notifyScmFixers = value
    }

    @Canonical
    static class IrcPublisherChannel {
        String name
        String password
        boolean notificationOnly
    }
}
