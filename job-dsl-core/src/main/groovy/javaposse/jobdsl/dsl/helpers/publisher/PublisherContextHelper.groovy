package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context

class PublisherContextHelper extends AbstractContextHelper<PublisherContextHelper.PublisherContext> {

    PublisherContextHelper(List<WithXmlAction> withXmlActions) {
        super(withXmlActions)
    }

    def publishers(Closure closure) {
        execute(closure, new PublisherContextHelper.PublisherContext())
    }

    Closure generateWithXmlClosure(PublisherContextHelper.PublisherContext context) {
        return { Node project ->
            def publishersNode
            if (project.publishers.isEmpty()) {
                publishersNode = project.appendNode('publishers')
            } else {
                publishersNode = project.publishers[0]
            }
            context.publisherNodes.each {
                publishersNode << it
            }
        }
    }

    static class PublisherContext implements Context {
        List<Node> publisherNodes = []

        PublisherContext() {
        }

        PublisherContext(List<Node> publisherNodes) {
            this.publisherNodes = publisherNodes
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
         * TODO Support list for recipients
         * TODO Escape XML for all subject and content fields
         */
        def extendedEmail(String recipients = null, Closure emailClosure = null) {
            return extendedEmail(recipients, null, emailClosure)
        }

        def extendedEmail(String recipients, String subjectTemplate, Closure emailClosure = null) {
            return extendedEmail(recipients, subjectTemplate, null, emailClosure)
        }

        def extendedEmail(String recipients, String subjectTemplate, String contentTemplate, Closure emailClosure = null) {
            EmailContext emailContext = new EmailContext()
            AbstractContextHelper.executeInContext(emailClosure, emailContext)

            // Validate that we have the typical triggers, if nothing is provided
            if (emailContext.emailTriggers.isEmpty()) {
                emailContext.emailTriggers << new EmailContext.EmailTrigger('Failure')
                emailContext.emailTriggers << new EmailContext.EmailTrigger('Success')
            }

            recipients = recipients ?: '$DEFAULT_RECIPIENTS'
            subjectTemplate = subjectTemplate ?: '$DEFAULT_SUBJECT'
            contentTemplate = contentTemplate ?: '$DEFAULT_CONTENT'

            // Now that the context has what we need
            def nodeBuilder = NodeBuilder.newInstance()
            def emailNode = nodeBuilder.'hudson.plugins.emailext.ExtendedEmailPublisher' {
                recipientList recipients
                contentType 'default'
                defaultSubject subjectTemplate
                defaultContent contentTemplate
                attachmentsPattern ''

                configuredTriggers {
                    emailContext.emailTriggers.each { EmailContext.EmailTrigger trigger ->
                        "hudson.plugins.emailext.plugins.trigger.${trigger.triggerShortName}Trigger" {
                            email {
                                recipientList trigger.recipientList
                                subject trigger.subject
                                body trigger.body
                                sendToDevelopers trigger.sendToDevelopers
                                sendToRequester trigger.sendToRequester
                                includeCulprits trigger.includeCulprits
                                sendToRecipientList trigger.sendToRecipientList
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
         <hudson.tasks.ArtifactArchiver>
         <artifacts>build/libs/*</artifacts>
         <latestOnly>false</latestOnly>
         </hudson.tasks.ArtifactArchiver>
         * @param glob
         * @param excludeGlob
         * @param latestOnly
         */
        def archiveArtifacts(String glob, String excludeGlob = null, Boolean latestOnlyBoolean = false) {
            def nodeBuilder = new NodeBuilder()

            Node archiverNode = nodeBuilder.'hudson.tasks.ArtifactArchiver' {
                artifacts glob
                if (excludeGlob) {
                    excludes excludeGlob
                }
                latestOnly latestOnlyBoolean ? 'true' : 'false'
            }

            publisherNodes << archiverNode
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
        def archiveJunit(String glob, boolean retainLongStdout = false, boolean allowClaimingOfFailedTests = false, boolean publishTestAttachments = false) {
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
         * With only the target specified:
         <hudson.plugins.jabber.im.transport.JabberPublisher>
         <targets>
         <hudson.plugins.im.GroupChatIMMessageTarget>
         <name>api@conference.jabber.netflix.com</name>
         <notificationOnly>false</notificationOnly>
         </hudson.plugins.im.GroupChatIMMessageTarget>
         </targets>
         <strategy>ALL</strategy> // all
         or <strategy>FAILURE_AND_FIXED</strategy> // failure and fixed
         or <strategy>ANY_FAILURE</strategy> // failure
         or <strategy>STATECHANGE_ONLY</strategy> // change
         <notifyOnBuildStart>false</notifyOnBuildStart> // Notify on build starts
         <notifySuspects>false</notifySuspects> // Notify SCM committers
         <notifyCulprits>false</notifyCulprits> // Notify SCM culprits
         <notifyFixers>false</notifyFixers> // Notify upstream committers
         <notifyUpstreamCommitters>false</notifyUpstreamCommitters> // Notify SCM fixers

         // Channel Notification Message
         <buildToChatNotifier class="hudson.plugins.im.build_notify.DefaultBuildToChatNotifier"/> // Summary + SCM change
         or <buildToChatNotifier class="hudson.plugins.im.build_notify.SummaryOnlyBuildToChatNotifier"/> // Just Summary
         or <buildToChatNotifier class="hudson.plugins.im.build_notify.BuildParametersBuildToChatNotifier"/> // Summary and build parameters
         or <buildToChatNotifier class="hudson.plugins.im.build_notify.PrintFailingTestsBuildToChatNotifier"/> // Summary, SCM changes and failed tests
         <matrixMultiplier>ONLY_CONFIGURATIONS</matrixMultiplier>
         </hudson.plugins.jabber.im.transport.JabberPublisher>
         */
        def publishJabber(String target, Closure jabberClosure = null) {
            publishJabber(target, null, null, jabberClosure)
        }

        def publishJabber(String target, String strategyName, Closure jabberClosure = null) {
            publishJabber(target, strategyName, null, jabberClosure)
        }

        def publishJabber(String targetsArg, String strategyName, String channelNotificationName, Closure jabberClosure = null) {
            JabberContext jabberContext = new JabberContext()
            jabberContext.strategyName = strategyName ?: 'ALL'
            jabberContext.channelNotificationName = channelNotificationName ?: 'Default'
            AbstractContextHelper.executeInContext(jabberClosure, jabberContext)

            // Validate values
            assert validJabberStrategyNames.contains(jabberContext.strategyName), "Jabber Strategy needs to be one of these values: ${validJabberStrategyNames.join(',')}"
            assert validJabberChannelNotificationNames.contains(jabberContext.channelNotificationName), "Jabber Channel Notification name needs to be one of these values: ${validJabberChannelNotificationNames.join(',')}"

            def nodeBuilder = NodeBuilder.newInstance()
            def publishNode = nodeBuilder.'hudson.plugins.jabber.im.transport.JabberPublisher' {
                targets {
                    targetsArg.split().each { target ->
                        def isGroup = target.startsWith('*')
                        def targetClean = isGroup ? target.substring(1) : target
                        'hudson.plugins.im.GroupChatIMMessageTarget' {
                            delegate.createNode('name', targetClean)
                            if (isGroup) {
                                notificationOnly 'false'
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
                buildToChatNotifier('class': "hudson.plugins.im.build_notify.${jabberContext.channelNotificationName}BuildToChatNotifier")
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
            assert !scpContext.entries.isEmpty(), "Scp publish requires at least one entry"

            def nodeBuilder = NodeBuilder.newInstance()
            // TODO Possibility to update existing publish node
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
         * <workspaceGlob></workspaceGlob>
         * <workspaceExcludeGlob></workspaceExcludeGlob>
         * <criteria>Any</criteria>
         * <archiveMethod>TAR</archiveMethod>
         * <overrideDefaultExcludes>true</overrideDefaultExcludes>
         * </hudson.plugins.cloneworkspace.CloneWorkspacePublisher>
         */
        def publishCloneWorkspace(String workspaceGlob, Closure cloneWorkspaceClosure) {
            publishCloneWorkspace(workspaceGlob, '', 'Any', 'TAR', false, null)
        }

        def publishCloneWorkspace(String workspaceGlobArg, String workspaceExcludeGlobArg = '', String criteriaArg = 'Any', String archiveMethodArg = 'TAR', boolean overrideDefaultExcludesArg = false, Closure cloneWorkspaceClosure = null) {
            CloneWorkspaceContext cloneWorkspaceContext = new CloneWorkspaceContext()
            cloneWorkspaceContext.criteria = criteriaArg ?: 'Any'
            cloneWorkspaceContext.archiveMethod = archiveMethodArg ?: 'TAR'
            AbstractContextHelper.executeInContext(cloneWorkspaceClosure, cloneWorkspaceContext)

            // Validate values
            assert validCloneWorkspaceCriteria.contains(cloneWorkspaceContext.criteria), "Clone Workspace Criteria needs to be one of these values: ${validCloneWorkspaceCriteria.join(',')}"
            assert validCloneWorkspaceArchiveMethods.contains(cloneWorkspaceContext.archiveMethod), "Clone Workspace Archive Method needs to be one of these values: ${validCloneWorkspaceArchiveMethods.join(',')}"

            def nodeBuilder = NodeBuilder.newInstance()
            def publishNode = nodeBuilder.'hudson.plugins.cloneworkspace.CloneWorkspacePublisher' {
                workspaceGlob workspaceGlobArg
                workspaceExcludeGlob workspaceExcludeGlobArg
                criteria criteriaArg
                archiveMethod archiveMethodArg
                overrideDefaultExcludes overrideDefaultExcludesArg
            }
            publisherNodes << publishNode
        }

        def validCloneWorkspaceCriteria = ['Any', 'Not Failed', 'Successful']
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
            def thresholdColorMap = ['SUCCESS': 'BLUE', 'UNSTABLE': 'YELLOW', 'FAILURE': 'RED']
            def thresholdOrdinalMap = ['SUCCESS': '0', 'UNSTABLE': '1', 'FAILURE': '2']
            assert thresholdColorMap.containsKey(thresholdName), "thresholdName must be one of these values ${thresholdColorMap.keySet().join(',')}"

            def nodeBuilder = new NodeBuilder()
            Node publishNode = nodeBuilder.'hudson.tasks.BuildTrigger' {
                childProjects projectName
                threshold {
                    delegate.createNode('name', thresholdName)
                    ordinal thresholdOrdinalMap[thresholdName]
                    color thresholdColorMap[thresholdName]
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

            def nodeBuilder = NodeBuilder.newInstance()
            def publishNode = nodeBuilder.'hudson.plugins.parameterizedtrigger.BuildTrigger' {
                configs {
                    downstreamContext.triggers.each { DownstreamTriggerContext trigger ->
                        'hudson.plugins.parameterizedtrigger.BuildTriggerConfig' {
                            projects trigger.projects
                            condition trigger.condition
                            triggerWithNoParameters trigger.triggerWithNoParameters ? 'true' : 'false'
                            if (trigger.hasParameter()) {
                                configs {
                                    if (trigger.usingCurrentBuild) {
                                        'hudson.plugins.parameterizedtrigger.CurrentBuildParameters' ''
                                    }

                                    if (trigger.usingPropertiesFile) {
                                        'hudson.plugins.parameterizedtrigger.FileBuildParameters' {
                                            propertiesFile trigger.propFile
                                        }
                                    }

                                    if (trigger.usingGitRevision) {
                                        'hudson.plugins.git.GitRevisionBuildParameters' {
                                            'combineQueuedCommits' trigger.combineQueuedCommits ? 'true' : 'false'
                                        }
                                    }

                                    if (trigger.usingPredefined) {
                                        'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters' {
                                            delegate.createNode('properties', trigger.predefinedProps.join('\n'))
                                        }
                                    }

                                    if (trigger.usingMatrixSubset) {
                                        'hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters' {
                                            filter trigger.matrixSubsetFilter
                                        }
                                    }

                                    if (trigger.usingSubversionRevision) {
                                        'hudson.plugins.parameterizedtrigger.SubversionRevisionBuildParameters' {}
                                    }
                                }
                            } else {
                                configs('class': 'java.util.Collections$EmptyList')
                            }
                        }
                    }
                }
            }
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
            def publishNode = nodeBuilder.'hudson.plugins.violations.ViolationsPublisher'(plugin: 'violations@0.7.11') {
                config {
                    suppressions(class: "tree-set") {
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
                                    pattern(violationsEntry.pattern?:'')
                                }
                            }
                        }
                    }
                    limit(violationsContext.perFileDisplayLimit.toString())
                    sourcePathPattern(violationsContext.sourcePathPattern?:'')
                    fauxProjectPath(violationsContext.fauxProjectPath?:'')
                    encoding(violationsContext.sourceEncoding?:'default')
                }
            }
            publisherNodes << publishNode
        }
    }
}











