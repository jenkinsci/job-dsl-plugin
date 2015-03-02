package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.jobs.BuildFlowJob
import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import javaposse.jobdsl.dsl.jobs.MatrixJob
import javaposse.jobdsl.dsl.jobs.MavenJob
import javaposse.jobdsl.dsl.jobs.MultiJob
import javaposse.jobdsl.dsl.jobs.WorkflowJob

@Deprecated
enum JobType {
    Freeform(FreeStyleJob),
    Maven(MavenJob),
    Multijob(MultiJob),
    BuildFlow(BuildFlowJob),
    Workflow(WorkflowJob),
    Matrix(MatrixJob)

    final Class<? extends Job> jobClass

    JobType(Class<? extends Job> jobClass) {
        this.jobClass = jobClass
    }

    static find(String enumName) {
        values().find { it.name().toLowerCase() == enumName.toLowerCase() }
    }
}
