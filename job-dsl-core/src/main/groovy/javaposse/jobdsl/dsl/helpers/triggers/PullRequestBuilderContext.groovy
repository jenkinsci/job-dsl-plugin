package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.helpers.Context

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

    def admin(String admin) {
        admins << admin
    }

    def admins(Iterable<String> admins) {
        admins.each {
            admin(it)
        }
    }

    def userWhitelist(String user) {
        userWhitelist << user
    }

    def userWhitelist(Iterable<String> users) {
        users.each {
            userWhitelist(it)
        }
    }

    def orgWhitelist(String organization) {
        orgWhitelist << organization
    }

    def orgWhitelist(Iterable<String> organizations) {
        organizations.each {
            orgWhitelist(it)
        }
    }

    def cron(String cron) {
        this.cron = cron
    }

    def triggerPhrase(String triggerPhrase) {
        this.triggerPhrase = triggerPhrase
    }


    def onlyTriggerPhrase(boolean onlyTriggerPhrase = true) {
        this.onlyTriggerPhrase = onlyTriggerPhrase
    }

    def useGitHubHooks(boolean useGitHubHooks = true) {
        this.useGitHubHooks = useGitHubHooks
    }

    def permitAll(boolean permitAll = true) {
        this.permitAll = permitAll
    }

    def autoCloseFailedPullRequests(boolean autoCloseFailedPullRequests = true) {
        this.autoCloseFailedPullRequests = autoCloseFailedPullRequests
    }

}
