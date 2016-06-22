package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import javaposse.jobdsl.dsl.jobs.MatrixJob
import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

class CoerceSpec extends Specification {
    private final resourcesDir = new File(getClass().getResource('/simple.dsl').toURI()).parentFile
    private final JobManagement jm = new FileJobManagement(resourcesDir)

    def setup() {
        XMLUnit.setIgnoreWhitespace(true)
    }

    def 'Matrix job coerces to FreeStyleJob'() {
        setup:
        JobManagement jm = new FileJobManagement(resourcesDir)
        FreeStyleJob job = new FreeStyleJob(jm)

        when:
        job.using('matrix')
        String project = job.xml

        then:
        project.contains('<project>')
        ! project.contains('<matrix-project>')
        ! project.contains('<axes>')
        ! project.contains('executionStrategy')
        ! project.contains('<childCustomWorkspace>')
        ! project.contains('<combinationFilter>')
    }

    def 'FreeStyle job coerces to MatrixJob'() {
        setup:
        JobManagement jm = new FileJobManagement(resourcesDir)
        MatrixJob job = new MatrixJob(jm)

        when:
        job.using('job')
        String project = job.xml

        then:
        project.contains('<matrix-project>')
        ! project.contains('<project>')
        project.contains('<axes>')
        project.contains('executionStrategy')
    }
}
