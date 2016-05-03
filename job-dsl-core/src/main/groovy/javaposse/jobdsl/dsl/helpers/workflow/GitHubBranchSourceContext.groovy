package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.Context

class GitHubBranchSourceContext implements Context {
    String apiUri = 'https://api.github.com'
    String scanCredentialsId
    String checkoutCredentialsId = 'SAME'
    String repoOwner
    String repository
    String includes = '*'
    String excludes
    boolean ignoreOnPushNotifications

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
     * If set, ignores push notifications. Defaults to {@code false}.
     */
    void ignoreOnPushNotifications(boolean ignoreOnPushNotifications = true) {
        this.ignoreOnPushNotifications = ignoreOnPushNotifications
    }
}
