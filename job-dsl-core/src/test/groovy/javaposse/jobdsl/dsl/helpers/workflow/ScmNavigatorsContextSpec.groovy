package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class ScmNavigatorsContextSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    ScmNavigatorsContext context = new ScmNavigatorsContext(jobManagement, Mock(Item))

    def 'has no navigators by default'() {
        expect:
        context.scmNavigatorNodes.empty
    }

    def 'node from extension is added'() {
        setup:
        Node node = Mock(Node)

        when:
        context.addExtensionNode(node)

        then:
        context.scmNavigatorNodes.size() == 1
        context.scmNavigatorNodes[0] == node
    }
}
