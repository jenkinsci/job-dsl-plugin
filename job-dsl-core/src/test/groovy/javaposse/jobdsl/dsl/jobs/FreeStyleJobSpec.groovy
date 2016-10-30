package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class FreeStyleJobSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final FreeStyleJob job = new FreeStyleJob(jobManagement, 'test')

    def 'construct simple free style job and generate xml from it'() {
        when:
        def xml = job.node

        then:
        xml.name() == 'project'
        xml.children().size() == 14
    }

    def 'free style job can roam by default'() {
        when:
        Node xml = job.node

        then:
        xml.canRoam[0].value() == ['true']
    }
}
