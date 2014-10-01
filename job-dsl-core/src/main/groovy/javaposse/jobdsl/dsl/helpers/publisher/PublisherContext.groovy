package javaposse.jobdsl.dsl.helpers.publisher

import com.google.common.base.Preconditions
import com.google.common.base.Strings
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.helpers.common.BuildPipelineContext
import javaposse.jobdsl.dsl.helpers.common.DownstreamContext

class PublisherContext implements Context {
    private final JobManagement jobManagement

    List<Node> publisherNodes = []

    @Delegate
    StaticAnalysisPublisherContext staticAnalysisPublisherHelper

    PublisherContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement

        staticAnalysisPublisherHelper = new StaticAnalysisPublisherContext(publisherNodes, jobManagement)
    }

    /**
     <hudson.plugins.emailext.ExtendedEmailPublisher>
     <recipientList>billing@company.com</recipientList>
     <configuredTriggers>
     <hudson.plugins.emailext.plugins.trigger.FailureTrigger>
     <email>
     <recipientList/>
     <subject>$PROJECT_DEFAULT_SUBJECT</subject>
     <body>$PROJECT_DEFAULT_CONTENT</body>
     <sendToDevelopers>false</sendToDevelopers>
     <sendToRequester>false</sendToRequester>
     <includeCulprits>false</includeCulprits>
     <sendToRecipientList>true</sendToRecipientList>
     </email>
     </hudson.plugins.emailext.plugins.trigger.FailureTrigger>
     <hudson.plugins.emailext.plugins.trigger.SuccessTrigger>
     <email>
     <recipientList/>
     <subject>$PROJECT_DEFAULT_SUBJECT</subject>
     <body>$PROJECT_DEFAULT_CONTENT</body>
     <sendToDevelopers>false</sendToDevelopers>
     <sendToRequester>false</sendToRequester>
     <includeCulprits>false</includeCulprits>
     <sendToRecipientList>true</sendToRecipientList>
     </email>
     </hudson.plugins.emailext.plugins.trigger.SuccessTrigger>
     </configuredTriggers>
     <contentType>default</contentType>
     <defaultSubject>$DEFAULT_SUBJECT</defaultSubject>
     <defaultContent>$DEFAULT_CONTENT</defaultContent>
     <attachmentsPattern/>
     </hudson.plugins.emailext.ExtendedEmailPublisher>
     * @return
     */
    def extendedEmail(String recipients = null, Closure emailClosure = null) {
        extendedEmail(recipients, null, emailClosure)
    }

    def extendedEmail(String recipients, String subjectTemplate, Closure emailClosure = null) {
        extendedEmail(recipients, subjectTemplate, null, emailClosure)
    }

    def extendedEmail(String recipients, String subjectTemplate, String contentTemplate, Closure emailClosure = null) {
        EmailContext emailContext = new EmailContext()
        AbstractContextHelper.executeInContext(emailClosure, emailContext)

        // Validate that we have the typical triggers, if nothing is provided
        if (emailContext.emailTriggers.isEmpty()) {
            emailContext.emailTriggers << new EmailContext.EmailTrigger('Failure')
            emailContext.emailTriggers << new EmailContext.EmailTrigger('Success')
        }

        // Now that the context has what we need
        def nodeBuilder = NodeBuilder.newInstance()
        def emailNode = nodeBuilder.'hudson.plugins.emailext.ExtendedEmailPublisher' {
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
            emailContext.configureClosure.resolveStrategy = Closure.DELEGATE_FIRST
            WithXmlAction action = new WithXmlAction(emailContext.configureClosure)
            action.execute(emailNode)
        }

        publisherNodes << emailNode
    }

    /**
     <hudson.tasks.Mailer>
     <recipients>nbn@nineconsult.dk</recipients>
     <dontNotifyEveryUnstableBuild>false</dontNotifyEveryUnstableBuild>
     <sendToIndividuals>true</sendToIndividuals>
     </hudson.tasks.Mailer>
     */
    def mailer(String mailRecipients, Boolean dontNotifyEveryUnstableBuildBoolean = false,
               Boolean sendToIndividualsBoolean = false) {
        def nodeBuilder = new NodeBuilder()
        Node mailerNode = nodeBuilder.'hudson.tasks.Mailer' {
            recipients(mailRecipients)
            dontNotifyEveryUnstableBuild(dontNotifyEveryUnstableBuildBoolean)
            sendToIndividuals(sendToIndividualsBoolean)
        }
        publisherNodes << mailerNode
    }

    /**
     <hudson.tasks.ArtifactArchiver>
     <artifacts>build/libs/*</artifacts>
     <excludes>build/libs/bad/*</excludes>
     <latestOnly>false</latestOnly>
     <allowEmptyArchive>false</allowEmptyArchive>
     </hudson.tasks.ArtifactArchiver>

     Note: allowEmpty is not compatible with jenkins <= 1.480

     * @param glob
     * @param excludeGlob
     * @param latestOnly
     */
    def archiveArtifacts(Closure artifactsClosure) {
        ArchiveArtifactsContext artifactsContext = new ArchiveArtifactsContext()
        AbstractContextHelper.executeInContext(artifactsClosure, artifactsContext)

        def nodeBuilder = new NodeBuilder()

        Node archiverNode = nodeBuilder.'hudson.tasks.ArtifactArchiver' {
            artifacts artifactsContext.patternValue
            latestOnly artifactsContext.latestOnlyValue ? 'true' : 'false'

            if (artifactsContext.allowEmptyValue != null) {
                allowEmptyArchive artifactsContext.allowEmptyValue ? 'true' : 'false'
            }

            if (artifactsContext.excludesValue) {
                excludes artifactsContext.excludesValue
            }
        }

        publisherNodes << archiverNode
    }

    def archiveArtifacts(String glob, String excludeGlob = null, Boolean latestOnlyBoolean = false) {
        archiveArtifacts {
            pattern glob
            exclude excludeGlob
            latestOnly latestOnlyBoolean
        }
    }

    /**
     * Everything checked:
     <hudson.tasks.junit.JUnitResultArchiver>
     <testResults>build/test/*.xml</testResults> // Can be empty
     <keepLongStdio>true</keepLongStdio>
     <testDataPublishers> // Empty if no extra publishers
     <hudson.plugins.claim.ClaimTestDataPublisher/> // Allow claiming of failed tests
     <hudson.plugins.junitattachments.AttachmentPublisher/> // Publish test attachments
     </testDataPublishers>
     </hudson.tasks.junit.JUnitResultArchiver>
     */
    def archiveJunit(String glob, boolean retainLongStdout = false, boolean allowClaimingOfFailedTests = false,
                     boolean publishTestAttachments = false) {
        def nodeBuilder = new NodeBuilder()

        Node archiverNode = nodeBuilder.'hudson.tasks.junit.JUnitResultArchiver' {
            testResults glob
            keepLongStdio retainLongStdout ? 'true' : 'false'
            testDataPublishers {
                if (allowClaimingOfFailedTests) {
                    'hudson.plugins.claim.ClaimTestDataPublisher' ''
                }
                if (publishTestAttachments) {
                    'hudson.plugins.junitattachments.AttachmentPublisher' ''
                }
            }
        }

        publisherNodes << archiverNode

    }

    /**
     * <xunit>
     *     <types>
     *         <JUnitType>
     *             <pattern></pattern>
     *             <skipNoTestFiles>false</skipNoTestFiles>
     *             <failIfNotNew>true</failIfNotNew>
     *             <deleteOutputFiles>true</deleteOutputFiles>
     *             <stopProcessingIfError>true</stopProcessingIfError>
     *         </JUnitType>
     *         <CustomType>
     *             <pattern></pattern>
     *             <skipNoTestFiles>false</skipNoTestFiles>
     *             <failIfNotNew>true</failIfNotNew>
     *             <deleteOutputFiles>true</deleteOutputFiles>
     *             <stopProcessingIfError>true</stopProcessingIfError>
     *             <customXSL></customXSL>
     *         </CustomType>
     *     </types>
     *     <thresholds>
     *         <org.jenkinsci.plugins.xunit.threshold.FailedThreshold>
     *             <unstableThreshold></unstableThreshold>
     *             <unstableNewThreshold></unstableNewThreshold>
     *             <failureThreshold></failureThreshold>
     *             <failureNewThreshold></failureNewThreshold>
     *         </org.jenkinsci.plugins.xunit.threshold.FailedThreshold>
     *         <org.jenkinsci.plugins.xunit.threshold.SkippedThreshold>
     *             <unstableThreshold></unstableThreshold>
     *             <unstableNewThreshold></unstableNewThreshold>
     *             <failureThreshold></failureThreshold>
     *             <failureNewThreshold></failureNewThreshold>
     *         </org.jenkinsci.plugins.xunit.threshold.SkippedThreshold>
     *     </thresholds>
     *     <thresholdMode>1</thresholdMode>
     *     <extraConfiguration>
     *         <testTimeMargin>3000</testTimeMargin>
     *     </extraConfiguration>
     * </xunit>
     */
    def archiveXUnit(Closure xUnitClosure) {
        ArchiveXUnitContext xUnitContext = new ArchiveXUnitContext()
        AbstractContextHelper.executeInContext(xUnitClosure, xUnitContext)

        publisherNodes << NodeBuilder.newInstance().'xunit' {
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
     <hudson.plugins.jacoco.JacocoPublisher>
     <execPattern>"target/*.exec"</execPattern>
     <classPattern>"target/classes"</classPattern>
     <sourcePattern>"src/main/java"</sourcePattern>
     <inclusionPattern>"*.class"</inclusionPattern>
     <exclusionPattern>"*.Test*"</exclusionPattern>
     <minimumInstructionCoverage>0</minimumInstructionCoverage>
     <minimumBranchCoverage>0</minimumBranchCoverage>
     <minimumComplexityCoverage>0</minimumComplexityCoverage>
     <minimumLineCoverage>0</minimumLineCoverage>
     <minimumMethodCoverage>0</minimumMethodCoverage>
     <minimumClassCoverage>0</minimumClassCoverage>
     <maximumInstructionCoverage>0</maximumInstructionCoverage>
     <maximumBranchCoverage>0</maximumBranchCoverage>
     <maximumComplexityCoverage>0</maximumComplexityCoverage>
     <maximumLineCoverage>0</maximumLineCoverage>
     <maximumMethodCoverage>0</maximumMethodCoverage>
     <maximumClassCoverage>0</maximumClassCoverage>
     <changeBuildStatus>false</changeBuildStatus>
     </hudson.plugins.jacoco.JacocoPublisher>
     **/
    def jacocoCodeCoverage(Closure jacocoClosure = null) {

        JacocoContext jacocoContext = new JacocoContext()
        AbstractContextHelper.executeInContext(jacocoClosure, jacocoContext)

        def nodeBuilder = NodeBuilder.newInstance()

        Node jacocoNode = nodeBuilder.'hudson.plugins.jacoco.JacocoPublisher' {
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
                changeBuildStatus Boolean.toString(jacocoContext.changeBuildStatus)
            }
        }

        publisherNodes << jacocoNode
    }

    /**
     <htmlpublisher.HtmlPublisher>
     <reportTargets>
     <htmlpublisher.HtmlPublisherTarget>
     <reportName>Gradle Tests</reportName>
     <reportDir>build/reports/tests/</reportDir>
     <reportFiles>index.html</reportFiles>
     <keepAll>false</keepAll>
     <wrapperName>htmlpublisher-wrapper.html</wrapperName>
     </htmlpublisher.HtmlPublisherTarget>
     </reportTargets>
     </htmlpublisher.HtmlPublisher>
     */
    def publishHtml(Closure htmlReportContext) {
        HtmlReportContext reportContext = new HtmlReportContext()
        AbstractContextHelper.executeInContext(htmlReportContext, reportContext)

        // Now that the context has what we need
        def nodeBuilder = NodeBuilder.newInstance()
        def htmlPublisherNode = nodeBuilder.'htmlpublisher.HtmlPublisher' {
            reportTargets {
                reportContext.targets.each { HtmlReportContext.HtmlPublisherTarget target ->
                    'htmlpublisher.HtmlPublisherTarget' {
                        // All fields can have a blank, odd.
                        reportName target.reportName
                        reportDir target.reportDir
                        reportFiles target.reportFiles
                        keepAll target.keepAll
                        wrapperName target.wrapperName
                    }
                }
            }
        }
        publisherNodes << htmlPublisherNode
    }

    /**
     * <hudson.plugins.jabber.im.transport.JabberPublisher>
     *     <targets>
     *         <hudson.plugins.im.GroupChatIMMessageTarget>
     *             <name>api@conference.jabber.netflix.com</name>
     *             <notificationOnly>false</notificationOnly>
     *         </hudson.plugins.im.GroupChatIMMessageTarget>
     *     </targets>
     *     <strategy>ALL</strategy>
     *     <notifyOnBuildStart>false</notifyOnBuildStart>
     *     <notifySuspects>false</notifySuspects>
     *     <notifyCulprits>false</notifyCulprits>
     *     <notifyFixers>false</notifyFixers>
     *     <notifyUpstreamCommitters>false</notifyUpstreamCommitters>
     *     <buildToChatNotifier class="hudson.plugins.im.build_notify.DefaultBuildToChatNotifier"/>
     *     <matrixMultiplier>ONLY_CONFIGURATIONS</matrixMultiplier>
     * </hudson.plugins.jabber.im.transport.JabberPublisher>
     */
    def publishJabber(String target, Closure jabberClosure = null) {
        publishJabber(target, null, null, jabberClosure)
    }

    def publishJabber(String target, String strategyName, Closure jabberClosure = null) {
        publishJabber(target, strategyName, null, jabberClosure)
    }

    def publishJabber(String targetsArg, String strategyName, String channelNotificationName,
                      Closure jabberClosure = null) {
        JabberContext jabberContext = new JabberContext()
        jabberContext.strategyName = strategyName ?: 'ALL'
        jabberContext.channelNotificationName = channelNotificationName ?: 'Default'
        AbstractContextHelper.executeInContext(jabberClosure, jabberContext)

        // Validate values
        assert validJabberStrategyNames.contains(jabberContext.strategyName),
                "Jabber Strategy needs to be one of these values: ${validJabberStrategyNames.join(',')}"
        assert validJabberChannelNotificationNames.contains(jabberContext.channelNotificationName),
                'Jabber Channel Notification name needs to be one of these values: ' +
                        validJabberChannelNotificationNames.join(',')

        def notifierClass = "hudson.plugins.im.build_notify.${jabberContext.channelNotificationName}BuildToChatNotifier"

        def nodeBuilder = NodeBuilder.newInstance()
        def publishNode = nodeBuilder.'hudson.plugins.jabber.im.transport.JabberPublisher' {
            targets {
                targetsArg.split().each { target ->
                    def isGroup = target.startsWith('*')
                    if (isGroup) {
                        def targetClean = target[1..-1]
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
            notifyOnBuildStart jabberContext.notifyOnBuildStart ? 'true' : 'false'
            notifySuspects jabberContext.notifyOnBuildStart ? 'true' : 'false'
            notifyCulprits jabberContext.notifyCulprits ? 'true' : 'false'
            notifyFixers jabberContext.notifyFixers ? 'true' : 'false'
            notifyUpstreamCommitters jabberContext.notifyUpstreamCommitters ? 'true' : 'false'
            buildToChatNotifier(class: notifierClass)
            matrixMultiplier 'ONLY_CONFIGURATIONS'
        }
        publisherNodes << publishNode
    }

    def validJabberStrategyNames = ['ALL', 'FAILURE_AND_FIXED', 'ANY_FAILURE', 'STATECHANGE_ONLY']
    def validJabberChannelNotificationNames = ['Default', 'SummaryOnly', 'BuildParameters', 'PrintFailingTests']

    /**
     <be.certipost.hudson.plugin.SCPRepositoryPublisher>
     <siteName>javadoc</siteName>
     <entries>
     <be.certipost.hudson.plugin.Entry>
     <filePath/>
     <sourceFile>api-sdk/*</sourceFile>
     <keepHierarchy>true</keepHierarchy>
     </be.certipost.hudson.plugin.Entry>
     </entries>
     </be.certipost.hudson.plugin.SCPRepositoryPublisher>
     */
    def publishScp(String site, Closure scpClosure) {
        ScpContext scpContext = new ScpContext()
        AbstractContextHelper.executeInContext(scpClosure, scpContext)

        // Validate values
        assert !scpContext.entries.isEmpty(), 'Scp publish requires at least one entry'

        def nodeBuilder = NodeBuilder.newInstance()
        def publishNode = nodeBuilder.'be.certipost.hudson.plugin.SCPRepositoryPublisher' {
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
        publisherNodes << publishNode
    }

    /**
     * Clone Workspace SCM
     *
     * <hudson.plugins.cloneworkspace.CloneWorkspacePublisher>
     *     <workspaceGlob></workspaceGlob>
     *     <workspaceExcludeGlob></workspaceExcludeGlob>
     *     <criteria>Any</criteria>
     *     <archiveMethod>TAR</archiveMethod>
     *     <overrideDefaultExcludes>true</overrideDefaultExcludes>
     * </hudson.plugins.cloneworkspace.CloneWorkspacePublisher>
     */
    def publishCloneWorkspace(String workspaceGlob, Closure cloneWorkspaceClosure) {
        publishCloneWorkspace(workspaceGlob, '', 'Any', 'TAR', false, cloneWorkspaceClosure)
    }

    def publishCloneWorkspace(String workspaceGlob, String workspaceExcludeGlob, Closure cloneWorkspaceClosure) {
        publishCloneWorkspace(workspaceGlob, workspaceExcludeGlob, 'Any', 'TAR', false, cloneWorkspaceClosure)
    }

    def publishCloneWorkspace(String workspaceGlob, String workspaceExcludeGlob, String criteria, String archiveMethod,
                              Closure cloneWorkspaceClosure) {
        publishCloneWorkspace(
                workspaceGlob, workspaceExcludeGlob, criteria, archiveMethod, false, cloneWorkspaceClosure
        )
    }

    def publishCloneWorkspace(String workspaceGlobArg, String workspaceExcludeGlobArg = '', String criteriaArg = 'Any',
                              String archiveMethodArg = 'TAR', boolean overrideDefaultExcludesArg = false,
                              Closure cloneWorkspaceClosure = null) {
        CloneWorkspaceContext cloneWorkspaceContext = new CloneWorkspaceContext()
        cloneWorkspaceContext.criteria = criteriaArg ?: 'Any'
        cloneWorkspaceContext.archiveMethod = archiveMethodArg ?: 'TAR'
        cloneWorkspaceContext.workspaceExcludeGlob = workspaceExcludeGlobArg ?: ''
        cloneWorkspaceContext.overrideDefaultExcludes = overrideDefaultExcludesArg ?: false
        AbstractContextHelper.executeInContext(cloneWorkspaceClosure, cloneWorkspaceContext)

        // Validate values
        assert validCloneWorkspaceCriteria.contains(cloneWorkspaceContext.criteria),
                "Clone Workspace Criteria needs to be one of these values: ${validCloneWorkspaceCriteria.join(',')}"
        assert validCloneWorkspaceArchiveMethods.contains(cloneWorkspaceContext.archiveMethod),
                'Clone Workspace Archive Method needs to be one of these values: ' +
                        validCloneWorkspaceArchiveMethods.join(',')

        def nodeBuilder = NodeBuilder.newInstance()
        def publishNode = nodeBuilder.'hudson.plugins.cloneworkspace.CloneWorkspacePublisher' {
            workspaceGlob workspaceGlobArg
            workspaceExcludeGlob cloneWorkspaceContext.workspaceExcludeGlob
            criteria cloneWorkspaceContext.criteria
            archiveMethod cloneWorkspaceContext.archiveMethod
            overrideDefaultExcludes cloneWorkspaceContext.overrideDefaultExcludes
        }
        publisherNodes << publishNode
    }

    static List<String> validCloneWorkspaceCriteria = ['Any', 'Not Failed', 'Successful']
    def validCloneWorkspaceArchiveMethods = ['TAR', 'ZIP']

    /**
     * Downstream build
     *
     <hudson.tasks.BuildTrigger>
     <childProjects>DSL-Tutorial-1-Test</childProjects>
     <threshold>
     <name>SUCCESS</name>
     <ordinal>0</ordinal>
     <color>BLUE</color>
     </threshold>
     // or
     <threshold><name>UNSTABLE</name><ordinal>1</ordinal><color>YELLOW</color></threshold>
     // or
     <threshold><name>FAILURE</name><ordinal>2</ordinal><color>RED</color></threshold>
     </hudson.tasks.BuildTrigger>
     */
    def downstream(String projectName, String thresholdName = 'SUCCESS') {
        assert DownstreamContext.THRESHOLD_COLOR_MAP.containsKey(thresholdName),
                "thresholdName must be one of these values ${DownstreamContext.THRESHOLD_COLOR_MAP.keySet().join(',')}"

        def nodeBuilder = new NodeBuilder()
        Node publishNode = nodeBuilder.'hudson.tasks.BuildTrigger' {
            childProjects projectName
            threshold {
                delegate.createNode('name', thresholdName)
                ordinal DownstreamContext.THRESHOLD_ORDINAL_MAP[thresholdName]
                color DownstreamContext.THRESHOLD_COLOR_MAP[thresholdName]
            }
        }

        publisherNodes << publishNode
    }

    /**
     Trigger parameterized build on other projects.

     <hudson.plugins.parameterizedtrigger.BuildTrigger>
     <configs>
     <hudson.plugins.parameterizedtrigger.BuildTriggerConfig>
     <configs>
     <hudson.plugins.parameterizedtrigger.CurrentBuildParameters/> // Current build parameters
     <hudson.plugins.parameterizedtrigger.FileBuildParameters> // Parameters from properties file
     <propertiesFile>some.properties</propertiesFile>
     </hudson.plugins.parameterizedtrigger.FileBuildParameters>
     <hudson.plugins.git.GitRevisionBuildParameters> // Pass-through Git commit that was built
     <combineQueuedCommits>false</combineQueuedCommits>
     </hudson.plugins.git.GitRevisionBuildParameters>
     <hudson.plugins.parameterizedtrigger.PredefinedBuildParameters> // Predefined properties
     <properties>prop1=value1
     prop2=value2</properties>
     </hudson.plugins.parameterizedtrigger.PredefinedBuildParameters>
     <hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters> // Restrict matrix execution to a subset
     <filter>label=="${TARGET}"</filter>
     </hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters>
     <hudson.plugins.parameterizedtrigger.SubversionRevisionBuildParameters/> // Subversion revision
     </configs>
     <projects>NEBULA-ubuntu-packaging-plugin</projects>
     <condition>SUCCESS</condition>
     <triggerWithNoParameters>false</triggerWithNoParameters>
     </hudson.plugins.parameterizedtrigger.BuildTriggerConfig>
     <hudson.plugins.parameterizedtrigger.BuildTriggerConfig>
     <configs class="java.util.Collections$EmptyList"/>
     <projects>DSL-Tutorial-1-Test</projects>
     <condition>SUCCESS</condition> // SUCCESS, UNSTABLE, UNSTABLE_OR_BETTER, UNSTABLE_OR_WORSE, FAILED, ALWAYS
     <triggerWithNoParameters>false</triggerWithNoParameters>
     </hudson.plugins.parameterizedtrigger.BuildTriggerConfig>
     </configs>
     </hudson.plugins.parameterizedtrigger.BuildTrigger>
     */
    def downstreamParameterized(Closure downstreamClosure) {
        DownstreamContext downstreamContext = new DownstreamContext()
        AbstractContextHelper.executeInContext(downstreamClosure, downstreamContext)

        def publishNode = downstreamContext.createDownstreamNode(false)
        publisherNodes << publishNode
    }

    def violations(Closure violationsClosure = null) {
        violations(100, violationsClosure)
    }

    def violations(int perFileDisplayLimit, Closure violationsClosure = null) {
        ViolationsContext violationsContext = new ViolationsContext()
        violationsContext.perFileDisplayLimit = perFileDisplayLimit
        AbstractContextHelper.executeInContext(violationsClosure, violationsContext)

        def nodeBuilder = NodeBuilder.newInstance()
        def publishNode = nodeBuilder.'hudson.plugins.violations.ViolationsPublisher' {
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
        publisherNodes << publishNode
    }

    /*
    <hudson.plugins.chucknorris.CordellWalkerRecorder>
     <factGenerator/>
    </hudson.plugins.chucknorris.CordellWalkerRecorder>
    */

    def chucknorris() {
        def nodeBuilder = NodeBuilder.newInstance()
        def publishNode = nodeBuilder.'hudson.plugins.chucknorris.CordellWalkerRecorder' {
            'factGenerator' ''
        }
        publisherNodes << publishNode
    }

    def irc(Closure ircClosure) {
        IrcContext ircContext = new IrcContext()
        AbstractContextHelper.executeInContext(ircClosure, ircContext)

        def nodeBuilder = NodeBuilder.newInstance()
        def publishNode = nodeBuilder.'hudson.plugins.ircbot.IrcPublisher' {
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

            def className = "hudson.plugins.im.build_notify.${ircContext.notificationMessage}BuildToChatNotifier"
            buildToChatNotifier(class: className)
        }

        publisherNodes << publishNode
    }

    def cobertura(String reportFile, Closure coberturaClosure = null) {

        CoberturaContext coberturaContext = new CoberturaContext()
        AbstractContextHelper.executeInContext(coberturaClosure, coberturaContext)

        publisherNodes << NodeBuilder.newInstance().'hudson.plugins.cobertura.CoberturaPublisher' {
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
     * <hudson.plugins.claim.ClaimPublisher/>
     */
    def allowBrokenBuildClaiming() {
        publisherNodes << NodeBuilder.newInstance().'hudson.plugins.claim.ClaimPublisher'()
    }

    /**
     * Configures Fingerprinting
     *
     * <hudson.tasks.Fingerprinter>
     *    <targets>**</targets>
     *    <recordBuildArtifacts>true</recordBuildArtifacts>
     * </hudson.tasks.Fingerprinter>
     *
     */
    def fingerprint(String targets, boolean recordBuildArtifacts = false) {
        publisherNodes << NodeBuilder.newInstance().'hudson.tasks.Fingerprinter' {
            delegate.targets(targets ?: '')
            delegate.recordBuildArtifacts(recordBuildArtifacts)
        }
    }

    /**
     * Configures the Description Setter Plugin
     *
     * <publishers>
     *     <hudson.plugins.descriptionsetter.DescriptionSetterPublisher>
     *         <regexp>foo</regexp>
     *         <regexpForFailed>bar</regexpForFailed>
     *         <description>Hello</description>
     *         <descriptionForFailed>World</descriptionForFailed>
     *         <setForMatrix>false</setForMatrix>
     *     </hudson.plugins.descriptionsetter.DescriptionSetterPublisher>
     */
    def buildDescription(String regularExpression, String description = '', String regularExpressionForFailed = '',
                         String descriptionForFailed = '', boolean multiConfigurationBuild = false) {
        publisherNodes << NodeBuilder.newInstance().'hudson.plugins.descriptionsetter.DescriptionSetterPublisher' {
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
     * Configures the Jenkins Text Finder plugin
     *
     * <publishers>
     *     <hudson.plugins.textfinder.TextFinderPublisher>
     *         <fileSet>*.txt</fileSet>
     *         <regexp/>
     *         <succeedIfFound>false</succeedIfFound>
     *         <unstableIfFound>false</unstableIfFound>
     *         <alsoCheckConsoleOutput>false</alsoCheckConsoleOutput>
     *     </hudson.plugins.textfinder.TextFinderPublisher>
     */
    def textFinder(String regularExpression, String fileSet = '', boolean alsoCheckConsoleOutput = false,
                   boolean succeedIfFound = false, unstableIfFound = false) {
        publisherNodes << NodeBuilder.newInstance().'hudson.plugins.textfinder.TextFinderPublisher' {
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
     * Configures the Jenkins Post Build Task plugin
     *
     * <publishers>
     *     <hudson.plugins.postbuildtask.PostbuildTask>
     *          <tasks>
     *              <hudson.plugins.postbuildtask.TaskProperties>
     *                  <logTexts>
     *                      <hudson.plugins.postbuildtask.LogProperties>
     *                          <logText>BUILD SUCCESSFUL</logText>
     *                          <operator>AND</operator>
     *                      </hudson.plugins.postbuildtask.LogProperties>
     *                  </logTexts>
     *                  <EscalateStatus>false</EscalateStatus>
     *                  <RunIfJobSuccessful>false</RunIfJobSuccessful>
     *                  <script>git clean -fdx</script>
     *              </hudson.plugins.postbuildtask.TaskProperties>
     *          </tasks>
     *      </hudson.plugins.postbuildtask.PostbuildTask>
     */
    def postBuildTask(Closure postBuildClosure) {
        PostBuildTaskContext postBuildContext = new PostBuildTaskContext()
        AbstractContextHelper.executeInContext(postBuildClosure, postBuildContext)

        publisherNodes << NodeBuilder.newInstance().'hudson.plugins.postbuildtask.PostbuildTask' {
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
     * <publishers>
     *     ...
     *     <hudson.tasks.test.AggregatedTestResultPublisher>
     *         <jobs>some-downstream-test</jobs>
     *         <includeFailedBuilds>false</includeFailedBuilds>
     *     </hudson.tasks.test.AggregatedTestResultPublisher>
     *     ...
     * </publishers>
     */
    def aggregateDownstreamTestResults(String jobs = null, boolean includeFailedBuilds = false) {
        publisherNodes << NodeBuilder.newInstance().'hudson.tasks.test.AggregatedTestResultPublisher' {
            if (jobs) {
                delegate.jobs(jobs)
            }
            delegate.includeFailedBuilds(includeFailedBuilds)
        }
    }

    def static enum Behavior {
        DoNothing(0),
        MarkUnstable(1),
        MarkFailed(2)

        final int value

        Behavior(int value) {
            this.value = value
        }
    }

    /**
     * Configures the Groovy Postbuild script plugin
     *
     * <publishers>
     *     <org.jvnet.hudson.plugins.groovypostbuild.GroovyPostbuildRecorder>
     *         <groovyScript>
     *         script
     *         </groovyScript>
     *         <behavior>0</behavior>
     *     </org.jvnet.hudson.plugins.groovypostbuild.GroovyPostbuildRecorder>
     */
    def groovyPostBuild(String script, Behavior behavior = Behavior.DoNothing) {
        publisherNodes << NodeBuilder.newInstance().'org.jvnet.hudson.plugins.groovypostbuild.GroovyPostbuildRecorder' {
            delegate.groovyScript(script)
            delegate.behavior(behavior.value)
        }
    }

    /**
     * Configures the Javadoc Plugin, used to archive Javadoc artifacts.
     *
     * Uses the Jenkins Javadoc Plugin: https://wiki.jenkins-ci.org/display/JENKINS/Javadoc+Plugin
     *
     * <publishers>
     *     <hudson.tasks.JavadocArchiver>
     *         <javadocDir>foo</javadocDir>
     *         <keepAll>false</keepAll>
     *     </hudson.tasks.JavadocArchiver>
     * </publishers>
     */
    def archiveJavadoc(Closure javadocClosure = null) {
        JavadocContext javadocContext = new JavadocContext()
        AbstractContextHelper.executeInContext(javadocClosure, javadocContext)

        def nodeBuilder = NodeBuilder.newInstance()

        Node javadocNode = nodeBuilder.'hudson.tasks.JavadocArchiver' {
            javadocDir javadocContext.javadocDir
            keepAll javadocContext.keepAll
        }

        publisherNodes << javadocNode
    }

    /**
     * Configures the Associated Files plugin to associate archived files from
     * outside Jenkins proper.
     *
     * See https://wiki.jenkins-ci.org/display/JENKINS/Associated+Files+Plugin
     *
     * <publishers>
     *     <org.jenkinsci.plugins.associatedfiles.AssociatedFilesPublisher>
     *         <associatedFiles>/mnt/jenkins-staging/binary-staging/${JOB_NAME}-${BUILD_ID}</associatedFiles>
     *     </org.jenkinsci.plugins.associatedfiles.AssociatedFilesPublisher>
     * </publishers>
     */
    def associatedFiles(String files = null) {
        publisherNodes << NodeBuilder.newInstance().'org.jenkinsci.plugins.associatedfiles.AssociatedFilesPublisher' {
            delegate.associatedFiles(files)
        }
    }

    /**
     * Configures the Emma Code Coverage plugin
     *
     * <publishers>
     *     <hudson.plugins.emma.EmmaPublisher>
     *         <includes>coverage-results/coverage.xml</includes>
     *         <healthReports>
     *             <minClass>0</minClass>
     *             <maxClass>100</maxClass>
     *             <minMethod>0</minMethod>
     *             <maxMethod>70</maxMethod>
     *             <minBlock>0</minBlock>
     *             <maxBlock>80</maxBlock>
     *             <minLine>0</minLine>
     *             <maxLine>80</maxLine>
     *             <minCondition>0</minCondition>
     *             <maxCondition>0</maxCondition>
     *         </healthReports>
     *     </hudson.plugins.emma.EmmaPublisher>
     */
    def emma(String fileSet = '', Closure emmaClosure = null) {
        EmmaContext emmaContext = new EmmaContext()
        AbstractContextHelper.executeInContext(emmaClosure, emmaContext)

        publisherNodes << NodeBuilder.newInstance().'hudson.plugins.emma.EmmaPublisher' {
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
     * {@code
     *   <publishers>
     *      <hudson.plugins.robot.RobotPublisher plugin="robot@1.3.2">
     *          <outputPath>target/robotframework-reports</outputPath>
     *          <passThreshold>100.0</passThreshold>
     *          <unstableThreshold>0.0</unstableThreshold>
     *          <onlyCritical>false</onlyCritical>
     *          <reportFileName>report.html</reportFileName>
     *          <logFileName>log.html</logFileName>
     *          <outputFileName>output.xml</outputFileName>
     *      </hudson.plugins.robot.RobotPublisher>
     *  </publishers>
     *}
     * @see https://wiki.jenkins-ci.org/display/JENKINS/Robot+Framework+Plugin
     */
    def publishRobotFrameworkReports(Closure robotClosure = null) {

        RobotFrameworkContext context = new RobotFrameworkContext()
        AbstractContextHelper.executeInContext(robotClosure, context)

        def nodeBuilder = NodeBuilder.newInstance()
        Node robotNode = nodeBuilder.'hudson.plugins.robot.RobotPublisher' {
            passThreshold context.passThreshold
            unstableThreshold context.unstableThreshold
            outputPath context.outputPath
            onlyCritical context.onlyCritical
            reportFileName context.reportFileName
            logFileName context.logFileName
            outputFileName context.outputFileName
        }

        publisherNodes << robotNode
    }

    /**
     * Configures a Build Pipeline Trigger
     *
     * <publishers>
     *     <au.com.centrumsystems.hudson.plugin.buildpipeline.trigger.BuildPipelineTrigger>
     *         <configs>
     *             <hudson.plugins.parameterizedtrigger.PredefinedBuildParameters>
     *                 <properties>ARTIFACT_BUILD_NUMBER=$BUILD_NUMBER</properties>
     *             </hudson.plugins.parameterizedtrigger.PredefinedBuildParameters>
     *         </configs>
     *         <downstreamProjectNames>acme-project</downstreamProjectNames>
     *     </au.com.centrumsystems.hudson.plugin.buildpipeline.trigger.BuildPipelineTrigger>
     * </publishers>
     */
    def buildPipelineTrigger(String downstreamProjectNames, Closure closure = null) {
        BuildPipelineContext buildPipelineContext = new BuildPipelineContext()
        AbstractContextHelper.executeInContext(closure, buildPipelineContext)

        def nodeBuilder = NodeBuilder.newInstance()
        publisherNodes << nodeBuilder.'au.com.centrumsystems.hudson.plugin.buildpipeline.trigger.BuildPipelineTrigger' {
            delegate.downstreamProjectNames(downstreamProjectNames ?: '')
            configs(buildPipelineContext.parameterNodes)
        }
    }

    /**
     * Create commit status notifications on the commits based on the outcome of the build.
     *
     * <publishers>
     *     <com.cloudbees.jenkins.GitHubCommitNotifier/>
     * </publishers>
     */
    def githubCommitNotifier() {
        publisherNodes << new NodeBuilder().'com.cloudbees.jenkins.GitHubCommitNotifier'()
    }

    /**
     * <publishers>
     *     <hudson.plugins.git.GitPublisher>
     *         <configVersion>2</configVersion>
     *         <pushMerge>false</pushMerge>
     *         <pushOnlyIfSuccess>true</pushOnlyIfSuccess>
     *         <tagsToPush>
     *             <hudson.plugins.git.GitPublisher_-TagToPush>
     *                 <targetRepoName>origin</targetRepoName>
     *                 <tagName>foo-$PIPELINE_VERSION</tagName>
     *                 <tagMessage>Release $PIPELINE_VERSION</tagMessage>
     *                 <createTag>true</createTag>
     *                 <updateTag>false</updateTag>
     *             </hudson.plugins.git.GitPublisher_-TagToPush>
     *         </tagsToPush>
     *         <branchesToPush>
     *             <hudson.plugins.git.GitPublisher_-BranchToPush>
     *                 <targetRepoName>origin</targetRepoName>
     *                 <branchName>master</branchName>
     *             </hudson.plugins.git.GitPublisher_-BranchToPush>
     *         </branchesToPush>
     *     </hudson.plugins.git.GitPublisher>
     * </publishers>
     */
    def git(Closure gitPublisherClosure) {
        GitPublisherContext context = new GitPublisherContext()
        AbstractContextHelper.executeInContext(gitPublisherClosure, context)

        publisherNodes << NodeBuilder.newInstance().'hudson.plugins.git.GitPublisher' {
            configVersion(2)
            pushMerge(context.pushMerge)
            pushOnlyIfSuccess(context.pushOnlyIfSuccess)
            tagsToPush(context.tags)
            branchesToPush(context.branches)
        }
    }

    /**
     * <publishers>
     *     <com.flowdock.jenkins.FlowdockNotifier>
     *         <flowToken>hash</flowToken>
     *         <notificationTags/>
     *         <chatNotification>false</chatNotification>
     *         <notifyMap>
     *             <entry>
     *                 <com.flowdock.jenkins.BuildResult>ABORTED</com.flowdock.jenkins.BuildResult>
     *                 <boolean>false</boolean>
     *             </entry>
     *             <entry>
     *                 <com.flowdock.jenkins.BuildResult>SUCCESS</com.flowdock.jenkins.BuildResult>
     *                 <boolean>true</boolean>
     *             </entry>
     *             <entry>
     *                 <com.flowdock.jenkins.BuildResult>FIXED</com.flowdock.jenkins.BuildResult>
     *                 <boolean>true</boolean>
     *             </entry>
     *             <entry>
     *                 <com.flowdock.jenkins.BuildResult>UNSTABLE</com.flowdock.jenkins.BuildResult>
     *                 <boolean>false</boolean>
     *             </entry>
     *             <entry>
     *                 <com.flowdock.jenkins.BuildResult>FAILURE</com.flowdock.jenkins.BuildResult>
     *                 <boolean>true</boolean>
     *             </entry>
     *             <entry>
     *                 <com.flowdock.jenkins.BuildResult>NOT_BUILT</com.flowdock.jenkins.BuildResult>
     *                 <boolean>false</boolean>
     *             </entry>
     *         </notifyMap>
     *         <notifySuccess>true</notifySuccess>
     *         <notifyFailure>true</notifyFailure>
     *         <notifyFixed>true</notifyFixed>
     *         <notifyUnstable>false</notifyUnstable>
     *         <notifyAborted>false</notifyAborted>
     *         <notifyNotBuilt>false</notifyNotBuilt>
     *     </com.flowdock.jenkins.FlowdockNotifier>
     * </publishers>
     */
    def flowdock(String token, Closure flowdockPublisherClosure = null) {
        FlowdockPublisherContext context = new FlowdockPublisherContext()
        AbstractContextHelper.executeInContext(flowdockPublisherClosure, context)

        publisherNodes << NodeBuilder.newInstance().'com.flowdock.jenkins.FlowdockNotifier' {
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

    def flowdock(String[] tokens, Closure flowdockPublisherClosure = null) {
        // Validate values
        assert tokens != null && tokens.length > 0, 'Flowdock publish requires at least one flow token'
        flowdock(tokens.join(','), flowdockPublisherClosure)
    }

    /**
     * Configures the StashNotifier plugin.
     *
     * <publishers>
     *     <org.jenkinsci.plugins.stashNotifier.StashNotifier>
     *         <stashServerBaseUrl/>
     *         <stashUserName/>
     *         <stashUserPassword>y1/kpoWAZo+gBl7xAmdWIQ==</stashUserPassword>
     *         <ignoreUnverifiedSSLPeer>false</ignoreUnverifiedSSLPeer>
     *         <commitSha1/>
     *         <includeBuildNumberInKey>false</includeBuildNumberInKey>
     *     </org.jenkinsci.plugins.stashNotifier.StashNotifier>
     * </publishers>
     *
     * See https://wiki.jenkins-ci.org/display/JENKINS/StashNotifier+Plugin
     */
    def stashNotifier(Closure stashNotifierClosure = null) {
        StashNotifierContext context = new StashNotifierContext()
        AbstractContextHelper.executeInContext(stashNotifierClosure, context)
        publisherNodes << NodeBuilder.newInstance().'org.jenkinsci.plugins.stashNotifier.StashNotifier' {
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
     * https://wiki.jenkins-ci.org/display/JENKINS/Flexible+Publish+Plugin
     *
     * <org.jenkins__ci.plugins.flexible__publish.FlexiblePublisher>
     *     <publishers>
     *         <org.jenkins__ci.plugins.flexible__publish.ConditionalPublisher>
     *             <condition class="org.jenkins_ci.plugins.run_condition.core.AlwaysRun"/>
     *             <publisher class="hudson.tasks.ArtifactArchiver">
     *                 <artifacts/>
     *                 <latestOnly>false</latestOnly>
     *                 <allowEmptyArchive>false</allowEmptyArchive>
     *                 <onlyIfSuccessful>false</onlyIfSuccessful>
     *             </publisher>
     *             <runner class="org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail"/>
     *         </org.jenkins__ci.plugins.flexible__publish.ConditionalPublisher>
     *     </publishers>
     * </org.jenkins__ci.plugins.flexible__publish.FlexiblePublisher>
     */
    def flexiblePublish(Closure flexiblePublishClosure) {
        def context = new FlexiblePublisherContext(jobManagement)
        AbstractContextHelper.executeInContext(flexiblePublishClosure, context)

        Node action = context.action
        Preconditions.checkArgument(action != null, 'no publisher or build step specified')

        publisherNodes << new NodeBuilder().'org.jenkins__ci.plugins.flexible__publish.FlexiblePublisher' {
            delegate.publishers {
                'org.jenkins__ci.plugins.flexible__publish.ConditionalPublisher' {

                    condition(class: context.condition.conditionClass) {
                        context.condition.addArgs(delegate)
                    }
                    publisher(class: new XmlFriendlyReplacer().unescapeName(action.name().toString()), action.value())
                    runner(class: 'org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail')
                }
            }
        }
    }

    /**
     *
     * Configures the Maven Deployment Linker plugin.
     *
     * <publishers>
     *     <hudson.plugins.mavendeploymentlinker.MavenDeploymentLinkerRecorder>
     *         <regexp>*.tar.gz</regexp>
     *     </hudson.plugins.mavendeploymentlinker.MavenDeploymentLinkerRecorder>
     * </publishers
     *
     * See https://wiki.jenkins-ci.org/display/JENKINS/Maven+Deployment+Linker
     */
    def mavenDeploymentLinker(String regex) {
        publisherNodes << new NodeBuilder().'hudson.plugins.mavendeploymentlinker.MavenDeploymentLinkerRecorder' {
            regexp(regex)
        }
    }

    /**
     * Configures the post build action of the Workspace Cleanup Plugin to delete the workspace.
     *
     * <publishers>
     *     <hudson.plugins.ws__cleanup.WsCleanup>
     *         <patterns>
     *             <hudson.plugins.ws__cleanup.Pattern>
     *                 <pattern>*.java</pattern>
     *                 <type>INCLUDE</type>
     *             </hudson.plugins.ws__cleanup.Pattern>
     *             <hudson.plugins.ws__cleanup.Pattern>
     *                 <pattern>*.log</pattern>
     *                 <type>EXCLUDE</type>
     *             </hudson.plugins.ws__cleanup.Pattern>
     *         </patterns>
     *         <deleteDirs>false</deleteDirs>
     *         <cleanWhenSuccess>true</cleanWhenSuccess>
     *         <cleanWhenUnstable>true</cleanWhenUnstable>
     *         <cleanWhenFailure>true</cleanWhenFailure>
     *         <cleanWhenNotBuilt>true</cleanWhenNotBuilt>
     *         <cleanWhenAborted>true</cleanWhenAborted>
     *         <notFailBuild>false</notFailBuild>
     *         <externalDelete>rm</externalDelete>
     *     </hudson.plugins.ws__cleanup.WsCleanup>
     * </publishers>
     *
     * See https://wiki.jenkins-ci.org/display/JENKINS/Workspace+Cleanup+Plugin
     */
    def wsCleanup(Closure closure = null) {
        PostBuildCleanupContext context = new PostBuildCleanupContext()
        AbstractContextHelper.executeInContext(closure, context)

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
     * <publishers>
     *     <org.jenkinsci.plugins.rundeck.RundeckNotifier>
     *         <jobId>b4c1a982-d872-4a2b-aba4-f355371b2a8f</jobId>
     *         <options> key1=value1 key2=value2 </options>
     *         <nodeFilters> key1=value1 key2=value2 </nodeFilters>
     *         <tag/>
     *         <shouldWaitForRundeckJob>true</shouldWaitForRundeckJob>
     *         <shouldFailTheBuild>true</shouldFailTheBuild>
     *     </org.jenkinsci.plugins.rundeck.RundeckNotifier>
     * </publishers>
     */
    def rundeck(String jobIdentifier, Closure rundeckClosure = null) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(jobIdentifier), 'jobIdentifier cannot be null or empty')

        RundeckContext rundeckContext = new RundeckContext()
        AbstractContextHelper.executeInContext(rundeckClosure, rundeckContext)

        publisherNodes << NodeBuilder.newInstance().'org.jenkinsci.plugins.rundeck.RundeckNotifier' {
            jobId jobIdentifier
            options rundeckContext.options.collect { key, value -> "${key}=${value}" }.join('\n')
            nodeFilters rundeckContext.nodeFilters.collect { key, value -> "${key}=${value}" }.join('\n')
            tag rundeckContext.tag
            shouldWaitForRundeckJob rundeckContext.shouldWaitForRundeckJob
            shouldFailTheBuild rundeckContext.shouldFailTheBuild
        }
    }
}
