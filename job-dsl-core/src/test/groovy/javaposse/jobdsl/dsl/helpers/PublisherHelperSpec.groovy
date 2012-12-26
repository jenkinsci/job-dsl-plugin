package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.WithXmlAction

import javaposse.jobdsl.dsl.helpers.PublisherContextHelper.PublisherContext
import spock.lang.Specification
import javax.xml.stream.events.NotationDeclaration

public class PublisherHelperSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    PublisherContextHelper helper = new PublisherContextHelper(mockActions)
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
        targetNode.notificationOnly[0].value() == 'false'
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
        context.publishJabber('me@gmail.com', 'ANY_FAILURE', 'SummaryOnly' )

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.plugins.jabber.im.transport.JabberPublisher'
        def targetNode = publisherNode.targets[0].'hudson.plugins.im.GroupChatIMMessageTarget'[0]
        targetNode.name[0].value() == 'me@gmail.com'
        targetNode.notificationOnly[0].value() == 'false'
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
        targetNode.notificationOnly[0].value() == 'false'
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

    def 'call scp publish with not enough entries'() {
        when:
        context.publishScp('javadoc')

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

    def 'call step via helper'() {
        when:
        helper.publishers {
            extendedEmail()
        }

        then:
        1 * mockActions.add(_)
    }
}
