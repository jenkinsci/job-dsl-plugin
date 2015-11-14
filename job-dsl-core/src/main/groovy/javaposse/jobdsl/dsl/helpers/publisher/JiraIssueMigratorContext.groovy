package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class JiraIssueMigratorContext implements Context {
    String jiraProjectKey
    String jiraRelease
    String jiraReplaceVersion
    String jiraQuery

    /**
     * Sets a jira ProjectKey for the jiraIssueMigrator.
     */
    void jiraProjectKey(String jiraProjectKey) {
        this.jiraProjectKey = jiraProjectKey
    }

    /**
     * Sets a jira Release for the jiraIssueMigrator.
     */
    void jiraRelease(String jiraRelease) {
        this.jiraRelease = jiraRelease
    }

    /**
     * Sets a jira replace version for the jiraIssueMigrator.
     */
    void jiraReplaceVersion(String jiraReplaceVersion) {
        this.jiraReplaceVersion = jiraReplaceVersion
    }

    /**
     * Sets a jira Query for the jiraIssueMigrator.
     */
    void jiraQuery(String jiraQuery) {
        this.jiraQuery = jiraQuery
    }
}
