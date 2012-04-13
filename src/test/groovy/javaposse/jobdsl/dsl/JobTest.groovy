package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.Job;
import javaposse.jobdsl.dsl.JobManagement;
import spock.lang.*

class JobTest extends Specification {
    def "construct a job"() {
        setup:
        JobManagement jm = Mock()

        when:
        def job = new Job(jm)

        then:
        notThrown(Exception)
    }

    def "set name on a job"() {
        setup:
        JobManagement jm = Mock()

        when:
        def job = new Job(jm)
        job.name = "NAME"

        then:
        job.name == "NAME"
    }

    def "load template from job"() {
        setup:
        JobManagement jm = Mock()
        Job job = new Job(jm)

        when:
        job.using("TMPL")

        then:
        1 * jm.getConfig("TMPL") >> minimalXml
    }

    def "load template and generate xml from job"() {
        setup:
        JobManagement jm = Mock()
        Job job = new Job(jm)


        when:
        job.using("TMPL")

        then:
        1 * jm.getConfig("TMPL") >> minimalXml
        job.xml == '<?xml version="1.0" encoding="UTF-8"?>'+minimalXml // This is a dangerous check, since we're not guaranteed to get the XML in order
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

    def minimalXml = 
'''<project>
  <actions/>
  <description/>
  <keepDependencies>false</keepDependencies>
  <properties/>
</project>
'''
}
