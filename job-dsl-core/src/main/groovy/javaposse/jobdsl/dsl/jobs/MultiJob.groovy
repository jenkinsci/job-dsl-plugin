package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement

class MultiJob extends Job {
    MultiJob(JobManagement jobManagement) {
        super(jobManagement)
    }
}
