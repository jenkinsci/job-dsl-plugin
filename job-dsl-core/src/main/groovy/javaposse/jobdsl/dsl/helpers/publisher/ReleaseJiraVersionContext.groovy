package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class ReleaseJiraVersionContext implements Context {
    String projectKey
    String release

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
}
