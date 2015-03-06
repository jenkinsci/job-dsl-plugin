package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.JobManagement
import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

class BuildFlowJobSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final BuildFlowJob job = new BuildFlowJob(jobManagement)

    def setup() {
        XMLUnit.setIgnoreWhitespace(true)
    }

    def 'construct simple Build Flow job and generate xml from it'() {
        when:
        def xml = job.xml

        then:
        assertXMLEqual BuildFlowJob.TEMPLATE, xml
    }

    def 'buildFlow constructs xml'() {
        when:
        job.buildFlow('build Flow Block')

        then:
        job.node.dsl.size() == 1
        job.node.dsl[0].value() == 'build Flow Block'
    }
}
