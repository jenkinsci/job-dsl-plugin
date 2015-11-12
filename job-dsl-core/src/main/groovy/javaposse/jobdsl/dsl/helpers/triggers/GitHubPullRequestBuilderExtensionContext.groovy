package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class GitHubPullRequestBuilderExtensionContext implements Context {
    List<Node> extensionNodes = []

    /**
     * Updates the commit status during the build.
     */
    void commitStatus(@DslContext(GitHubPullRequestBuilderCommitStatusContext) Closure closure) {
        GitHubPullRequestBuilderCommitStatusContext context = new GitHubPullRequestBuilderCommitStatusContext()
        ContextHelper.executeInContext(closure, context)

        extensionNodes << new NodeBuilder().'org.jenkinsci.plugins.ghprb.extensions.status.GhprbSimpleStatus' {
            commitStatusContext(context.context ?: '')
            triggeredStatus(context.triggeredStatus ?: '')
            startedStatus(context.startedStatus ?: '')
            statusUrl(context.statusUrl ?: '')
            completedStatus(context.completedStatus)
        }
    }
}
