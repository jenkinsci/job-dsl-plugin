package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
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
    @RequiresPlugin(id = 'github-branch-source', minimumVersion = '2.2.0')
    void github(@DslContext(GitHubBranchSourceContext) Closure branchSourceClosure) {
        GitHubBranchSourceContext context = new GitHubBranchSourceContext(jobManagement)
        ContextHelper.executeInContext(branchSourceClosure, context)

        branchSourceNodes << new NodeBuilder().'jenkins.branch.BranchSource' {
            source(class: 'org.jenkinsci.plugins.github_branch_source.GitHubSCMSource') {
                id(context.id)
                if (context.apiUri) {
                    apiUri(context.apiUri)
                }
                credentialsId(context.scanCredentialsId ?: '')
                repoOwner(context.repoOwner ?: '')
                repository(context.repository ?: '')
                traits {
                    if (context.buildOriginBranch || context.buildOriginBranchWithPR) {
                        'org.jenkinsci.plugins.github__branch__source.BranchDiscoveryTrait' {
                            strategyId((context.buildOriginBranch ? 1 : 0) + (context.buildOriginBranchWithPR ? 2 : 0))
                        }
                    }
                    if (context.buildOriginPRMerge || context.buildOriginPRHead) {
                        'org.jenkinsci.plugins.github__branch__source.OriginPullRequestDiscoveryTrait' {
                            strategyId((context.buildOriginPRMerge ? 1 : 0) + (context.buildOriginPRHead ? 2 : 0))
                        }
                    }
                    if (context.buildForkPRMerge || context.buildForkPRHead) {
                        'org.jenkinsci.plugins.github__branch__source.ForkPullRequestDiscoveryTrait' {
                            strategyId((context.buildForkPRMerge ? 1 : 0) + (context.buildForkPRHead ? 2 : 0))
                            trust(class:
                            'org.jenkinsci.plugins.github_branch_source.ForkPullRequestDiscoveryTrait$TrustPermission')
                        }
                    }
                    if (context.checkoutCredentialsId != null &&
                            'SAME' != context.scanCredentialsId &&
                            context.checkoutCredentialsId != context.scanCredentialsId) {
                        'org.jenkinsci.plugins.github__branch__source.SSHCheckoutTrait' {
                            credentialsId(context.checkoutCredentialsId)
                        }
                    }
                    if ((context.includes != null && '*' != context.includes) ||
                            (context.excludes != null && '' != context.excludes)) {
                        'jenkins.scm.impl.trait.WildcardSCMHeadFilterTrait' {
                            includes(context.includes ?: '*')
                            excludes(context.excludes ?: '')
                        }
                    }
                    if (context.pattern != null && '.*' != context.pattern) {
                        'jenkins.scm.impl.trait.RegexSCMSourceFilterTrait' {
                            regex(context.pattern)
                        }
                    }
                    if (!context.noTags) {
                        'jenkins.plugins.git.traits.CloneOptionTrait' {
                            extension(class: 'hudson.plugins.git.extensions.impl.CloneOption') {
                                noTags(context.noTags)
                            }
                        }
                    }
                }
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
