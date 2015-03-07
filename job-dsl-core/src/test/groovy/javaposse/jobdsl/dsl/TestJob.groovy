package javaposse.jobdsl.dsl

class TestJob extends Job {
    TestJob(JobManagement jobManagement) {
        super(jobManagement)
    }

    @Override
    protected String getTemplate() {
        '<test/>'
    }
}
