package javaposse.jobdsl.dsl

class TestJob extends Job {
    TestJob(JobManagement jobManagement) {
        super(jobManagement)
    }

    @SuppressWarnings('EmptyMethod')
    @Deprecated
    void foo() {
    }
}
