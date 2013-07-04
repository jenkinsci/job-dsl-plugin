package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContextHelper.PublisherContext
import spock.lang.Specification

public class PublisherHelperSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    PublisherContextHelper helper = new PublisherContextHelper(mockActions, JobType.Freeform)
    PublisherContext context = new PublisherContext()

    def 'empty call extended email method'() {
        when:
        context.extendedEmail()

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        Node emailPublisher = context.publisherNodes[0]
        emailPublisher.name() == 'hudson.plugins.emailext.ExtendedEmailPublisher'
        emailPublisher.recipientList[0].value() == '$DEFAULT_RECIPIENTS'
        emailPublisher.defaultSubject[0].value() == '$DEFAULT_SUBJECT'
        emailPublisher.contentType[0].value() == 'default'
        Node triggers = emailPublisher.configuredTriggers[0]
        triggers.children().size() == 2
        Node email = triggers.children()[0].email[0]
        email.recipientList[0].value() == ''
        email.subject[0].value() == '$PROJECT_DEFAULT_SUBJECT'
        email.body[0].value() == '$PROJECT_DEFAULT_CONTENT'
    }

    def 'call extended email with args'() {
        when:
        context.extendedEmail('me@halfempty.org', 'Oops', 'Something broken') {
            trigger('PreBuild')
            trigger(triggerName: 'StillUnstable', subject: 'Subject', body:'Body', recipientList:'RecipientList',
                    sendToDevelopers: true, sendToRequester: true, includeCulprits: true, sendToRecipientList: false)
            configure { node ->
                node / contentType << 'html'
            }
        }

        then:
        Node emailPublisher = context.publisherNodes[0]
        emailPublisher.recipientList[0].value() == 'me@halfempty.org'
        emailPublisher.defaultSubject[0].value() == 'Oops'
        emailPublisher.defaultContent[0].value() == 'Something broken'
        emailPublisher.contentType.size() == 1
        emailPublisher.contentType[0].value() == 'html'
        Node triggers = emailPublisher.configuredTriggers[0]
        triggers.children().size() == 2
        Node emailDefault = triggers.children()[0].email[0]
        emailDefault.recipientList[0].value() == ''
        emailDefault.subject[0].value() == '$PROJECT_DEFAULT_SUBJECT'
        emailDefault.body[0].value() == '$PROJECT_DEFAULT_CONTENT'
        emailDefault.sendToDevelopers[0].value() as String == 'false'
        emailDefault.sendToRequester[0].value() as String == 'false'
        emailDefault.includeCulprits[0].value() as String == 'false'
        emailDefault.sendToRecipientList[0].value() as String == 'true'

        triggers.children()[1].name() == 'hudson.plugins.emailext.plugins.trigger.StillUnstableTrigger'
        Node email = triggers.children()[1].email[0]
        email.recipientList[0].value() == 'RecipientList'
        email.subject[0].value() == 'Subject'
        email.body[0].value() == 'Body'
        email.sendToDevelopers[0].value() as String == 'true'
        email.sendToRequester[0].value() as String == 'true'
        email.includeCulprits[0].value() as String == 'true'
        email.sendToRecipientList[0].value() as String == 'false'
    }


    def 'loop triggers to set send to requester'() {
        when:
        // Given by Thaddeus Diamond <thaddeus@hadapt.com> in mailing list
        def triggerNames = ['Unstable', 'Aborted', 'Success', 'Failure']
        context.extendedEmail('', '$DEFAULT_SUBJECT', '$DEFAULT_CONTENT') {
            triggerNames.each { result -> trigger triggerName: result, sendToRequester: true }
        }

        then:
        Node emailPublisher = context.publisherNodes[0]

        emailPublisher.recipientList[0].value() == '' // Not $DEFAULT_RECIPIENTS, not sure if this is valid

        Node triggers = emailPublisher.configuredTriggers[0]
        triggers.children().size() == triggerNames.size()
        Node emailDefault = triggers.children()[0].email[0]
        emailDefault.sendToDevelopers[0].value() == 'false'
        emailDefault.sendToRequester[0].value() == 'true'
    }

    def 'call archive artifacts with all args'() {
        when:
        context.archiveArtifacts('include/*', 'exclude/*', true)

        then:
        Node archiveNode = context.publisherNodes[0]
        archiveNode.name() == 'hudson.tasks.ArtifactArchiver'
        archiveNode.artifacts[0].value() == 'include/*'
        archiveNode.excludes[0].value() == 'exclude/*'
        archiveNode.latestOnly[0].value() == 'true'

    }

    def 'call archive artifacts least args'() {
        when:
        context.archiveArtifacts('include/*')

        then:
        Node archiveNode = context.publisherNodes[0]
        archiveNode.name() == 'hudson.tasks.ArtifactArchiver'
        archiveNode.artifacts[0].value() == 'include/*'
        archiveNode.excludes.isEmpty()
        archiveNode.latestOnly[0].value() == 'false'

    }

    def 'call junit archive with all args'() {
        when:
        context.archiveJunit('include/*', true, true, true)

        then:
        Node archiveNode = context.publisherNodes[0]
        archiveNode.name() == 'hudson.tasks.junit.JUnitResultArchiver'
        archiveNode.testResults[0].value() == 'include/*'
        archiveNode.keepLongStdio[0].value() == 'true'
        archiveNode.testDataPublishers[0].'hudson.plugins.claim.ClaimTestDataPublisher'[0] != null
        archiveNode.testDataPublishers[0].'hudson.plugins.junitattachments.AttachmentPublisher'[0] != null
    }


    def 'call junit archive with minimal args'() {
        when:
        context.archiveJunit('include/*')

        then:
        Node archiveNode = context.publisherNodes[0]
        archiveNode.name() == 'hudson.tasks.junit.JUnitResultArchiver'
        archiveNode.testResults[0].value() == 'include/*'
        archiveNode.keepLongStdio[0].value() == 'false'
        archiveNode.testDataPublishers[0] != null
        !archiveNode.testDataPublishers[0].children().any { it.name() == 'hudson.plugins.claim.ClaimTestDataPublisher' }
        !archiveNode.testDataPublishers[0].children().any { it.name() == 'hudson.plugins.junitattachments.AttachmentPublisher' }
    }

    def 'calling minimal html publisher'() {
        when:
        context.publishHtml {
            report 'build/*'
        }

        then:
        Node publisherHtmlNode = context.publisherNodes[0]
        publisherHtmlNode.name() == 'htmlpublisher.HtmlPublisher'
        !publisherHtmlNode.reportTargets.isEmpty()
        def target = publisherHtmlNode.reportTargets[0].'htmlpublisher.HtmlPublisherTarget'[0]
        target.reportName[0].value() == ''
        target.reportDir[0].value() == 'build/*'
        target.reportFiles[0].value() == 'index.html'
        target.keepAll[0].value() == 'false'
        target.wrapperName[0].value() == 'htmlpublisher-wrapper.html'
    }

    def 'calling html publisher with a few args'() {
        when:
        context.publishHtml {
            report reportName: 'Report Name', reportDir: 'build/*', reportFiles: 'content.html', keepAll: true
        }

        then:
        Node publisherHtmlNode = context.publisherNodes[0]
        publisherHtmlNode.name() == 'htmlpublisher.HtmlPublisher'
        !publisherHtmlNode.reportTargets.isEmpty()
        def target = publisherHtmlNode.reportTargets[0].'htmlpublisher.HtmlPublisherTarget'[0]
        target.reportName[0].value() == 'Report Name'
        target.reportDir[0].value() == 'build/*'
        target.reportFiles[0].value() == 'content.html'
        target.keepAll[0].value() == 'true'
        target.wrapperName[0].value() == 'htmlpublisher-wrapper.html'
    }

    def 'calling html publisher with map syntax without all args'() {
        when:
        context.publishHtml {
            report reportName: 'Report Name', reportDir: 'build/*'
        }

        then:
        Node publisherHtmlNode = context.publisherNodes[0]
        publisherHtmlNode.name() == 'htmlpublisher.HtmlPublisher'
        !publisherHtmlNode.reportTargets.isEmpty()
        def target = publisherHtmlNode.reportTargets[0].'htmlpublisher.HtmlPublisherTarget'[0]
        target.reportName[0].value() == 'Report Name'
        target.reportDir[0].value() == 'build/*'
        target.reportFiles[0].value() == 'index.html'
        target.keepAll[0].value() == 'false'
        target.wrapperName[0].value() == 'htmlpublisher-wrapper.html'
    }

    def 'calling html publisher with multiple reports'() {
        when:
        context.publishHtml {
            report('build/*', 'Build Report')
            report('test/*', 'Test Report')
        }

        then:
        Node publisherHtmlNode = context.publisherNodes[0]
        publisherHtmlNode.name() == 'htmlpublisher.HtmlPublisher'
        !publisherHtmlNode.reportTargets.isEmpty()
        def target1 = publisherHtmlNode.reportTargets[0].'htmlpublisher.HtmlPublisherTarget'[0]
        target1.reportName[0].value() == 'Build Report'
        target1.reportDir[0].value() == 'build/*'

        def target2 = publisherHtmlNode.reportTargets[0].'htmlpublisher.HtmlPublisherTarget'[1]
        target2.reportName[0].value() == 'Test Report'
        target2.reportDir[0].value() == 'test/*'
    }

    def 'call Jabber publish with minimal args'() {
        when:
        context.publishJabber('me@gmail.com')

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.plugins.jabber.im.transport.JabberPublisher'
        def targetNode = publisherNode.targets[0].'hudson.plugins.im.GroupChatIMMessageTarget'[0]
        targetNode.name[0].value() == 'me@gmail.com'
        targetNode.notificationOnly.size() == 0 // No noficationOnly when not a group
        publisherNode.strategy[0].value() == 'ALL'
        publisherNode.notifyOnBuildStart[0].value() == 'false'
        publisherNode.notifySuspects[0].value() == 'false'
        publisherNode.notifyCulprits[0].value() == 'false'
        publisherNode.notifyFixers[0].value() == 'false'
        publisherNode.notifyUpstreamCommitters[0].value() == 'false'
        Node buildToNode = publisherNode.buildToChatNotifier[0]
        buildToNode.attributes().containsKey('class')
        buildToNode.attribute('class') == 'hudson.plugins.im.build_notify.DefaultBuildToChatNotifier'
        publisherNode.matrixMultiplier[0].value() == 'ONLY_CONFIGURATIONS'

    }

    def 'call Jabber publish with all args'() {
        when:
        context.publishJabber('me@gmail.com *tools@hipchat.com', 'ANY_FAILURE', 'SummaryOnly' )

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.plugins.jabber.im.transport.JabberPublisher'
        publisherNode.targets[0].'hudson.plugins.im.GroupChatIMMessageTarget'.size() == 2

        def emailTargetNode = publisherNode.targets[0].'hudson.plugins.im.GroupChatIMMessageTarget'[0]
        emailTargetNode.name[0].value() == 'me@gmail.com'
        emailTargetNode.notificationOnly.size() == 0

        def confTargetNode = publisherNode.targets[0].'hudson.plugins.im.GroupChatIMMessageTarget'[1]
        confTargetNode.name[0].value() == 'tools@hipchat.com'
        confTargetNode.notificationOnly[0].value() == 'false'

        publisherNode.strategy[0].value() == 'ANY_FAILURE'
        Node buildToNode = publisherNode.buildToChatNotifier[0]
        buildToNode.attributes().containsKey('class')
        buildToNode.attribute('class') == 'hudson.plugins.im.build_notify.SummaryOnlyBuildToChatNotifier'
    }

    def 'call Jabber publish with closure args'() {
        when:
        context.publishJabber('me@gmail.com', 'ANY_FAILURE', 'SummaryOnly' ) {
            strategyName 'FAILURE_AND_FIXED' // ALL,  FAILURE_AND_FIXED, ANY_FAILURE, STATECHANGE_ONLY
            notifyOnBuildStart = true
            notifySuspects = true
            notifyCulprits = true
            notifyFixers = true
            notifyUpstreamCommitters = true
            channelNotificationName = 'PrintFailingTests' // Default, SummaryOnly, BuildParameters, PrintFailingTests
        }

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.plugins.jabber.im.transport.JabberPublisher'
        def targetNode = publisherNode.targets[0].'hudson.plugins.im.GroupChatIMMessageTarget'[0]
        targetNode.name[0].value() == 'me@gmail.com'
        targetNode.notificationOnly.size() == 0
        publisherNode.strategy[0].value() == 'FAILURE_AND_FIXED'
        publisherNode.notifyOnBuildStart[0].value() == 'true'
        publisherNode.notifySuspects[0].value() == 'true'
        publisherNode.notifyCulprits[0].value() == 'true'
        publisherNode.notifyFixers[0].value() == 'true'
        publisherNode.notifyUpstreamCommitters[0].value() == 'true'
        Node buildToNode = publisherNode.buildToChatNotifier[0]
        buildToNode.attributes().containsKey('class')
        buildToNode.attribute('class') == 'hudson.plugins.im.build_notify.PrintFailingTestsBuildToChatNotifier'
        publisherNode.matrixMultiplier[0].value() == 'ONLY_CONFIGURATIONS'
    }


    def 'call Jabber publish to get exceptions'() {
        when:
        context.publishJabber('me@gmail.com', 'NOPE')

        then:
        thrown(AssertionError)

        when:
        context.publishJabber('me@gmail.com', 'ALL', 'Nope')

        then:
        thrown(AssertionError)

    }

    def 'call Clone Workspace publish with minimal args'() {
        when:
        context.publishCloneWorkspace('*/**')

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.plugins.cloneworkspace.CloneWorkspacePublisher'
        publisherNode.workspaceGlob[0].value() == '*/**'
        publisherNode.workspaceExcludeGlob[0].value() == ''
        publisherNode.criteria[0].value() == 'Any'
        publisherNode.archiveMethod[0].value() == 'TAR'
        publisherNode.overrideDefaultExcludes[0].value() == false
    }

    def 'call Clone Workspace publish with all args'() {
        when:
        context.publishCloneWorkspace('*/**', '*/.svn', 'Not Failed', 'ZIP', true)

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.plugins.cloneworkspace.CloneWorkspacePublisher'
        publisherNode.workspaceGlob[0].value() == '*/**'
        publisherNode.workspaceExcludeGlob[0].value() == '*/.svn'
        publisherNode.criteria[0].value() == 'Not Failed'
        publisherNode.archiveMethod[0].value() == 'ZIP'
        publisherNode.overrideDefaultExcludes[0].value() == true
    }

    def 'call Clone Workspace publish to get exceptions'() {
        when:
        context.publishCloneWorkspace('*/**', '*/.svn', 'Quite plainly wrong', 'ZIP', true)

        then:
        thrown(AssertionError)

        when:
        context.publishCloneWorkspace('*/**', '*/.svn', 'Not Failed', 'ZAP', true)

        then:
        thrown(AssertionError)
    }

    def 'call scp publish with not enough entries'() {
        when:
        context.publishScp('javadoc', null)

        then:
        thrown(AssertionError)
    }

    def 'call scp publish with closure'() {
        when:
        context.publishScp('javadoc') {
            entry('api-sdk/**/*')
        }

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'be.certipost.hudson.plugin.SCPRepositoryPublisher'
        publisherNode.siteName[0].value() == 'javadoc'
        def entryNode = publisherNode.entries[0].'be.certipost.hudson.plugin.Entry'[0]
        entryNode.filePath[0].value() == ''
        entryNode.sourceFile[0].value() == 'api-sdk/**/*'
        entryNode.keepHierarchy[0].value() == 'false'

        when:
        context.publishScp('javadoc') {
            entry('build/javadocs/**/*', 'javadoc', true)
        }

        then:
        Node publisherNode2 = context.publisherNodes[1]
        publisherNode2.name() == 'be.certipost.hudson.plugin.SCPRepositoryPublisher'
        publisherNode2.siteName[0].value() == 'javadoc'
        def entryNode2 = publisherNode2.entries[0].'be.certipost.hudson.plugin.Entry'[0]
        entryNode2.filePath[0].value() == 'javadoc'
        entryNode2.sourceFile[0].value() == 'build/javadocs/**/*'
        entryNode2.keepHierarchy[0].value() == 'true'
    }

    def 'call trigger downstream without args'() {
        when:
        context.downstream('THE-JOB')

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.tasks.BuildTrigger'
        publisherNode.childProjects[0].value() == 'THE-JOB'
        publisherNode.threshold[0].name[0].value() == 'SUCCESS'
        publisherNode.threshold[0].ordinal[0].value() == '0'
        publisherNode.threshold[0].color[0].value() == 'BLUE'
    }

    def 'call trigger downstream'() {
            when:
        context.downstream('THE-JOB', 'FAILURE')

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.tasks.BuildTrigger'
        publisherNode.childProjects[0].value() == 'THE-JOB'
        publisherNode.threshold[0].name[0].value() == 'FAILURE'
        publisherNode.threshold[0].ordinal[0].value() == '2'
        publisherNode.threshold[0].color[0].value() == 'RED'
    }

    def 'call trigger downstream with bad args'() {
        when:
        context.downstream('THE-JOB', 'BAD')

        then:
        thrown(AssertionError)
    }

    def 'call downstream ext with all args'() {
        when:
        context.downstreamParameterized {
            trigger('Project1, Project2', 'UNSTABLE_OR_BETTER', true) {
                currentBuild() // Current build parameters
                propertiesFile('dir/my.properties') // Parameters from properties file
                gitRevision(false) // Pass-through Git commit that was built
                predefinedProp('key1', 'value1') // Predefined properties
                predefinedProps([key2: 'value2', key3: 'value3'])
                predefinedProps('key4=value4\nkey5=value5') // Newline separated
                matrixSubset('label=="${TARGET}"') // Restrict matrix execution to a subset
                subversionRevision() // Subversion Revision
            }
            trigger('Project2') {
                currentBuild()
            }
        }

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.plugins.parameterizedtrigger.BuildTrigger'
        publisherNode.configs[0].children().size() == 2
        Node first = publisherNode.configs[0].'hudson.plugins.parameterizedtrigger.BuildTriggerConfig'[0]
        first.projects[0].value() == 'Project1, Project2'
        first.condition[0].value() == 'UNSTABLE_OR_BETTER'
        first.triggerWithNoParameters[0].value() == 'true'
        first.configs[0].'hudson.plugins.parameterizedtrigger.CurrentBuildParameters'[0] instanceof Node
        first.configs[0].'hudson.plugins.parameterizedtrigger.FileBuildParameters'[0].propertiesFile[0].value() == 'dir/my.properties'
        first.configs[0].'hudson.plugins.git.GitRevisionBuildParameters'[0].combineQueuedCommits[0].value() == 'false'
        first.configs[0].'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'.size() == 1
        first.configs[0].'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'[0].'properties'[0].value() ==
                'key1=value1\nkey2=value2\nkey3=value3\nkey4=value4\nkey5=value5'
        first.configs[0].'hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters'[0].filter[0].value() == 'label=="${TARGET}"'
        first.configs[0].'hudson.plugins.parameterizedtrigger.SubversionRevisionBuildParameters'[0] instanceof Node

        Node second = publisherNode.configs[0].'hudson.plugins.parameterizedtrigger.BuildTriggerConfig'[1]
        second.projects[0].value() == 'Project2'
        second.condition[0].value() == 'SUCCESS'
        second.triggerWithNoParameters[0].value() == 'false'
        second.configs[0].'hudson.plugins.parameterizedtrigger.CurrentBuildParameters'[0] instanceof Node

        when:
        context.downstreamParameterized {
            trigger('Project3') {
            }
        }

        then:
        Node third = context.publisherNodes[1].configs[0].'hudson.plugins.parameterizedtrigger.BuildTriggerConfig'[0]
        third.projects[0].value() == 'Project3'
        third.condition[0].value() == 'SUCCESS'
        third.triggerWithNoParameters[0].value() == 'false'
        third.configs[0].attribute('class') == 'java.util.Collections$EmptyList'

        when:
        context.downstreamParameterized {
            trigger('Project4', 'WRONG')
        }

        then:
        thrown(AssertionError)
    }


    def 'call violations plugin with no args has correct defaults'() {
        when:
        context.violations()

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.config[0].limit[0].value() == '100'
        publisherNode.config[0].sourcePathPattern[0].value() == ''
        publisherNode.config[0].fauxProjectPath[0].value() == ''
        publisherNode.config[0].encoding[0].value() == 'default'
        def typeConfigsNode = publisherNode.config[0].typeConfigs[0]
        typeConfigsNode.entry.size() == 16
        def simianNode = typeConfigsNode.entry.find { it.string[0].value() == 'simian'}
        simianNode != null
        def typeConfigNode = simianNode.'hudson.plugins.violations.TypeConfig'[0]
        typeConfigNode.type[0].value() == 'simian'
        typeConfigNode.min[0].value() == '10'
        typeConfigNode.max[0].value() == '999'
        typeConfigNode.unstable[0].value() == '999'
        typeConfigNode.usePattern[0].value() == 'false'
        typeConfigNode.pattern[0].value() == ''
    }

    def 'call violations plugin with all args'() {
        when:
        context.violations(50) {
            sourcePathPattern 'source pattern'
            fauxProjectPath 'faux path'
            perFileDisplayLimit 51
            checkstyle(10, 11, 10, 'test-report/*.xml')
            findbugs(12, 13, 12)
        }

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.plugins.violations.ViolationsPublisher'
        publisherNode.config[0].suppressions.size() == 1
        publisherNode.config[0].limit[0].value() == '51'
        publisherNode.config[0].sourcePathPattern[0].value() == 'source pattern'
        publisherNode.config[0].fauxProjectPath[0].value() == 'faux path'
        publisherNode.config[0].encoding[0].value() == 'default'
        def typeConfigsNode = publisherNode.config[0].typeConfigs[0]
        typeConfigsNode.entry.size() == 16
        def checkstyleNode = typeConfigsNode.entry.find { it.string[0].value() == 'checkstyle'}
        checkstyleNode != null
        checkstyleNode.'hudson.plugins.violations.TypeConfig'[0].type[0].value() == 'checkstyle'
        checkstyleNode.'hudson.plugins.violations.TypeConfig'[0].min[0].value() == '10'
        checkstyleNode.'hudson.plugins.violations.TypeConfig'[0].max[0].value() == '11'
        checkstyleNode.'hudson.plugins.violations.TypeConfig'[0].unstable[0].value() == '10'
        checkstyleNode.'hudson.plugins.violations.TypeConfig'[0].usePattern[0].value() == 'true'
        checkstyleNode.'hudson.plugins.violations.TypeConfig'[0].pattern[0].value() == 'test-report/*.xml'
        def findbugsNode = typeConfigsNode.entry.find { it.string[0].value() == 'findbugs'}
        findbugsNode.'hudson.plugins.violations.TypeConfig'[0].type[0].value() == 'findbugs'
        findbugsNode.'hudson.plugins.violations.TypeConfig'[0].min[0].value() == '12'
        findbugsNode.'hudson.plugins.violations.TypeConfig'[0].max[0].value() == '13'
        findbugsNode.'hudson.plugins.violations.TypeConfig'[0].unstable[0].value() == '12'
        findbugsNode.'hudson.plugins.violations.TypeConfig'[0].usePattern[0].value() == 'false'
        findbugsNode.'hudson.plugins.violations.TypeConfig'[0].pattern[0].value() == ''
        def jslintNode = typeConfigsNode.entry.find { it.string[0].value() == 'jslint'}
        jslintNode.'hudson.plugins.violations.TypeConfig'[0].type[0].value() == 'jslint'
        jslintNode.'hudson.plugins.violations.TypeConfig'[0].min[0].value() == '10'
        jslintNode.'hudson.plugins.violations.TypeConfig'[0].max[0].value() == '999'
        jslintNode.'hudson.plugins.violations.TypeConfig'[0].unstable[0].value() == '999'
        jslintNode.'hudson.plugins.violations.TypeConfig'[0].usePattern[0].value() == 'false'
        jslintNode.'hudson.plugins.violations.TypeConfig'[0].pattern[0].value() == ''
    }

    def 'call violations plugin with bad types'() {
        when:
        context.violations {
            badType 10
        }

        then:
        thrown(IllegalArgumentException)
    }

    def 'call step via helper'() {
        when:
        helper.publishers {
            extendedEmail()
        }

        then:
        1 * mockActions.add(_)
    }

    def 'can run cordell walker'() {
        when:
        helper.publishers {
            chucknorris()
        }

        then:
        1 * mockActions.add(_)
    }

    def 'cordell walker constructs xml'() {
        when:
        context.chucknorris()

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.plugins.chucknorris.CordellWalkerRecorder'
        publisherNode.value()[0].name() == "factGenerator"
        publisherNode.value()[0].value() == ""
    }

    def 'irc channels are added'() {
        when:
        context.irc {
            channel('#c1')
            channel('#c2')
        }

        then:
        context.publisherNodes.size() == 1
        Node ircPublisher = context.publisherNodes[0]
        ircPublisher.name() == 'hudson.plugins.ircbot.IrcPublisher'
        def targets = ircPublisher.value()[0]
        targets.value().size == 2
        targets.value()[0].name() == 'hudson.plugins.im.GroupChatIMMessageTarget'
        targets.value()[0].value()[0].name() == 'name'
        targets.value()[0].value()[0].value() == '#c1'
        targets.value()[1].name() == 'hudson.plugins.im.GroupChatIMMessageTarget'
        targets.value()[1].value()[0].name() == 'name'
        targets.value()[1].value()[0].value() == '#c2'
    }

    def 'irc notification strategy is set'() {
        when:
        context.irc {
            strategy('STATECHANGE_ONLY')
        }

        then:
        context.publisherNodes[0].strategy[0].value() == 'STATECHANGE_ONLY'
    }

    def 'irc notification invalid strategy triggers exception'() {
        when:
        context.irc {
            strategy('invalid')
        }

        then:
        thrown(IllegalArgumentException)
    }

    def 'notifyScmFixers is set'() {
        when:
        context.irc {
            notifyScmFixers(true)
        }

        then:
        context.publisherNodes[0].notifyFixers[0].value() == 'true'
    }

    def 'irc notification message is set'() {
        when:
        context.irc {
            channel('#c1')
            notificationMessage('SummaryOnly')
        }

        then:
        context.publisherNodes.size() == 1
        Node ircPublisher = context.publisherNodes[0]
        ircPublisher.name() == 'hudson.plugins.ircbot.IrcPublisher'
        ircPublisher.getAt('buildToChatNotifier')[0].attributes()['class'] == 'hudson.plugins.im.build_notify.SummaryOnlyBuildToChatNotifier'
    }

    def 'default notification message is set if not specified'() {
        when:
        context.irc {
            channel('#c1')
        }

        then:
        context.publisherNodes.size() == 1
        Node ircPublisher = context.publisherNodes[0]
        ircPublisher.name() == 'hudson.plugins.ircbot.IrcPublisher'
        ircPublisher.getAt('buildToChatNotifier')[0].attributes()['class'] == 'hudson.plugins.im.build_notify.DefaultBuildToChatNotifier'
    }

    def 'default notification strategy is set if not specified'() {
        when:
        context.irc {
            channel('#c1')
        }

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].strategy[0].value() == 'ALL'
    }

    def 'given the required cobertura report file name all defaults are set for your pleasure'() {
        when:
        context.cobertura('reportfilename')

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].children().size() == 12
        context.publisherNodes[0].coberturaReportFile[0].value() == 'reportfilename'
        context.publisherNodes[0].onlyStable[0].value() == false
        context.publisherNodes[0].failUnhealthy[0].value() == false
        context.publisherNodes[0].failUnstable[0].value() == false
        context.publisherNodes[0].autoUpdateHealth[0].value() == false
        context.publisherNodes[0].autoUpdateStability[0].value() == false
        context.publisherNodes[0].zoomCoverageChart[0].value() == false
        context.publisherNodes[0].failNoReports[0].value() == true
        assertTarget('healthyTarget', 0, 'METHOD', '8000000')
        assertTarget('healthyTarget', 1, 'LINE', '8000000')
        assertTarget('healthyTarget', 2, 'CONDITIONAL', '7000000')
        assertTarget('unhealthyTarget', 0, 'METHOD', '0')
        assertTarget('unhealthyTarget', 1, 'LINE', '0')
        assertTarget('unhealthyTarget', 2, 'CONDITIONAL', '0')
        assertTarget('failingTarget', 0, 'METHOD', '0')
        assertTarget('failingTarget', 1, 'LINE', '0')
        assertTarget('failingTarget', 2, 'CONDITIONAL', '0')
        context.publisherNodes[0].sourceEncoding[0].value() == 'ASCII'
    }

    private void assertTarget(String targetName, int position, String type, String value) {
        assert context.publisherNodes[0]."${targetName}"[0].targets[0].entry[position].getAt('hudson.plugins.cobertura.targets.CoverageMetric')[0].value() == type
        assert context.publisherNodes[0]."${targetName}"[0].targets[0].entry[position].getAt('int')[0].value() == value
    }

    def 'the closure makes it possible to override all the cobertura flags'() {
        when:
        context.cobertura('reportfilename') {
            onlyStable(true)
            failUnhealthy(true)
            failUnstable(true)
            autoUpdateHealth(true)
            autoUpdateStability(true)
            zoomCoverageChart(true)
            failNoReports(false)
        }

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].children().size() == 12
        context.publisherNodes[0].coberturaReportFile[0].value() == 'reportfilename'
        context.publisherNodes[0].onlyStable[0].value() == true
        context.publisherNodes[0].failUnhealthy[0].value() == true
        context.publisherNodes[0].failUnstable[0].value() == true
        context.publisherNodes[0].autoUpdateHealth[0].value() == true
        context.publisherNodes[0].autoUpdateStability[0].value() == true
        context.publisherNodes[0].zoomCoverageChart[0].value() == true
        context.publisherNodes[0].failNoReports[0].value() == false
    }

    def 'overriding cobertura default targets'() {
        when:
        context.cobertura('reportfilename') {
            methodTarget(1, 2, 3)
            lineTarget(4, 5, 6)
            conditionalTarget(7, 8, 9)
        }

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].children().size() == 12
        assertTarget('healthyTarget', 0, 'METHOD', '1')
        assertTarget('unhealthyTarget', 0, 'METHOD', '2')
        assertTarget('failingTarget', 0, 'METHOD', '3')
        assertTarget('healthyTarget', 1, 'LINE', '4')
        assertTarget('unhealthyTarget', 1, 'LINE', '5')
        assertTarget('failingTarget', 1, 'LINE', '6')
        assertTarget('healthyTarget', 2, 'CONDITIONAL', '7')
        assertTarget('unhealthyTarget', 2, 'CONDITIONAL', '8')
        assertTarget('failingTarget', 2, 'CONDITIONAL', '9')
    }

    def 'adding cobertura extra targets'() {
        when:
        context.cobertura('reportfilename') {
            fileTarget(1, 2, 3)
            packageTarget(4, 5, 6)
            classTarget(7, 8, 9)
        }

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].children().size() == 12
        assertTarget('healthyTarget', 3, 'FILES', '1')
        assertTarget('unhealthyTarget', 3, 'FILES', '2')
        assertTarget('failingTarget', 3, 'FILES', '3')
        assertTarget('healthyTarget', 4, 'PACKAGES', '4')
        assertTarget('unhealthyTarget', 4, 'PACKAGES', '5')
        assertTarget('failingTarget', 4, 'PACKAGES', '6')
        assertTarget('healthyTarget', 5, 'CLASSES', '7')
        assertTarget('unhealthyTarget', 5, 'CLASSES', '8')
        assertTarget('failingTarget', 5, 'CLASSES', '9')
    }

    def 'checking for invalid cobertura target type'() {
        when:
        context.cobertura('reportfilename') {
            target('invalid', 1, 2, 3)
        }
        then:
        thrown(IllegalArgumentException)
    }

    def 'null source encoding for cobertura'() {
        when:
        context.cobertura('reportfilename') {
            sourceEncoding(null)
        }
        then:
        thrown(NullPointerException)
    }

    def 'UTF-8 source encoding for cobertura should be the default instead of ASCII'() {
        when:
        context.cobertura('reportfilename') {
            sourceEncoding('UTF-8')
        }
        then:
        context.publisherNodes[0].sourceEncoding[0].value() == 'UTF-8'
    }
}
