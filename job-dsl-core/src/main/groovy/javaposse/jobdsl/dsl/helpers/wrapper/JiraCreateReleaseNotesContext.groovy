package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context

class JiraCreateReleaseNotesContext implements Context {
    String jiraEnvironmentVariable
    String jiraProjectKey
    String jiraRelease
    String jiraFilter

    /**
     * Sets a jira EnvironmentVariableh for the JiraCreateReleaseNotesContext.
     */
    void jiraEnvironmentVariable(String jiraEnvironmentVariable) {
        this.jiraEnvironmentVariable = jiraEnvironmentVariable
    }

    /**
     * Sets a jira ProjectKey for the JiraCreateReleaseNotesContext.
     */
    void jiraProjectKey(String jiraProjectKey) {
        this.jiraProjectKey = jiraProjectKey
    }

    /**
     * Sets a jira Release for the JiraCreateReleaseNotesContext.
     */
    void jiraRelease(String jiraRelease) {
        this.jiraRelease = jiraRelease
    }

    /**
     * Sets a jira filter for the JiraCreateReleaseNotesContext.
     */
    void jiraFilter(String jiraFilter) {
        this.jiraFilter = jiraFilter
    }
}
