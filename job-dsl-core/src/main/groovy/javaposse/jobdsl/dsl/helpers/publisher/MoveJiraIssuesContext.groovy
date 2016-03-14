package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class MoveJiraIssuesContext implements Context {
    String projectKey
    String release
    String replaceVersion
    String query

    /**
     * Sets a jira ProjectKey for the moveJiraIssuesContext.
     */
    void projectKey(String projectKey) {
        this.projectKey = projectKey
    }

    /**
     * Sets a jira Release for the moveJiraIssuesContext.
     */
    void release(String release) {
        this.release = release
    }

    /**
     * Sets a jira replace version for the moveJiraIssuesContext.
     */
    void replaceVersion(String replaceVersion) {
        this.replaceVersion = replaceVersion
    }

    /**
     * Sets a jira Query for the moveJiraIssuesContext.
     */
    void query(String query) {
        this.query = query
    }
}
