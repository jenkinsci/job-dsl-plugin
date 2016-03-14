package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class CreateJiraVersionContext implements Context {
    String projectKey
    String version

    /**
     * Sets a jira ProjectKey for the CreateJiraVersionContext.
     */
    void projectKey(String projectKey) {
        this.projectKey = projectKey
    }

    /**
     * Sets a jira Version for the CreateJiraVersionContext.
     */
    void version(String version) {
        this.version = version
    }
}
