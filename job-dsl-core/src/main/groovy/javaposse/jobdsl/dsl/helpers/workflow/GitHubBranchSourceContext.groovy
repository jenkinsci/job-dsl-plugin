package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class GitHubBranchSourceContext extends AbstractContext {
    String apiUri
    String scanCredentialsId
    String checkoutCredentialsId = 'SAME'
    String repoOwner
    String repository
    String includes = '*'
    String excludes
    boolean buildOriginBranch = true
    boolean buildOriginBranchWithPR = true
    boolean buildOriginPRMerge = false
    boolean buildOriginPRHead = false
    boolean buildForkPRMerge = true
    boolean buildForkPRHead = false

    GitHubBranchSourceContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Sets the GitHub API URI. Defaults to GitHub.
     */
    void apiUri(String apiUri) {
        this.apiUri = apiUri
    }

    /**
     * Sets scan credentials for authentication with GitHub.
     */
    void scanCredentialsId(String scanCredentialsId) {
        this.scanCredentialsId = scanCredentialsId
    }

    /**
     * Sets checkout credentials for authentication with GitHub. Defaults to the scan credentials.
     */
    void checkoutCredentialsId(String checkoutCredentialsId) {
        this.checkoutCredentialsId = checkoutCredentialsId
    }

    /**
     * Sets the name of the GitHub Organization or GitHub User Account.
     */
    void repoOwner(String repoOwner) {
        this.repoOwner = repoOwner
    }

    /**
     * Sets the name of the GitHub repository.
     */
    void repository(String repository) {
        this.repository = repository
    }

    /**
     * Sets a pattern for branches to include.
     */
    void includes(String includes) {
        this.includes = includes
    }

    /**
     * Sets a pattern for branches to exclude.
     */
    void excludes(String excludes) {
        this.excludes = excludes
    }

    /**
     * Build origin branches. Defaults to {@code true}.
     *
     * @since 1.54
     */
    @RequiresPlugin(id = 'github-branch-source', minimumVersion = '1.8')
    void buildOriginBranch(boolean buildOriginBranch = true) {
        this.buildOriginBranch = buildOriginBranch
    }

    /**
     * Build origin branches also filed as PRs. Defaults to {@code true}.
     *
     * @since 1.54
     */
    @RequiresPlugin(id = 'github-branch-source', minimumVersion = '1.8')
    void buildOriginBranchWithPR(boolean buildOriginBranchWithPR = true) {
        this.buildOriginBranchWithPR = buildOriginBranchWithPR
    }

    /**
     * Build origin PRs (merged with base branch). Defaults to {@code false}.
     *
     * @since 1.54
     */
    @RequiresPlugin(id = 'github-branch-source', minimumVersion = '1.8')
    void buildOriginPRMerge(boolean buildOriginPRMerge = true) {
        this.buildOriginPRMerge = buildOriginPRMerge
    }

    /**
     * Build origin PRs (unmerged head). Defaults to {@code false}.
     *
     * @since 1.54
     */
    @RequiresPlugin(id = 'github-branch-source', minimumVersion = '1.8')
    void buildOriginPRHead(boolean buildOriginPRHead = true) {
        this.buildOriginPRHead = buildOriginPRHead
    }

    /**
     * Build fork PRs (merged with base branch). Defaults to {@code true}.
     *
     * @since 1.54
     */
    @RequiresPlugin(id = 'github-branch-source', minimumVersion = '1.8')
    void buildForkPRMerge(boolean buildForkPRMerge = true) {
        this.buildForkPRMerge = buildForkPRMerge
    }

    /**
     * Build fork PRs (unmerged head). Defaults to {@code false}.
     *
     * @since 1.54
     */
    @RequiresPlugin(id = 'github-branch-source', minimumVersion = '1.8')
    void buildForkPRHead(boolean buildForkPRHead = true) {
        this.buildForkPRHead = buildForkPRHead
    }

    /**
     * If set, ignores push notifications. Defaults to {@code false}.
     */
    @SuppressWarnings(['GroovyUnusedDeclaration', 'EmptyMethod'])
    @Deprecated
    void ignoreOnPushNotifications(boolean ignoreOnPushNotifications = true) {
    }
}
