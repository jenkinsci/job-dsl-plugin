package javaposse.jobdsl.dsl.helpers.triggers

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context


class TriggerContext implements Context {
    List<WithXmlAction> withXmlActions
    JobType jobType
    List<Node> triggerNodes

    TriggerContext(List<WithXmlAction> withXmlActions = [], JobType jobType = JobType.Freeform, List<Node> triggerNodes = []) {
        this.withXmlActions = withXmlActions
        this.jobType = jobType
        this.triggerNodes = triggerNodes
    }

    /**
     * Adds DSL  for adding and configuring the URL trigger plugin to a job.
     *
     * Uses a default cron execution schedule "H/5 * * * *", every 5 minutes with some jitter to prevent load spikes.
     *
     * @param contextClosure closure for configuring the context
     */
    def urlTrigger(Closure contextClosure) {
        urlTrigger(null, contextClosure)
    }

    /**
     * Adds DSL  for adding and configuring the URL trigger plugin to a job.
     *
     * @param crontab crontab execution spec
     * @param contextClosure closure for configuring the context
     */
    def urlTrigger(String crontab, Closure contextClosure) {

        UrlTriggerContext urlTriggerContext = new UrlTriggerContext(crontab)
        AbstractContextHelper.executeInContext(contextClosure, urlTriggerContext)

        def nodeBuilder = new NodeBuilder()
        def urlTriggerNode = nodeBuilder.'org.jenkinsci.plugins.urltrigger.URLTrigger'(plugin: 'urltrigger@0.31') {
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

                            /* Does not work right now due to dependencies on Jenkins for encryption */
                            /*if (entry.username && entry.password) {
                                username entry.username
                                password entry.password
                            }*/

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

    def cron(String cronString) {
        Preconditions.checkNotNull(cronString)
        triggerNodes << new NodeBuilder().'hudson.triggers.TimerTrigger' {
            spec cronString
        }
    }

    /**
     <triggers class="vector">
     <hudson.triggers.SCMTrigger>
     <spec>10 * * * *</spec>
     </hudson.triggers.SCMTrigger>
     </triggers>
     */
    def scm(String cronString) {
        Preconditions.checkNotNull(cronString)
        triggerNodes << new NodeBuilder().'hudson.triggers.SCMTrigger' {
            spec cronString
        }
    }

    /**
     * Trigger that runs jobs on push notifications from Github/Github enterprise
     */
    def githubPush() {
        def attributes = [plugin: 'github@1.6']
        triggerNodes << new NodeBuilder().'com.cloudbees.jenkins.GitHubPushTrigger'(attributes) {
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
     *      <triggerPhrase></triggerPhrase>
     *      <onlyTriggerPhrase>false</onlyTriggerPhrase>
     *      <useGitHubHooks>true</useGitHubHooks>
     *      <permitAll>true</permitAll>
     *      <autoCloseFailedPullRequests>false</autoCloseFailedPullRequests>
     *  </org.jenkinsci.plugins.ghprb.GhprbTrigger>
     */
    def pullRequest(Closure contextClosure = null) {

        PullRequestBuilderContext pullRequestBuilderContext = new PullRequestBuilderContext()
        AbstractContextHelper.executeInContext(contextClosure, pullRequestBuilderContext)

        def nodeBuilder = NodeBuilder.newInstance()

        def pullRequestNode = nodeBuilder.'org.jenkinsci.plugins.ghprb.GhprbTrigger' {
            adminlist pullRequestBuilderContext.admins.join('\n')
            whitelist pullRequestBuilderContext.whiteList
            orgslist pullRequestBuilderContext.whiteListedOrgs.join('\n')
            delegate.cron(pullRequestBuilderContext.cron)
            triggerPhrase pullRequestBuilderContext.triggerPhrase
            onlyTriggerPhrase pullRequestBuilderContext.onlyTriggerPhrase
            useGitHubHooks pullRequestBuilderContext.useGitHubHooks
            permitAll pullRequestBuilderContext.permitAll
            autoCloseFailedPullRequests pullRequestBuilderContext.autoCloseFailedPullRequests
        }

        triggerNodes << pullRequestNode
    }

    /**
     <com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTrigger>
     <spec></spec>
     <gerritProjects>
     <com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.GerritProject>
     <compareType>PLAIN</compareType>
     <pattern>test-project</pattern>
     <branches>
     <com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.Branch>
     <compareType>ANT</compareType>
     <pattern>**</pattern>
     </com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.Branch>
     </branches>
     </com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.GerritProject>
     </gerritProjects>
     <silentMode>false</silentMode>
     <escapeQuotes>true</escapeQuotes>
     <buildStartMessage></buildStartMessage>
     <buildFailureMessage></buildFailureMessage>
     <buildSuccessfulMessage></buildSuccessfulMessage>
     <buildUnstableMessage></buildUnstableMessage>
     <buildNotBuiltMessage></buildNotBuiltMessage>
     <buildUnsuccessfulFilepath></buildUnsuccessfulFilepath>
     <customUrl></customUrl>
     <triggerOnEvents>
     <com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.events.PluginChangeMergedEvent/>
     <com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.events.PluginPatchsetCreatedEvent/>
     </triggerOnEvents>
     <dynamicTriggerConfiguration>false</dynamicTriggerConfiguration>
     <triggerConfigURL></triggerConfigURL>
     <triggerInformationAction/>
     </com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTrigger>
     *
     *
     * @param triggerEvents Can be ommited and the plugin will user PatchsetCreated and DraftPublished by default. Provide in
     *                      show name format: ChangeMerged, CommentAdded, DraftPublished, PatchsetCreated, RefUpdated
     * @return
     */
    def gerrit(Closure contextClosure = null) {
        // See what they set up in the contextClosure before generating xml
        GerritContext gerritContext = new GerritContext()
        AbstractContextHelper.executeInContext(contextClosure, gerritContext)

        def nodeBuilder = new NodeBuilder()
        def gerritNode = nodeBuilder.'com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTrigger' {
            spec ''
            if (gerritContext.projects) {
                gerritProjects {
                    gerritContext.projects.each { GerritContext.GerritSpec project, List<GerritContext.GerritSpec> brancheSpecs ->
                        'com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.GerritProject' {
                            compareType project.type
                            pattern project.pattern
                            branches {
                                brancheSpecs.each { GerritContext.GerritSpec branch ->
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
                    gerritContext.eventContext.eventShortNames.each { eventShortName ->
                        "com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.events.Plugin${eventShortName}Event" ''
                    }
                }
            }
            gerritBuildStartedVerifiedValue Integer.toString(gerritContext.startedVerified)
            gerritBuildStartedCodeReviewValue Integer.toString(gerritContext.startedCodeReview)
            gerritBuildSuccessfulVerifiedValue Integer.toString(gerritContext.successfulVerified)
            gerritBuildSuccessfulCodeReviewValue Integer.toString(gerritContext.successfulCodeReview)
            gerritBuildFailedVerifiedValue Integer.toString(gerritContext.failedVerified)
            gerritBuildFailedCodeReviewValue Integer.toString(gerritContext.failedCodeReview)
            gerritBuildUnstableVerifiedValue Integer.toString(gerritContext.unstableVerified)
            gerritBuildUnstableCodeReviewValue Integer.toString(gerritContext.unstableCodeReview)
            gerritBuildNotBuiltVerifiedValue Integer.toString(gerritContext.notBuiltVerified)
            gerritBuildNotBuiltCodeReviewValue Integer.toString(gerritContext.notBuiltCodeReview)
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
    def snapshotDependencies(boolean checkSnapshotDependencies) {
        Preconditions.checkState jobType == JobType.Maven, "snapshotDependencies can only be applied for Maven jobs"
        withXmlActions << new WithXmlAction({
            it.children().removeAll { it instanceof Node && it.name() == "ignoreUpstremChanges" }
            it.appendNode "ignoreUpstremChanges", !checkSnapshotDependencies
        })
    }
}
