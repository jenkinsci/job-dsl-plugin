package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.JobType

class StepContext extends AbstractStepContext {

    JobType type

    StepContext(JobType jobType) {
        super()
        this.type = jobType
    }

    StepContext(List<Node> stepNodes, JobType jobType) {
        super(stepNodes)
        this.type = jobType
    }

}
