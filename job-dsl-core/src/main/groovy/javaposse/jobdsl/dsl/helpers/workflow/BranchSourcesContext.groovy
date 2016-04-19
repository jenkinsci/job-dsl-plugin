package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class BranchSourcesContext implements Context {
    List<Node> branchSourceNodes = []

    /**
     * Adds a Git branch source. Can be called multiple times to add more branch sources.
     */
    void git(@DslContext(GitBranchSourceContext) Closure branchSourceClosure) {
        GitBranchSourceContext context = new GitBranchSourceContext()
        ContextHelper.executeInContext(branchSourceClosure, context)

        branchSourceNodes << new NodeBuilder().'jenkins.branch.BranchSource' {
            source(class: 'jenkins.plugins.git.GitSCMSource') {
                id(UUID.randomUUID())
                remote(context.remote ?: '')
                credentialsId(context.credentialsId ?: '')
                includes(context.includes ?: '')
                excludes(context.excludes ?: '')
                ignoreOnPushNotifications(context.ignoreOnPushNotifications)
            }
            strategy(class: 'jenkins.branch.DefaultBranchPropertyStrategy') {
                properties(class: 'empty-list')
            }
        }
    }
    /**
     * Adds a GitHub branch source. Can be called multiple times to add more branch sources.
     */
    void github(@DslContext(GitHubBranchSourceContext) Closure branchSourceClosure) {
        GitHubBranchSourceContext context = new GitHubBranchSourceContext()
        ContextHelper.executeInContext(branchSourceClosure, context)

        branchSourceNodes << new NodeBuilder().'jenkins.branch.BranchSource' {
            source(class: 'org.jenkinsci.plugins.github_branch_source.GitHubSCMSource') {
                id(UUID.randomUUID())
                apiUri(context.apiUri ?: '')
                scanCredentialsId(context.scanCredentialsId ?: '')
                checkoutCredentialsId(context.checkoutCredentialsId ?: '')
                repoOwner(context.repoOwner ?: '')
                repository(context.repository ?: '')
                includes(context.includes ?: '')
                excludes(context.excludes ?: '')
                ignoreOnPushNotifications(context.ignoreOnPushNotifications)
            }
            strategy(class: 'jenkins.branch.DefaultBranchPropertyStrategy') {
                properties(class: 'empty-list')
            }
        }
    }
}
