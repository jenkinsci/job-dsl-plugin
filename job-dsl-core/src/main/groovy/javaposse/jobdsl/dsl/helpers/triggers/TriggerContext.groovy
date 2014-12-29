package javaposse.jobdsl.dsl.helpers.triggers

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.triggers.GerritContext.GerritSpec

class TriggerContext implements Context {
    private final List<WithXmlAction> withXmlActions
    private final JobType jobType
    private final JobManagement jobManagement
    final List<Node> triggerNodes = []

    TriggerContext(List<WithXmlAction> withXmlActions, JobType jobType, JobManagement jobManagement) {
        this.withXmlActions = withXmlActions
        this.jobType = jobType
        this.jobManagement = jobManagement
    }

    /**
     * Adds DSL for adding and configuring the URL trigger plugin to a job.
     *
     * @param crontab crontab execution spec
     * @param contextClosure closure for configuring the context
     */
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

    /**
     * <hudson.triggers.SCMTrigger>
     *     <spec>10 * * * *</spec>
     * </hudson.triggers.SCMTrigger>
     */
    void scm(String cronString) {
        Preconditions.checkNotNull(cronString)
        triggerNodes << new NodeBuilder().'hudson.triggers.SCMTrigger' {
            spec cronString
        }
    }

    /**
     * Trigger that runs jobs on push notifications from Github/Github enterprise.
     *
     * <com.cloudbees.jenkins.GitHubPushTrigger>
     *     <spec/>
     * </com.cloudbees.jenkins.GitHubPushTrigger>
     */
    void githubPush() {
        triggerNodes << new NodeBuilder().'com.cloudbees.jenkins.GitHubPushTrigger' {
            spec ''
        }
    }

    /**
     *  Configures the Jenkins GitHub pull request builder plugin
     *  Depends on the github-api, github, and git plugins
     *
     *  <org.jenkinsci.plugins.ghprb.GhprbTrigger>
     *      <adminlist></adminlist>
     *      <whitelist></whitelist>
     *      <orgslist></orgslist>
     *      <cron></cron>
     *      <spec></spec>
     *      <triggerPhrase></triggerPhrase>
     *      <onlyTriggerPhrase>false</onlyTriggerPhrase>
     *      <useGitHubHooks>true</useGitHubHooks>
     *      <permitAll>true</permitAll>
     *      <autoCloseFailedPullRequests>false</autoCloseFailedPullRequests>
     *  </org.jenkinsci.plugins.ghprb.GhprbTrigger>
     */
    void pullRequest(@DslContext(PullRequestBuilderContext) Closure contextClosure) {
        PullRequestBuilderContext pullRequestBuilderContext = new PullRequestBuilderContext()
        ContextHelper.executeInContext(contextClosure, pullRequestBuilderContext)

        triggerNodes << new NodeBuilder().'org.jenkinsci.plugins.ghprb.GhprbTrigger' {
            adminlist pullRequestBuilderContext.admins.join('\n')
            whitelist pullRequestBuilderContext.userWhitelist.join('\n')
            orgslist pullRequestBuilderContext.orgWhitelist.join('\n')
            delegate.cron(pullRequestBuilderContext.cron)
            spec pullRequestBuilderContext.cron
            triggerPhrase pullRequestBuilderContext.triggerPhrase
            onlyTriggerPhrase pullRequestBuilderContext.onlyTriggerPhrase
            useGitHubHooks pullRequestBuilderContext.useGitHubHooks
            permitAll pullRequestBuilderContext.permitAll
            autoCloseFailedPullRequests pullRequestBuilderContext.autoCloseFailedPullRequests
        }
    }

    /**
     * <com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTrigger>
     *     <spec></spec>
     *     <gerritProjects>
     *         <com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.GerritProject>
     *             <compareType>PLAIN</compareType>
     *             <pattern>test-project</pattern>
     *             <branches>
     *                 <com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.Branch>
     *                    <compareType>ANT</compareType>
     *                    <pattern>**</pattern>
     *                 </com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.Branch>
     *             </branches>
     *         </com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.GerritProject>
     *     </gerritProjects>
     *     <silentMode>false</silentMode>
     *     <escapeQuotes>true</escapeQuotes>
     *     <buildStartMessage></buildStartMessage>
     *     <buildFailureMessage></buildFailureMessage>
     *     <buildSuccessfulMessage></buildSuccessfulMessage>
     *     <buildUnstableMessage></buildUnstableMessage>
     *     <buildNotBuiltMessage></buildNotBuiltMessage>
     *     <buildUnsuccessfulFilepath></buildUnsuccessfulFilepath>
     *     <customUrl></customUrl>
     *     <triggerOnEvents>
     *         <com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.events.PluginChangeMergedEvent/>
     *         <com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.events.PluginPatchsetCreatedEvent/>
     *     </triggerOnEvents>
     *     <dynamicTriggerConfiguration>false</dynamicTriggerConfiguration>
     *     <triggerConfigURL></triggerConfigURL>
     *     <triggerInformationAction/>
     * </com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTrigger>
     *
     * @param triggerEvents Can be ommited and the plugin will user PatchsetCreated and DraftPublished by default.
     *                      Provide in show name format: ChangeMerged, CommentAdded, DraftPublished, PatchsetCreated,
     *                      RefUpdated
     * @return
     */
    void gerrit(@DslContext(GerritContext) Closure contextClosure = null) {
        // See what they set up in the contextClosure before generating xml
        GerritContext gerritContext = new GerritContext(jobManagement)
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
     * If set to <code>true</code>, Jenkins will parse the POMs of this project, and see if any of its snapshot
     * dependencies are built on this Jenkins as well. If so, Jenkins will set up build dependency relationship so that
     * whenever the dependency job is built and a new SNAPSHOT jar is created, Jenkins will schedule a build of this
     * project. Defaults to <code>false</code>.
     * @param checkSnapshotDependencies set to <code>true</code> to check snapshot dependencies
     */
    void snapshotDependencies(boolean checkSnapshotDependencies) {
        Preconditions.checkState jobType == JobType.Maven, 'snapshotDependencies can only be applied for Maven jobs'
        withXmlActions << WithXmlAction.create {
            it.children().removeAll { it instanceof Node && it.name() == 'ignoreUpstremChanges' }
            it.appendNode 'ignoreUpstremChanges', !checkSnapshotDependencies
        }
    }
}
