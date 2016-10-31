package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import spock.lang.Specification

class MavenTriggerContextSpec extends Specification {
    JobManagement mockJobManagement = Mock(JobManagement)
    Item item = new FreeStyleJob(mockJobManagement, 'test')
    MavenTriggerContext context = new MavenTriggerContext(mockJobManagement, item)

    def 'call snapshotDependencies for Maven job succeeds'() {
        when:
        context.snapshotDependencies(value)

        then:
        item.node.ignoreUpstremChanges[0].value() == !value

        where:
        value << [true, false]
    }
}
