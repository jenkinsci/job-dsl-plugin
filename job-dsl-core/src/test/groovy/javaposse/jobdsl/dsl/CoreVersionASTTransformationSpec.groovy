package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import spock.lang.Specification

class CoreVersionASTTransformationSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    FreeStyleJob job = new FreeStyleJob(jobManagement)

    def 'require plugin'() {
        when:
        job.steps {
            maven {
                injectBuildVariables()
            }
        }

        then:
        1 * jobManagement.requireMinimumCoreVersion('2.12')
    }
}
