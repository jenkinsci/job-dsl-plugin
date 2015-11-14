package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class JiraIssueUpdateBuilderContext implements Context {
    String jqlSearch
    String workflowActionName
    String comment

    /**
     * Sets a jira jqlSearch for the JiraIssueUpdateBuilderContext.
     */
    void jqlSearch(String jqlSearch) {
        this.jqlSearch = jqlSearch
    }

    /**
     * Sets a jira workflowActionName for the JiraIssueUpdateBuilderContext.
     */
    void workflowActionName(String workflowActionName) {
        this.workflowActionName = workflowActionName
    }

    /**
     * Sets a jira comment for the JiraIssueUpdateBuilderContext.
     */
    void comment(String comment) {
        this.comment = comment
    }
}
