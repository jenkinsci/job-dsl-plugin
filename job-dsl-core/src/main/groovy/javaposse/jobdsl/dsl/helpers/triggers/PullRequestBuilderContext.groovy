package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.helpers.Context

class PullRequestBuilderContext implements Context {

    List admins = []
    String whiteList = ''
    List whiteListedOrgs = []
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

    def whiteList(String whiteList) {
        this.whiteList = whiteList
    }

    def whiteListedOrg(String whiteListedOrg) {
        whiteListedOrgs << whiteListedOrg
    }

    def whiteListedOrgs(Iterable<String> addOrgs) {
        addOrgs.each {
            whiteListedOrg(it)
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
