package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin

@ContextType('jenkins.branch.BranchSource')
class BranchSourcesContext extends AbstractExtensibleContext {
    List<Node> branchSourceNodes = []

    BranchSourcesContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    /**
     * Adds a Git branch source. Can be called multiple times to add more branch sources.
     */
    @RequiresPlugin(id = 'git', minimumVersion = '2.5.3')
    void git(@DslContext(GitBranchSourceContext) Closure branchSourceClosure) {
        GitBranchSourceContext context = new GitBranchSourceContext()
        ContextHelper.executeInContext(branchSourceClosure, context)

        Preconditions.checkNotNullOrEmpty(context.id, 'id must be specified')

        branchSourceNodes << new NodeBuilder().'jenkins.branch.BranchSource' {
            source(class: 'jenkins.plugins.git.GitSCMSource') {
                id(context.id)
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
     *
     * @since 1.46
     */
    @RequiresPlugin(id = 'github-branch-source', minimumVersion = '1.8')
    void github(@DslContext(GitHubBranchSourceContext) Closure branchSourceClosure) {
        GitHubBranchSourceContext context = new GitHubBranchSourceContext(jobManagement)
        ContextHelper.executeInContext(branchSourceClosure, context)

        Preconditions.checkNotNullOrEmpty(context.id, 'id must be specified')

        branchSourceNodes << new NodeBuilder().'jenkins.branch.BranchSource' {
            source(class: 'org.jenkinsci.plugins.github_branch_source.GitHubSCMSource') {
                id(context.id)
                if (context.apiUri) {
                    apiUri(context.apiUri)
                }
                scanCredentialsId(context.scanCredentialsId ?: '')
                checkoutCredentialsId(context.checkoutCredentialsId ?: '')
                repoOwner(context.repoOwner ?: '')
                repository(context.repository ?: '')
                includes(context.includes ?: '')
                excludes(context.excludes ?: '')
                buildOriginBranch(context.buildOriginBranch)
                buildOriginBranchWithPR(context.buildOriginBranchWithPR)
                buildOriginPRMerge(context.buildOriginPRMerge)
                buildOriginPRHead(context.buildOriginPRHead)
                buildForkPRMerge(context.buildForkPRMerge)
                buildForkPRHead(context.buildForkPRHead)
            }
            strategy(class: 'jenkins.branch.DefaultBranchPropertyStrategy') {
                properties(class: 'empty-list')
            }
        }
    }

    @Override
    protected void addExtensionNode(Node node) {
        branchSourceNodes << node
    }
}
