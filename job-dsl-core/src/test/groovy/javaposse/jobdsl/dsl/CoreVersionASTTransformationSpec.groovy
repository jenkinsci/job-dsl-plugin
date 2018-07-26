package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import spock.lang.Specification

class CoreVersionASTTransformationSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final FreeStyleJob job = new FreeStyleJob(jobManagement, 'test')

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
