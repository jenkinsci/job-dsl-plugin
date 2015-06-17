package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class BuildFlowPublisherContextSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    Item item = Mock(Item)
    BuildFlowPublisherContext context = new BuildFlowPublisherContext(jobManagement, item)

    def 'call aggregateBuildFlowTests'() {
        when:
        context.aggregateBuildFlowTests()

        then:
        with(context.publisherNodes[0]) {
            name() == 'org.zeroturnaround.jenkins.flowbuildtestaggregator.FlowTestAggregator'
            children().size() == 0
        }
        1 * jobManagement.requireMinimumPluginVersion('build-flow-test-aggregator', '1.1')
    }
}
