package javaposse.jobdsl.dsl.helpers.publisher

import com.google.common.base.Preconditions
import com.google.common.base.Strings
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder
import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.AbstractExtensibleContext
import javaposse.jobdsl.dsl.helpers.common.BuildPipelineContext
import javaposse.jobdsl.dsl.helpers.common.DownstreamContext
import javaposse.jobdsl.dsl.helpers.step.PublishOverSshContext

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Strings.isNullOrEmpty

class PublisherContext extends AbstractExtensibleContext {
    List<Node> publisherNodes = []

    PublisherContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    @Override
    protected void addExtensionNode(Node node) {
        publisherNodes << node
    }

    void extendedEmail(String recipients = null, @DslContext(EmailContext) Closure emailClosure = null) {
        extendedEmail(recipients, null, emailClosure)
    }

    void extendedEmail(String recipients, String subjectTemplate,
                       @DslContext(EmailContext) Closure emailClosure = null) {
        extendedEmail(recipients, subjectTemplate, null, emailClosure)
    }

    @RequiresPlugin(id = 'email-ext')
    void extendedEmail(String recipients, String subjectTemplate, String contentTemplate,
                       @DslContext(EmailContext) Closure emailClosure = null) {
        EmailContext emailContext = new EmailContext()
        ContextHelper.executeInContext(emailClosure, emailContext)

        // Validate that we have the typical triggers, if nothing is provided
        if (emailContext.emailTriggers.isEmpty()) {
            emailContext.emailTriggers << new EmailContext.EmailTrigger('Failure')
            emailContext.emailTriggers << new EmailContext.EmailTrigger('Success')
        }

        // Now that the context has what we need
        Node emailNode = new NodeBuilder().'hudson.plugins.emailext.ExtendedEmailPublisher' {
            recipientList recipients != null ? recipients : '$DEFAULT_RECIPIENTS'
            contentType 'default'
            defaultSubject subjectTemplate ?: '$DEFAULT_SUBJECT'
            defaultContent contentTemplate ?: '$DEFAULT_CONTENT'
            attachmentsPattern ''

            configuredTriggers {
                emailContext.emailTriggers.each { EmailContext.EmailTrigger trigger ->
                    "hudson.plugins.emailext.plugins.trigger.${trigger.triggerShortName}Trigger" {
                        email {
                            recipientList trigger.recipientList
                            subject trigger.subject
                            body trigger.body
                            sendToDevelopers trigger.sendToDevelopers as String
                            sendToRequester trigger.sendToRequester as String
                            includeCulprits trigger.includeCulprits as String
                            sendToRecipientList trigger.sendToRecipientList as String
                        }
                    }
                }
            }
        }

        // Apply their overrides
        if (emailContext.configureClosure) {
            WithXmlAction action = new WithXmlAction(emailContext.configureClosure)
            action.execute(emailNode)
        }

        publisherNodes << emailNode
    }

    /**
     * @since 1.17
     */
    @RequiresPlugin(id = 'mailer')
    void mailer(String mailRecipients, Boolean dontNotifyEveryUnstableBuildBoolean = false,
               Boolean sendToIndividualsBoolean = false) {
        publisherNodes << new NodeBuilder().'hudson.tasks.Mailer' {
            recipients(mailRecipients)
            dontNotifyEveryUnstableBuild(dontNotifyEveryUnstableBuildBoolean)
            sendToIndividuals(sendToIndividualsBoolean)
        }
    }

