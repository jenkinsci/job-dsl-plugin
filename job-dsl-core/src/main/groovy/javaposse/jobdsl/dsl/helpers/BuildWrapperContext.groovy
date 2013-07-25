package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType

class BuildWrapperContext implements Context {
    List<Node> buildWrapperNodes = []
    JobType type

    BuildWrapperContext(JobType jobType) {
        this.type = jobType
    }

    BuildWrapperContext(List<Node> buildWrapperNodes, JobType jobType) {
        this(jobType)
        this.buildWrapperNodes = buildWrapperNodes
    }

    def timestamps() {
        def nodeBuilder = new NodeBuilder()
        buildWrapperNodes << nodeBuilder.'hudson.plugins.timestamper.TimestamperBuildWrapper'()
    }
}
