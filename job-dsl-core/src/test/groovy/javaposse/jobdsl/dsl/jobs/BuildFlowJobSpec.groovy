package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class BuildFlowJobSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final BuildFlowJob job = new BuildFlowJob(jobManagement)

    def 'construct simple Build Flow job and generate xml from it'() {
        when:
        def xml = job.node

        then:
        xml.name() == 'com.cloudbees.plugins.flow.BuildFlow'
        xml.children().size() == 16
    }

    def 'buildFlow constructs xml'() {
        when:
        job.buildFlow('build Flow Block')

        then:
        job.node.dsl.size() == 1
        job.node.dsl[0].value() == 'build Flow Block'
    }
}
