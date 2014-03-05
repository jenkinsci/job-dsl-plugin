package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.helpers.Context

class PullRequestBuilderContext implements Context {

    List admins = []
    List whitelisted = []
    List whitelistedOrgs = []
    String cron = ''
    String triggerPhrase = ''
    boolean onlyTriggerPhrase = false
    boolean useGitHubHooks = true
    boolean permitAll = true
    boolean autoCloseFailedPullRequests = false

    def admin(String admin) {
        admins << admin
    }

    def admins(Iterable<String> addAdmins) {
        addAdmins.each {
            admin(it)
        }
    }

    def whitelist(String whitelist) {
        whitelisted << whitelist
    }

    def whitelisted(Iterable<String> addWhitelist) {
        addWhitelist.each {
            whitelist(it)
        }
    }

    def whitelistedOrg(String whitelistedOrg) {
        whitelistedOrgs << whitelistedOrg
    }

    def whitelistedOrgs(Iterable<String> addOrgs) {
        addOrgs.each {
            whitelistedOrg(it)
        }
    }

    def cron(String cron) {
        this.cron = cron
    }

    def triggerPhrase(String triggerPhrase) {
        this.triggerPhrase = triggerPhrase
    }


    def onlyTriggerPhrase(boolean onlyTriggerPhrase) {
        this.onlyTriggerPhrase = onlyTriggerPhrase
    }

    def useGitHubHooks(boolean useGitHubHooks) {
        this.useGitHubHooks = useGitHubHooks
    }

    def permitAll(boolean permitAll) {
        this.permitAll = permitAll
    }

    def autoCloseFailedPullRequests(boolean autoCloseFailedPullRequests) {
        this.autoCloseFailedPullRequests = autoCloseFailedPullRequests
    }

}
