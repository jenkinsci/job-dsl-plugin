package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement

class FreeStyleJob extends Job {
    FreeStyleJob(JobManagement jobManagement) {
        super(jobManagement)
    }
}
