package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

class MatrixJobSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final MatrixJob job = new MatrixJob(jobManagement)

    def 'construct simple Matrix job and generate xml from it'() {
        when:
        def xml = job.xml

        then:
        assertXMLEqual MatrixJob.TEMPLATE, xml
    }

    def 'combinationFilter constructs xml'() {
        when:
        job.combinationFilter('LABEL1 == "TEST"')

        then:
        job.node.combinationFilter.size() == 1
        job.node.combinationFilter[0].value() == 'LABEL1 == "TEST"'
    }

    def 'runSequentially constructs xml'() {
        when:
        job.runSequentially(false)

        then:
        job.node.executionStrategy.runSequentially.size() == 1
        job.node.executionStrategy.runSequentially[0].value() == false
    }

    def 'touchStoneFilter constructs xml'() {
        when:
        job.touchStoneFilter('LABEL1 == "TEST"', true)

        then:
        with(job.node.executionStrategy) {
            touchStoneCombinationFilter.size() == 1
            touchStoneCombinationFilter[0].value() == 'LABEL1 == "TEST"'
            touchStoneResultCondition.size() == 1
            touchStoneResultCondition[0].children().size() == 3
            touchStoneResultCondition[0].name[0].value() == 'UNSTABLE'
            touchStoneResultCondition[0].color[0].value() == 'YELLOW'
            touchStoneResultCondition[0].ordinal[0].value() == 1
        }

        when:
        job.touchStoneFilter('LABEL1 == "TEST"', false)

        then:
        with(job.node.executionStrategy) {
            touchStoneCombinationFilter.size() == 1
            touchStoneCombinationFilter[0].value() == 'LABEL1 == "TEST"'
            touchStoneResultCondition.size() == 1
            touchStoneResultCondition[0].children().size() == 3
            touchStoneResultCondition[0].name[0].value() == 'STABLE'
            touchStoneResultCondition[0].color[0].value() == 'BLUE'
            touchStoneResultCondition[0].ordinal[0].value() == 0
        }
    }

    def 'can set axis'() {
        when:
        job.axes {
            label('LABEL1', 'a', 'b', 'c')
        }

        then:
        job.node.axes.size() == 1
        job.node.axes[0].children().size() == 1
        job.node.axes[0].children()[0].name() == 'hudson.matrix.LabelAxis'
    }

    def 'axes configure block constructs xml'() {
        when:
        job.axes {
            configure { axes ->
                axes << 'FooAxis'()
            }
        }

        then:
        job.node.axes.size() == 1
        job.node.axes[0].children().size() == 1
        job.node.axes[0].children()[0].name() == 'FooAxis'
    }
}