    /**
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
            latestOnly(artifactsContext.latestOnly)
            allowEmptyArchive(artifactsContext.allowEmpty)
            if (!jobManagement.jenkinsVersion.isOlderThan(new VersionNumber('1.575'))) {
                defaultExcludes(artifactsContext.defaultExcludes)
            }
            if (!jobManagement.jenkinsVersion.isOlderThan(new VersionNumber('1.571'))) {
                delegate.fingerprint(artifactsContext.fingerprint)
            }
            if (!jobManagement.jenkinsVersion.isOlderThan(new VersionNumber('1.567'))) {
                onlyIfSuccessful(artifactsContext.onlyIfSuccessful)
            }
        }
    }

    void archiveArtifacts(String glob, String excludeGlob = null) {
        archiveArtifacts {
            pattern(glob)
            exclude(excludeGlob)
        }
    }

    @Deprecated
    void archiveArtifacts(String glob, String excludeGlob, boolean latestOnly) {
        jobManagement.logDeprecationWarning()

        archiveArtifacts {
            pattern(glob)
            exclude(excludeGlob)
            delegate.latestOnly(latestOnly)
        }
    }

    /**
     * @since 1.26
     */
    void archiveJunit(String glob, @DslContext(ArchiveJUnitContext) Closure junitClosure = null) {
        ArchiveJUnitContext junitContext = new ArchiveJUnitContext(jobManagement)
        ContextHelper.executeInContext(junitClosure, junitContext)

        publisherNodes << new NodeBuilder().'hudson.tasks.junit.JUnitResultArchiver' {
            testResults(glob)
            keepLongStdio(junitContext.retainLongStdout)
            testDataPublishers(junitContext.testDataPublishersContext.testDataPublishers)
        }
    }

