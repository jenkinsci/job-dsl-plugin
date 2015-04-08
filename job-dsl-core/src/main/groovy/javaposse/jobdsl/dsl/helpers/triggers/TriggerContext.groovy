package javaposse.jobdsl.dsl.helpers.triggers

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.triggers.GerritContext.GerritSpec

class TriggerContext implements Context {
    protected final JobManagement jobManagement
    final List<Node> triggerNodes = []

    TriggerContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    /**
     * Adds DSL for adding and configuring the URL trigger plugin to a job.
     *
     * @param crontab crontab execution spec
     * @param contextClosure closure for configuring the context
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
        Preconditions.checkNotNull(cronString)
        triggerNodes << new NodeBuilder().'hudson.triggers.TimerTrigger' {
            spec cronString
        }
    }

    void scm(String cronString, @DslContext(ScmTriggerContext) Closure scmTriggerClosure = null) {
        Preconditions.checkNotNull(cronString)

        ScmTriggerContext scmTriggerContext = new ScmTriggerContext()
        ContextHelper.executeInContext(scmTriggerClosure, scmTriggerContext)

        triggerNodes << new NodeBuilder().'hudson.triggers.SCMTrigger' {
            spec cronString
            ignorePostCommitHooks scmTriggerContext.ignorePostCommitHooks
        }
    }

    /**
     * Trigger that runs jobs on push notifications from GitHub.
     */
    @RequiresPlugin(id = 'github')
    void githubPush() {
        triggerNodes << new NodeBuilder().'com.cloudbees.jenkins.GitHubPushTrigger' {
            spec ''
        }
    }

    /**
     *  Configures the Jenkins GitHub pull request builder plugin.
     */
    @RequiresPlugin(id = 'ghprb')
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
     * Upstream build
     *
     <jenkins.triggers.ReverseBuildTrigger>
     <spec/>
     <upstreamProjects>DSL-Tutorial-1-Test</upstreamProjects>
     <threshold>
         <name>SUCCESS</name>
         <ordinal>0</ordinal>
         <color>BLUE</color>
         <completeBuild>true</completeBuild>
     </threshold>
     </jenkins.triggers.ReverseBuildTrigger>
    */
    void upstream(String projectName, String thresholdName = 'SUCCESS') {
        Preconditions.checkNotNull(projectName)

        assert UpstreamContext.THRESHOLD_COLOR_MAP.containsKey(thresholdName),
                "thresholdName must be one of these values ${UpstreamContext.THRESHOLD_COLOR_MAP.keySet().join(',')}"

        NodeBuilder nodeBuilder = new NodeBuilder()

        Node triggerNode = nodeBuilder.'jenkins.triggers.ReverseBuildTrigger' {
           spec ''
           upstreamProjects projectName
           threshold {
             delegate.createNode('name', thresholdName)
             ordinal UpstreamContext.THRESHOLD_ORDINAL_MAP[thresholdName]
             color UpstreamContext.THRESHOLD_COLOR_MAP[thresholdName]
             completeBuild true
           }
        }

        triggerNodes << triggerNode
    }
}
