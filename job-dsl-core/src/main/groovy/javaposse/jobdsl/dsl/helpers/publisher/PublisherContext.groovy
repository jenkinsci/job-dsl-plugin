package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.helpers.common.ArtifactDeployerContext
import javaposse.jobdsl.dsl.helpers.common.PublishOverSshContext
import javaposse.jobdsl.dsl.jobs.MatrixJob

import static javaposse.jobdsl.dsl.ContextHelper.toNamedNode
import static javaposse.jobdsl.dsl.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty
import static javaposse.jobdsl.dsl.helpers.common.Threshold.THRESHOLD_COLOR_MAP
import static javaposse.jobdsl.dsl.helpers.common.Threshold.THRESHOLD_COMPLETED_BUILD
import static javaposse.jobdsl.dsl.helpers.common.Threshold.THRESHOLD_ORDINAL_MAP

@ContextType('hudson.tasks.Publisher')
class PublisherContext extends AbstractExtensibleContext {
    List<Node> publisherNodes = []

    PublisherContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    @Override
    protected void addExtensionNode(Node node) {
        publisherNodes << node
    }

    /**
     * Sends customizable email notifications.
     *
     * @since 1.43
     */
    @RequiresPlugin(id = 'email-ext', minimumVersion = '2.40.5')
    void extendedEmail(@DslContext(ExtendedEmailContext) Closure closure) {
        ExtendedEmailContext context = new ExtendedEmailContext()
        ContextHelper.executeInContext(closure, context)

        if (!context.triggersContext.configuredTriggers) {
            context.triggers {
                failure()
            }
        }

        Node emailNode = new NodeBuilder().'hudson.plugins.emailext.ExtendedEmailPublisher' {
            recipientList(context.recipientList ? context.recipientList.join(', ') : '$DEFAULT_RECIPIENTS')
            configuredTriggers(context.triggersContext.configuredTriggers)
            contentType(context.contentType)
            defaultSubject(context.defaultSubject ?: '')
            defaultContent(context.defaultContent ?: '')
            attachmentsPattern(context.attachmentPatterns.join(', '))
            presendScript(context.preSendScript ?: '')
            classpath {
                context.additionalGroovyClasspath.each { classpath ->
                    'hudson.plugins.emailext.GroovyScriptPath' {
                        path(classpath ?: '')
                    }
                }
            }
            attachBuildLog(context.attachBuildLog)
            compressBuildLog(context.compressBuildLog)
            replyTo(context.replyToList ? context.replyToList.join(', ') : '$DEFAULT_REPLYTO')
            saveOutput(context.saveToWorkspace)
            disabled(context.disabled)
        }

        ContextHelper.executeConfigureBlock(emailNode, context.configureBlock)

        publisherNodes << emailNode
    }

    /**
     * Sends email notifications.
     *
     * @since 1.17
     */
    @RequiresPlugin(id = 'mailer')
    void mailer(String recipients, Boolean dontNotifyEveryUnstableBuild = false, Boolean sendToIndividuals = false) {
        publisherNodes << new NodeBuilder().'hudson.tasks.Mailer' {
            delegate.recipients(recipients)
            delegate.dontNotifyEveryUnstableBuild(dontNotifyEveryUnstableBuild)
            delegate.sendToIndividuals(sendToIndividuals)
        }
    }

    /**
     * Archives artifacts with each build.
     *
     * @since 1.20
     */
    void archiveArtifacts(@DslContext(ArchiveArtifactsContext) Closure artifactsClosure) {
        ArchiveArtifactsContext artifactsContext = new ArchiveArtifactsContext(jobManagement)
        ContextHelper.executeInContext(artifactsClosure, artifactsContext)

        publisherNodes << new NodeBuilder().'hudson.tasks.ArtifactArchiver' {
            artifacts(artifactsContext.patterns.join(','))
            if (artifactsContext.excludes) {
                excludes(artifactsContext.excludes)
            }
            allowEmptyArchive(artifactsContext.allowEmpty)
            defaultExcludes(artifactsContext.defaultExcludes)
            delegate.fingerprint(artifactsContext.fingerprint)
            onlyIfSuccessful(artifactsContext.onlyIfSuccessful)
        }
    }

    /**
     * Archives artifacts with each build.
     */
    void archiveArtifacts(String glob, String excludeGlob = null) {
        archiveArtifacts {
            pattern(glob)
            exclude(excludeGlob)
        }
    }

    /**
     * Publishes JUnit test result reports.
     *
     * @since 1.26
     */
    @RequiresPlugin(id = 'junit', minimumVersion = '1.10')
    void archiveJunit(String glob, @DslContext(ArchiveJUnitContext) Closure junitClosure = null) {

        ArchiveJUnitContext junitContext = new ArchiveJUnitContext(jobManagement)
        ContextHelper.executeInContext(junitClosure, junitContext)

        publisherNodes << new NodeBuilder().'hudson.tasks.junit.JUnitResultArchiver' {
            testResults(glob)
            keepLongStdio(junitContext.retainLongStdout)
            testDataPublishers(junitContext.testDataPublishersContext.testDataPublishers)
            allowEmptyResults(junitContext.allowEmptyResults)
            healthScaleFactor(junitContext.healthScaleFactor)
        }
    }

    /**
     * Publishes reports generated from results of various testing tools.
     *
     * @since 1.24
     */
    @RequiresPlugin(id = 'xunit')
    void archiveXUnit(@DslContext(ArchiveXUnitContext) Closure xUnitClosure) {
        ArchiveXUnitContext xUnitContext = new ArchiveXUnitContext(jobManagement)
        ContextHelper.executeInContext(xUnitClosure, xUnitContext)

        publisherNodes << new NodeBuilder().'xunit' {
            types {
                xUnitContext.resultFiles.each { ArchiveXUnitResultFileContext resultFile ->
                    "${resultFile.type}" {
                        pattern resultFile.pattern
                        skipNoTestFiles resultFile.skipNoTestFiles
                        failIfNotNew resultFile.failIfNotNew
                        deleteOutputFiles resultFile.deleteOutputFiles
                        stopProcessingIfError resultFile.stopProcessingIfError
                        if (resultFile instanceof ArchiveXUnitCustomToolContext) {
                            customXSL resultFile.stylesheet
                        }
                    }
                }
            }
            thresholds {
                'org.jenkinsci.plugins.xunit.threshold.FailedThreshold' {
                    unstableThreshold xUnitContext.failedThresholdsContext.unstable == null ? ''
                            : xUnitContext.failedThresholdsContext.unstable
                    unstableNewThreshold xUnitContext.failedThresholdsContext.unstableNew == null ? ''
                            : xUnitContext.failedThresholdsContext.unstableNew
                    failureThreshold xUnitContext.failedThresholdsContext.failure == null ? ''
                            : xUnitContext.failedThresholdsContext.failure
                    failureNewThreshold xUnitContext.failedThresholdsContext.failureNew == null ? ''
                            : xUnitContext.failedThresholdsContext.failureNew
                }
                'org.jenkinsci.plugins.xunit.threshold.SkippedThreshold' {
                    unstableThreshold xUnitContext.skippedThresholdsContext.unstable == null ? ''
                            : xUnitContext.skippedThresholdsContext.unstable
                    unstableNewThreshold xUnitContext.skippedThresholdsContext.unstableNew == null ? ''
                            : xUnitContext.skippedThresholdsContext.unstableNew
                    failureThreshold xUnitContext.skippedThresholdsContext.failure == null ? ''
                            : xUnitContext.skippedThresholdsContext.failure
                    failureNewThreshold xUnitContext.skippedThresholdsContext.failureNew == null ? ''
                            : xUnitContext.skippedThresholdsContext.failureNew
                }
            }
            thresholdMode xUnitContext.thresholdMode.xmlValue
            extraConfiguration {
                testTimeMargin xUnitContext.timeMargin
            }
        }
    }

    /**
     * Publishes TestNG test result reports.
     *
     * @since 1.40
     */
    @RequiresPlugin(id = 'testng-plugin', minimumVersion = '1.10')
    void archiveTestNG(String glob = '**/testng-results.xml',
                       @DslContext(ArchiveTestNGContext) Closure testNGClosure = null) {
        ArchiveTestNGContext testNGContext = new ArchiveTestNGContext(jobManagement)
        ContextHelper.executeInContext(testNGClosure, testNGContext)

        publisherNodes << new NodeBuilder().'hudson.plugins.testng.Publisher' {
            reportFilenamePattern(glob)
            escapeTestDescp(testNGContext.escapeTestDescription)
            escapeExceptionMsg(testNGContext.escapeExceptionMessages)
            showFailedBuilds(testNGContext.showFailedBuildsInTrendGraph)
            unstableOnSkippedTests(testNGContext.markBuildAsUnstableOnSkippedTests)
            failureOnFailedTestConfig(testNGContext.markBuildAsFailureOnFailedConfiguration)
        }
    }

    /**
     * Publishes Gatling load simulation reports.
     *
     * @since 1.41
     */
    @RequiresPlugin(id = 'gatling', minimumVersion = '1.1.1')
    void archiveGatling(@DslContext(ArchiveGatlingContext) Closure gatlingClosure = null) {
        ArchiveGatlingContext gatlingContext = new ArchiveGatlingContext(jobManagement)
        ContextHelper.executeInContext(gatlingClosure, gatlingContext)

        publisherNodes << new NodeBuilder().'io.gatling.jenkins.GatlingPublisher' {
            enabled(gatlingContext.enabled)
        }
    }

