package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class JiraContext implements Context {
    boolean jiraCreateReleaseNotes = false
    final JiraCreateReleaseNotesContext jiraCreateReleaseNotesContext = new JiraCreateReleaseNotesContext()

    /**
     * Adds a jiraCreateReleaseNotes.
     */
    void jiraCreateReleaseNotes(@DslContext(JiraCreateReleaseNotesContext) Closure closure) {
        jiraCreateReleaseNotes = true
        ContextHelper.executeInContext(closure, jiraCreateReleaseNotesContext)
    }
}
