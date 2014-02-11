package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.helpers.Context

class PullRequestBuilderContext implements Context {

    String spec = ''
    String adminlist = ''
    String whitelist = ''
    String orgslist = ''
    String cron = ''
    String triggerPhrase = 'ok to test'
    boolean onlyTriggerPhrase = false
    boolean useGitHubHooks = true
    boolean permitAll = true
    boolean autoCloseFailedPullRequests = false

    def spec(String spec) {
        this.spec = spec
    }

    def adminlist(String adminlist) {
        this.adminlist = adminlist
    }

    def whitelist(String whitelist) {
        this.whitelist = whitelist
    }

    def orgslist(String orgslist) {
        this.orgslist = orgslist
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