    /**
     * Publishes a JaCoCo coverage report.
     *
     * @since 1.17
     */
    @RequiresPlugin(id = 'jacoco')
    void jacocoCodeCoverage(@DslContext(JacocoContext) Closure jacocoClosure = null) {
        JacocoContext jacocoContext = new JacocoContext(jobManagement)
        ContextHelper.executeInContext(jacocoClosure, jacocoContext)

        publisherNodes << new NodeBuilder().'hudson.plugins.jacoco.JacocoPublisher' {
            execPattern jacocoContext.execPattern
            classPattern jacocoContext.classPattern
            sourcePattern jacocoContext.sourcePattern
            inclusionPattern jacocoContext.inclusionPattern
            exclusionPattern jacocoContext.exclusionPattern
            minimumInstructionCoverage jacocoContext.minimumInstructionCoverage
            minimumBranchCoverage jacocoContext.minimumBranchCoverage
            minimumComplexityCoverage jacocoContext.minimumComplexityCoverage
            minimumLineCoverage jacocoContext.minimumLineCoverage
            minimumMethodCoverage jacocoContext.minimumMethodCoverage
            minimumClassCoverage jacocoContext.minimumClassCoverage
            maximumInstructionCoverage jacocoContext.maximumInstructionCoverage
            maximumBranchCoverage jacocoContext.maximumBranchCoverage
            maximumComplexityCoverage jacocoContext.maximumComplexityCoverage
            maximumLineCoverage jacocoContext.maximumLineCoverage
            maximumMethodCoverage jacocoContext.maximumMethodCoverage
            maximumClassCoverage jacocoContext.maximumClassCoverage
            if (jacocoContext.changeBuildStatus != null) {
                changeBuildStatus jacocoContext.changeBuildStatus
            }
        }
    }

    /**
     * Plots data across builds.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'plot', minimumVersion = '1.9')
    void plotBuildData(@DslContext(PlotsContext) Closure plotsClosure) {
        PlotsContext plotsContext = new PlotsContext()
        ContextHelper.executeInContext(plotsClosure, plotsContext)

        publisherNodes << new NodeBuilder().'hudson.plugins.plot.PlotPublisher' {
            plots {
                plotsContext.plots.each { PlotContext plot ->
                    'hudson.plugins.plot.Plot' {
                        title(plot.title ?: '')
                        yaxis(plot.yAxis ?: '')
                        series {
                            plot.dataSeriesList.each { PlotSeriesContext data ->
                                "hudson.plugins.plot.${data.seriesType}" {
                                    file(data.fileName)
                                    fileType(data.fileType)
                                    if (data instanceof PlotPropertiesSeriesContext) {
                                        label(data.label ?: '')
                                    }
                                    if (data instanceof PlotCSVSeriesContext) {
                                        label()
                                        inclusionFlag(data.inclusionFlag)
                                        exclusionValues(data.exclusionSet.join(','))
                                        switch (data.inclusionFlag) {
                                            case ['INCLUDE_BY_STRING', 'EXCLUDE_BY_STRING']:
                                                strExclusionSet {
                                                    data.exclusionSet.each { String exclusion ->
                                                        string(exclusion)
                                                    }
                                                }
                                                break
                                            case ['INCLUDE_BY_COLUMN', 'EXCLUDE_BY_COLUMN']:
                                                colExclusionSet {
                                                    data.exclusionSet.each { String exclusion ->
                                                        'int'(exclusion)
                                                    }
                                                }
                                                break
                                        }
                                        url(data.url ?: '')
                                        displayTableFlag(data.showTable)
                                    }
                                    if (data instanceof PlotXMLSeriesContext) {
                                        label()
                                        xpathString(data.xpath ?: '')
                                        url(data.url ?: '')
                                        nodeTypeString(data.nodeType)
                                    }
                                }
                            }
                        }
                        group(plot.group)
                        numBuilds(plot.numberOfBuilds ?: '')
                        csvFileName(plot.dataStore)
                        csvLastModification(0)
                        style(plot.style)
                        useDescr(plot.useDescriptions)
                        keepRecords(plot.keepRecords)
                        exclZero(plot.excludeZero)
                        logarithmic(plot.logarithmic)
                    }
                }
            }
        }
    }

    /**
     * Publishes HTML reports.
     */
    @RequiresPlugin(id = 'htmlpublisher', minimumVersion = '1.5')
    void publishHtml(@DslContext(HtmlReportContext) Closure htmlReportContext) {
        HtmlReportContext reportContext = new HtmlReportContext(jobManagement)
        ContextHelper.executeInContext(htmlReportContext, reportContext)

        publisherNodes << new NodeBuilder().'htmlpublisher.HtmlPublisher' {
            reportTargets {
                reportContext.targets.each { HtmlReportTargetContext target ->
                    'htmlpublisher.HtmlPublisherTarget' {
                        reportName(target.reportName)
                        reportDir(target.reportDir)
                        reportFiles(target.reportFiles)
                        keepAll(target.keepAll)
                        allowMissing(target.allowMissing)
                        alwaysLinkToLastBuild(target.alwaysLinkToLastBuild)
                    }
                }
            }
        }
    }

    /**
     * Sends notifications to Jabber.
     */
    @RequiresPlugin(id = 'jabber', minimumVersion = '1.35')
    void publishJabber(String targets, @DslContext(JabberContext) Closure jabberClosure = null) {
        JabberContext jabberContext = new JabberContext(jobManagement)
        ContextHelper.executeInContext(jabberClosure, jabberContext)

        publisherNodes << new NodeBuilder().'hudson.plugins.jabber.im.transport.JabberPublisher' {
            delegate.targets {
                targets.split().each { target ->
                    if (target.startsWith('*') || target.contains('@conference.')) {
                        'hudson.plugins.im.GroupChatIMMessageTarget' {
                            name(target.startsWith('*') ? target[1..-1] : target)
                            notificationOnly(false)
                        }
                    } else {
                        'hudson.plugins.im.DefaultIMMessageTarget' {
                            value(target)
                        }
                    }
                }
            }
            strategy(jabberContext.strategyName)
            notifyOnBuildStart(jabberContext.notifyOnBuildStart)
            notifySuspects(jabberContext.notifySuspects)
            notifyCulprits(jabberContext.notifyCulprits)
            notifyFixers(jabberContext.notifyFixers)
            notifyUpstreamCommitters(jabberContext.notifyUpstreamCommitters)
            buildToChatNotifier(
                    class: "hudson.plugins.im.build_notify.${jabberContext.channelNotificationName}BuildToChatNotifier"
            )
            matrixMultiplier('ONLY_CONFIGURATIONS')
        }
    }

    /**
     * Uploads artifacts to the remote sites using the SFTP (SSH) protocol. The site is specified in the global Jenkins
     * configuration.
     */
    @RequiresPlugin(id = 'scp')
    void publishScp(String site, @DslContext(ScpContext) Closure scpClosure) {
        ScpContext scpContext = new ScpContext()
        ContextHelper.executeInContext(scpClosure, scpContext)

        // Validate values
        checkArgument(!scpContext.entries.empty, 'Scp publish requires at least one entry')

        publisherNodes << new NodeBuilder().'be.certipost.hudson.plugin.SCPRepositoryPublisher' {
            siteName site
            entries {
                scpContext.entries.each { ScpContext.ScpEntry entry ->
                    'be.certipost.hudson.plugin.Entry' {
                        filePath entry.destination
                        sourceFile entry.source
                        keepHierarchy entry.keepHierarchy ? 'true' : 'false'
                    }
                }
            }
        }
    }

    /**
     * Archives files for Clone Workspace SCM source.
     */
    @RequiresPlugin(id = 'clone-workspace-scm')
    void publishCloneWorkspace(String workspaceGlob, @DslContext(CloneWorkspaceContext) Closure cloneWorkspaceClosure) {
        publishCloneWorkspace(workspaceGlob, '', 'Any', 'TAR', false, cloneWorkspaceClosure)
    }

    /**
     * Archives files for Clone Workspace SCM source.
     */
    @RequiresPlugin(id = 'clone-workspace-scm')
    void publishCloneWorkspace(String workspaceGlob, String workspaceExcludeGlob,
                               @DslContext(CloneWorkspaceContext) Closure cloneWorkspaceClosure) {
        publishCloneWorkspace(workspaceGlob, workspaceExcludeGlob, 'Any', 'TAR', false, cloneWorkspaceClosure)
    }

    /**
     * Archives files for Clone Workspace SCM source.
     */
    @RequiresPlugin(id = 'clone-workspace-scm')
    void publishCloneWorkspace(String workspaceGlob, String workspaceExcludeGlob, String criteria, String archiveMethod,
                               @DslContext(CloneWorkspaceContext) Closure cloneWorkspaceClosure) {
        publishCloneWorkspace(
                workspaceGlob, workspaceExcludeGlob, criteria, archiveMethod, false, cloneWorkspaceClosure
        )
    }

    /**
     * Archives files for Clone Workspace SCM source.
     */
    @RequiresPlugin(id = 'clone-workspace-scm')
    void publishCloneWorkspace(String workspaceGlob, String workspaceExcludeGlob = '', String criteria = 'Any',
                               String archiveMethod = 'TAR', boolean overrideDefaultExcludes = false,
                               @DslContext(CloneWorkspaceContext) Closure cloneWorkspaceClosure = null) {
        CloneWorkspaceContext cloneWorkspaceContext = new CloneWorkspaceContext()
        cloneWorkspaceContext.criteria = criteria ?: 'Any'
        cloneWorkspaceContext.archiveMethod = archiveMethod ?: 'TAR'
        cloneWorkspaceContext.workspaceExcludeGlob = workspaceExcludeGlob ?: ''
        cloneWorkspaceContext.overrideDefaultExcludes = overrideDefaultExcludes ?: false
        ContextHelper.executeInContext(cloneWorkspaceClosure, cloneWorkspaceContext)

        // Validate values
        checkArgument(
                validCloneWorkspaceCriteria.contains(cloneWorkspaceContext.criteria),
                "Clone Workspace Criteria needs to be one of these values: ${validCloneWorkspaceCriteria.join(',')}"
        )
        checkArgument(
                validCloneWorkspaceArchiveMethods.contains(cloneWorkspaceContext.archiveMethod),
                'Clone Workspace Archive Method needs to be one of these values: ' +
                        validCloneWorkspaceArchiveMethods.join(',')
        )

        publisherNodes << new NodeBuilder().'hudson.plugins.cloneworkspace.CloneWorkspacePublisher' {
            delegate.workspaceGlob(workspaceGlob)
            delegate.workspaceExcludeGlob(cloneWorkspaceContext.workspaceExcludeGlob)
            delegate.criteria(cloneWorkspaceContext.criteria)
            delegate.archiveMethod(cloneWorkspaceContext.archiveMethod)
            delegate.overrideDefaultExcludes(cloneWorkspaceContext.overrideDefaultExcludes)
        }
    }

