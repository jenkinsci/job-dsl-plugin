package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import spock.lang.Specification

class BuildFlowHelperSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    BuildFlowHelper helper = new BuildFlowHelper(mockActions, JobType.BuildFlow)
    Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.XML))

    def 'can run buildFlowBlock'() {
        when:
        helper.buildFlow('build block')

        then:
        1 * mockActions.add(_)
    }

    def 'cannot run buildFlow twice'() {
        when:
        helper.buildFlow('build block')
        helper.buildFlow('build block again')

        then:
        thrown(IllegalStateException)
    }

    def 'cannot create Build Flow for free style jobs'() {
        setup:
        BuildFlowHelper helper = new BuildFlowHelper(mockActions, JobType.Freeform)

        when:
        helper.buildFlow('build block')

        then:
        thrown(IllegalStateException)
    }

    def 'cannot create Build Flow for Maven jobs'() {
        setup:
        BuildFlowHelper helper = new BuildFlowHelper(mockActions, JobType.Maven)

        when:
        helper.buildFlow('build block')

        then:
        thrown(IllegalStateException)
    }

    def 'buildFlow constructs xml'() {
        when:
        def action = helper.buildFlow('build Flow Block')
        action.execute(root)

        then:
        root.dsl.size() == 1
        root.dsl[0].value() == 'build Flow Block'
    }
}
