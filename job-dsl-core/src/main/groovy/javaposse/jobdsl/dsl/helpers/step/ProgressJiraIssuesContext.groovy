package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class ProgressJiraIssuesContext implements Context {
    String jqlSearch
    String workflowActionName
    String comment

    /**
     * Issues which match this JQL Query will be progressed using the specified workflow action.
     */
    void jqlSearch(String jqlSearch) {
        this.jqlSearch = jqlSearch
    }

    /**
     * The workflow action to be performed on the selected JIRA issues.
     */
    void workflowActionName(String workflowActionName) {
        this.workflowActionName = workflowActionName
    }

    /**
     * An optional comment to be added to the issue after updating the workflow.
     */
    void comment(String comment) {
        this.comment = comment
    }
}
