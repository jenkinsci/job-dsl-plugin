package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType

class WrapperContext implements Context {
    List<Node> wrapperNodes = []
    JobType type

    WrapperContext(JobType jobType) {
        this.type = jobType
    }

    WrapperContext(List<Node> wrapperNodes, JobType jobType) {
        this(jobType)
        this.wrapperNodes = wrapperNodes
    }

    def timestamps() {
        def nodeBuilder = new NodeBuilder()
        wrapperNodes << nodeBuilder.'hudson.plugins.timestamper.TimestamperBuildWrapper'()
    }
}
