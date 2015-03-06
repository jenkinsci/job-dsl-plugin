package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class WorkflowJobSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final WorkflowJob job = new WorkflowJob(jobManagement)

    def 'construct simple workflow job and generate xml from it'() {
        when:
        def xml = job.node

        then:
        xml.name() == 'flow-definition'
        xml.children().size() == 6
    }

    def 'minimal cps workflow'() {
        when:
        job.definition {
            cps {
            }
        }

        then:
        with(job.node.definition[0]) {
            attribute('class') == 'org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition'
            children().size() == 2
            script[0].value() == ''
            sandbox[0].value() == false
        }
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
