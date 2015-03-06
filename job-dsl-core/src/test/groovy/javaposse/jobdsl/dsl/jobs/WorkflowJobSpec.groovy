package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.JobManagement
import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

class WorkflowJobSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final WorkflowJob job = new WorkflowJob(jobManagement)

    def setup() {
        XMLUnit.setIgnoreWhitespace(true)
    }

    def 'minimal workflow job'() {
        when:
        def xml = job.xml

        then:
        assertXMLEqual WorkflowJob.TEMPLATE, xml
    }

    def 'minimal cps workflow'() {
        when:
        job.definition {
            cps {
            }
        }

        then:
        assertXMLEqual WorkflowJob.TEMPLATE, job.xml
    }

    def 'full cps workflow'() {
        when:
        job.definition {
            cps {
                script('foo')
                sandbox()
            }
        }

        then:
        with(job.node.definition[0]) {
            attribute('class') == 'org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition'
            children().size() == 2
            script[0].value() == 'foo'
            sandbox[0].value() == true
        }
    }
}
