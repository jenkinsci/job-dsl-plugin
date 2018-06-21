package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Project

class FreeStyleJob extends Project {
    FreeStyleJob(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }
}
