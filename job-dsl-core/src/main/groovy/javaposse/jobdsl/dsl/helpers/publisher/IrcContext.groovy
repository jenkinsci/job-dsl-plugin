package javaposse.jobdsl.dsl.helpers.publisher

import com.google.common.base.Preconditions
import groovy.transform.Canonical
import javaposse.jobdsl.dsl.helpers.Context

/**
 * @author Zsolt Takacs <zsolt@takacs.cc>
 */
class IrcContext implements Context {
    def channels = []

    def strategies = ['ALL', 'ANY_FAILURE', 'FAILURE_AND_FIXED', 'STATECHANGE_ONLY']

    def notificationMessages = ['Default',  'SummaryOnly', 'BuildParameters', 'PrintFailingTests']

    def strategy

    def notificationMessage

    def notifyOnBuildStarts = false

    def notifyScmCommitters = false

    def notifyScmCulprits = false

    def notifyUpstreamCommitters = false

    def notifyScmFixers = false

    IrcContext() {
        strategy = strategies[0]
        notificationMessage = notificationMessages[0]
    }

    def channel(String name, String password = '', boolean notificationOnly = false) {
        Preconditions.checkArgument(name != null && name.length() > 0, 'Channel name for irc channel is required!')

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
            strategies.contains(strategy), "Possible values: ${strategies.join(',')}"
        )

        this.strategy = strategy
    }

    def notificationMessage(String notificationMessage) {
        Preconditions.checkArgument(
            notificationMessages.contains(notificationMessage),
            "Possible values: ${notificationMessages.join(',')}"
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

