package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.jobs.MultiJob
import spock.lang.Specification

class MultiJobSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    MultiJob job = new MultiJob(jobManagement)

    def 'deprecation warning'() {
        when:
        new MultiJob(jobManagement)

        then:
        1 * jobManagement.logPluginDeprecationWarning('jenkins-multijob-plugin', '1.13')
    }

    def 'call steps'() {
        when:
        job.steps {
            phase('ls')
        }

        then:
        job.node.builders[0].children()[0].name() == 'com.tikal.jenkins.plugins.multijob.MultiJobBuilder'
    }
}
