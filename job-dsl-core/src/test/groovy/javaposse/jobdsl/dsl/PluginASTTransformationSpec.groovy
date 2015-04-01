package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import spock.lang.Specification

class PluginASTTransformationSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    FreeStyleJob job = new FreeStyleJob(jobManagement)

    def 'require plugin'() {
        when:
        job.publishers {
            extendedEmail('foo@bar.org')
        }

        then:
        1 * jobManagement.requirePlugin('email-ext')
    }

    def 'require plugin with minimum version'() {
        when:
        job.publishers {
            plotBuildData {
            }
        }

        then:
        1 * jobManagement.requireMinimumPluginVersion('plot', '1.9')
    }
}
