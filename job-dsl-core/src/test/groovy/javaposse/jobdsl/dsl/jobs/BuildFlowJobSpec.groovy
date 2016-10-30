package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class BuildFlowJobSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final BuildFlowJob job = new BuildFlowJob(jobManagement, 'test')

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

    def 'buildNeedsWorkspace constructs xml'() {
        when:
        job.buildNeedsWorkspace()

        then:
        job.node.buildNeedsWorkspace.size() == 1
        job.node.buildNeedsWorkspace[0].value() == true
        1 * jobManagement.requireMinimumPluginVersion('build-flow-plugin', '0.12')

        when:
        job.buildNeedsWorkspace(false)

        then:
        job.node.buildNeedsWorkspace.size() == 1
        job.node.buildNeedsWorkspace[0].value() == false
        1 * jobManagement.requireMinimumPluginVersion('build-flow-plugin', '0.12')
    }

    def 'dslFile constructs xml'() {
        when:
        job.dslFile('foo')

        then:
        job.node.dslFile.size() == 1
        job.node.dslFile[0].value() == 'foo'
        job.node.buildNeedsWorkspace.size() == 1
        job.node.buildNeedsWorkspace[0].value() == true
        (1.._) * jobManagement.requireMinimumPluginVersion('build-flow-plugin', '0.12')
    }
}
