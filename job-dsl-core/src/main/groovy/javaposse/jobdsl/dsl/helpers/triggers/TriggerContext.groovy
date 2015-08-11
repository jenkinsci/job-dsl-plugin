package javaposse.jobdsl.dsl.helpers.triggers

import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.AbstractExtensibleContext
import javaposse.jobdsl.dsl.helpers.common.DownstreamContext
import javaposse.jobdsl.dsl.helpers.triggers.GerritContext.GerritSpec

class TriggerContext extends AbstractExtensibleContext {
    final List<Node> triggerNodes = []

    TriggerContext(JobManagement jobManagement, Item item) {
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

        // Apply their overrides
        if (urlTriggerContext.configureClosure) {
            WithXmlAction action = new WithXmlAction(urlTriggerContext.configureClosure)
            action.execute(urlTriggerNode)
        }

        triggerNodes << urlTriggerNode
    }

    void cron(String cronString) {
        Preconditions.checkNotNull(cronString, 'cronString must be specified')

        triggerNodes << new NodeBuilder().'hudson.triggers.TimerTrigger' {
            spec cronString
        }
    }

    void scm(String cronString, @DslContext(ScmTriggerContext) Closure scmTriggerClosure = null) {
        Preconditions.checkNotNull(cronString, 'cronString must be specified')

        ScmTriggerContext scmTriggerContext = new ScmTriggerContext()
        ContextHelper.executeInContext(scmTriggerClosure, scmTriggerContext)

        triggerNodes << new NodeBuilder().'hudson.triggers.SCMTrigger' {
            spec cronString
            ignorePostCommitHooks scmTriggerContext.ignorePostCommitHooks
        }
    }

    /**
     * Trigger that runs jobs on push notifications from GitHub.
     *
     * @since 1.16
     */
    @RequiresPlugin(id = 'github')
    void githubPush() {
        triggerNodes << new NodeBuilder().'com.cloudbees.jenkins.GitHubPushTrigger' {
            spec ''
        }
    }

