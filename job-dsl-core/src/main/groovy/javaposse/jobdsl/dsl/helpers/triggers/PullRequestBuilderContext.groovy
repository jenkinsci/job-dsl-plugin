package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context

class PullRequestBuilderContext implements Context {

    List admins = []
    List userWhitelist = []
    List orgWhitelist = []
    String cron = 'H/5 * * * *'
    String triggerPhrase = ''
    boolean onlyTriggerPhrase = false
    boolean useGitHubHooks = false
    boolean permitAll = false
    boolean autoCloseFailedPullRequests = false

    void admin(String admin) {
        admins << admin
    }

    void admins(Iterable<String> admins) {
        admins.each {
            admin(it)
        }
    }

    void userWhitelist(String user) {
        userWhitelist << user
    }

    void userWhitelist(Iterable<String> users) {
        users.each {
            userWhitelist(it)
        }
    }

    void orgWhitelist(String organization) {
        orgWhitelist << organization
    }

    void orgWhitelist(Iterable<String> organizations) {
        organizations.each {
            orgWhitelist(it)
        }
    }

    void cron(String cron) {
        this.cron = cron
    }

    void triggerPhrase(String triggerPhrase) {
        this.triggerPhrase = triggerPhrase
    }

    void onlyTriggerPhrase(boolean onlyTriggerPhrase = true) {
        this.onlyTriggerPhrase = onlyTriggerPhrase
    }

    void useGitHubHooks(boolean useGitHubHooks = true) {
        this.useGitHubHooks = useGitHubHooks
    }

    void permitAll(boolean permitAll = true) {
        this.permitAll = permitAll
    }

    void autoCloseFailedPullRequests(boolean autoCloseFailedPullRequests = true) {
        this.autoCloseFailedPullRequests = autoCloseFailedPullRequests
    }

}
