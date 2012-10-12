package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import spock.lang.Specification

public class TriggerHelperSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    TriggerHelper helper = new TriggerHelper(mockActions)
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
            configure {
                gerritBuildStartedVerifiedValue 0
                gerritBuildStartedCodeReviewValue 0
                gerritBuildSuccessfulVerifiedValue 1
                gerritBuildSuccessfulCodeReviewValue 2
                gerritBuildFailedVerifiedValue -2
                gerritBuildFailedCodeReviewValue -2
                gerritBuildUnstableVerifiedValue -1
                gerritBuildUnstableCodeReviewValue -1
                gerritBuildNotBuiltVerifiedValue 0
                gerritBuildNotBuiltCodeReviewValue 0
            }
        }

        then:
        def gerritTrigger = context.triggerNodes[0]
        gerritTrigger.gerritBuildSuccessfulVerifiedValue[0].value() as String == '1'

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

    def 'execute withXml Action'() {
        Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.xml))
        def nodeBuilder = new NodeBuilder()

        Node triggerNode = nodeBuilder.'hudson.triggers.SCMTrigger' {
            spec '2 3 * * * *'
        }

        when:
        def withXmlAction = helper.generateWithXmlAction(new TriggerContext([triggerNode]))
        withXmlAction.execute(root)

        then:
        root.triggers[0].'hudson.triggers.SCMTrigger'[0].spec[0].text() == '2 3 * * * *'
    }
}
