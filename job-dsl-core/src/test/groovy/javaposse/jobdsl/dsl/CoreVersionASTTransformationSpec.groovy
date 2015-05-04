package javaposse.jobdsl.dsl

import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import spock.lang.Specification

class CoreVersionASTTransformationSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    FreeStyleJob job = new FreeStyleJob(jobManagement)

    def 'require plugin'() {
        setup:
        jobManagement.jenkinsVersion >> new VersionNumber('1.565')

        when:
        job.publishers {
            archiveArtifacts {
                fingerprint()
            }
        }

        then:
        1 * jobManagement.requireMinimumCoreVersion('1.571')
    }
}