    /**
     * @since 1.24
     */
    @RequiresPlugin(id = 'xunit')
    void archiveXUnit(@DslContext(ArchiveXUnitContext) Closure xUnitClosure) {
        ArchiveXUnitContext xUnitContext = new ArchiveXUnitContext()
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
                    unstableThreshold xUnitContext.failedThresholdsContext.unstable
                    unstableNewThreshold xUnitContext.failedThresholdsContext.unstableNew
                    failureThreshold xUnitContext.failedThresholdsContext.failure
                    failureNewThreshold xUnitContext.failedThresholdsContext.failureNew
                }
                'org.jenkinsci.plugins.xunit.threshold.SkippedThreshold' {
                    unstableThreshold xUnitContext.skippedThresholdsContext.unstable
                    unstableNewThreshold xUnitContext.skippedThresholdsContext.unstableNew
                    failureThreshold xUnitContext.skippedThresholdsContext.failure
                    failureNewThreshold xUnitContext.skippedThresholdsContext.failureNew
                }
            }
            thresholdMode xUnitContext.thresholdMode.xmlValue
            extraConfiguration {
                testTimeMargin xUnitContext.timeMargin
            }
        }
    }

    /**
     * @since 1.17
     */
    @RequiresPlugin(id = 'jacoco')
    void jacocoCodeCoverage(@DslContext(JacocoContext) Closure jacocoClosure = null) {
        JacocoContext jacocoContext = new JacocoContext()
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

    @RequiresPlugin(id = 'htmlpublisher')
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
                        if (!jobManagement.getPluginVersion('htmlpublisher')?.isOlderThan(new VersionNumber('1.3'))) {
                            allowMissing(target.allowMissing)
                        }
                        wrapperName('htmlpublisher-wrapper.html')
                    }
                }
            }
        }
    }

    void publishJabber(String target, @DslContext(JabberContext) Closure jabberClosure = null) {
        publishJabber(target, null, null, jabberClosure)
    }

    @Deprecated
    void publishJabber(String target, String strategyName, @DslContext(JabberContext) Closure jabberClosure = null) {
        publishJabber(target, strategyName, null, jabberClosure)
    }

    @Deprecated
    @RequiresPlugin(id = 'jabber')
    void publishJabber(String targetsArg, String strategyName, String channelNotificationName,
                       @DslContext(JabberContext) Closure jabberClosure = null) {
        if (strategyName || channelNotificationName) {
            jobManagement.logDeprecationWarning()
        }

        JabberContext jabberContext = new JabberContext()
        jabberContext.strategyName = strategyName ?: 'ALL'
        jabberContext.channelNotificationName = channelNotificationName ?: 'Default'
        ContextHelper.executeInContext(jabberClosure, jabberContext)

        // Validate values
        Preconditions.checkArgument(
                validJabberStrategyNames.contains(jabberContext.strategyName),
                "Jabber Strategy needs to be one of these values: ${validJabberStrategyNames.join(',')}"
        )
        Preconditions.checkArgument(
                validJabberChannelNotificationNames.contains(jabberContext.channelNotificationName),
                'Jabber Channel Notification name needs to be one of these values: ' +
                        validJabberChannelNotificationNames.join(',')
        )

        publisherNodes << new NodeBuilder().'hudson.plugins.jabber.im.transport.JabberPublisher' {
            targets {
                targetsArg.split().each { target ->
                    boolean isGroup = target.startsWith('*')
                    if (isGroup) {
                        String targetClean = target[1..-1]
                        'hudson.plugins.im.GroupChatIMMessageTarget' {
                            delegate.createNode('name', targetClean)
                            notificationOnly 'false'
                        }
                    } else {
                        'hudson.plugins.im.DefaultIMMessageTarget' {
                            delegate.createNode('value', target)
                        }
                    }
                }
            }
            strategy jabberContext.strategyName
            notifyOnBuildStart jabberContext.notifyOnBuildStart
            notifySuspects jabberContext.notifySuspects
            notifyCulprits jabberContext.notifyCulprits
            notifyFixers jabberContext.notifyFixers
            notifyUpstreamCommitters jabberContext.notifyUpstreamCommitters
            buildToChatNotifier(
                    class: "hudson.plugins.im.build_notify.${jabberContext.channelNotificationName}BuildToChatNotifier"
            )
            matrixMultiplier 'ONLY_CONFIGURATIONS'
        }
    }

    @Deprecated
    Set<String> validJabberStrategyNames = ['ALL', 'FAILURE_AND_FIXED', 'ANY_FAILURE', 'STATECHANGE_ONLY']
    @Deprecated
    Set<String> validJabberChannelNotificationNames = ['Default', 'SummaryOnly', 'BuildParameters', 'PrintFailingTests']

    @RequiresPlugin(id = 'scp')
    void publishScp(String site, @DslContext(ScpContext) Closure scpClosure) {
        ScpContext scpContext = new ScpContext()
        ContextHelper.executeInContext(scpClosure, scpContext)

        // Validate values
        assert !scpContext.entries.empty, 'Scp publish requires at least one entry'

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
     * Clone Workspace SCM
     */
    void publishCloneWorkspace(String workspaceGlob, @DslContext(CloneWorkspaceContext) Closure cloneWorkspaceClosure) {
        publishCloneWorkspace(workspaceGlob, '', 'Any', 'TAR', false, cloneWorkspaceClosure)
    }

    void publishCloneWorkspace(String workspaceGlob, String workspaceExcludeGlob,
                               @DslContext(CloneWorkspaceContext) Closure cloneWorkspaceClosure) {
        publishCloneWorkspace(workspaceGlob, workspaceExcludeGlob, 'Any', 'TAR', false, cloneWorkspaceClosure)
    }

    void publishCloneWorkspace(String workspaceGlob, String workspaceExcludeGlob, String criteria, String archiveMethod,
                               @DslContext(CloneWorkspaceContext) Closure cloneWorkspaceClosure) {
        publishCloneWorkspace(
                workspaceGlob, workspaceExcludeGlob, criteria, archiveMethod, false, cloneWorkspaceClosure
        )
    }

    @RequiresPlugin(id = 'clone-workspace-scm')
    void publishCloneWorkspace(String workspaceGlobArg, String workspaceExcludeGlobArg = '', String criteriaArg = 'Any',
                               String archiveMethodArg = 'TAR', boolean overrideDefaultExcludesArg = false,
                               @DslContext(CloneWorkspaceContext) Closure cloneWorkspaceClosure = null) {
        CloneWorkspaceContext cloneWorkspaceContext = new CloneWorkspaceContext()
        cloneWorkspaceContext.criteria = criteriaArg ?: 'Any'
        cloneWorkspaceContext.archiveMethod = archiveMethodArg ?: 'TAR'
        cloneWorkspaceContext.workspaceExcludeGlob = workspaceExcludeGlobArg ?: ''
        cloneWorkspaceContext.overrideDefaultExcludes = overrideDefaultExcludesArg ?: false
        ContextHelper.executeInContext(cloneWorkspaceClosure, cloneWorkspaceContext)

        // Validate values
        assert validCloneWorkspaceCriteria.contains(cloneWorkspaceContext.criteria),
                "Clone Workspace Criteria needs to be one of these values: ${validCloneWorkspaceCriteria.join(',')}"
        assert validCloneWorkspaceArchiveMethods.contains(cloneWorkspaceContext.archiveMethod),
                'Clone Workspace Archive Method needs to be one of these values: ' +
                        validCloneWorkspaceArchiveMethods.join(',')

        publisherNodes << new NodeBuilder().'hudson.plugins.cloneworkspace.CloneWorkspacePublisher' {
            workspaceGlob workspaceGlobArg
            workspaceExcludeGlob cloneWorkspaceContext.workspaceExcludeGlob
            criteria cloneWorkspaceContext.criteria
            archiveMethod cloneWorkspaceContext.archiveMethod
            overrideDefaultExcludes cloneWorkspaceContext.overrideDefaultExcludes
        }
    }

    static List<String> validCloneWorkspaceCriteria = ['Any', 'Not Failed', 'Successful']
    Set<String> validCloneWorkspaceArchiveMethods = ['TAR', 'ZIP']

    /**
     * Downstream build
     */
    void downstream(String projectName, String thresholdName = 'SUCCESS') {
        assert DownstreamContext.THRESHOLD_COLOR_MAP.containsKey(thresholdName),
                "thresholdName must be one of these values ${DownstreamContext.THRESHOLD_COLOR_MAP.keySet().join(',')}"

        publisherNodes << new NodeBuilder().'hudson.tasks.BuildTrigger' {
            childProjects projectName
            threshold {
                delegate.createNode('name', thresholdName)
                ordinal DownstreamContext.THRESHOLD_ORDINAL_MAP[thresholdName]
                color DownstreamContext.THRESHOLD_COLOR_MAP[thresholdName]
            }
        }
    }

    /**
     * Trigger parameterized build on other projects.
     */
    @RequiresPlugin(id = 'parameterized-trigger')
    void downstreamParameterized(@DslContext(DownstreamContext) Closure downstreamClosure) {
        DownstreamContext downstreamContext = new DownstreamContext(jobManagement)
        ContextHelper.executeInContext(downstreamClosure, downstreamContext)

        publisherNodes << downstreamContext.createDownstreamNode(false)
    }

    void violations(@DslContext(ViolationsContext) Closure violationsClosure = null) {
        violations(100, violationsClosure)
    }

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

    @RequiresPlugin(id = 'chucknorris')
    void chucknorris() {
        publisherNodes << new NodeBuilder().'hudson.plugins.chucknorris.CordellWalkerRecorder' {
            'factGenerator' ''
        }
    }

    /**
     * @since 1.15
     */
    @RequiresPlugin(id = 'ircbot')
    void irc(@DslContext(IrcContext) Closure ircClosure) {
        IrcContext ircContext = new IrcContext()
        ContextHelper.executeInContext(ircClosure, ircContext)

        publisherNodes << new NodeBuilder().'hudson.plugins.ircbot.IrcPublisher' {
            targets {
                ircContext.channels.each { IrcContext.IrcPublisherChannel channel ->
                    'hudson.plugins.im.GroupChatIMMessageTarget' {
                        delegate.createNode('name', channel.name)
                        password channel.password
                        notificationOnly channel.notificationOnly ? 'true' : 'false'
                    }
                }
            }
            strategy ircContext.strategy
            notifyOnBuildStart ircContext.notifyOnBuildStarts ? 'true' : 'false'
            notifySuspects ircContext.notifyScmCommitters ? 'true' : 'false'
            notifyCulprits ircContext.notifyScmCulprits ? 'true' : 'false'
            notifyFixers ircContext.notifyScmFixers ? 'true' : 'false'
            notifyUpstreamCommitters ircContext.notifyUpstreamCommitters ? 'true' : 'false'

            String className = "hudson.plugins.im.build_notify.${ircContext.notificationMessage}BuildToChatNotifier"
            buildToChatNotifier(class: className)
        }
    }

    /**
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
     * @since 1.17
     */
    @RequiresPlugin(id = 'claim')
    void allowBrokenBuildClaiming() {
        publisherNodes << new NodeBuilder().'hudson.plugins.claim.ClaimPublisher'()
    }

    /**
     * Configures Fingerprinting.
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
     * Configures the Description Setter Plugin.
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
     * Configures the Jenkins Text Finder plugin.
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
     * Configures the Jenkins Post Build Task plugin.
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
     * Configures Aggregate Downstream Test Results. Pass no args or null for jobs (first arg) to automatically
     * aggregate downstream test results. Pass in comma-delimited list for first arg to manually choose jobs.
     * Second argument is optional and sets whether failed builds are included.
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
     * Configures the Groovy Postbuild script plugin.
     *
     * @since 1.19
     */
    @RequiresPlugin(id = 'groovy-postbuild')
    void groovyPostBuild(String script, Behavior behavior = Behavior.DoNothing) {
        publisherNodes << new NodeBuilder().'org.jvnet.hudson.plugins.groovypostbuild.GroovyPostbuildRecorder' {
            delegate.groovyScript(script)
            delegate.behavior(behavior.value)
        }
    }

    /**
     * Configures the Javadoc Plugin, used to archive Javadoc artifacts.
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
     * Configures the Associated Files plugin to associate archived files from
     * outside Jenkins proper.
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
     * Configures the Emma Code Coverage plugin.
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
     * Configures Jenkins job to publish Robot Framework reports.
     * By default the following values are applied. If an instance of a
     * closure is provided, the values from the closure will take effect.
     *
     * @since 1.21
     */
    @RequiresPlugin(id = 'robot')
    void publishRobotFrameworkReports(@DslContext(RobotFrameworkContext) Closure robotClosure = null) {
        if (jobManagement.getPluginVersion('robot')?.isOlderThan(new VersionNumber('1.4.3'))) {
            jobManagement.logDeprecationWarning('support for Robot Framework plugin versions older than 1.4.3')
        }

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
            if (!jobManagement.getPluginVersion('robot')?.isOlderThan(new VersionNumber('1.4.3'))) {
                disableArchiveOutput(context.disableArchiveOutput)
            }
            if (!jobManagement.getPluginVersion('robot')?.isOlderThan(new VersionNumber('1.2.1'))) {
                otherFiles {
                    context.otherFiles.each { String file ->
                        string(file)
                    }
                }
            }
        }
    }

    /**
     * Configures a Build Pipeline Trigger.
     *
     * @since 1.21
     */
    @RequiresPlugin(id = 'build-pipeline-plugin')
    void buildPipelineTrigger(String downstreamProjectNames, @DslContext(BuildPipelineContext) Closure closure = null) {
        BuildPipelineContext buildPipelineContext = new BuildPipelineContext(jobManagement)
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
     * @since 1.22
     */
    @RequiresPlugin(id = 'git')
    void git(@DslContext(GitPublisherContext) Closure gitPublisherClosure) {
        GitPublisherContext context = new GitPublisherContext(jobManagement)
        ContextHelper.executeInContext(gitPublisherClosure, context)

        publisherNodes << new NodeBuilder().'hudson.plugins.git.GitPublisher' {
            configVersion(2)
            pushMerge(context.pushMerge)
            pushOnlyIfSuccess(context.pushOnlyIfSuccess)
            if (!jobManagement.getPluginVersion('git')?.isOlderThan(new VersionNumber('2.2.6'))) {
                forcePush(context.forcePush)
            }
            tagsToPush(context.tags)
            branchesToPush(context.branches)
        }
    }

    /**
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
     * @since 1.23
     */
    void flowdock(String[] tokens, @DslContext(FlowdockPublisherContext) Closure flowdockPublisherClosure = null) {
        // Validate values
        assert tokens != null && tokens.length > 0, 'Flowdock publish requires at least one flow token'
        flowdock(tokens.join(','), flowdockPublisherClosure)
    }

    /**
     * Configures the StashNotifier plugin.
     *
     * @since 1.23
     */
    @RequiresPlugin(id = 'stashNotifier')
    void stashNotifier(@DslContext(StashNotifierContext) Closure stashNotifierClosure = null) {
        StashNotifierContext context = new StashNotifierContext()
        ContextHelper.executeInContext(stashNotifierClosure, context)

        publisherNodes << new NodeBuilder().'org.jenkinsci.plugins.stashNotifier.StashNotifier' {
            stashServerBaseUrl()
            stashUserName()
            stashUserPassword()
            ignoreUnverifiedSSLPeer(false)
            commitSha1(context.commitSha1)
            includeBuildNumberInKey(context.keepRepeatedBuilds)
        }
    }

    /**
     * Configures the FlexiblePublish plugin.
     *
     * @since 1.26
     */
    @RequiresPlugin(id = 'flexible-publish')
    void flexiblePublish(@DslContext(FlexiblePublisherContext) Closure flexiblePublishClosure) {
        if (jobManagement.getPluginVersion('flexible-publish')?.isOlderThan(new VersionNumber('0.13'))) {
            jobManagement.logDeprecationWarning('support for Flexible Publish plugin versions older than 0.13')
        }

        FlexiblePublisherContext context = new FlexiblePublisherContext(jobManagement, item)
        ContextHelper.executeInContext(flexiblePublishClosure, context)

        Preconditions.checkArgument(!context.actions.empty, 'no publisher or build step specified')

        publisherNodes << new NodeBuilder().'org.jenkins__ci.plugins.flexible__publish.FlexiblePublisher' {
            delegate.publishers {
                'org.jenkins__ci.plugins.flexible__publish.ConditionalPublisher' {
                    condition(class: context.condition.conditionClass) {
                        context.condition.addArgs(delegate)
                    }
                    if (jobManagement.getPluginVersion('flexible-publish')?.isOlderThan(new VersionNumber('0.13'))) {
                        publisher(
                                class: new XmlFriendlyNameCoder().decodeAttribute(context.actions[0].name().toString()),
                                context.actions[0].value()
                        )
                    } else {
                        publisherList(context.actions)
                    }
                    runner(class: 'org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail')
                }
            }
        }
    }

    /**
     *
     * Configures the Maven Deployment Linker plugin.
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
     * Configures the post build action of the Workspace Cleanup Plugin to delete the workspace.
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
     * @since 1.24
     */
    @RequiresPlugin(id = 'rundeck')
    void rundeck(String jobIdentifier, @DslContext(RundeckContext) Closure rundeckClosure = null) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(jobIdentifier), 'jobIdentifier cannot be null or empty')

        RundeckContext rundeckContext = new RundeckContext()
        ContextHelper.executeInContext(rundeckClosure, rundeckContext)

        publisherNodes << new NodeBuilder().'org.jenkinsci.plugins.rundeck.RundeckNotifier' {
            jobId jobIdentifier
            options rundeckContext.options.collect { key, value -> "${key}=${value}" }.join('\n')
            nodeFilters rundeckContext.nodeFilters.collect { key, value -> "${key}=${value}" }.join('\n')
            tag rundeckContext.tag
            shouldWaitForRundeckJob rundeckContext.shouldWaitForRundeckJob
            shouldFailTheBuild rundeckContext.shouldFailTheBuild
        }
    }

    /**
     * @since 1.26
     */
    @RequiresPlugin(id = 's3')
    void s3(String profile, @DslContext(S3BucketPublisherContext) Closure s3PublisherClosure) {
        checkArgument(!isNullOrEmpty(profile), 'profile must be specified')

        S3BucketPublisherContext context = new S3BucketPublisherContext()
        ContextHelper.executeInContext(s3PublisherClosure, context)

        publisherNodes << new NodeBuilder().'hudson.plugins.s3.S3BucketPublisher' {
            profileName(profile)
            entries(context.entries)
            userMetadata(context.metadata)
        }
    }

    /**
     * Configures the findbugs publisher.
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
     * Configures the PMD Publisher.
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
     * Configures the Checkstyle Publisher.
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
     * Configures the JsHint checkstyle Publisher.
     *
     * @since 1.20
     */
    @RequiresPlugin(id = 'jshint-checkstyle')
    void jshint(String pattern,
                @DslContext(StaticAnalysisContext) Closure staticAnalysisClosure = null) {
        publisherNodes << createDefaultStaticAnalysisNode(
                'hudson.plugins.jshint.CheckStylePublisher',
                staticAnalysisClosure,
                pattern
        )
    }

    /**
     * Configures the DRY Publisher.
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
     * Configures the Task Scanner Publisher.
     *
     * @since 1.17
     */
    @RequiresPlugin(id = 'tasks')
    void tasks(String pattern, excludePattern = '', high = '', normal = '', low = '', ignoreCase = false,
               @DslContext(StaticAnalysisContext) Closure staticAnalysisClosure = null) {
        StaticAnalysisContext staticAnalysisContext = new StaticAnalysisContext()
        ContextHelper.executeInContext(staticAnalysisClosure, staticAnalysisContext)

        publisherNodes << new NodeBuilder().'hudson.plugins.tasks.TasksPublisher' {
            addStaticAnalysisContextAndPattern(delegate, staticAnalysisContext, pattern)
            delegate.high(high)
            delegate.normal(normal)
            delegate.low(low)
            delegate.ignoreCase(ignoreCase)
            delegate.excludePattern(excludePattern)
        }
    }

    /**
     * Configures the CCM Publisher.
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
     * Configures the Android Lint Publisher.
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
     * Configures the OWASP Dependency-Check Publisher.
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
     * Configures the Compiler Warnings Publisher.
     *
     * @since 1.17
     */
    @RequiresPlugin(id = 'warnings', minimumVersion = '4.0')
    void warnings(List consoleParsers, Map parserConfigurations = [:],
                  @DslContext(WarningsContext) Closure warningsClosure = null) {
        WarningsContext warningsContext = new WarningsContext()
        ContextHelper.executeInContext(warningsClosure,  warningsContext)

        NodeBuilder nodeBuilder = new NodeBuilder()
        publisherNodes << nodeBuilder.'hudson.plugins.warnings.WarningsPublisher' {
            addStaticAnalysisContext(delegate,  warningsContext)
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
     * Configures the Analysis Collector Publisher.
     *
     * @since 1.26
     */
    @RequiresPlugin(id = 'analysis-collector')
    void analysisCollector(@DslContext(AnalysisCollectorContext) Closure analysisCollectorClosure = null) {
        AnalysisCollectorContext analysisCollectorContext = new AnalysisCollectorContext()
        ContextHelper.executeInContext(analysisCollectorClosure,  analysisCollectorContext)

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
     * @since 1.31
     */
    @RequiresPlugin(id = 'postbuildscript')
    void postBuildScripts(@DslContext(PostBuildScriptsContext) Closure closure) {
        PostBuildScriptsContext context = new PostBuildScriptsContext(jobManagement, item)
        ContextHelper.executeInContext(closure, context)

        publisherNodes << new NodeBuilder().'org.jenkinsci.plugins.postbuildscript.PostBuildScript' {
            buildSteps(context.stepContext.stepNodes)
            scriptOnlyIfSuccess(context.onlyIfBuildSucceeds)
        }
    }

    /**
     * @since 1.31
     */
    @RequiresPlugin(id = 'sonar')
    void sonar(@DslContext(SonarContext) Closure sonarClosure = null) {
        SonarContext sonarContext = new SonarContext()
        ContextHelper.executeInContext(sonarClosure, sonarContext)

        publisherNodes << new NodeBuilder().'hudson.plugins.sonar.SonarPublisher' {
            jdk('(Inherit From Job)')
            branch(sonarContext.branch ?: '')
            language()
            mavenOpts()
            jobAdditionalProperties()
            if (sonarContext.overrideTriggers) {
                triggers {
                    skipScmCause(false)
                    skipUpstreamCause(false)
                    envVar(sonarContext.sonarTriggersContext.skipIfEnvironmentVariable ?: '')
                }
            }
            mavenInstallationName('(Inherit From Job)')
            rootPom()
            settings(class: 'jenkins.mvn.DefaultSettingsProvider')
            globalSettings(class: 'jenkins.mvn.DefaultGlobalSettingsProvider')
            usePrivateRepository(false)
        }
    }

    /**
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
     * Configures the GitHub pull request builder plugin to perform an automatic request after a successful build.
     *
     * @since 1.33
     */
    @RequiresPlugin(id = 'ghprb', minimumVersion = '1.17')
    void mergePullRequest(@DslContext(PullRequestPublisherContext) Closure contextClosure = null) {
        PullRequestPublisherContext pullRequestPublisherContext = new PullRequestPublisherContext()
        ContextHelper.executeInContext(contextClosure, pullRequestPublisherContext)

        publisherNodes << new NodeBuilder().'org.jenkinsci.plugins.ghprb.GhprbPullRequestMerge' {
            onlyAdminsMerge(pullRequestPublisherContext.onlyAdminsMerge)
            disallowOwnCode(pullRequestPublisherContext.disallowOwnCode)
            onlyTriggerPhrase(pullRequestPublisherContext.onlyTriggerPhrase)
            mergeComment(pullRequestPublisherContext.mergeComment ?: '')
        }
    }

    /**
     * Configures the Build Publisher plugin to publish builds to a 'public' Jenkins server.
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
     * Allows notifications to be set to HipChat.
     *
     * @since 1.33
     */
    @RequiresPlugin(id = 'hipchat', minimumVersion = '0.1.9')
    void hipChat(@DslContext(HipChatPublisherContext) Closure hipChatClosure = null) {
        HipChatPublisherContext hipChatContext = new HipChatPublisherContext()
        ContextHelper.executeInContext(hipChatClosure, hipChatContext)

        publisherNodes << new NodeBuilder().'jenkins.plugins.hipchat.HipChatNotifier' {
            token(hipChatContext.token ?: '')
            room(hipChatContext.rooms.join(','))
            startNotification(hipChatContext.notifyBuildStart)
            notifySuccess(hipChatContext.notifySuccess)
            notifyAborted(hipChatContext.notifyAborted)
            notifyNotBuilt(hipChatContext.notifyNotBuilt)
            notifyUnstable(hipChatContext.notifyUnstable)
            notifyFailure(hipChatContext.notifyFailure)
            notifyBackToNormal(hipChatContext.notifyBackToNormal)
            startJobMessage(hipChatContext.startJobMessage ?: '')
            completeJobMessage(hipChatContext.completeJobMessage ?: '')
        }
    }

    /**
     * @since 1.34
     */
    @RequiresPlugin(id = 'publish-over-ssh', minimumVersion = '1.12')
    void publishOverSsh(@DslContext(PublishOverSshContext) Closure publishOverSshClosure) {
        PublishOverSshContext publishOverSshContext = new PublishOverSshContext()
        ContextHelper.executeInContext(publishOverSshClosure, publishOverSshContext)

        Preconditions.checkArgument(!publishOverSshContext.servers.empty, 'At least 1 server must be configured')

        publisherNodes << new NodeBuilder().'jenkins.plugins.publish__over__ssh.BapSshPublisherPlugin' {
            consolePrefix('SSH: ')
            currentNode.append(publishOverSshContext.node)
        }
    }

    private static createDefaultStaticAnalysisNode(String publisherClassName, Closure staticAnalysisClosure,
                                                   String pattern) {
        StaticAnalysisContext staticAnalysisContext = new StaticAnalysisContext()
        ContextHelper.executeInContext(staticAnalysisClosure, staticAnalysisContext)

        new NodeBuilder()."${publisherClassName}" {
            addStaticAnalysisContextAndPattern(delegate, staticAnalysisContext, pattern)
        }
    }

    @SuppressWarnings('NoDef')
    private static addStaticAnalysisContext(def nodeBuilder, StaticAnalysisContext context) {
        nodeBuilder.with {
            healthy(context.healthy)
            unHealthy(context.unHealthy)
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
