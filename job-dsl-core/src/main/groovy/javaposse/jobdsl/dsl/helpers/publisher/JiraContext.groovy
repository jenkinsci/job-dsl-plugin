package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class JiraContext implements Context {
    boolean jiraIssueUpdater = false
    boolean jiraReleaseVersionUpdater = false
    boolean jiraIssueMigrator = false
    boolean jiraVersionCreator = false
    boolean jiraCreateIssueNotifier = false
    final JiraReleaseVersionUpdaterContext jiraReleaseVersionUpdaterContext = new JiraReleaseVersionUpdaterContext()
    final JiraIssueMigratorContext jiraIssueMigratorContext = new JiraIssueMigratorContext()
    final JiraVersionCreatorContext jiraVersionCreatorContext = new JiraVersionCreatorContext()
    final JiraCreateIssueNotifierContext jiraCreateIssueNotifierContext = new JiraCreateIssueNotifierContext()

    /**
     * If set, jiraIssueUpdater publisher is configured. Defaults to {@code true}.
     */
    void jiraIssueUpdater(boolean jiraIssueUpdater = true) {
        this.jiraIssueUpdater = jiraIssueUpdater
    }

    /**
     * Adds a jiraReleaseVersionUpdater.
     */
    void jiraReleaseVersionUpdater(@DslContext(JiraReleaseVersionUpdaterContext) Closure closure) {
        jiraReleaseVersionUpdater = true
        ContextHelper.executeInContext(closure, jiraReleaseVersionUpdaterContext)
    }

    /**
     * Adds a jiraIssueMigrator.
     */
    void jiraIssueMigrator(@DslContext(JiraIssueMigratorContext) Closure closure) {
        jiraIssueMigrator = true
        ContextHelper.executeInContext(closure, jiraIssueMigratorContext)
    }

    /**
     * Adds a jiraVersionCreator.
     */
    void jiraVersionCreator(@DslContext(JiraVersionCreatorContext) Closure closure) {
        jiraVersionCreator = true
        ContextHelper.executeInContext(closure, jiraVersionCreatorContext)
    }

    /**
     * Adds a jiraCreateIssueNotifier.
     */
    void jiraCreateIssueNotifier(@DslContext(JiraCreateIssueNotifierContext) Closure closure) {
        jiraCreateIssueNotifier = true
        ContextHelper.executeInContext(closure, jiraCreateIssueNotifierContext)
    }
}
