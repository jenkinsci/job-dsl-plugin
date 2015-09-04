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

    /**
     * Adds admins for this job.
     */
    void admin(String admin) {
        admins << admin
    }

    /**
     * Adds admins for this job.
     */
    void admins(Iterable<String> admins) {
        admins.each {
            admin(it)
        }
    }

    /**
     * Adds whitelisted users for this job.
     */
    void userWhitelist(String user) {
        userWhitelist << user
    }

    /**
     * Adds whitelisted users for this job.
     */
    void userWhitelist(Iterable<String> users) {
        users.each {
            userWhitelist(it)
        }
    }

    /**
     * Adds organisation names whose members are considered whitelisted for this specific job.
     */
    void orgWhitelist(String organization) {
        orgWhitelist << organization
    }

    /**
     * Adds organisation names whose members are considered whitelisted for this specific job.
     */
    void orgWhitelist(Iterable<String> organizations) {
        organizations.each {
            orgWhitelist(it)
        }
    }

    /**
     * This schedules polling to GitHub for new changes in pull requests.
     */
    void cron(String cron) {
        this.cron = cron
    }

    /**
     * Extends the standard build comment message on github with a custom message file.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'ghprb', minimumVersion = '1.14')
    void commentFilePath(String commentFilePath) {
        this.commentFilePath = commentFilePath
    }

    /**
     * When filled, commenting this phrase in the pull request will trigger a build.
     */
    void triggerPhrase(String triggerPhrase) {
        this.triggerPhrase = triggerPhrase
    }

    /**
     * When set, only commenting the trigger phrase in the pull request will trigger a build.
     */
    void onlyTriggerPhrase(boolean onlyTriggerPhrase = true) {
        this.onlyTriggerPhrase = onlyTriggerPhrase
    }

    /**
     * Checking this option will disable regular polling for changes in GitHub and will try to create a GitHub hook.
     */
    void useGitHubHooks(boolean useGitHubHooks = true) {
        this.useGitHubHooks = useGitHubHooks
    }

    /**
     * Build every pull request automatically without asking.
     */
    void permitAll(boolean permitAll = true) {
        this.permitAll = permitAll
    }

    /**
     * Close pull request automatically when the build fails.
     */
    void autoCloseFailedPullRequests(boolean autoCloseFailedPullRequests = true) {
        this.autoCloseFailedPullRequests = autoCloseFailedPullRequests
    }

    /**
     * Allows members of whitelisted organisations to behave like admins.
     *
     * @since 1.35
     */
    @RequiresPlugin(id = 'ghprb', minimumVersion = '1.15-0')
    void allowMembersOfWhitelistedOrgsAsAdmin(boolean allowMembersOfWhitelistedOrgsAsAdmin = true) {
        this.allowMembersOfWhitelistedOrgsAsAdmin = allowMembersOfWhitelistedOrgsAsAdmin
    }

    /**
     * Adds additional trigger options.
     *
     * @since 1.38
     */
    @RequiresPlugin(id = 'ghprb', minimumVersion = '1.26')
    void extensions(@DslContext(GitHubPullRequestBuilderExtensionContext) Closure closure) {
        ContextHelper.executeInContext(closure, extensionContext)
    }
}
