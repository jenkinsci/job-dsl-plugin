package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class CreateJiraIssueContext implements Context {
    String projectKey
    String testDescription
    String assignee
    String component

    /**
     * Sets a jira ProjectKey for the CreateJiraIssueContext.
     */
    void projectKey(String projectKey) {
        this.projectKey = projectKey
    }

    /**
     * Sets a test Description for the CreateJiraIssueContext.
     */
    void testDescription(String testDescription) {
        this.testDescription = testDescription
    }

    /**
     * Sets a jira assignee for the CreateJiraIssueContext.
     */
    void assignee(String assignee) {
        this.assignee = assignee
    }

    /**
     * Sets the Jira component for the CreateJiraIssueContext.
     */
    void component(String component) {
        this.component = component
    }
}