    static List<String> validCloneWorkspaceCriteria = ['Any', 'Not Failed', 'Successful']
    Set<String> validCloneWorkspaceArchiveMethods = ['TAR', 'ZIP']

    /**
     * Triggers builds on other projects.
     *
     * The {@code threshold} must be one of {@code 'SUCCESS'}, {@code 'UNSTABLE'} or {@code 'FAILURE'}.
     */
    void downstream(String projectName, String thresholdName = 'SUCCESS') {
        checkArgument(
                THRESHOLD_COLOR_MAP.containsKey(thresholdName),
                "thresholdName must be one of these values ${THRESHOLD_COLOR_MAP.keySet().join(',')}"
        )

        publisherNodes << new NodeBuilder().'hudson.tasks.BuildTrigger' {
            childProjects projectName
            threshold {
                delegate.createNode('name', thresholdName)
                ordinal THRESHOLD_ORDINAL_MAP[thresholdName]
                color THRESHOLD_COLOR_MAP[thresholdName]
            }
        }
    }

    /**
     * Triggers builds on other projects.
     *
     * The {@code threshold} must be one of {@code 'SUCCESS'}, {@code 'UNSTABLE'} or {@code 'FAILURE'}.
     *
     * @since 1.39
     */
    void downstream(List<String> projectName, String thresholdName = 'SUCCESS') {
        downstream(projectName.join(', '), thresholdName)
    }

    /**
     * Triggers parameterized builds on other projects.
     */
    @RequiresPlugin(id = 'parameterized-trigger', minimumVersion = '2.26')
    void downstreamParameterized(@DslContext(DownstreamContext) Closure downstreamClosure) {
        DownstreamContext downstreamContext = new DownstreamContext(jobManagement, item)
        ContextHelper.executeInContext(downstreamClosure, downstreamContext)

        publisherNodes << new NodeBuilder().'hudson.plugins.parameterizedtrigger.BuildTrigger' {
            configs(downstreamContext.configs)
        }
    }

    /**
     * Generates reports from static code violations detectors.
     */
    @RequiresPlugin(id = 'violations')
    void violations(@DslContext(ViolationsContext) Closure violationsClosure = null) {
        violations(100, violationsClosure)
    }

    /**
     * Generates reports from static code violations detectors.
     */
    @RequiresPlugin(id = 'violations')
    void violations(int perFileDisplayLimit, @DslContext(ViolationsContext) Closure violationsClosure = null) {
        ViolationsContext violationsContext = new ViolationsContext()
        violationsContext.perFileDisplayLimit = perFileDisplayLimit
        ContextHelper.executeInContext(violationsClosure, violationsContext)

        publisherNodes << new NodeBuilder().'hudson.plugins.violations.ViolationsPublisher' {
            config {
                suppressions(class: 'tree-set') {
                    'no-comparator'()
                }
                typeConfigs {
                    'no-comparator'()
                    violationsContext.entries.each { String key, ViolationsContext.ViolationsEntry violationsEntry ->
                        entry {
                            string(key)
                            'hudson.plugins.violations.TypeConfig' {
                                type(key)
                                // These values are protected from ever being null or empty.
                                min(violationsEntry.min.toString())
                                max(violationsEntry.max.toString())
                                unstable(violationsEntry.unstable.toString())
                                usePattern(violationsEntry.pattern ? 'true' : 'false')
                                pattern(violationsEntry.pattern ?: '')
                            }
                        }
                    }
                }
                limit(violationsContext.perFileDisplayLimit.toString())
                sourcePathPattern(violationsContext.sourcePathPattern ?: '')
                fauxProjectPath(violationsContext.fauxProjectPath ?: '')
                encoding(violationsContext.sourceEncoding ?: 'default')
            }
        }
    }

    /**
     * Displays a picture of Chuck Norris (instead of Jenkins the butler) and a random Chuck Norris 'The Programmer'
     * fact on each build page.
     */
    @RequiresPlugin(id = 'chucknorris')
    void chucknorris() {
        publisherNodes << new NodeBuilder().'hudson.plugins.chucknorris.CordellWalkerRecorder' {
            'factGenerator' ''
        }
    }

    /**
     * Sends notifications to IRC.
     *
     * @since 1.15
     */
    @RequiresPlugin(id = 'ircbot', minimumVersion = '2.27')
    void irc(@DslContext(IrcContext) Closure ircClosure) {
        IrcContext ircContext = new IrcContext(jobManagement)
        ContextHelper.executeInContext(ircClosure, ircContext)

        publisherNodes << new NodeBuilder().'hudson.plugins.ircbot.IrcPublisher' {
            targets {
                ircContext.channels.each { IrcContext.IrcPublisherChannel channel ->
                    'hudson.plugins.im.GroupChatIMMessageTarget' {
                        delegate.createNode('name', channel.name)
                        password(channel.password)
                        notificationOnly(channel.notificationOnly)
                    }
                }
            }
            strategy(ircContext.strategy)
            notifyOnBuildStart(ircContext.notifyOnBuildStarts)
            notifySuspects(ircContext.notifyScmCommitters)
            notifyCulprits(ircContext.notifyScmCulprits)
            notifyFixers(ircContext.notifyScmFixers)
            notifyUpstreamCommitters(ircContext.notifyUpstreamCommitters)
            buildToChatNotifier(
                    class: "hudson.plugins.im.build_notify.${ircContext.notificationMessage}BuildToChatNotifier"
            )
        }
    }

    /**
     * Publishes a Cobertura coverage report.
     *
     * @since 1.16
     */
    @RequiresPlugin(id = 'cobertura')
    void cobertura(String reportFile, @DslContext(CoberturaContext) Closure coberturaClosure = null) {
        CoberturaContext coberturaContext = new CoberturaContext()
        ContextHelper.executeInContext(coberturaClosure, coberturaContext)

        publisherNodes << new NodeBuilder().'hudson.plugins.cobertura.CoberturaPublisher' {
            coberturaReportFile(reportFile)
            onlyStable(coberturaContext.onlyStable)
            failUnhealthy(coberturaContext.failUnhealthy)
            failUnstable(coberturaContext.failUnstable)
            autoUpdateHealth(coberturaContext.autoUpdateHealth)
            autoUpdateStability(coberturaContext.autoUpdateStability)
            zoomCoverageChart(coberturaContext.zoomCoverageChart)
            failNoReports(coberturaContext.failNoReports)
            ['healthyTarget', 'unhealthyTarget', 'failingTarget'].each { targetName ->
                "$targetName" {
                    targets(class: 'enum-map', 'enum-type': 'hudson.plugins.cobertura.targets.CoverageMetric') {
                        coberturaContext.targets.values().each { target ->
                            entry {
                                'hudson.plugins.cobertura.targets.CoverageMetric' target.targetType
                                'int' target."$targetName"
                            }
                        }
                    }
                }
            }
            sourceEncoding(coberturaContext.sourceEncoding)
        }
    }

    /**
     * Allows to claim unsuccessful builds.
     *
     * @since 1.17
     */
    @RequiresPlugin(id = 'claim')
    void allowBrokenBuildClaiming() {
        publisherNodes << new NodeBuilder().'hudson.plugins.claim.ClaimPublisher'()
    }

    /**
     * Activates fingerprinting for the build.
     *
     * @since 1.17
     */
    void fingerprint(String targets, boolean recordBuildArtifacts = false) {
        publisherNodes << new NodeBuilder().'hudson.tasks.Fingerprinter' {
            delegate.targets(targets ?: '')
            delegate.recordBuildArtifacts(recordBuildArtifacts)
        }
    }

    /**
     * Automatically sets a description for the build after it has completed.
     *
     * @since 1.17
     */
    @RequiresPlugin(id = 'description-setter')
    void buildDescription(String regularExpression, String description = '', String regularExpressionForFailed = '',
                          String descriptionForFailed = '', boolean multiConfigurationBuild = false) {
        publisherNodes << new NodeBuilder().'hudson.plugins.descriptionsetter.DescriptionSetterPublisher' {
            regexp(regularExpression)
            regexpForFailed(regularExpressionForFailed)
            if (description) {
                delegate.description(description)
            }
            if (descriptionForFailed) {
                delegate.descriptionForFailed(descriptionForFailed)
            }
            setForMatrix(multiConfigurationBuild)
        }
    }

    /**
     * Searches for keywords in files or the console log and uses that to downgrade a build to be unstable or a failure.
     *
     * @since 1.19
     */
    @RequiresPlugin(id = 'text-finder')
    void textFinder(String regularExpression, String fileSet = '', boolean alsoCheckConsoleOutput = false,
                    boolean succeedIfFound = false, unstableIfFound = false) {
        publisherNodes << new NodeBuilder().'hudson.plugins.textfinder.TextFinderPublisher' {
            if (fileSet) {
                delegate.fileSet(fileSet)
            }
            delegate.regexp(regularExpression)
            delegate.alsoCheckConsoleOutput(alsoCheckConsoleOutput)
            delegate.succeedIfFound(succeedIfFound)
            delegate.unstableIfFound(unstableIfFound)
        }
    }

    /**
     * Searches for a regular expression in the console log and, if matched, executes a script.
     *
     * @since 1.19
     */
    @RequiresPlugin(id = 'postbuild-task')
    void postBuildTask(@DslContext(PostBuildTaskContext) Closure postBuildClosure) {
        PostBuildTaskContext postBuildContext = new PostBuildTaskContext()
        ContextHelper.executeInContext(postBuildClosure, postBuildContext)

        publisherNodes << new NodeBuilder().'hudson.plugins.postbuildtask.PostbuildTask' {
            tasks {
                postBuildContext.tasks.each { PostBuildTaskContext.PostBuildTask task ->
                    'hudson.plugins.postbuildtask.TaskProperties' {
                        logTexts {
                            'hudson.plugins.postbuildtask.LogProperties' {
                                logText(task.logText)
                                operator(task.operator)
                            }
                        }
                        EscalateStatus(task.escalateStatus)
                        RunIfJobSuccessful(task.runIfJobSuccessful)
                        script(task.script)
                    }
                }
            }
        }
    }

