package javaposse.jobdsl.dsl

class TestJob extends Job {
    TestJob(JobManagement jobManagement) {
        super(jobManagement, 'test')
    }

    @SuppressWarnings('EmptyMethod')
    @Deprecated
    void foo() {
    }
}
