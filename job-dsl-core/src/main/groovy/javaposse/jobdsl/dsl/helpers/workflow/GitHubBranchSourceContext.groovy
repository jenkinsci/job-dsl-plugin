package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class GitHubBranchSourceContext extends AbstractContext {
    String apiUri = 'https://api.github.com'
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
     * Sets the GitHub API URI. Defaults to {@code 'https://api.github.com'}.
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
     * Build origin branches.
     */
    void buildOriginBranch(boolean buildOriginBranch) {
        this.buildOriginBranch = buildOriginBranch
    }

    /**
     * Build origin branches also filed as PRs.
     */
    void buildOriginBranchWithPR(boolean buildOriginBranchWithPR) {
        this.buildOriginBranchWithPR = buildOriginBranchWithPR
    }

    /**
     * Build origin PRs (merged with base branch).
     */
    void buildOriginPRMerge(boolean buildOriginPRMerge) {
        this.buildOriginPRMerge = buildOriginPRMerge
    }

    /**
     * Build origin PRs (unmerged head).
     */
    void buildOriginPRHead(boolean buildOriginPRHead) {
        this.buildOriginPRHead = buildOriginPRHead
    }

    /**
     * Build fork PRs (merged with base branch).
     */
    void buildForkPRMerge(boolean buildForkPRMerge) {
        this.buildForkPRMerge = buildForkPRMerge
    }

    /**
     * Build fork PRs (unmerged head).
     */
    void buildForkPRHead(boolean buildForkPRHead) {
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
