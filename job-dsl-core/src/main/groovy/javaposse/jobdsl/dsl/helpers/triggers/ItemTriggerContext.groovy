package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.AbstractExtensibleContext

/**
 * @since 1.42
 */
class ItemTriggerContext extends AbstractExtensibleContext {
    final List<Node> triggerNodes = []

    ItemTriggerContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    @Override
    protected void addExtensionNode(Node node) {
        triggerNodes << node
    }

    /**
     * Adds DSL for adding and configuring the URL trigger plugin to a job.
     *
     * @param crontab crontab execution spec
     * @param contextClosure closure for configuring the context
     * @since 1.16
     */
    @RequiresPlugin(id = 'urltrigger')
    void urlTrigger(String crontab = null, @DslContext(UrlTriggerContext) Closure contextClosure) {
        UrlTriggerContext urlTriggerContext = new UrlTriggerContext(crontab)
        ContextHelper.executeInContext(contextClosure, urlTriggerContext)

        Node urlTriggerNode = new NodeBuilder().'org.jenkinsci.plugins.urltrigger.URLTrigger' {
            spec urlTriggerContext.crontab
            if (urlTriggerContext.label) {
                labelRestriction true
                triggerLabel urlTriggerContext.label
            } else {
                labelRestriction false
            }
            if (urlTriggerContext.entries) {
                entries {
                    urlTriggerContext.entries.each { entry ->
                        'org.jenkinsci.plugins.urltrigger.URLTriggerEntry' {
                            url entry.url
                            statusCode entry.statusCode
                            timeout entry.timeout
                            proxyActivated entry.proxyActivated

                            checkStatus entry.checks.contains(UrlTriggerEntryContext.Check.status)
                            checkETag entry.checks.contains(UrlTriggerEntryContext.Check.etag)
                            checkLastModificationDate entry.checks.contains(UrlTriggerEntryContext.Check.lastModified)

                            if ((!entry.inspections.empty)) {
                                inspectingContent true
                                contentTypes {
                                    entry.inspections.each { insp ->
                                        "${insp.type.node}" {
                                            if (insp.type != UrlTriggerInspectionContext.Inspection.change) {
                                                "${insp.type.list}" {
                                                    insp.expressions.each { p ->
                                                        "${insp.type.entry}" {
                                                            "${insp.type.path}"(p)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }

                }
            }
        }

        ContextHelper.executeConfigureBlock(urlTriggerNode, urlTriggerContext.configureBlock)

        triggerNodes << urlTriggerNode
    }

    /**
     * Triggers the job based on regular intervals.
     *
     * To configure a multi-line entry, use a single trigger string with entries separated by {@code \n}.
     */
    void cron(String cronString) {
        Preconditions.checkNotNull(cronString, 'cronString must be specified')

        triggerNodes << new NodeBuilder().'hudson.triggers.TimerTrigger' {
            spec cronString
        }
    }

    /**
     * Builds pull requests from GitHub and will report the results back to the pull request.
     *
     * The pull request builder plugin requires a special Git SCM configuration, see the plugin documentation for
     * details.
     *
     * @since 1.22
     */
    @Deprecated
    @RequiresPlugin(id = 'ghprb', minimumVersion = '1.26')
    void pullRequest(@DslContext(PullRequestBuilderContext) Closure contextClosure) {
        PullRequestBuilderContext pullRequestBuilderContext = new PullRequestBuilderContext(jobManagement)
        ContextHelper.executeInContext(contextClosure, pullRequestBuilderContext)

        triggerNodes << new NodeBuilder().'org.jenkinsci.plugins.ghprb.GhprbTrigger' {
            adminlist pullRequestBuilderContext.admins.join('\n')
            whitelist pullRequestBuilderContext.userWhitelist.join('\n')
            orgslist pullRequestBuilderContext.orgWhitelist.join('\n')
            delegate.cron(pullRequestBuilderContext.cron)
            spec pullRequestBuilderContext.cron
            triggerPhrase pullRequestBuilderContext.triggerPhrase ?: ''
            onlyTriggerPhrase pullRequestBuilderContext.onlyTriggerPhrase
            useGitHubHooks pullRequestBuilderContext.useGitHubHooks
            permitAll pullRequestBuilderContext.permitAll
            autoCloseFailedPullRequests pullRequestBuilderContext.autoCloseFailedPullRequests
            commentFilePath pullRequestBuilderContext.commentFilePath ?: ''
            allowMembersOfWhitelistedOrgsAsAdmin pullRequestBuilderContext.allowMembersOfWhitelistedOrgsAsAdmin
            extensions(pullRequestBuilderContext.extensionContext.extensionNodes)
        }
    }

    /**
     * Trigger a build with a DOS script.
     *
     * @since 1.42
     */
    @RequiresPlugin(id = 'dos-trigger', minimumVersion = '1.23')
    void dos(String cronString, @DslContext(DosTriggerContext) Closure closure) {
        Preconditions.checkNotNullOrEmpty(cronString, 'cronString must be specified')

        DosTriggerContext context = new DosTriggerContext()
        ContextHelper.executeInContext(closure, context)

        triggerNodes << new NodeBuilder().'org.jenkinsci.plugins.dostrigger.DosTrigger' {
            spec(cronString)
            script(context.triggerScript ?: '')
            nextBuildNum(0)
        }
    }
}
