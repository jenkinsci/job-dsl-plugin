package javaposse.jobdsl.dsl

class TestProject extends Project {
    TestProject(JobManagement jobManagement) {
        super(jobManagement, 'test')
    }
}
