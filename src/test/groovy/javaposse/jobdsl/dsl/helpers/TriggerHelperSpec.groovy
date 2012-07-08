package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import javaposse.jobdsl.dsl.helpers.TriggerHelper.TriggerContext
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
