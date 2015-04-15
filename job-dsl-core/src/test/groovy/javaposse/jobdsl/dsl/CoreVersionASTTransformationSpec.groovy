package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import spock.lang.Specification

class CoreVersionASTTransformationSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    FreeStyleJob job = new FreeStyleJob(jobManagement)

    def 'require plugin'() {
        when:
        job.triggers {
            upstream('foo')
        }

        then:
        1 * jobManagement.requireMinimumCoreVersion('1.560')
    }
}