    /**
     * Aggregates downstream test results.
     *
     * @since 1.19
     */
    void aggregateDownstreamTestResults(String jobs = null, boolean includeFailedBuilds = false) {
        publisherNodes << new NodeBuilder().'hudson.tasks.test.AggregatedTestResultPublisher' {
            if (jobs) {
                delegate.jobs(jobs)
            }
            delegate.includeFailedBuilds(includeFailedBuilds)
        }
    }

    static enum Behavior {
        DoNothing(0),
        MarkUnstable(1),
        MarkFailed(2)

        final int value

        Behavior(int value) {
            this.value = value
        }
    }

    /**
     * Executes Groovy scripts after a build.
     *
     * @since 1.19
     */
    @RequiresPlugin(id = 'groovy-postbuild', minimumVersion = '2.2')
    void groovyPostBuild(String script, Behavior behavior = Behavior.DoNothing) {
        groovyPostBuild {
            delegate.script(script)
            delegate.behavior(behavior)
        }
    }

    /**
     * Executes Groovy scripts after a build.
     *
     * @since 1.37
     */
    @RequiresPlugin(id = 'groovy-postbuild', minimumVersion = '2.2')
    void groovyPostBuild(@DslContext(GroovyPostbuildContext) Closure groovyPostbuildClosure) {
        GroovyPostbuildContext groovyPostbuildContext = new GroovyPostbuildContext(jobManagement)
        ContextHelper.executeInContext(groovyPostbuildClosure, groovyPostbuildContext)

        publisherNodes << new NodeBuilder().'org.jvnet.hudson.plugins.groovypostbuild.GroovyPostbuildRecorder' {
            script {
                script(groovyPostbuildContext.script ?: '')
                sandbox(groovyPostbuildContext.sandbox)
                if (groovyPostbuildContext.classpath) {
                    classpath {
                        groovyPostbuildContext.classpath.each { value ->
                            entry {
                                url(value)
                            }
                        }
                    }
                }
            }
            behavior(groovyPostbuildContext.behavior.value)
        }
    }

    /**
     * Archives Javadoc artifacts.
     *
     * @since 1.19
     */
    @RequiresPlugin(id = 'javadoc')
    void archiveJavadoc(@DslContext(JavadocContext) Closure javadocClosure = null) {
        JavadocContext javadocContext = new JavadocContext()
        ContextHelper.executeInContext(javadocClosure, javadocContext)

        publisherNodes << new NodeBuilder().'hudson.tasks.JavadocArchiver' {
            javadocDir javadocContext.javadocDir
            keepAll javadocContext.keepAll
        }
    }

    /**
     * Marks files or directories outside of Jenkins as related to a build.
     *
     * @since 1.20
     */
    @RequiresPlugin(id = 'associated-files')
    void associatedFiles(String files = null) {
        publisherNodes << new NodeBuilder().'org.jenkinsci.plugins.associatedfiles.AssociatedFilesPublisher' {
            delegate.associatedFiles(files)
        }
    }

    /**
     * Publishes an Emma coverage report.
     *
     * @since 1.20
     */
    @RequiresPlugin(id = 'emma')
    void emma(String fileSet = '', @DslContext(EmmaContext) Closure emmaClosure = null) {
        EmmaContext emmaContext = new EmmaContext()
        ContextHelper.executeInContext(emmaClosure, emmaContext)

        publisherNodes << new NodeBuilder().'hudson.plugins.emma.EmmaPublisher' {
            includes(fileSet)
            healthReports {
                minClass(emmaContext.classRange.from)
                maxClass(emmaContext.classRange.to)
                minMethod(emmaContext.methodRange.from)
                maxMethod(emmaContext.methodRange.to)
                minBlock(emmaContext.blockRange.from)
                maxBlock(emmaContext.blockRange.to)
                minLine(emmaContext.lineRange.from)
                maxLine(emmaContext.lineRange.to)
                minCondition(emmaContext.conditionRange.from)
                maxCondition(emmaContext.conditionRange.to)
            }
        }
    }

    /**
     * Publishes Robot Framework test reports.
     *
     * @since 1.21
     */
    @RequiresPlugin(id = 'robot', minimumVersion = '1.4.3')
    void publishRobotFrameworkReports(@DslContext(RobotFrameworkContext) Closure robotClosure = null) {

        RobotFrameworkContext context = new RobotFrameworkContext(jobManagement)
        ContextHelper.executeInContext(robotClosure, context)

        publisherNodes << new NodeBuilder().'hudson.plugins.robot.RobotPublisher' {
            passThreshold(context.passThreshold)
            unstableThreshold(context.unstableThreshold)
            outputPath(context.outputPath)
            onlyCritical(context.onlyCritical)
            reportFileName(context.reportFileName)
            logFileName(context.logFileName)
            outputFileName(context.outputFileName)
            disableArchiveOutput(context.disableArchiveOutput)
            otherFiles {
                context.otherFiles.each { String file ->
                    string(file)
                }
            }
        }
    }

    /**
     * Adds a manual triggers for jobs that require intervention prior to execution.
     *
     * @since 1.21
     */
    @RequiresPlugin(id = 'build-pipeline-plugin')
    void buildPipelineTrigger(String downstreamProjectNames, @DslContext(BuildPipelineContext) Closure closure = null) {
        BuildPipelineContext buildPipelineContext = new BuildPipelineContext(jobManagement, item)
        ContextHelper.executeInContext(closure, buildPipelineContext)

        NodeBuilder nodeBuilder = new NodeBuilder()
        publisherNodes << nodeBuilder.'au.com.centrumsystems.hudson.plugin.buildpipeline.trigger.BuildPipelineTrigger' {
            delegate.downstreamProjectNames(downstreamProjectNames ?: '')
            configs(buildPipelineContext.parameterNodes)
        }
    }

    /**
     * Create commit status notifications on the commits based on the outcome of the build.
     *
     * @since 1.21
     */
    @RequiresPlugin(id = 'github')
    void githubCommitNotifier() {
        publisherNodes << new NodeBuilder().'com.cloudbees.jenkins.GitHubCommitNotifier'()
    }

    /**
     * Pushes tags or branches to a Git repository.
     *
     * @since 1.22
     */
    @RequiresPlugin(id = 'git', minimumVersion = '2.5.3')
    void git(@DslContext(GitPublisherContext) Closure gitPublisherClosure) {
        GitPublisherContext context = new GitPublisherContext(jobManagement)
        ContextHelper.executeInContext(gitPublisherClosure, context)

        publisherNodes << new NodeBuilder().'hudson.plugins.git.GitPublisher' {
            configVersion(2)
            pushMerge(context.pushMerge)
            pushOnlyIfSuccess(context.pushOnlyIfSuccess)
            forcePush(context.forcePush)
            tagsToPush(context.tags)
            branchesToPush(context.branches)
        }
    }

    /**
     * Sends build notification to Flowdock.
     *
     * For security reasons, do not use a hard-coded token. See
     * <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/Handling-Credentials">Handling Credentials</a> for
     * details about handling credentials in DSL scripts.
     *
     * @since 1.23
     */
    @RequiresPlugin(id = 'jenkins-flowdock-plugin')
    void flowdock(String token, @DslContext(FlowdockPublisherContext) Closure flowdockPublisherClosure = null) {
        FlowdockPublisherContext context = new FlowdockPublisherContext()
        ContextHelper.executeInContext(flowdockPublisherClosure, context)

        publisherNodes << new NodeBuilder().'com.flowdock.jenkins.FlowdockNotifier' {
            flowToken(token)
            notificationTags(context.notificationTags.join(','))
            chatNotification(context.chat)
            notifyMap {
                entry {
                    'com.flowdock.jenkins.BuildResult'('ABORTED')
                    'boolean'(context.aborted)
                }
                entry {
                    'com.flowdock.jenkins.BuildResult'('SUCCESS')
                    'boolean'(context.success)
                }
                entry {
                    'com.flowdock.jenkins.BuildResult'('FIXED')
                    'boolean'(context.fixed)
                }
                entry {
                    'com.flowdock.jenkins.BuildResult'('UNSTABLE')
                    'boolean'(context.unstable)
                }
                entry {
                    'com.flowdock.jenkins.BuildResult'('FAILURE')
                    'boolean'(context.failure)
                }
                entry {
                    'com.flowdock.jenkins.BuildResult'('NOT_BUILT')
                    'boolean'(context.notBuilt)
                }
            }
            notifySuccess(context.success)
            notifyFailure(context.failure)
            notifyFixed(context.fixed)
            notifyUnstable(context.unstable)
            notifyAborted(context.aborted)
            notifyNotBuilt(context.notBuilt)
        }
    }

    /**
     * Sends build notification to Flowdock.
     *
     * For security reasons, do not use a hard-coded token. See
     * <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/Handling-Credentials">Handling Credentials</a> for
     * details about handling credentials in DSL scripts.
     *
     * @since 1.23
     */
    @RequiresPlugin(id = 'jenkins-flowdock-plugin')
    void flowdock(String[] tokens, @DslContext(FlowdockPublisherContext) Closure flowdockPublisherClosure = null) {
        checkArgument(tokens != null && tokens.length > 0, 'Flowdock publish requires at least one flow token')

        flowdock(tokens.join(','), flowdockPublisherClosure)
    }

    /**
     * Notifies an Atlassian Stash instance of Jenkins builds in progress and of their results.
     *
     * @since 1.23
     */
    @RequiresPlugin(id = 'stashNotifier')
    void stashNotifier(@DslContext(StashNotifierContext) Closure stashNotifierClosure = null) {
        jobManagement.logPluginDeprecationWarning('stashNotifier', '1.11.6')

        StashNotifierContext context = new StashNotifierContext(jobManagement)
        ContextHelper.executeInContext(stashNotifierClosure, context)

        publisherNodes << new NodeBuilder().'org.jenkinsci.plugins.stashNotifier.StashNotifier' {
            stashServerBaseUrl(context.serverBaseUrl ?: '')
            if (jobManagement.isMinimumPluginVersionInstalled('stashNotifier', '1.9.0')) {
                credentialsId(context.credentialsId ?: '')
            } else {
                stashUserName()
                stashUserPassword()
            }
            ignoreUnverifiedSSLPeer(context.ignoreUnverifiedSSLCertificates)
            commitSha1(context.commitSha1 ?: '')
            includeBuildNumberInKey(context.keepRepeatedBuilds)
        }
    }

