package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class WorkflowDefinitionContextSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final Item item = Mock(Item)
    private final WorkflowDefinitionContext context = new WorkflowDefinitionContext(jobManagement, item)

    def 'add extension'() {
        given:
        Node paramNode = new NodeBuilder().'my.custom.Definition' {
            repoUrl('foo')
        }

        when:
        context.addExtensionNode(paramNode)

        then:
        context.definitionNode != null
        context.definitionNode.name() == 'definition'
        context.definitionNode.attributes().size() == 1
        context.definitionNode.attributes()['class'] == 'my.custom.Definition'
        context.definitionNode.children().size() == 1
        context.definitionNode.repoUrl.text() == 'foo'
    }
}
