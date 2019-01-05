package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import spock.lang.Specification

class PluginASTTransformationSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final FreeStyleJob job = new FreeStyleJob(jobManagement, 'test')
    private final JobParent jobParent = Spy(JobParent)

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
        1 * jobManagement.requireMinimumPluginVersion('cloudbees-folder', '5.14', true)
    }

    def 'require plugin on interface of interface'() {
        setup:
        jobParent.jm = jobManagement

        when:
        jobParent.buildPipelineView('test')

        then:
        1 * jobManagement.requirePlugin('build-pipeline-plugin', true)
    }

    def 'require plugin with minimum version and with failIfMissing on interface'() {
        setup:
        jobParent.jm = jobManagement

        when:
        jobParent.ivyJob('test')

        then:
        1 * jobManagement.requireMinimumPluginVersion('ivy', '1.23', true)
    }

    def 'require plugins'() {
        setup:
        jobParent.jm = jobManagement

        when:
        job.wrappers {
            rvm('test')
        }

        then:
        1 * jobManagement.requireMinimumPluginVersion('rvm', '0.6')
        1 * jobManagement.requireMinimumPluginVersion('ruby-runtime', '0.12')
    }
}