    /**
     * Add conditional post-build actions.
     *
     * If the <a href="https://wiki.jenkins-ci.org/display/JENKINS/Any+Build+Step+Plugin">Any Build Step Plugin</a> is
     * installed, build steps can be used along with publishers. When using versions older then 0.13 of the Flexible
     * Publish Plugin, only one build step or one publisher can be used.
     *
     * @since 1.26
     */
    @RequiresPlugin(id = 'flexible-publish', minimumVersion = '0.13')
    void flexiblePublish(@DslContext(FlexiblePublisherContext) Closure flexiblePublishClosure) {
        FlexiblePublisherContext context = new FlexiblePublisherContext(jobManagement, item)
        ContextHelper.executeInContext(flexiblePublishClosure, context)

        publisherNodes << new NodeBuilder().'org.jenkins__ci.plugins.flexible__publish.FlexiblePublisher' {
            delegate.publishers {
                context.conditionalActions.each { ConditionalActionsContext conditionalActionsContext ->
                    Node publisher = 'org.jenkins__ci.plugins.flexible__publish.ConditionalPublisher' {
                        publisherList(conditionalActionsContext.actions)
                        runner(class: conditionalActionsContext.runnerClass)
                        if (conditionalActionsContext.aggregationRunner) {
                            aggregationRunner(class: conditionalActionsContext.aggregationRunner)
                        }
                    }
                    publisher.append(toNamedNode('condition', conditionalActionsContext.runCondition))
                    if (conditionalActionsContext.aggregationCondition) {
                        publisher.append(
                                toNamedNode('aggregationCondition', conditionalActionsContext.aggregationCondition)
                        )
                    }
                }
            }
        }
    }

    /**
     * Add a summary to the build of the artifacts uploaded to a Maven repository.
     *
     * @since 1.23
     */
    @RequiresPlugin(id = 'maven-deployment-linker')
    void mavenDeploymentLinker(String regex) {
        publisherNodes << new NodeBuilder().'hudson.plugins.mavendeploymentlinker.MavenDeploymentLinkerRecorder' {
            regexp(regex)
        }
    }

    /**
     * Deletes files from the workspace after the build completed.
     *
     * @since 1.23
     */
    @RequiresPlugin(id = 'ws-cleanup')
    void wsCleanup(@DslContext(PostBuildCleanupContext) Closure closure = null) {
        PostBuildCleanupContext context = new PostBuildCleanupContext()
        ContextHelper.executeInContext(closure, context)

        publisherNodes << new NodeBuilder().'hudson.plugins.ws__cleanup.WsCleanup' {
            patterns(context.patternNodes)
            deleteDirs(context.deleteDirectories)
            cleanWhenSuccess(context.cleanWhenSuccess)
            cleanWhenUnstable(context.cleanWhenUnstable)
            cleanWhenFailure(context.cleanWhenFailure)
            cleanWhenNotBuilt(context.cleanWhenNotBuilt)
            cleanWhenAborted(context.cleanWhenAborted)
            notFailBuild(!context.failBuild)
            externalDelete(context.deleteCommand ?: '')
        }
    }

    /**
     * Uploads build artifacts to Amazon S3.
     *
     * @since 1.26
     * @deprecated use the <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/Automatically-Generated-DSL">
     *             Automatically Generated DSL</a> instead.
     */
    @RequiresPlugin(id = 's3', minimumVersion = '0.7')
    @Deprecated
    void s3(String profile, @DslContext(S3BucketPublisherContext) Closure s3PublisherClosure) {
        checkNotNullOrEmpty(profile, 'profile must be specified')

        S3BucketPublisherContext context = new S3BucketPublisherContext(jobManagement)
        ContextHelper.executeInContext(s3PublisherClosure, context)

        publisherNodes << new NodeBuilder().'hudson.plugins.s3.S3BucketPublisher' {
            profileName(profile)
            entries(context.entries)
            userMetadata(context.metadata)
        }
    }

    /**
     * Publishes FindBugs analysis results.
     *
     * @since 1.17
     */
    @RequiresPlugin(id = 'findbugs')
    void findbugs(String pattern, boolean isRankActivated = false,
                  @DslContext(StaticAnalysisContext) Closure staticAnalysisClosure = null) {
        StaticAnalysisContext staticAnalysisContext = new StaticAnalysisContext()
        ContextHelper.executeInContext(staticAnalysisClosure, staticAnalysisContext)

        publisherNodes << new NodeBuilder().'hudson.plugins.findbugs.FindBugsPublisher' {
            addStaticAnalysisContextAndPattern(delegate, staticAnalysisContext, pattern)
            delegate.isRankActivated(isRankActivated)
        }
    }

    /**
     * Publishes PMD analysis results.
     *
     * @since 1.17
     */
    @RequiresPlugin(id = 'pmd')
    void pmd(String pattern, @DslContext(StaticAnalysisContext) Closure staticAnalysisClosure = null) {
        publisherNodes << createDefaultStaticAnalysisNode(
                'hudson.plugins.pmd.PmdPublisher',
                staticAnalysisClosure,
                pattern
        )
    }

    /**
     * Publishes Checkstyle analysis results.
     *
     * @since 1.17
     */
    @RequiresPlugin(id = 'checkstyle')
    void checkstyle(String pattern, @DslContext(StaticAnalysisContext) Closure staticAnalysisClosure = null) {
        publisherNodes << createDefaultStaticAnalysisNode(
                'hudson.plugins.checkstyle.CheckStylePublisher',
                staticAnalysisClosure,
                pattern
        )
    }

    /**
     * Publishes duplicate code analysis results.
     *
     * @since 1.17
     */
    @RequiresPlugin(id = 'dry')
    void dry(String pattern, highThreshold = 50, normalThreshold = 25,
             @DslContext(StaticAnalysisContext) Closure staticAnalysisClosure = null) {
        StaticAnalysisContext staticAnalysisContext = new StaticAnalysisContext()
        ContextHelper.executeInContext(staticAnalysisClosure, staticAnalysisContext)

        publisherNodes << new NodeBuilder().'hudson.plugins.dry.DryPublisher' {
            addStaticAnalysisContextAndPattern(delegate, staticAnalysisContext, pattern)
            delegate.highThreshold(highThreshold)
            delegate.normalThreshold(normalThreshold)
        }
    }

    /**
     * Scans the workspace for open tasks.
     *
     * @since 1.17
     */
    @RequiresPlugin(id = 'tasks', minimumVersion = '4.41')
    void tasks(String pattern, excludePattern = '', high = '', normal = '', low = '', ignoreCase = false,
               @DslContext(TaskScannerContext) Closure closure = null) {
        TaskScannerContext context = new TaskScannerContext(jobManagement)
        ContextHelper.executeInContext(closure, context)

        publisherNodes << new NodeBuilder().'hudson.plugins.tasks.TasksPublisher' {
            addStaticAnalysisContextAndPattern(delegate, context, pattern)
            delegate.high(high)
            delegate.normal(normal)
            delegate.low(low)
            delegate.ignoreCase(ignoreCase)
            delegate.excludePattern(excludePattern)
            asRegexp(context.regularExpression)
        }
    }

    /**
     * Publishes CCM analysis results.
     *
     * @since 1.17
     */
    @RequiresPlugin(id = 'ccm')
    void ccm(String pattern, @DslContext(StaticAnalysisContext) Closure staticAnalysisClosure = null) {
        publisherNodes << createDefaultStaticAnalysisNode(
                'hudson.plugins.ccm.CcmPublisher',
                staticAnalysisClosure,
                pattern
        )
    }

    /**
     * Publishes Android Lint results.
     *
     * @since 1.17
     */
    @RequiresPlugin(id = 'android-lint')
    void androidLint(String pattern, @DslContext(StaticAnalysisContext) Closure staticAnalysisClosure = null) {
        publisherNodes << createDefaultStaticAnalysisNode(
                'org.jenkinsci.plugins.android__lint.LintPublisher',
                staticAnalysisClosure,
                pattern
        )
    }

    /**
     * Publishes OWASP dependency check results.
     *
     * @since 1.17
     */
    @RequiresPlugin(id = 'dependency-check-jenkins-plugin')
    void dependencyCheck(String pattern, @DslContext(StaticAnalysisContext) Closure staticAnalysisClosure = null) {
        publisherNodes << createDefaultStaticAnalysisNode(
                'org.jenkinsci.plugins.DependencyCheck.DependencyCheckPublisher',
                staticAnalysisClosure,
                pattern
        )
    }

    /**
     * Scans for compiler warnings.
     *
     * The first argument specifies the name of the console parsers to use. The second argument specifies a map of log
     * file parsers, the key is the name of the parser and the value defines the files to scan. The parser are either
     * built-in ones or custom parsers defined in the global Jenkins configuration.
     *
     * @since 1.17
     */
    @RequiresPlugin(id = 'warnings', minimumVersion = '4.0')
    void warnings(List consoleParsers, Map parserConfigurations = [:],
                  @DslContext(WarningsContext) Closure warningsClosure = null) {
        WarningsContext warningsContext = new WarningsContext()
        ContextHelper.executeInContext(warningsClosure, warningsContext)

        NodeBuilder nodeBuilder = new NodeBuilder()
        publisherNodes << nodeBuilder.'hudson.plugins.warnings.WarningsPublisher' {
            addStaticAnalysisContext(delegate, warningsContext)
            includePattern(warningsContext.includePattern)
            excludePattern(warningsContext.excludePattern)
            nodeBuilder.consoleParsers {
                (consoleParsers ?: []).each { name ->
                    nodeBuilder.'hudson.plugins.warnings.ConsoleParser' {
                        parserName(name)
                    }
                }
            }
            nodeBuilder.parserConfigurations {
                (parserConfigurations ?: [:]).each { name, filePattern ->
                    nodeBuilder.'hudson.plugins.warnings.ParserConfiguration' {
                        pattern(filePattern)
                        parserName(name)
                    }
                }
            }
        }
    }

