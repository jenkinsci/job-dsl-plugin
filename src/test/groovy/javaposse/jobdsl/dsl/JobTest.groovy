package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.Job;
import javaposse.jobdsl.dsl.JobTemplateMissingException;
import javaposse.jobdsl.dsl.JobManagement;

import spock.lang.*
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual
import static org.custommonkey.xmlunit.XMLAssert.assertXMLValid
import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists

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
        job.getXml()

        then:
        1 * jm.getConfig("TMPL") >> minimalXml
    }

    def "load an empty template from a manually constructed job and generate xml from it"() {
        setup:
        JobManagement jm = Mock()
        //jm.getConfig("TMPL") >> minimalXml
        Job job = new Job(jm)

        when:
        job.using("TMPL")
        def xml = job.getXml()

        then:
        _ * jm.getConfig("TMPL") >> minimalXml
        assertXMLEqual '<?xml version="1.0" encoding="UTF-8"?>' + minimalXml, xml
    }

    def "load large template from file"() {
        setup:
        JobManagement jm = new FileJobManagement(new File("src/test/resources"))
        Job job = new Job(jm)

        when:
        job.using("config")
        String project = job.getXml()

        then:
        // assertXpathExists('/description', project) // java.lang.NoSuchMethodError: org.apache.xpath.XPathContext
        project.contains('<description>Description</description>')
    }

    def "generate job from missing template - throws JobTemplateMissingException"() {
        setup:
        JobManagement jm = new FileJobManagement(new File("src/test/resources"))
        Job job = new Job(jm)

        when:
        job.using("TMPL-NOT_THERE")
        job.getXml()

        then:
        thrown(JobTemplateMissingException)
    }

    final minimalXml = '''
<project>
  <actions/>
  <description/>
  <keepDependencies>false</keepDependencies>
  <properties/>
</project>
'''

}