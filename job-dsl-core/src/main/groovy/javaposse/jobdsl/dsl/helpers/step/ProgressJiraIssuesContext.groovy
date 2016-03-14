package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class ProgressJiraIssuesContext implements Context {
    String jqlSearch
    String workflowActionName
    String comment

    /**
     * Sets a jira jqlSearch for the ProgressJiraIssuesContext.
     */
    void jqlSearch(String jqlSearch) {
        this.jqlSearch = jqlSearch
    }

    /**
     * Sets a jira workflowActionName for the ProgressJiraIssuesContext.
     */
    void workflowActionName(String workflowActionName) {
        this.workflowActionName = workflowActionName
    }

    /**
     * Sets a jira comment for the ProgressJiraIssuesContext.
     */
    void comment(String comment) {
        this.comment = comment
    }
}