    /**
     * Publishes combined analysis results.
     *
     * @since 1.26
     */
    @RequiresPlugin(id = 'analysis-collector')
    void analysisCollector(@DslContext(AnalysisCollectorContext) Closure analysisCollectorClosure = null) {
        AnalysisCollectorContext analysisCollectorContext = new AnalysisCollectorContext()
        ContextHelper.executeInContext(analysisCollectorClosure, analysisCollectorContext)

        publisherNodes << new NodeBuilder().'hudson.plugins.analysis.collector.AnalysisPublisher' {
            addStaticAnalysisContext(delegate, analysisCollectorContext)
            isCheckStyleDeactivated(!analysisCollectorContext.includeCheckstyle)
            isDryDeactivated(!analysisCollectorContext.includeDry)
            isFindBugsDeactivated(!analysisCollectorContext.includeFindbugs)
            isPmdDeactivated(!analysisCollectorContext.includePmd)
            isOpenTasksDeactivated(!analysisCollectorContext.includeTasks)
            isWarningsDeactivated(!analysisCollectorContext.includeWarnings)
        }
    }

    /**
     * Execute a set of scripts at the end of the build.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'postbuildscript', minimumVersion = '0.17')
    @Deprecated
    void postBuildScripts(@DslContext(PostBuildScriptsContext) Closure closure) {
        PostBuildScriptsContext context = new PostBuildScriptsContext(jobManagement, item)
        ContextHelper.executeInContext(closure, context)

        publisherNodes << new NodeBuilder().'org.jenkinsci.plugins.postbuildscript.PostBuildScript' {
            buildSteps(context.stepContext.stepNodes)
            scriptOnlyIfSuccess(context.onlyIfBuildSucceeds)
            scriptOnlyIfFailure(context.onlyIfBuildFails)
            markBuildUnstable(context.markBuildUnstable)
            if (item instanceof MatrixJob) {
                executeOn(context.executeOn)
            }
        }
    }

    /**
     * Triggers SonarQube analysis.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'sonar')
    void sonar(@DslContext(SonarContext) Closure sonarClosure = null) {
        SonarContext sonarContext = new SonarContext()
        ContextHelper.executeInContext(sonarClosure, sonarContext)

        publisherNodes << new NodeBuilder().'hudson.plugins.sonar.SonarPublisher' {
            if (sonarContext.installationName) {
                installationName(sonarContext.installationName)
            }
            jdk(sonarContext.jdk)
            branch(sonarContext.branch ?: '')
            language()
            mavenOpts()
            jobAdditionalProperties(sonarContext.additionalProperties ?: '')
            if (sonarContext.overrideTriggers) {
                triggers {
                    skipScmCause(false)
                    skipUpstreamCause(false)
                    envVar(sonarContext.sonarTriggersContext.skipIfEnvironmentVariable ?: '')
                }
            }
            mavenInstallationName(sonarContext.mavenInstallation)
            rootPom()
            settings(class: 'jenkins.mvn.DefaultSettingsProvider')
            globalSettings(class: 'jenkins.mvn.DefaultGlobalSettingsProvider')
            usePrivateRepository(false)
        }
    }

    /**
     * Allows to automatically reschedule a build after a failure. By default a progressive
     * delay with an increment of 5 minutes and a maximum of 3 hours is used.
     *
     * @since 1.33
     */
    @RequiresPlugin(id = 'naginator', minimumVersion = '1.15')
    void retryBuild(@DslContext(NaginatorContext) Closure naginatorClosure = null) {
        NaginatorContext naginatorContext = new NaginatorContext()
        ContextHelper.executeInContext(naginatorClosure, naginatorContext)

        Node naginatorNode = new NodeBuilder().'com.chikli.hudson.plugin.naginator.NaginatorPublisher' {
            regexpForRerun()
            rerunIfUnstable(naginatorContext.rerunIfUnstable)
            rerunMatrixPart(false)
            checkRegexp(false)
            maxSchedule(naginatorContext.retryLimit)
        }
        naginatorNode.append(naginatorContext.delay)
        publisherNodes << naginatorNode
    }

    /**
     * Publishes builds to another Jenkins instance.
     *
     * @since 1.33
     */
    @RequiresPlugin(id = 'build-publisher', minimumVersion = '1.20')
    void publishBuild(@DslContext(PublishBuildContext) Closure contextClosure = null) {
        PublishBuildContext publishBuildContext = new PublishBuildContext()
        ContextHelper.executeInContext(contextClosure, publishBuildContext)

        publisherNodes << new NodeBuilder().'hudson.plugins.build__publisher.BuildPublisher' {
            publishUnstableBuilds(publishBuildContext.publishUnstable)
            publishFailedBuilds(publishBuildContext.publishFailed)
            if (publishBuildContext.discardOldBuilds) {
                logRotator {
                    daysToKeep(publishBuildContext.daysToKeep)
                    numToKeep(publishBuildContext.numToKeep)
                    artifactDaysToKeep(publishBuildContext.artifactDaysToKeep)
                    artifactNumToKeep(publishBuildContext.artifactNumToKeep)
                }
            }
        }
    }

    /**
     * Sends notifications to Mattermost.
     *
     * @since 1.44
     * @deprecated use the <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/Automatically-Generated-DSL">
     *     Automatically Generated DSL</a> instead
     */
    @RequiresPlugin(id = 'mattermost', minimumVersion = '1.5.0')
    @Deprecated
    void mattermost(@DslContext(MattermostPublisherContext) Closure mattermostClosure = null) {
        MattermostPublisherContext mattermostContext = new MattermostPublisherContext()
        ContextHelper.executeInContext(mattermostClosure, mattermostContext)

        publisherNodes << new NodeBuilder().'jenkins.plugins.mattermost.MattermostNotifier' {
            startNotification(mattermostContext.notifyBuildStart)
            notifySuccess(mattermostContext.notifySuccess)
            notifyAborted(mattermostContext.notifyAborted)
            notifyNotBuilt(mattermostContext.notifyNotBuilt)
            notifyUnstable(mattermostContext.notifyUnstable)
            notifyFailure(mattermostContext.notifyFailure)
            notifyBackToNormal(mattermostContext.notifyBackToNormal)
            notifyRepeatedFailure(mattermostContext.notifyRepeatedFailure)
            includeTestSummary(mattermostContext.includeTestSummary)
            showCommitList(mattermostContext.showCommitList)
            includeCustomMessage(mattermostContext.customMessage as boolean)
            endpoint(mattermostContext.endpoint ?: '')
            room(mattermostContext.room ?: '')
            icon(mattermostContext.icon ?: '')
            customMessage(mattermostContext.customMessage ?: '')
        }
    }

    /**
     * Send artifacts to an SSH server (using SFTP) and/or execute commands over SSH.
     *
     * @since 1.34
     */
    @RequiresPlugin(id = 'publish-over-ssh', minimumVersion = '1.12')
    void publishOverSsh(@DslContext(PublishOverSshContext) Closure publishOverSshClosure) {
        PublishOverSshContext publishOverSshContext = new PublishOverSshContext()
        ContextHelper.executeInContext(publishOverSshClosure, publishOverSshContext)

        checkArgument(!publishOverSshContext.servers.empty, 'At least 1 server must be configured')

        publisherNodes << new NodeBuilder().'jenkins.plugins.publish__over__ssh.BapSshPublisherPlugin' {
            consolePrefix('SSH: ')
            currentNode.append(publishOverSshContext.node)
        }
    }

    /**
     * Uploads a dSYM file to Crittercism.
     *
     * @since 1.38
     */
    @RequiresPlugin(id = 'crittercism-dsym', minimumVersion = '1.1')
    void crittercismDsymUpload(@DslContext(CrittercismDsymRecorderContext) Closure closure) {
        CrittercismDsymRecorderContext dsymUploadContext = new CrittercismDsymRecorderContext()
        ContextHelper.executeInContext(closure, dsymUploadContext)

        publisherNodes << new NodeBuilder().'org.jenkinsci.plugins.crittercism__dsym.CrittercismDsymRecorder' {
            apiKey(dsymUploadContext.apiKey ?: '')
            appID(dsymUploadContext.appID ?: '')
            filePath(dsymUploadContext.filePath ?: '')
        }
    }

    /**
     * Runs a job after all immediate downstream jobs have completed.
     *
     * @since 1.35
     */
    @RequiresPlugin(id = 'join', minimumVersion = '1.15')
    void joinTrigger(@DslContext(JoinTriggerContext) Closure joinTriggerClosure) {
        jobManagement.logPluginDeprecationWarning('join', '1.21')

        JoinTriggerContext joinTriggerContext = new JoinTriggerContext(jobManagement, item)
        ContextHelper.executeInContext(joinTriggerClosure, joinTriggerContext)

        publisherNodes << new NodeBuilder().'join.JoinTrigger' {
            joinProjects(joinTriggerContext.projects.join(', '))
            joinPublishers(joinTriggerContext.publisherContext.publisherNodes)
            if (jobManagement.isMinimumPluginVersionInstalled('join', '1.20')) {
                resultThreshold {
                    name(joinTriggerContext.resultThreshold)
                    ordinal(THRESHOLD_ORDINAL_MAP[joinTriggerContext.resultThreshold])
                    color(THRESHOLD_COLOR_MAP[joinTriggerContext.resultThreshold])
                    completeBuild(THRESHOLD_COMPLETED_BUILD[joinTriggerContext.resultThreshold])
                }
            } else {
                evenIfDownstreamUnstable(joinTriggerContext.evenIfDownstreamUnstable)
            }
        }
    }

