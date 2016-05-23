package javaposse.jobdsl.dsl

import spock.lang.Specification

class DeprecationWarningASTTransformationSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    TestJob testJob = new TestJob(jobManagement)

    def 'logs deprecation warning'() {
        when:
        testJob.foo()

        then:
        1 * jobManagement.logDeprecationWarning()
    }
}
