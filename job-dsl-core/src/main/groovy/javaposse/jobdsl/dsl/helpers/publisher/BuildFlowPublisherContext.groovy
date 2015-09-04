package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class BuildFlowPublisherContext extends PublisherContext {
    BuildFlowPublisherContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    /**
     * Aggregates test results from builds started dynamically by build flow jobs.
     *
     * @since 1.35
     */
    @RequiresPlugin(id = 'build-flow-test-aggregator', minimumVersion = '1.1')
    void aggregateBuildFlowTests() {
        publisherNodes << new NodeBuilder().'org.zeroturnaround.jenkins.flowbuildtestaggregator.FlowTestAggregator'()
    }
}
