package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class PullRequestBuilderContext extends AbstractContext {
    List admins = []
    List userWhitelist = []
    List orgWhitelist = []
    String cron = 'H/5 * * * *'
    String triggerPhrase
    boolean onlyTriggerPhrase = false
    boolean useGitHubHooks = false
    boolean permitAll = false
    boolean autoCloseFailedPullRequests = false
    boolean allowMembersOfWhitelistedOrgsAsAdmin = false
    String commentFilePath
    GitHubPullRequestBuilderExtensionContext extensionContext = new GitHubPullRequestBuilderExtensionContext()

    PullRequestBuilderContext(JobManagement jobManagement) {
        super(jobManagement)
    }

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

    /**
     * @since 1.31
     */
    @RequiresPlugin(id = 'ghprb', minimumVersion = '1.14')
    void commentFilePath(String commentFilePath) {
        this.commentFilePath = commentFilePath
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

    /**
     * @since 1.35
     */
    @RequiresPlugin(id = 'ghprb', minimumVersion = '1.15-0')
    void allowMembersOfWhitelistedOrgsAsAdmin(boolean allowMembersOfWhitelistedOrgsAsAdmin = true) {
        this.allowMembersOfWhitelistedOrgsAsAdmin = allowMembersOfWhitelistedOrgsAsAdmin
    }

    /**
     * @since 1.38
     */
    @RequiresPlugin(id = 'ghprb', minimumVersion = '1.26.2')
    void extensions(@DslContext(GitHubPullRequestBuilderExtensionContext) Closure closure) {
        ContextHelper.executeInContext(closure, extensionContext)
    }
}
