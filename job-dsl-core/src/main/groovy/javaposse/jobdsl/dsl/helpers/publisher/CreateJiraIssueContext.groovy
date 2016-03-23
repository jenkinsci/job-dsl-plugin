package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class CreateJiraIssueContext implements Context {
    String projectKey
    String testDescription
    String assignee
    String component

    /**
     * Sets the JIRA project key.
     */
    void projectKey(String projectKey) {
        this.projectKey = projectKey
    }

    /**
     * Sets the description for the issue.
     */
    void testDescription(String testDescription) {
        this.testDescription = testDescription
    }

    /**
     * Sets an assignee for the issue.
     */
    void assignee(String assignee) {
        this.assignee = assignee
    }

    /**
     * Sets component names that the issue should be assigned to, separated by comma.
     */
    void component(String component) {
        this.component = component
    }
}
