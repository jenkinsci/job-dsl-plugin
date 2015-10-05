package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import spock.lang.Specification

class PluginASTTransformationSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    FreeStyleJob job = new FreeStyleJob(jobManagement)
    JobParent jobParent = Spy(JobParent)

    def 'require plugin'() {
        when:
        job.publishers {
            mailer('foo@bar.org')
        }

        then:
        1 * jobManagement.requirePlugin('mailer')
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

    def 'require plugin on interface'() {
        setup:
        jobParent.jm = jobManagement

        when:
        jobParent.folder('test')

        then:
        1 * jobManagement.requirePlugin('cloudbees-folder')
    }

    def 'require plugin on interface of interface'() {
        setup:
        jobParent.jm = jobManagement

        when:
        jobParent.buildPipelineView('test')

        then:
        1 * jobManagement.requirePlugin('build-pipeline-plugin')
    }
}
