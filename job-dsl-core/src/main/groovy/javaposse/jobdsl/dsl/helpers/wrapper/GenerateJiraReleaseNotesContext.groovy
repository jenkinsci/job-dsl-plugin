package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context

class GenerateJiraReleaseNotesContext implements Context {
    String environmentVariable
    String projectKey
    String release
    String filter

    /**
     * Sets a jira EnvironmentVariable for the GenerateJiraReleaseNotesContext.
     */
    void environmentVariable(String environmentVariable) {
        this.environmentVariable = environmentVariable
    }

    /**
     * Sets a jira ProjectKey for the GenerateJiraReleaseNotesContext.
     */
    void projectKey(String projectKey) {
        this.projectKey = projectKey
    }

    /**
     * Sets a jira Release for the GenerateJiraReleaseNotesContext.
     */
    void release(String release) {
        this.release = release
    }

    /**
     * Sets a jira filter for the GenerateJiraReleaseNotesContext.
     */
    void filter(String filter) {
        this.filter = filter
    }
}
