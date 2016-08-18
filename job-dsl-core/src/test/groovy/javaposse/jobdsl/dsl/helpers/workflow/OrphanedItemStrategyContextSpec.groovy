package javaposse.jobdsl.dsl.helpers.workflow

import spock.lang.Specification

class OrphanedItemStrategyContextSpec extends Specification {
    OrphanedItemStrategyContext context = new OrphanedItemStrategyContext()

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
