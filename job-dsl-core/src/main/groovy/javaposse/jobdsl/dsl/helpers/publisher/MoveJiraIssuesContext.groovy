package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class MoveJiraIssuesContext implements Context {
    String projectKey
    String release
    String replaceVersion
    String query

    /**
     * Sets the JIRA project key.
     */
    void projectKey(String projectKey) {
        this.projectKey = projectKey
    }

    /**
     * Specifies the name of the parameter which will contain the release version.
     */
    void release(String release) {
        this.release = release
    }

    /**
     * If a value is provided, then only this version will be replaced instead of all versions.
     */
    void replaceVersion(String replaceVersion) {
        this.replaceVersion = replaceVersion
    }

    /**
     * Issues which match this JQL Query will be moved to this release version.
     */
    void query(String query) {
        this.query = query
    }
}
