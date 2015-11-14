package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class JiraReleaseVersionUpdaterContext implements Context {
    String jiraProjectKey
    String jiraRelease

    /**
     * Sets a jira ProjectKey for the jiraReleaseVersionUpdater.
     */
    void jiraProjectKey(String jiraProjectKey) {
        this.jiraProjectKey = jiraProjectKey
    }

    /**
     * Sets a jira Release for the jiraReleaseVersionUpdater.
     */
    void jiraRelease(String jiraRelease) {
        this.jiraRelease = jiraRelease
    }
}
