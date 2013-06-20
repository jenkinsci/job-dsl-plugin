package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import spock.lang.Specification

public class TriggerHelperSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    TriggerContextHelper helper = new TriggerContextHelper(mockActions, JobType.Freeform)
    TriggerContext context = new TriggerContext()

    def 'call cron trigger methods'() {
        when:
        context.cron('*/10 * * * *')

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def timerTrigger = context.triggerNodes[0]
        timerTrigger.name() == 'hudson.triggers.TimerTrigger'
        timerTrigger.spec[0].value() == '*/10 * * * *'
    }

    def 'call scm trigger methods'() {
        when:
        context.scm('*/5 * * * *')

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def timerTrigger = context.triggerNodes[0]
        timerTrigger.name() == 'hudson.triggers.SCMTrigger'
        timerTrigger.spec[0].value() == '*/5 * * * *'
    }

    def 'call trigger via helper'() {
        when:
        helper.triggers {
            cron('0 12 * * * *')
        }

        then:
        1 * mockActions.add(_)

        // TODO Support this notation
//        when:
//        helper.trigger.cron('0 13 0 0 0 0')
//
//        then:
//        1 * mockActions.add(_)
    }

    def 'call empty gerrit trigger methods'() {
        when:
        context.gerrit {
        }

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def gerritTrigger = context.triggerNodes[0]
        gerritTrigger.name().contains('GerritTrigger')
        !gerritTrigger.buildStartMessage.isEmpty()
    }

    def 'call advanced gerrit trigger methods'() {
        when:
        context.gerrit {
            events {
                ChangeMerged
                DraftPublished
            }
            project('reg_exp:myProject', ['ant:feature-branch', 'plain:origin/refs/mybranch']) // full access
            project('test-project', '**') // simplified
            configure { node ->
                node / gerritBuildSuccessfulVerifiedValue << '10'
            }
        }

        then:
        def gerritTrigger = context.triggerNodes[0]
        gerritTrigger.gerritBuildSuccessfulVerifiedValue.size() == 1
        gerritTrigger.gerritBuildSuccessfulVerifiedValue[0].value() as String == '10'

        gerritTrigger.gerritBuildFailedCodeReviewValue.size() == 1
        gerritTrigger.gerritBuildFailedCodeReviewValue[0].value() == '0'

        Node gerritEvents = gerritTrigger.triggerOnEvents[0]
        gerritEvents.children().size() == 2
        gerritEvents.children()[0].name().contains('com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.events.Plugin')

        Node gerritProjects = gerritTrigger.gerritProjects[0]
        gerritProjects.children().size() == 2

        Node gerritProject = gerritProjects.children()[0]
        gerritProject.name() == 'com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.GerritProject'
        gerritProject.compareType[0].value() == 'REG_EXP'
        gerritProject.pattern[0].value() == 'myProject'
        gerritProject.branches[0].children().size() == 2

        Node gerritBranch = gerritProject.branches[0].children()[0]
        gerritBranch.name() == 'com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.Branch'
        gerritBranch.compareType[0].value() == 'ANT'
        gerritBranch.pattern[0].value() == 'feature-branch'

        Node gerritProjectSimple = gerritProjects.children()[1]
        gerritProjectSimple.compareType[0].value() == 'PLAIN'
        gerritProjectSimple.pattern[0].value() == 'test-project'
        gerritProjectSimple.branches[0].children().size() == 1
        // Assume branch is fine
    }


    def 'call gerrit trigger and verify build status value settings'() {
      when:
        context.gerrit {
            events {
                PatchsetCreated
                DraftPublished
            }

            project('test-project', '**')
        }
      then:
        def gerritTrigger = context.triggerNodes[0]
        gerritTrigger.gerritBuildSuccessfulCodeReviewValue.size() == 1
        gerritTrigger.gerritBuildSuccessfulCodeReviewValue[0].value() == '0'

        gerritTrigger.gerritBuildSuccessfulVerifiedValue.size() == 1
        gerritTrigger.gerritBuildSuccessfulVerifiedValue[0].value() as String == '1'

        gerritTrigger.gerritBuildFailedVerifiedValue.size() == 1
        gerritTrigger.gerritBuildFailedVerifiedValue[0].value() as String == '-1'

        gerritTrigger.gerritBuildFailedCodeReviewValue.size() == 1
        gerritTrigger.gerritBuildFailedCodeReviewValue[0].value() as String == '0'

        gerritTrigger.gerritBuildUnstableVerifiedValue.size() == 1
        gerritTrigger.gerritBuildUnstableVerifiedValue[0].value() as String == '0'

        gerritTrigger.gerritBuildUnstableCodeReviewValue.size() == 1
        gerritTrigger.gerritBuildUnstableCodeReviewValue[0].value() == '0'
    }


    def 'call gerrit trigger and verify build status value methods'() {
      when:
        context.gerrit {
            events {
                PatchsetCreated
                DraftPublished
            }

            project('test-project', '**')

            buildSuccessful(11,10)
            buildFailed('-21',20)
            buildUnstable(30,'32')
            buildNotBuilt('40','42')
            buildStarted('50','55')
        }
      then:
        def gerritTrigger = context.triggerNodes[0]
        gerritTrigger.gerritBuildSuccessfulCodeReviewValue.size() == 1
        gerritTrigger.gerritBuildSuccessfulCodeReviewValue[0].value() == '10'

        gerritTrigger.gerritBuildSuccessfulVerifiedValue.size() == 1
        gerritTrigger.gerritBuildSuccessfulVerifiedValue[0].value() as String == '11'

        gerritTrigger.gerritBuildFailedVerifiedValue.size() == 1
        gerritTrigger.gerritBuildFailedVerifiedValue[0].value() as String == '-21'

        gerritTrigger.gerritBuildFailedCodeReviewValue.size() == 1
        gerritTrigger.gerritBuildFailedCodeReviewValue[0].value() as String == '20'

        gerritTrigger.gerritBuildUnstableVerifiedValue.size() == 1
        gerritTrigger.gerritBuildUnstableVerifiedValue[0].value() as String == '30'

        gerritTrigger.gerritBuildUnstableCodeReviewValue.size() == 1
        gerritTrigger.gerritBuildUnstableCodeReviewValue[0].value() == '32'

        gerritTrigger.gerritBuildNotBuiltVerifiedValue.size() == 1
        gerritTrigger.gerritBuildNotBuiltVerifiedValue[0].value() as String == '40'

        gerritTrigger.gerritBuildNotBuiltCodeReviewValue.size() == 1
        gerritTrigger.gerritBuildNotBuiltCodeReviewValue[0].value() == '42'

        gerritTrigger.gerritBuildStartedVerifiedValue.size() == 1
        gerritTrigger.gerritBuildStartedVerifiedValue[0].value() as String == '50'

        gerritTrigger.gerritBuildStartedCodeReviewValue.size() == 1
        gerritTrigger.gerritBuildStartedCodeReviewValue[0].value() == '55'

    }

    def 'execute withXml Action'() {
        Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.xml))
        def nodeBuilder = new NodeBuilder()

        Node triggerNode = nodeBuilder.'hudson.triggers.SCMTrigger' {
            spec '2 3 * * * *'
        }

        when:
        def withXmlAction = helper.generateWithXmlAction(new TriggerContext([], JobType.Freeform, [triggerNode]))
        withXmlAction.execute(root)

        then:
        root.triggers[0].'hudson.triggers.SCMTrigger'[0].spec[0].text() == '2 3 * * * *'
    }

    def 'call snapshotDependencies for free-style job fails'() {
        when:
        context.snapshotDependencies(false)

        then:
        thrown(IllegalStateException)
    }

    def 'call snapshotDependencies for Maven job succeeds'() {
        when:
        TriggerContext context = new TriggerContext([], JobType.Maven, [])
        context.snapshotDependencies(false)

        then:
        context.withXmlActions != null
        context.withXmlActions.size() == 1
    }
}
