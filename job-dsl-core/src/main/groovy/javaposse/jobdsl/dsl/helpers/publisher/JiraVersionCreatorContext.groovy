package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class JiraVersionCreatorContext implements Context {
    String jiraProjectKey
    String jiraVersion

    /**
     * Sets a jira ProjectKey for the JiraVersionCreatorContext.
     */
    void jiraProjectKey(String jiraProjectKey) {
        this.jiraProjectKey = jiraProjectKey
    }

    /**
     * Sets a jira Version for the JiraVersionCreatorContext.
     */
    void jiraVersion(String jiraVersion) {
        this.jiraVersion = jiraVersion
    }
}
