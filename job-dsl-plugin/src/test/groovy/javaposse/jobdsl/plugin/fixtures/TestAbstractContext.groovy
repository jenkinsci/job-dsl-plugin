package javaposse.jobdsl.plugin.fixtures

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class TestAbstractContext extends AbstractContext {
    TestAbstractContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    JobManagement getJobManagement() {
        this.@jobManagement
    }
}
