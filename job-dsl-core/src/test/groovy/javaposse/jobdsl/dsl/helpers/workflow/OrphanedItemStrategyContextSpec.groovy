package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class OrphanedItemStrategyContextSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final Item item = Mock(Item)
    private final OrphanedItemStrategyContext context = new OrphanedItemStrategyContext(jobManagement, item)

    def 'extension node is transformed to orphanedItemStrategy node'() {
        Node node = new Node(null, 'org.example.CustomStrategy', [foo: 'bar'])
        node.appendNode('test', 'value')

        when:
        context.addExtensionNode(node)

        then:
        with(context.orphanedItemStrategyNode) {
            name() == 'orphanedItemStrategy'
            attributes().size() == 2
            attribute('class') == 'org.example.CustomStrategy'
            attribute('foo') == 'bar'
            children().size() == 1
            test[0].text() == 'value'
        }
    }

    def 'discardOldItems with minimal options'() {
        when:
        context.discardOldItems {}

        then:
        context.orphanedItemStrategyNode != null
        with(context.orphanedItemStrategyNode) {
            name() == 'orphanedItemStrategy'
            children().size() == 3
            attribute('class') == 'com.cloudbees.hudson.plugins.folder.computed.DefaultOrphanedItemStrategy'
            pruneDeadBranches[0].value() == true
            daysToKeep[0].value() == -1
            numToKeep[0].value() == -1
        }
    }

    def 'discardOldItems with all options'() {
        when:
        context.discardOldItems {
            daysToKeep(10)
            numToKeep(12)
        }

        then:
        context.orphanedItemStrategyNode != null
        with(context.orphanedItemStrategyNode) {
            name() == 'orphanedItemStrategy'
            children().size() == 3
            attribute('class') == 'com.cloudbees.hudson.plugins.folder.computed.DefaultOrphanedItemStrategy'
            pruneDeadBranches[0].value() == true
            daysToKeep[0].value() == 10
            numToKeep[0].value() == 12
        }
    }
}
