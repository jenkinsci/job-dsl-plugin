package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.JobManagement
import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

class FreeStyleJobSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final FreeStyleJob job = new FreeStyleJob(jobManagement)

    def setup() {
        XMLUnit.setIgnoreWhitespace(true)
    }

    def 'construct simple free style job and generate xml from it'() {
        when:
        def xml = job.xml

        then:
        assertXMLEqual FreeStyleJob.TEMPLATE, xml
    }

    def 'free style job can roam by default'() {
        when:
        Node xml = job.node

        then:
        xml.canRoam[0].value() == ['true']
    }
}
