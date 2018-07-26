package javaposse.jobdsl.dsl

import spock.lang.Specification

class DeprecationWarningASTTransformationSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final TestJob testJob = new TestJob(jobManagement)

    def 'logs deprecation warning'() {
        when:
        testJob.foo()

        then:
        1 * jobManagement.logDeprecationWarning()
    }
}
