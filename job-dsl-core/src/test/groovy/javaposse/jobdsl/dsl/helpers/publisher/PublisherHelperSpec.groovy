package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import spock.lang.Specification

import static javaposse.jobdsl.dsl.helpers.publisher.PublisherContext.Behavior.MarkUnstable

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
    def 'call standard mailer method'() {
        when:
        context.mailer('recipient')

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        Node mailerPublisher = context.publisherNodes[0]
        mailerPublisher.name() == 'hudson.tasks.Mailer'
        mailerPublisher.recipients[0].value() as String == 'recipient'
        mailerPublisher.dontNotifyEveryUnstableBuild[0].value() as Boolean == false
        mailerPublisher.sendToIndividuals[0].value() as Boolean == false
    }

    def 'call standard mailer method with all args'() {
        when:
        context.mailer('recipient2', true, true)

        then:
        context.publisherNodes != null
        context.publisherNodes.size() == 1
        Node mailerPublisher = context.publisherNodes[0]
        mailerPublisher.name() == 'hudson.tasks.Mailer'
        mailerPublisher.recipients[0].value() as String == 'recipient2'
        mailerPublisher.dontNotifyEveryUnstableBuild[0].value() as Boolean == true
        mailerPublisher.sendToIndividuals[0].value() as Boolean == true
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
        archiveNode.allowEmptyArchive.isEmpty()
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
        archiveNode.allowEmptyArchive.isEmpty()
    }

    def 'call archive artifacts with closure'() {
        when:
        context.archiveArtifacts {
            pattern 'include/*'
            exclude 'exclude/*'
            allowEmpty()
            latestOnly()
        }

        then:
        Node archiveNode = context.publisherNodes[0]
        archiveNode.name() == 'hudson.tasks.ArtifactArchiver'
        archiveNode.artifacts[0].value() == 'include/*'
        archiveNode.excludes[0].value() == 'exclude/*'
        archiveNode.latestOnly[0].value() == 'true'
        archiveNode.allowEmptyArchive[0].value() == 'true'
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

    def 'call jacoco code coverage with no args'() {
        when:

        context.jacocoCodeCoverage()

        then:
        Node jacocoNode = context.publisherNodes[0]
        jacocoNode.name() == 'hudson.plugins.jacoco.JacocoPublisher'
        jacocoNode.execPattern[0].value() == '**/target/**.exec'
        jacocoNode.minimumInstructionCoverage[0].value() == "0"             
    }
   
    def 'call jacoco code coverage with all args'() {
        when:
        context.jacocoCodeCoverage {
            execPattern 'execfiles'
            classPattern 'classdir'
            sourcePattern 'sourcedir'
            inclusionPattern 'inclusiondir'
            exclusionPattern 'exclusiondir'
            minimumInstructionCoverage '1'
            minimumBranchCoverage '2'
            minimumComplexityCoverage '3' 
            minimumLineCoverage '4' 
            minimumMethodCoverage '5' 
            minimumClassCoverage '6' 
            maximumInstructionCoverage '7' 
            maximumBranchCoverage '8' 
            maximumComplexityCoverage '9' 
            maximumLineCoverage '10' 
            maximumMethodCoverage '11' 
            maximumClassCoverage '12'
        }

        then:
        Node jacocoNode = context.publisherNodes[0]
        jacocoNode.name() == 'hudson.plugins.jacoco.JacocoPublisher'
        jacocoNode.execPattern[0].value() == 'execfiles'
        jacocoNode.classPattern[0].value() == 'classdir'
        jacocoNode.sourcePattern[0].value() == 'sourcedir'
        jacocoNode.inclusionPattern[0].value() == 'inclusiondir'
        jacocoNode.exclusionPattern[0].value() == 'exclusiondir'
        jacocoNode.minimumInstructionCoverage[0].value() == "1"
        jacocoNode.minimumBranchCoverage[0].value() == "2"
        jacocoNode.minimumComplexityCoverage[0].value() == "3"
        jacocoNode.minimumLineCoverage[0].value() == "4"
        jacocoNode.minimumMethodCoverage[0].value() == "5"
        jacocoNode.minimumClassCoverage[0].value() == "6"
        jacocoNode.maximumInstructionCoverage[0].value() == "7"
        jacocoNode.maximumBranchCoverage[0].value() == "8"
        jacocoNode.maximumComplexityCoverage[0].value() == "9"
        jacocoNode.maximumLineCoverage[0].value() == "10"
        jacocoNode.maximumMethodCoverage[0].value() == "11"
        jacocoNode.maximumClassCoverage[0].value() == "12"
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

    def 'call Clone Workspace with Closure'() {
        when:
        context.publishCloneWorkspace('*/**') {
            criteria 'Not Failed'
            archiveMethod 'ZIP'
            workspaceExcludeGlob '*/.svn'
            overrideDefaultExcludes true
        }

        then:
        Node publisherNode = context.publisherNodes[0]
        publisherNode.name() == 'hudson.plugins.cloneworkspace.CloneWorkspacePublisher'
        publisherNode.workspaceGlob[0].value() == '*/**'
        publisherNode.workspaceExcludeGlob[0].value() == '*/.svn'
        publisherNode.criteria[0].value() == 'Not Failed'
        publisherNode.archiveMethod[0].value() == 'ZIP'
        publisherNode.overrideDefaultExcludes[0].value() == true
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
        publisherNode.threshold[0].ordinal[0].value() == 0
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
        publisherNode.threshold[0].ordinal[0].value() == 2
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
                boolParam('aParam')
                boolParam('bParam', false)
                boolParam('cParam', true)
                sameNode()
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

        def boolParams = first.configs[0].'hudson.plugins.parameterizedtrigger.BooleanParameters'[0].configs[0]
        boolParams.children().size() == 3
        def boolNode = boolParams.'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[0]
        boolNode.name[0].value() == 'aParam'
        boolNode.value[0].value() == 'false'
        def boolNode1 = boolParams.'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[1]
        boolNode1.name[0].value() == 'bParam'
        boolNode1.value[0].value() == 'false'
        def boolNode2 = boolParams.'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[2]
        boolNode2.name[0].value() == 'cParam'
        boolNode2.value[0].value() == 'true'

        def nodeNode = first.configs[0].'hudson.plugins.parameterizedtrigger.NodeParameters'[0]
        nodeNode != null

        first.block.isEmpty()

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
        typeConfigsNode.entry.size() == 17
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
            jshint(10, 11, 10, 'test-report/*.xml')
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
        typeConfigsNode.entry.size() == 17
        def checkstyleNode = typeConfigsNode.entry.find { it.string[0].value() == 'checkstyle'}
        checkstyleNode != null
        checkstyleNode.'hudson.plugins.violations.TypeConfig'[0].type[0].value() == 'checkstyle'
        checkstyleNode.'hudson.plugins.violations.TypeConfig'[0].min[0].value() == '10'
        checkstyleNode.'hudson.plugins.violations.TypeConfig'[0].max[0].value() == '11'
        checkstyleNode.'hudson.plugins.violations.TypeConfig'[0].unstable[0].value() == '10'
        checkstyleNode.'hudson.plugins.violations.TypeConfig'[0].usePattern[0].value() == 'true'
        checkstyleNode.'hudson.plugins.violations.TypeConfig'[0].pattern[0].value() == 'test-report/*.xml'
        def jshintNode = typeConfigsNode.entry.find { it.string[0].value() == 'jshint'}
        jshintNode != null
        jshintNode.'hudson.plugins.violations.TypeConfig'[0].type[0].value() == 'jshint'
        jshintNode.'hudson.plugins.violations.TypeConfig'[0].min[0].value() == '10'
        jshintNode.'hudson.plugins.violations.TypeConfig'[0].max[0].value() == '11'
        jshintNode.'hudson.plugins.violations.TypeConfig'[0].unstable[0].value() == '10'
        jshintNode.'hudson.plugins.violations.TypeConfig'[0].usePattern[0].value() == 'true'
        jshintNode.'hudson.plugins.violations.TypeConfig'[0].pattern[0].value() == 'test-report/*.xml'
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
        assertTarget('healthyTarget', 0, 'METHOD', '100000')
        assertTarget('unhealthyTarget', 0, 'METHOD', '200000')
        assertTarget('failingTarget', 0, 'METHOD', '300000')
        assertTarget('healthyTarget', 1, 'LINE', '400000')
        assertTarget('unhealthyTarget', 1, 'LINE', '500000')
        assertTarget('failingTarget', 1, 'LINE', '600000')
        assertTarget('healthyTarget', 2, 'CONDITIONAL', '700000')
        assertTarget('unhealthyTarget', 2, 'CONDITIONAL', '800000')
        assertTarget('failingTarget', 2, 'CONDITIONAL', '900000')
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
        assertTarget('healthyTarget', 3, 'FILES', '100000')
        assertTarget('unhealthyTarget', 3, 'FILES', '200000')
        assertTarget('failingTarget', 3, 'FILES', '300000')
        assertTarget('healthyTarget', 4, 'PACKAGES', '400000')
        assertTarget('unhealthyTarget', 4, 'PACKAGES', '500000')
        assertTarget('failingTarget', 4, 'PACKAGES', '600000')
        assertTarget('healthyTarget', 5, 'CLASSES', '700000')
        assertTarget('unhealthyTarget', 5, 'CLASSES', '800000')
        assertTarget('failingTarget', 5, 'CLASSES', '900000')
    }

    def 'checking for invalid cobertura target type'() {
        when:
        context.cobertura('reportfilename') {
            target('invalid', 1, 2, 3)
        }
        then:
        thrown(IllegalArgumentException)
    }

    def 'checking for invalid cobertura target treshold: negative'() {
        when:
            context.cobertura('reportfilename') {
                target('invalid', h, u, f)
            }
        then:
            thrown(IllegalArgumentException)
        where:
            h  |  u |  f
            -1 |  1 |  1
            1  | -1 |  1
            1  |  1 | -1
    }

    def 'checking for invalid cobertura target treshold: more than 100 percent'() {
        when:
            context.cobertura('reportfilename') {
                target('invalid', h, u, f)
            }
        then:
            thrown(IllegalArgumentException)
        where:
            h  |  u  |  f
           101 |  1  |  1
            1  | 101 |  1
            1  |  1  | 101
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

    def 'call allowBrokenBuildClaiming'() {
        when:
        context.allowBrokenBuildClaiming()

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.claim.ClaimPublisher'
    }

    def 'add fingerprinting'(targets, recordArtifacts) {
        when:
        context.fingerprint(targets, recordArtifacts)

        then:
        context.publisherNodes.size() == 1
        def fingerprintNode = context.publisherNodes[0]
        fingerprintNode.targets[0].value() == targets
        fingerprintNode.recordBuildArtifacts[0].value() == recordArtifacts

        where:
        targets    | recordArtifacts
        '**/*'     | false
        ''         | true
        '**/*arst' | false
        'whatever' | true
    }

    def 'call buildDescription with one argument'() {
        when:
        context.buildDescription('success')

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.descriptionsetter.DescriptionSetterPublisher'
        context.publisherNodes[0].regexp[0].value() == 'success'
        context.publisherNodes[0].regexpForFailed[0].value() == ''
        context.publisherNodes[0].description[0].value() == ''
        context.publisherNodes[0].descriptionForFailed == []
        context.publisherNodes[0].setForMatrix[0].value() == false
    }

    def 'call buildDescription with two arguments'() {
        when:
        context.buildDescription('success', 'AWSUM!')

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.descriptionsetter.DescriptionSetterPublisher'
        context.publisherNodes[0].regexp[0].value() == 'success'
        context.publisherNodes[0].regexpForFailed[0].value() == ''
        context.publisherNodes[0].description[0].value() == 'AWSUM!'
        context.publisherNodes[0].descriptionForFailed == []
        context.publisherNodes[0].setForMatrix[0].value() == false
    }

    def 'call buildDescription with three arguments'() {
        when:
        context.buildDescription('success', 'AWSUM!', 'failed')

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.descriptionsetter.DescriptionSetterPublisher'
        context.publisherNodes[0].regexp[0].value() == 'success'
        context.publisherNodes[0].regexpForFailed[0].value() == 'failed'
        context.publisherNodes[0].description[0].value() == 'AWSUM!'
        context.publisherNodes[0].descriptionForFailed == []
        context.publisherNodes[0].setForMatrix[0].value() == false
    }

    def 'call buildDescription with four arguments'() {
        when:
        context.buildDescription('success', 'AWSUM!', 'failed', 'NOES!')

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.descriptionsetter.DescriptionSetterPublisher'
        context.publisherNodes[0].regexp[0].value() == 'success'
        context.publisherNodes[0].regexpForFailed[0].value() == 'failed'
        context.publisherNodes[0].description[0].value() == 'AWSUM!'
        context.publisherNodes[0].descriptionForFailed[0].value() == 'NOES!'
        context.publisherNodes[0].setForMatrix[0].value() == false
    }

    def 'call buildDescription with five arguments'() {
        when:
        context.buildDescription('success', 'AWSUM!', 'failed', 'NOES!', true)

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.descriptionsetter.DescriptionSetterPublisher'
        context.publisherNodes[0].regexp[0].value() == 'success'
        context.publisherNodes[0].regexpForFailed[0].value() == 'failed'
        context.publisherNodes[0].description[0].value() == 'AWSUM!'
        context.publisherNodes[0].descriptionForFailed[0].value() == 'NOES!'
        context.publisherNodes[0].setForMatrix[0].value() == true
    }

    def 'call textFinder with one argument'() {
        when:
        context.textFinder('foo')

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.textfinder.TextFinderPublisher'
        context.publisherNodes[0].regexp[0].value() == 'foo'
        context.publisherNodes[0].fileSet.size() == 0
        context.publisherNodes[0].alsoCheckConsoleOutput[0].value() == false
        context.publisherNodes[0].succeedIfFound[0].value() == false
        context.publisherNodes[0].unstableIfFound[0].value() == false
    }

    def 'call textFinder with two arguments'() {
        when:
        context.textFinder('foo', '*.txt')

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.textfinder.TextFinderPublisher'
        context.publisherNodes[0].regexp[0].value() == 'foo'
        context.publisherNodes[0].fileSet[0].value() == '*.txt'
        context.publisherNodes[0].alsoCheckConsoleOutput[0].value() == false
        context.publisherNodes[0].succeedIfFound[0].value() == false
        context.publisherNodes[0].unstableIfFound[0].value() == false
    }

    def 'call textFinder with three arguments'() {
        when:
        context.textFinder('foo', '*.txt', true)

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.textfinder.TextFinderPublisher'
        context.publisherNodes[0].regexp[0].value() == 'foo'
        context.publisherNodes[0].fileSet[0].value() == '*.txt'
        context.publisherNodes[0].alsoCheckConsoleOutput[0].value() == true
        context.publisherNodes[0].succeedIfFound[0].value() == false
        context.publisherNodes[0].unstableIfFound[0].value() == false
    }

    def 'call textFinder with four arguments'() {
        when:
        context.textFinder('foo', '*.txt', true, true)

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.textfinder.TextFinderPublisher'
        context.publisherNodes[0].regexp[0].value() == 'foo'
        context.publisherNodes[0].fileSet[0].value() == '*.txt'
        context.publisherNodes[0].alsoCheckConsoleOutput[0].value() == true
        context.publisherNodes[0].succeedIfFound[0].value() == true
        context.publisherNodes[0].unstableIfFound[0].value() == false
    }

    def 'call textFinder with five arguments'() {
        when:
        context.textFinder('foo', '*.txt', true, true, true)

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.textfinder.TextFinderPublisher'
        context.publisherNodes[0].regexp[0].value() == 'foo'
        context.publisherNodes[0].fileSet[0].value() == '*.txt'
        context.publisherNodes[0].alsoCheckConsoleOutput[0].value() == true
        context.publisherNodes[0].succeedIfFound[0].value() == true
        context.publisherNodes[0].unstableIfFound[0].value() == true
    }

    def 'call postBuildTask with two arguments'() {
        when:
        context.postBuildTask() {
            task('BUILD SUCCESSFUL', 'git clean -fdx')
        }

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.postbuildtask.PostbuildTask'
        context.publisherNodes[0].tasks[0].'hudson.plugins.postbuildtask.TaskProperties'[0].logTexts[0].'hudson.plugins.postbuildtask.LogProperties'[0].logText[0].value() == 'BUILD SUCCESSFUL'
        context.publisherNodes[0].tasks[0].'hudson.plugins.postbuildtask.TaskProperties'[0].logTexts[0].'hudson.plugins.postbuildtask.LogProperties'[0].operator[0].value() == 'AND'
        context.publisherNodes[0].tasks[0].'hudson.plugins.postbuildtask.TaskProperties'[0].EscalateStatus[0].value() == false
        context.publisherNodes[0].tasks[0].'hudson.plugins.postbuildtask.TaskProperties'[0].RunIfJobSuccessful[0].value() == false
        context.publisherNodes[0].tasks[0].'hudson.plugins.postbuildtask.TaskProperties'[0].script[0].value() == 'git clean -fdx'
    }

    def 'call postBuildTask with two tasks'() {
        when:
        context.postBuildTask() {
            task('BUILD SUCCESSFUL', 'git clean -fdx')
            task('BUILD FAILED', 'git gc', true, true)
        }

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.postbuildtask.PostbuildTask'
        context.publisherNodes[0].tasks[0].'hudson.plugins.postbuildtask.TaskProperties'[0].logTexts[0].'hudson.plugins.postbuildtask.LogProperties'[0].logText[0].value() == 'BUILD SUCCESSFUL'
        context.publisherNodes[0].tasks[0].'hudson.plugins.postbuildtask.TaskProperties'[0].logTexts[0].'hudson.plugins.postbuildtask.LogProperties'[0].operator[0].value() == 'AND'
        context.publisherNodes[0].tasks[0].'hudson.plugins.postbuildtask.TaskProperties'[0].EscalateStatus[0].value() == false
        context.publisherNodes[0].tasks[0].'hudson.plugins.postbuildtask.TaskProperties'[0].RunIfJobSuccessful[0].value() == false
        context.publisherNodes[0].tasks[0].'hudson.plugins.postbuildtask.TaskProperties'[0].script[0].value() == 'git clean -fdx'

        context.publisherNodes[0].tasks[0].'hudson.plugins.postbuildtask.TaskProperties'[1].logTexts[0].'hudson.plugins.postbuildtask.LogProperties'[0].logText[0].value() == 'BUILD FAILED'
        context.publisherNodes[0].tasks[0].'hudson.plugins.postbuildtask.TaskProperties'[1].logTexts[0].'hudson.plugins.postbuildtask.LogProperties'[0].operator[0].value() == 'AND'
        context.publisherNodes[0].tasks[0].'hudson.plugins.postbuildtask.TaskProperties'[1].EscalateStatus[0].value() == true
        context.publisherNodes[0].tasks[0].'hudson.plugins.postbuildtask.TaskProperties'[1].RunIfJobSuccessful[0].value() == true
        context.publisherNodes[0].tasks[0].'hudson.plugins.postbuildtask.TaskProperties'[1].script[0].value() == 'git gc'
    }

    def 'call aggregate downstream test results with no args'() {
        when:
        context.aggregateDownstreamTestResults()

        then:
        Node aggregateNode = context.publisherNodes[0]
        aggregateNode.name() == 'hudson.tasks.test.AggregatedTestResultPublisher'
        aggregateNode.jobs[0] == null
        aggregateNode.includeFailedBuilds[0].value() == false
    }

    def 'call aggregate downstream test results with job listing'() {
        when:
        context.aggregateDownstreamTestResults('project-A, project-B')

        then:
        Node aggregateNode = context.publisherNodes[0]
        aggregateNode.name() == 'hudson.tasks.test.AggregatedTestResultPublisher'
        aggregateNode.jobs[0].value() == 'project-A, project-B'
        aggregateNode.includeFailedBuilds[0].value() == false
    }

    def 'call aggregate downstream test results with null job listing and overriden includeFailedBuilds'() {
        when:
        context.aggregateDownstreamTestResults(null, true)

        then:
        Node aggregateNode = context.publisherNodes[0]
        aggregateNode.name() == 'hudson.tasks.test.AggregatedTestResultPublisher'
        aggregateNode.jobs[0] == null
        aggregateNode.includeFailedBuilds[0].value() == true
    }

    def 'call aggregate downstream test results with job listing and overriden includeFailedBuilds'() {
        when:
        context.aggregateDownstreamTestResults('project-A, project-B', true)

        then:
        Node aggregateNode = context.publisherNodes[0]
        aggregateNode.name() == 'hudson.tasks.test.AggregatedTestResultPublisher'
        aggregateNode.jobs[0].value() == 'project-A, project-B'
        aggregateNode.includeFailedBuilds[0].value() == true
    }

    def 'call groovyPostBuild'() {
        when:
        context.groovyPostBuild('foo')

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'org.jvnet.hudson.plugins.groovypostbuild.GroovyPostbuildRecorder'
        context.publisherNodes[0].groovyScript[0].value() == 'foo'
        context.publisherNodes[0].behavior[0].value() == 0
    }

    def 'call groovyPostBuild with overriden failure behavior'() {
        when:
        context.groovyPostBuild('foo', MarkUnstable)

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'org.jvnet.hudson.plugins.groovypostbuild.GroovyPostbuildRecorder'
        context.publisherNodes[0].groovyScript[0].value() == 'foo'
        context.publisherNodes[0].behavior[0].value() == 1
    }

    def 'call javadoc archiver with no args'() {
        when:
        context.archiveJavadoc()

        then:
        Node javadocNode = context.publisherNodes[0]
        javadocNode.name() == 'hudson.tasks.JavadocArchiver'
        javadocNode.javadocDir[0].value() == ''
        javadocNode.keepAll[0].value() == false
    }

    def 'call javadoc archiver with all args'() {
        when:
        context.archiveJavadoc {
            javadocDir 'build/javadoc'
            keepAll true
        }

        then:
        Node javadocNode = context.publisherNodes[0]
        javadocNode.name() == 'hudson.tasks.JavadocArchiver'
        javadocNode.javadocDir[0].value() == 'build/javadoc'
        javadocNode.keepAll[0].value() == true
    }

    def 'call associated files with normal args'() {
        when:
        context.associatedFiles('/foo/file/${VARIABLE}')

        then:
        Node associatedFilesNode = context.publisherNodes[0]
        associatedFilesNode.name() == 'org.jenkinsci.plugins.associatedfiles.AssociatedFilesPublisher'
        associatedFilesNode.associatedFiles[0].value() == '/foo/file/${VARIABLE}'
    }

    def 'call emma with one argument'() {
        when:
        context.emma('coverage-results/coverage.xml')

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.emma.EmmaPublisher'
        context.publisherNodes[0].includes[0].value() == 'coverage-results/coverage.xml'
        context.publisherNodes[0].healthReports[0].minClass[0].value() == 0
        context.publisherNodes[0].healthReports[0].maxClass[0].value() == 100
        context.publisherNodes[0].healthReports[0].minMethod[0].value() == 0
        context.publisherNodes[0].healthReports[0].maxMethod[0].value() == 70
        context.publisherNodes[0].healthReports[0].minBlock[0].value() == 0
        context.publisherNodes[0].healthReports[0].maxBlock[0].value() == 80
        context.publisherNodes[0].healthReports[0].minLine[0].value() == 0
        context.publisherNodes[0].healthReports[0].maxLine[0].value() == 80
        context.publisherNodes[0].healthReports[0].minCondition[0].value() == 0
        context.publisherNodes[0].healthReports[0].maxCondition[0].value() == 80
    }

    def 'call emma with range thresholds'() {
        when:
        context.emma('coverage-results/coverage.xml') {
            classThreshold(5..90)
            methodThreshold(10..80)
            blockThreshold(15..75)
            lineThreshold(20..70)
            conditionThreshold(25..65)
        }

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.emma.EmmaPublisher'
        context.publisherNodes[0].includes[0].value() == 'coverage-results/coverage.xml'
        context.publisherNodes[0].healthReports[0].minClass[0].value() == 5
        context.publisherNodes[0].healthReports[0].maxClass[0].value() == 90
        context.publisherNodes[0].healthReports[0].minMethod[0].value() == 10
        context.publisherNodes[0].healthReports[0].maxMethod[0].value() == 80
        context.publisherNodes[0].healthReports[0].minBlock[0].value() == 15
        context.publisherNodes[0].healthReports[0].maxBlock[0].value() == 75
        context.publisherNodes[0].healthReports[0].minLine[0].value() == 20
        context.publisherNodes[0].healthReports[0].maxLine[0].value() == 70
        context.publisherNodes[0].healthReports[0].minCondition[0].value() == 25
        context.publisherNodes[0].healthReports[0].maxCondition[0].value() == 65
    }

    def 'call emma with individual thresholds'() {
        when:
        context.emma('coverage-results/coverage.xml') {
            minClass(5)
            maxClass(90)
            minMethod(10)
            maxMethod(80)
            minBlock(15)
            maxBlock(75)
            minLine(20)
            maxLine(70)
            minCondition(25)
            maxCondition(65)
        }

        then:
        context.publisherNodes.size() == 1
        context.publisherNodes[0].name() == 'hudson.plugins.emma.EmmaPublisher'
        context.publisherNodes[0].includes[0].value() == 'coverage-results/coverage.xml'
        context.publisherNodes[0].healthReports[0].minClass[0].value() == 5
        context.publisherNodes[0].healthReports[0].maxClass[0].value() == 90
        context.publisherNodes[0].healthReports[0].minMethod[0].value() == 10
        context.publisherNodes[0].healthReports[0].maxMethod[0].value() == 80
        context.publisherNodes[0].healthReports[0].minBlock[0].value() == 15
        context.publisherNodes[0].healthReports[0].maxBlock[0].value() == 75
        context.publisherNodes[0].healthReports[0].minLine[0].value() == 20
        context.publisherNodes[0].healthReports[0].maxLine[0].value() == 70
        context.publisherNodes[0].healthReports[0].minCondition[0].value() == 25
        context.publisherNodes[0].healthReports[0].maxCondition[0].value() == 65
    }

    def 'call emma with bad range values'() {
        when:
        context.emma('coverage-results/coverage.xml') {
            minClass(-5)
        }

        then:
        thrown(IllegalArgumentException)

        when:
        context.emma('coverage-results/coverage.xml') {
            minLine(101)
        }

        then:
        thrown(IllegalArgumentException)

        when:
        context.emma('coverage-results/coverage.xml') {
            maxCondition(101)
        }

        then:
        thrown(IllegalArgumentException)

        when:
        context.emma('coverage-results/coverage.xml') {
            maxBlock(-1)
        }

        then:
        thrown(IllegalArgumentException)

        when:
        context.emma('coverage-results/coverage.xml') {
            classThreshold(-5..90)
        }

        then:
        thrown(IllegalArgumentException)

        when:
        context.emma('coverage-results/coverage.xml') {
            methodThreshold(5..101)
        }

        then:
        thrown(IllegalArgumentException)
    }
    
    def 'publish Robot framework report using default values'() {
        when:
        context.publishRobotFrameworkReports()

        then:
        Node node = context.publisherNodes[0]
        node.name() == 'hudson.plugins.robot.RobotPublisher'
        node.outputPath[0].value() == RobotFrameworkContext.DEFAULT_OUTPUT_PATH
        node.passThreshold[0].value() == 100.0
        node.unstableThreshold[0].value() == 0.0
        node.onlyCritical[0].value() == false
        node.reportFileName[0].value() == RobotFrameworkContext.DEFAULT_REPORT_FILE_NAME
        node.logFileName[0].value() == RobotFrameworkContext.DEFAULT_LOG_FILE_NAME
        node.outputFileName[0].value() == RobotFrameworkContext.DEFAULT_OUTPUT_FILE_NAME
    }

    def 'publish Robot framework report using specific value for outputPath'() {
        when:
        context.publishRobotFrameworkReports { outputPath("/path/to/foo") }

        then:
        Node node = context.publisherNodes[0]
        node.name() == 'hudson.plugins.robot.RobotPublisher'
        node.outputPath[0].value() == '/path/to/foo'
    }

    def 'publish Robot framework report using specific values for passThreshold and unstableThreshold'() {
        when:
        context.publishRobotFrameworkReports{
            passThreshold(100.0)
            unstableThreshold(10.0)
        }

        then:
        Node node = context.publisherNodes[0]
        node.name() == 'hudson.plugins.robot.RobotPublisher'
        node.passThreshold[0].value() == 100.0
        node.unstableThreshold[0].value() == 10.0
    }

    def 'publish Robot framework report using specific value for onlyCritical'() {
        when:
        context.publishRobotFrameworkReports { onlyCritical(true) }

        then:
        Node node = context.publisherNodes[0]
        node.name() == 'hudson.plugins.robot.RobotPublisher'
        node.passThreshold[0].value() == 100.0
        node.unstableThreshold[0].value() == 0.0
        node.onlyCritical[0].value() == true
    }

    def 'publish Robot framework report using a configure closure'() {
        when:
        context.publishRobotFrameworkReports() {
            passThreshold(50.0)
            unstableThreshold(10.0)
            outputPath("/path/to/foo")
        }

        then:
        Node node = context.publisherNodes[0]
        node.name() == 'hudson.plugins.robot.RobotPublisher'
        node.passThreshold[0].value() == 50.0
        node.unstableThreshold[0].value() == 10.0
        node.outputPath[0].value() == "/path/to/foo"
        node.onlyCritical[0].value() == false
        node.reportFileName[0].value() == RobotFrameworkContext.DEFAULT_REPORT_FILE_NAME
    }
}
