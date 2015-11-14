package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class JiraContext implements Context {
    boolean jiraIssueUpdateBuilder = false
    final JiraIssueUpdateBuilderContext jiraIssueUpdateBuilderContext = new JiraIssueUpdateBuilderContext()

    /**
     * Adds a jiraIssueUpdateBuilder.
     */
    void jiraIssueUpdateBuilder(@DslContext(JiraIssueUpdateBuilderContext) Closure closure) {
        jiraIssueUpdateBuilder = true
        ContextHelper.executeInContext(closure, jiraIssueUpdateBuilderContext)
    }
}
