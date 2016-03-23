package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class CreateJiraVersionContext implements Context {
    String projectKey
    String version

    /**
     * Sets the JIRA project key.
     */
    void projectKey(String projectKey) {
        this.projectKey = projectKey
    }

    /**
     * Specifies the name of the parameter which will contain the release version.
     */
    void version(String version) {
        this.version = version
    }
}
