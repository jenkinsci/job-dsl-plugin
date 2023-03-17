package javaposse.jobdsl.plugin.fixtures;

import javaposse.jobdsl.dsl.AbstractContext;
import javaposse.jobdsl.dsl.JobManagement;

public class TestAbstractContext extends AbstractContext {
    public TestAbstractContext(JobManagement jobManagement) {
        super(jobManagement);
    }

    public JobManagement getJobManagement() {
        return this.jobManagement;
    }
}
