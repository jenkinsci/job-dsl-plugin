package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.Job;
import javaposse.jobdsl.dsl.JobTemplateMissingException;
import javaposse.jobdsl.dsl.JobManagement;
import spock.lang.*

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

class JobTest extends Specification {
    def "construct a job manually (not from a DSL script)"() {
        setup:
        JobManagement jm = Mock()

        when:
        def job = new Job(jm)

        then:
        notThrown(Exception)
    }

    def "set name on a manually constructed job"() {
        setup:
        JobManagement jm = Mock()

        when:
        def job = new Job(jm)
        job.name = "NAME"

        then:
        job.name == "NAME"
    }

    def "load an empty template from a manually constructed job"() {
        setup:
        JobManagement jm = Mock()
        Job job = new Job(jm)

        when:
        job.using("TMPL")

        then:
        1 * jm.getConfig("TMPL") >> minimalXml
    }

    def "load an empty template from a manually constructed job and generate xml from it"() {
        setup:
        JobManagement jm = Mock()
        Job job = new Job(jm)

        when:
        job.using("TMPL")

        then:
        1 * jm.getConfig("TMPL") >> minimalXml
        assertXMLEqual '<?xml version="1.0" encoding="UTF-8"?>' + minimalXml, job.xml
    }

    def "load large template from file"() {
        setup:
        JobManagement jm = new FileJobManagement(new File("src/test/resources"))
        Job job = new Job(jm)

        when:
        job.using("config")

        then:
        job.project.description.text() == 'Description'
    }

    def "generate job from missing template - throws JobTemplateMissingException"() {
        setup:
        JobManagement jm = new FileJobManagement(new File("src/test/resources"))
        Job job = new Job(jm)

        when:
        job.using("TMPL-NOT_THERE")

        then:
        thrown(JobTemplateMissingException)
    }

    def minimalXml = '''
<project>
  <actions/>
  <description/>
  <keepDependencies>false</keepDependencies>
  <properties/>
</project>
'''

}