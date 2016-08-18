package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class EnvironmentVariableContributorsContextSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    Item item = Mock(Item)
    EnvironmentVariableContributorsContext context = new EnvironmentVariableContributorsContext(jobManagement, item)

    def 'node from extension is added'() {
        setup:
        Node node = Mock(Node)

        when:
        context.addExtensionNode(node)

        then:
        context.contributors[0] == node
    }
}
