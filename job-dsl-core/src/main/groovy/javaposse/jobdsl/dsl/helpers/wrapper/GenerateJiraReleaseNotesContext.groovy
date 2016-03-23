package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context

class GenerateJiraReleaseNotesContext implements Context {
    String environmentVariable
    String projectKey
    String release
    String filter

    /**
     * Specifies the environment variable to which the release notes will be stored.
     */
    void environmentVariable(String environmentVariable) {
        this.environmentVariable = environmentVariable
    }

    /**
     * Specifies the name of the parameter which will contain the release version.
     */
    void projectKey(String projectKey) {
        this.projectKey = projectKey
    }

    /**
     * Sets the JIRA project key.
     */
    void release(String release) {
        this.release = release
    }

    /**
     * Applies additional filtering criteria to the issue filter.
     */
    void filter(String filter) {
        this.filter = filter
    }
}
