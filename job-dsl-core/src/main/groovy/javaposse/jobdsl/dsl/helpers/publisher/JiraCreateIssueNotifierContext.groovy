package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class JiraCreateIssueNotifierContext implements Context {
    String projectKey
    String testDescription
    String assignee
    String component

    /**
     * Sets a jira ProjectKey for the JiraCreateIssueNotifier.
     */
    void projectKey(String projectKey) {
        this.projectKey = projectKey
    }

    /**
     * Sets a test Description for the JiraCreateIssueNotifier.
     */
    void testDescription(String testDescription) {
        this.testDescription = testDescription
    }

    /**
     * Sets a jira assignee for the JiraCreateIssueNotifier.
     */
    void assignee(String assignee) {
        this.assignee = assignee
    }

    /**
     * Sets the Jira component for the JiraCreateIssueNotifier.
     */
    void component(String component) {
        this.component = component
    }
}
