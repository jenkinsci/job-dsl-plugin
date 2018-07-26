package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.jobs.MultiJob
import spock.lang.Specification

class MultiJobSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final MultiJob job = new MultiJob(jobManagement, 'test')

    def 'call steps'() {
        when:
        job.steps {
            phase('ls')
        }

        then:
        job.node.builders[0].children()[0].name() == 'com.tikal.jenkins.plugins.multijob.MultiJobBuilder'
    }

    def 'call pollSubjobs'() {
        when:
        job.pollSubjobs(value)

        then:
        job.node.pollSubjobs[0].value() == value

        where:
        value << [true, false]
    }
}
