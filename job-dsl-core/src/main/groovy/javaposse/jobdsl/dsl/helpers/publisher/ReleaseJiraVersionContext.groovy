package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class ReleaseJiraVersionContext implements Context {
    String projectKey
    String release

    /**
     * Sets a jira ProjectKey for the releaseJiraVersionContext.
     */
    void projectKey(String projectKey) {
        this.projectKey = projectKey
    }

    /**
     * Sets a jira Release for the releaseJiraVersionContext.
     */
    void release(String release) {
        this.release = release
    }
}