    /**
     * Uploads Debian packages.
     *
     * @since 1.36
     */
    @RequiresPlugin(id = 'debian-package-builder', minimumVersion = '1.6.7')
    void debianPackage(String repoId, @DslContext(DebianPackagePublisherContext) Closure closure = null) {
        Preconditions.checkNotNullOrEmpty(repoId, 'repoId must be specified')

        DebianPackagePublisherContext context = new DebianPackagePublisherContext()
        ContextHelper.executeInContext(closure, context)

        publisherNodes << new NodeBuilder().'ru.yandex.jenkins.plugins.debuilder.DebianPackagePublisher' {
            delegate.repoId(repoId)
            commitMessage(context.commitMessage ?: '')
            commitChanges(context.commitMessage as boolean)
        }
    }

    private static Node createDefaultStaticAnalysisNode(String publisherClassName, Closure staticAnalysisClosure,
                                                        String pattern) {
        StaticAnalysisContext staticAnalysisContext = new StaticAnalysisContext()
        ContextHelper.executeInContext(staticAnalysisClosure, staticAnalysisContext)

        new NodeBuilder()."${publisherClassName}" {
            addStaticAnalysisContextAndPattern(delegate, staticAnalysisContext, pattern)
        }
    }

    /**
     * Sends build status and coverage information to Pharbicator.
     *
     * @since 1.39
     */
    @RequiresPlugin(id = 'phabricator-plugin', minimumVersion = '1.8.1')
    void phabricatorNotifier(@DslContext(PhabricatorNotifierContext) Closure phabricatorNotifierClosure = null) {
        PhabricatorNotifierContext phabricatorNotifierContext = new PhabricatorNotifierContext()
        ContextHelper.executeInContext(phabricatorNotifierClosure, phabricatorNotifierContext)

        publisherNodes << new NodeBuilder().'com.uber.jenkins.phabricator.PhabricatorNotifier' {
            commentOnSuccess(phabricatorNotifierContext.commentOnSuccess)
            commentWithConsoleLinkOnFailure(phabricatorNotifierContext.commentWithConsoleLinkOnFailure)
            commentFile(phabricatorNotifierContext.commentFile ?: '')
            commentSize(phabricatorNotifierContext.commentSize)
            preserveFormatting(phabricatorNotifierContext.preserveFormatting)
            uberallsEnabled(phabricatorNotifierContext.enableUberalls)
        }
    }

    /**
     * Deploys artifacts from the build workspace to remote locations.
     *
     * @since 1.39
     */
    @RequiresPlugin(id = 'artifactdeployer', minimumVersion = '0.33')
    @Deprecated
    void artifactDeployer(@DslContext(ArtifactDeployerPublisherContext) Closure closure) {
        ArtifactDeployerPublisherContext context = new ArtifactDeployerPublisherContext()
        ContextHelper.executeInContext(closure, context)

        publisherNodes << new NodeBuilder().'org.jenkinsci.plugins.artifactdeployer.ArtifactDeployerPublisher' {
            entries {
                context.entries.each { ArtifactDeployerContext entry ->
                    'org.jenkinsci.plugins.artifactdeployer.ArtifactDeployerEntry' {
                        includes(entry.includes ?: '')
                        basedir(entry.baseDir ?: '')
                        excludes(entry.excludes ?: '')
                        remote(entry.remoteFileLocation ?: '')
                        flatten(entry.flatten)
                        deleteRemote(entry.cleanUp)
                        deleteRemoteArtifacts(entry.deleteRemoteArtifacts)
                        deleteRemoteArtifactsByScript(entry.deleteRemoteArtifactsByScript as boolean)
                        if (entry.deleteRemoteArtifactsByScript) {
                            groovyExpression(entry.deleteRemoteArtifactsByScript)
                        }
                        failNoFilesDeploy(entry.failIfNoFiles)
                    }
                }
            }
            deployEvenBuildFail(context.deployIfFailed)
        }
    }

    /**
     * Generates trend report for SLOCCount and cloc.
     *
     * @since 1.41
     */
    @RequiresPlugin(id = 'sloccount', minimumVersion = '1.20')
    void slocCount(@DslContext(SlocCountContext) Closure closure) {
        SlocCountContext context = new SlocCountContext()
        ContextHelper.executeInContext(closure, context)

        publisherNodes << new NodeBuilder().'hudson.plugins.sloccount.SloccountPublisher' {
            pattern(context.pattern ?: '')
            encoding(context.encoding ?: '')
            numBuildsInGraph(context.buildsInGraph)
            commentIsCode(context.commentIsCode)
            ignoreBuildFailure(context.ignoreBuildFailure)
        }
    }

    /**
     * Performs subversion tagging (technically speaking svn copy) on successful builds.
     *
     * @since 1.41
     */
    @RequiresPlugin(id = 'svn-tag', minimumVersion = '1.18')
    @Deprecated
    void svnTag(@DslContext(SubversionTagContext) Closure closure) {
        SubversionTagContext context = new SubversionTagContext()
        ContextHelper.executeInContext(closure, context)

        publisherNodes << new NodeBuilder().'hudson.plugins.svn__tag.SvnTagPublisher' {
            tagBaseURL(context.baseUrl)
            tagComment(context.comment)
            tagDeleteComment(context.deleteComment)
        }
    }

    /**
     * Publishes Cucumber results as HTML reports.
     *
     * @since 1.41
     */
    @RequiresPlugin(id = 'cucumber-reports', minimumVersion = '0.6.0')
    void cucumberReports(@DslContext(CucumberReportsContext) Closure closure) {
        CucumberReportsContext context = new CucumberReportsContext()
        ContextHelper.executeInContext(closure, context)

        publisherNodes << new NodeBuilder().'net.masterthought.jenkins.CucumberReportPublisher' {
            jsonReportDirectory(context.jsonReportPath ?: '')
            pluginUrlPath(context.pluginUrlPath ?: '')
            fileIncludePattern(context.fileIncludePattern ?: '')
            fileExcludePattern(context.fileExcludePattern ?: '')
            skippedFails(context.failOnSkippedSteps)
            pendingFails(context.failOnPendingSteps)
            undefinedFails(context.failOnUndefinedSteps)
            missingFails(context.failOnMissingSteps)
            noFlashCharts(context.turnOffFlashCharts)
            ignoreFailedTests(context.ignoreFailedTests)
            parallelTesting(context.parallelTesting)
        }
    }

    /**
     * Publishes Cucumber test results.
     *
     * @since 1.41
     */
    @RequiresPlugin(id = 'cucumber-testresult-plugin', minimumVersion = '0.8.2')
    void cucumberTestResults(@DslContext(CucumberTestResultContext) Closure closure) {
        CucumberTestResultContext context = new CucumberTestResultContext()
        ContextHelper.executeInContext(closure, context)

        publisherNodes << new NodeBuilder().
                'org.jenkinsci.plugins.cucumber.jsontestsupport.CucumberTestResultArchiver' {
            testResults(context.jsonReportFiles ?: '')
            ignoreBadSteps(context.ignoreBadSteps)
        }
    }

    /**
     * Updates relevant Mantis issues.
     *
     * @since 1.41
     */
    @RequiresPlugin(id = 'mantis', minimumVersion = '0.26')
    void mantis(@DslContext(MantisContext) Closure closure) {
        MantisContext context = new MantisContext()
        ContextHelper.executeInContext(closure, context)

        publisherNodes << new NodeBuilder().'hudson.plugins.mantis.MantisIssueUpdater' {
            keepNotePrivate(context.keepNotePrivate)
            recordChangelog(context.recordChangelogToNote)
        }
    }

    /**
     * Deploys artifacts to Weblogic environments.
     *
     * @since 1.41
     */
    @RequiresPlugin(id = 'weblogic-deployer-plugin', minimumVersion = '2.9.1')
    void deployToWeblogic(@DslContext(WeblogicDeployerContext) Closure weblogicClosure) {
        WeblogicDeployerContext context = new WeblogicDeployerContext()
        ContextHelper.executeInContext(weblogicClosure, context)

        publisherNodes << new NodeBuilder().'org.jenkinsci.plugins.deploy.weblogic.WeblogicDeploymentPlugin' {
            mustExitOnFailure(context.mustExitOnFailure)
            forceStopOnFirstFailure(context.forceStopOnFirstFailure)
            selectedDeploymentStrategyIds {
                context.weblogicDeployerPolicyContext.deploymentPolicies.each { strategyId ->
                    string(strategyId)
                }
            }
            isDeployingOnlyWhenUpdates(context.deployingOnlyWhenUpdates)
            deployedProjectsDependencies(context.deployedProjectsDependencies ?: '')
            delegate.tasks(context.taskNodes)
        }
    }

    /**
     * Measures Ruby code complexity.
     *
     * @since 1.42
     */
    @RequiresPlugin(id = 'rubyMetrics', minimumVersion = '1.6.3')
    void flog(@DslContext(FlogContext) Closure closure = null) {
        FlogContext context = new FlogContext()
        ContextHelper.executeInContext(closure, context)

        publisherNodes << new NodeBuilder().'hudson.plugins.rubyMetrics.flog.FlogPublisher' {
            rbDirectories(context.rubyDirectories.join('\n'))
            splittedDirectories {
                (context.rubyDirectories ?: ['.']).each {
                    delegate.string(it)
                }
            }
        }
    }

    /**
     * Publishes Rails notes.
     *
     * @since 1.42
     */
    @RequiresPlugin(id = 'rubyMetrics', minimumVersion = '1.6.3')
    void railsNotes(@DslContext(RailsTaskContext) Closure closure = null) {
        RailsTaskContext context = new RailsTaskContext()
        ContextHelper.executeInContext(closure, context)

        publisherNodes <<
                createRailsTaskNode('hudson.plugins.rubyMetrics.railsNotes.RailsNotesPublisher', 'notes', context)
    }

    /**
     * Publishes Rails stats.
     *
     * @since 1.42
     */
    @RequiresPlugin(id = 'rubyMetrics', minimumVersion = '1.6.3')
    void railsStats(@DslContext(RailsTaskContext) Closure closure) {
        RailsTaskContext context = new RailsTaskContext()
        ContextHelper.executeInContext(closure, context)

        publisherNodes <<
                createRailsTaskNode('hudson.plugins.rubyMetrics.railsStats.RailsStatsPublisher', 'stats', context)
    }