    /**
     * Configures the Jenkins GitHub pull request builder plugin.
     *
     * @since 1.22
     */
    @RequiresPlugin(id = 'ghprb')
    void pullRequest(@DslContext(PullRequestBuilderContext) Closure contextClosure) {
        jobManagement.logPluginDeprecationWarning('ghprb', '1.15-0')

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
            if (!jobManagement.getPluginVersion('ghprb')?.isOlderThan(new VersionNumber('1.14'))) {
                commentFilePath pullRequestBuilderContext.commentFilePath ?: ''
            }
            if (!jobManagement.getPluginVersion('ghprb')?.isOlderThan(new VersionNumber('1.15-0'))) {
                allowMembersOfWhitelistedOrgsAsAdmin pullRequestBuilderContext.allowMembersOfWhitelistedOrgsAsAdmin
            }
            if (!jobManagement.getPluginVersion('ghprb')?.isOlderThan(new VersionNumber('1.22-0'))) {
                'extensions' {
                    'org.jenkinsci.plugins.ghprb.extensions.status.GhprbSimpleStatus' {
                        commitStatusContext pullRequestBuilderContext.commitStatusContext ?: ''
                        triggeredStatus pullRequestBuilderContext.triggeredStatus ?: ''
                        startedStatus pullRequestBuilderContext.startedStatus ?: ''
                        'completedStatus' {
                            'org.jenkinsci.plugins.ghprb.extensions.comments.GhprbBuildResultMessage' {
                                message pullRequestBuilderContext.buildResultSuccessMessage ?: ''
                                result pullRequestBuilderContext.buildResultSuccess
                            }
                            'org.jenkinsci.plugins.ghprb.extensions.comments.GhprbBuildResultMessage' {
                                message pullRequestBuilderContext.buildResultFailureMessage ?: ''
                                result pullRequestBuilderContext.buildResultFailure
                            }
                        }
                    }
                }
            }
        }
    }

    @RequiresPlugin(id = 'gerrit-trigger')
    void gerrit(@DslContext(GerritContext) Closure contextClosure = null) {
        // See what they set up in the contextClosure before generating xml
        GerritContext gerritContext = new GerritContext()
        ContextHelper.executeInContext(contextClosure, gerritContext)

        NodeBuilder nodeBuilder = new NodeBuilder()
        Node gerritNode = nodeBuilder.'com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTrigger' {
            spec ''
            if (gerritContext.projects) {
                gerritProjects {
                    gerritContext.projects.each { GerritSpec project, List<GerritSpec> brancheSpecs ->
                        'com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.GerritProject' {
                            compareType project.type
                            pattern project.pattern
                            branches {
                                brancheSpecs.each { GerritSpec branch ->
                                    'com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.Branch' {
                                        compareType branch.type
                                        pattern branch.pattern
                                    }
                                }
                            }
                        }
                    }
                }
            }
            silentMode false
            escapeQuotes true
            buildStartMessage ''
            buildFailureMessage ''
            buildSuccessfulMessage ''
            buildUnstableMessage ''
            buildNotBuiltMessage ''
            buildUnsuccessfulFilepath ''
            customUrl ''
            if (gerritContext.eventContext.eventShortNames) {
                triggerOnEvents {
                    gerritContext.eventContext.eventShortNames.each {
                        "com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.events.Plugin${it}Event" ''
                    }
                }
            }
            gerritContext.with {
                if (startedVerified != null) {
                    gerritBuildStartedVerifiedValue startedVerified
                }
                if (startedCodeReview != null) {
                    gerritBuildStartedCodeReviewValue startedCodeReview
                }
                if (successfulVerified != null) {
                    gerritBuildSuccessfulVerifiedValue successfulVerified
                }
                if (successfulCodeReview != null) {
                    gerritBuildSuccessfulCodeReviewValue successfulCodeReview
                }
                if (failedVerified != null) {
                    gerritBuildFailedVerifiedValue failedVerified
                }
                if (failedCodeReview != null) {
                    gerritBuildFailedCodeReviewValue failedCodeReview
                }
                if (unstableVerified != null) {
                    gerritBuildUnstableVerifiedValue unstableVerified
                }
                if (unstableCodeReview != null) {
                    gerritBuildUnstableCodeReviewValue unstableCodeReview
                }
                if (notBuiltVerified != null) {
                    gerritBuildNotBuiltVerifiedValue notBuiltVerified
                }
                if (notBuiltCodeReview != null) {
                    gerritBuildNotBuiltCodeReviewValue notBuiltCodeReview
                }
            }
            dynamicTriggerConfiguration false
            triggerConfigURL ''
            triggerInformationAction ''
        }

        // Apply their overrides
        if (gerritContext.configureClosure) {
            WithXmlAction action = new WithXmlAction(gerritContext.configureClosure)
            action.execute(gerritNode)
        }

        triggerNodes << gerritNode
    }

    /**
     * @since 1.33
     */
    void upstream(String projects, String threshold = 'SUCCESS') {
        Preconditions.checkNotNullOrEmpty(projects, 'projects must be specified')
        Preconditions.checkArgument(
                DownstreamContext.THRESHOLD_COLOR_MAP.containsKey(threshold),
                "threshold must be one of ${DownstreamContext.THRESHOLD_COLOR_MAP.keySet().join(', ')}"
        )

        triggerNodes << new NodeBuilder().'jenkins.triggers.ReverseBuildTrigger' {
            spec()
            upstreamProjects(projects)
            delegate.threshold {
                name(threshold)
                ordinal(DownstreamContext.THRESHOLD_ORDINAL_MAP[threshold])
                color(DownstreamContext.THRESHOLD_COLOR_MAP[threshold])
                completeBuild(true)
            }
        }
    }

    /**
     * Allows to schedule a build on Jenkins after a job execution on RunDeck.
     *
     * @since 1.33
     */
    @RequiresPlugin(id = 'rundeck', minimumVersion = '3.4')
    void rundeck(@DslContext(RundeckTriggerContext) Closure closure = null) {
        RundeckTriggerContext context = new RundeckTriggerContext()
        ContextHelper.executeInContext(closure, context)

        triggerNodes << new NodeBuilder().'org.jenkinsci.plugins.rundeck.RundeckTrigger' {
            spec()
            filterJobs(context.filterJobs)
            jobsIdentifiers {
                context.jobIdentifiers.each { String jobsIdentifier ->
                    string(jobsIdentifier)
                }
            }
            executionStatuses {
                context.executionStatuses.each { String status ->
                    string(status)
                }
            }
        }
    }
}