    private Node createRailsTaskNode(String publisherName, String task, RailsTaskContext context) {
        new NodeBuilder()."$publisherName" {
            rakeInstallation(context.rakeVersion ?: '')
            rakeWorkingDir(context.rakeWorkingDirectory ?: '')
            delegate.task(task)
            rake {
                rakeInstallation(context.rakeVersion ?: '')
                rakeWorkingDir(context.rakeWorkingDirectory ?: '')
                delegate.tasks(task)
                silent(true)
                bundleExec(true)
            }
        }
    }

    /**
     * Publishes Selenium reports.
     *
     * @since 1.42
     */
    @RequiresPlugin(id = 'seleniumhq', minimumVersion = '0.4')
    void seleniumReport(String testReportPattern = null, @DslContext(SeleniumReportContext) Closure closure = null) {
        SeleniumReportContext context = new SeleniumReportContext()
        ContextHelper.executeInContext(closure, context)

        publisherNodes << new NodeBuilder().'hudson.plugins.seleniumhq.SeleniumhqPublisher' {
            testResults(testReportPattern ?: '')
            useTestCommands(context.useTestCommands)
        }
    }

    /**
     * Publishes Selenium HTML reports.
     *
     * @since 1.42
     */
    @RequiresPlugin(id = 'seleniumhtmlreport', minimumVersion = '1.0')
    void seleniumHtmlReport(String testResultLocation = 'target',
                            @DslContext(SeleniumHtmlReportContext) Closure closure = null) {
        SeleniumHtmlReportContext context = new SeleniumHtmlReportContext()
        ContextHelper.executeInContext(closure, context)

        publisherNodes << new NodeBuilder().'org.jvnet.hudson.plugins.seleniumhtmlreport.SeleniumHtmlReportPublisher' {
            SELENIUM__REPORTS__TARGET('seleniumReports')
            testResultsDir(testResultLocation ?: '')
            failureIfExceptionOnParsingResultFiles(context.failOnExceptions)
        }
    }

    /**
     * Parses RCov HTML report files and shows them in Jenkins with a trend graph.
     *
     * @since 1.42
     */
    @RequiresPlugin(id = 'rubyMetrics', minimumVersion = '1.6.3')
    void rcov(@DslContext(RcovContext) Closure closure) {
        RcovContext context = new RcovContext()
        ContextHelper.executeInContext(closure, context)

        publisherNodes << new NodeBuilder().'hudson.plugins.rubyMetrics.rcov.RcovPublisher' {
            reportDir(context.reportDirectory ?: '')
            targets {
                context.entries.each { String key, RcovContext.MetricEntry entry ->
                    'hudson.plugins.rubyMetrics.rcov.model.MetricTarget' {
                        metric(key)
                        healthy(entry.healthy)
                        unhealthy(entry.unhealthy)
                        unstable(entry.unstable)
                    }
                }
            }
        }
    }

    /**
     * Publishes Clover PHP coverage reports.
     *
     * @since 1.43
     */
    @RequiresPlugin(id = 'cloverphp', minimumVersion = '0.5')
    void cloverPHP(String xmlLocation, @DslContext(CloverPhpContext) Closure closure = null) {
        checkNotNullOrEmpty(xmlLocation, 'xmlLocation must be specified')

        CloverPhpContext context = new CloverPhpContext()
        ContextHelper.executeInContext(closure, context)

        publisherNodes << new NodeBuilder().'org.jenkinsci.plugins.cloverphp.CloverPHPPublisher' {
            delegate.xmlLocation(xmlLocation)
            publishHtmlReport(context.publishHtmlReport)
            if (context.reportDirectory) {
                reportDir(context.reportDirectory)
            }
            disableArchiving(context.publishHtmlReportContext.disableArchiving)
            healthyTarget {
                if (context.healthyMethodCoverage != null) {
                    methodCoverage(context.healthyMethodCoverage)
                }
                if (context.healthyStatementCoverage != null) {
                    statementCoverage(context.healthyStatementCoverage)
                }
            }
            unhealthyTarget {
                if (context.unhealthyMethodCoverage != null) {
                    methodCoverage(context.unhealthyMethodCoverage)
                }
                if (context.unhealthyStatementCoverage != null) {
                    statementCoverage(context.unhealthyStatementCoverage)
                }
            }
            failingTarget {
                if (context.unstableMethodCoverage != null) {
                    methodCoverage(context.unstableMethodCoverage)
                }
                if (context.unstableStatementCoverage != null) {
                    statementCoverage(context.unstableStatementCoverage)
                }
            }
        }
    }

    /**
     * Changes the expression of Mr. Jenkins in the background when your builds fail.
     *
     * @since 1.43
     */
    @RequiresPlugin(id = 'emotional-jenkins-plugin', minimumVersion = '1.2')
    void emotional() {
        publisherNodes << new NodeBuilder().'org.jenkinsci.plugins.emotional__jenkins.EmotionalJenkinsPublisher'()
    }

    /**
    * Updates JIRA issues.
    *
    * @since 1.45
    */
    @RequiresPlugin(id = 'jira', minimumVersion = '1.39')
    void jiraIssueUpdater() {
       publisherNodes << new NodeBuilder().'hudson.plugins.jira.JiraIssueUpdater'()
    }

     /**
     * Marks a JIRA version as released.
     *
     * @since 1.45
     */
     @RequiresPlugin(id = 'jira', minimumVersion = '1.39')
     void releaseJiraVersion(@DslContext(ReleaseJiraVersionContext) Closure closure) {
        ReleaseJiraVersionContext context = new ReleaseJiraVersionContext()
        ContextHelper.executeInContext(closure, context)

        publisherNodes << new NodeBuilder().'hudson.plugins.jira.JiraReleaseVersionUpdater' {
            jiraProjectKey(context.projectKey ?: '')
            jiraRelease(context.release ?: '')
        }
     }

      /**
      * Moves a set of JIRA issues to a new version.
      *
      * @since 1.45
      */
      @RequiresPlugin(id = 'jira', minimumVersion = '1.39')
      void moveJiraIssues(@DslContext(MoveJiraIssuesContext) Closure closure) {
         MoveJiraIssuesContext context = new MoveJiraIssuesContext()
         ContextHelper.executeInContext(closure, context)

         publisherNodes << new NodeBuilder().'hudson.plugins.jira.JiraIssueMigrator' {
             jiraProjectKey(context.projectKey ?: '')
             jiraRelease(context.release ?: '')
             jiraReplaceVersion(context.replaceVersion ?: '')
             jiraQuery(context.query ?: '')
         }
     }

    /**
    * Creates a JIRA version.
    *
    * @since 1.45
    */
    @RequiresPlugin(id = 'jira', minimumVersion = '1.39')
    void createJiraVersion(@DslContext(CreateJiraVersionContext) Closure closure) {
       CreateJiraVersionContext context = new CreateJiraVersionContext()
       ContextHelper.executeInContext(closure, context)

       publisherNodes << new NodeBuilder().'hudson.plugins.jira.JiraVersionCreator' {
           jiraProjectKey(context.projectKey ?: '')
           jiraVersion(context.version ?: '')
       }
    }

    /**
    * Creates a JIRA issue.
    *
    * @since 1.45
    */
    @RequiresPlugin(id = 'jira', minimumVersion = '1.39')
    void createJiraIssue(@DslContext(CreateJiraIssueContext) Closure closure) {
       CreateJiraIssueContext context = new CreateJiraIssueContext()
       ContextHelper.executeInContext(closure, context)

       publisherNodes << new NodeBuilder().'hudson.plugins.jira.JiraCreateIssueNotifier' {
           projectKey(context.projectKey ?: '')
           testDescription(context.testDescription ?: '')
           assignee(context.assignee ?: '')
           component(context.component ?: '')
       }
    }

    /**
     * Parses the console output and highlights error/warning/info lines.
     *
     * @since 1.46
     */
    @RequiresPlugin(id = 'log-parser', minimumVersion = '2.0')
    void consoleParsing(@DslContext(LogParserContext) Closure closure) {
        LogParserContext context = new LogParserContext()
        ContextHelper.executeInContext(closure, context)

        checkArgument(context.globalRules || context.projectRules, 'No rule path specified')
        checkArgument(!context.globalRules || !context.projectRules, 'Only one rule path must be specified')

        publisherNodes << new NodeBuilder().'hudson.plugins.logparser.LogParserPublisher' {
            unstableOnWarning(context.unstableOnWarning)
            failBuildOnError(context.failBuildOnError)
            showGraphs(context.showGraphs)
            if (context.projectRules) {
                projectRulePath(context.projectRules ?: '')
                useProjectRule(true)
            } else {
                parsingRulesPath(context.globalRules ?: '')
                useProjectRule(false)
            }
        }
    }

    @SuppressWarnings('NoDef')
    private static addStaticAnalysisContext(def nodeBuilder, StaticAnalysisContext context) {
        nodeBuilder.with {
            healthy(context.healthy ?: '')
            unHealthy(context.unHealthy ?: '')
            thresholdLimit(context.thresholdLimit)
            defaultEncoding(context.defaultEncoding)
            canRunOnFailed(context.canRunOnFailed)
            useStableBuildAsReference(context.useStableBuildAsReference)
            useDeltaValues(context.useDeltaValues)
            thresholds {
                context.thresholdMap.each { threshold, values ->
                    values.each { value, num ->
                        nodeBuilder."${threshold}${value.capitalize()}"(num)
                    }
                }
            }
            shouldDetectModules(context.shouldDetectModules)
            dontComputeNew(context.dontComputeNew)
            doNotResolveRelativePaths(context.doNotResolveRelativePaths)
        }
    }

    @SuppressWarnings('NoDef')
    private static addStaticAnalysisPattern(def nodeBuilder, String pattern) {
        nodeBuilder.pattern(pattern)
    }

    @SuppressWarnings('NoDef')
    private static addStaticAnalysisContextAndPattern(def nodeBuilder, StaticAnalysisContext context, String pattern) {
        addStaticAnalysisContext(nodeBuilder, context)
        addStaticAnalysisPattern(nodeBuilder, pattern)
    }
}
