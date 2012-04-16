package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.JobManagement;
import javaposse.jobdsl.plugin.JenkinsJobManagement;
import javaposse.jobdsl.dsl.JobConfigurationNotFoundException;

import spock.lang.*
import static org.custommonkey.xmlunit.XMLAssert.*
import static org.custommonkey.xmlunit.XMLUnit.*

class JobManagementTest extends Specification {

    def "get non-existent config"() {
        setup:
        JobManagement jm = new FileJobManagement(new File("src/test/resources"))

        when:
        jm.getConfig("TMPL-NOT-THERE")

        then:
        thrown(JobConfigurationNotFoundException)
    }

    def "get config - no name provided"() {
        // Should return an empty, default config
        setup:
        setIgnoreWhitespace(Boolean.TRUE); // XMLUnit
        JobManagement jm = new JenkinsJobManagement()

        when:
        String xml = jm.getConfig("")

        System.out.println("XML returned: \n" + xml);
        System.out.println("Minimal XML:\n" + minimalXml);

        then:
        assertXMLEqual minimalXml, xml
    }

//    def "create new config"() {
//        // Should create a new job as expected
//        setup:
//        setIgnoreWhitespace(Boolean.TRUE); // XMLUnit
//        JobManagement jm = new JenkinsJobManagement()
//        // Need to mock out JenkinsJobManagement.jenkins.getItemByFullName("MY-NEW-JOB") => returns null (no job found)
//
//        when:
//        Boolean successfull = jm.createOrUpdateConfig("MY-NEW-JOB", minimalXml)
//
//        then:
//        assertTrue(successful)
//        // Check that JenkinsJobManagement.jenkins.createProjectFromXML("MY-NEW-JOB", inputStreamOfNewConfig) is called once
//    }
//
//    def "update existing config - change value of existing node"() {
//        setup:
//        setIgnoreWhitespace(Boolean.TRUE); // XMLUnit
//        JobManagement jm = new JenkinsJobManagement()
//        // Need to mock out JenkinsJobManagement.jenkins.getItemByFullName("MY-UPDATED-JOB") => finds the job
//
//        when:
//        Boolean successfull = jm.createOrUpdateConfig("MY-UPDATED-JOB", updatedXml_keepDepIsTrue)
//
//        then:
//        assertTrue(successful)
//        // Check that JenkinsJobManagement.jenkins.createProjectFromXML("MY-NEW-JOB", inputStreamOfNewConfig) is called once
//        // Check that the new config for this job is as expected now the update was successful
//    }

    def "create new config - name not provided (NULL)"() {
        // Should throw a "NewJobNameMissingException
        setup:
        setIgnoreWhitespace(Boolean.TRUE); // XMLUnit
        JobManagement jm = new JenkinsJobManagement()

        when:
        jm.createOrUpdateConfig(null, updatedXml_keepDepIsTrue)

        then:
        thrown(JobNameNotProvidedException)
    }

    def "create new config - name not provided (EMPTY)"() {
        // Should throw a "NewJobNameMissingException
        setup:
        setIgnoreWhitespace(Boolean.TRUE); // XMLUnit
        JobManagement jm = new JenkinsJobManagement()

        when:
        jm.createOrUpdateConfig("", updatedXml_keepDepIsTrue)

        then:
        thrown(JobNameNotProvidedException)
    }

    def "create new config - config XML not provided (NULL)"() {
        // Should throw a "NewJobNameMissingException
        setup:
        setIgnoreWhitespace(Boolean.TRUE); // XMLUnit
        JobManagement jm = new JenkinsJobManagement()

        when:
        jm.createOrUpdateConfig("NEW-JOB-NAME", null)

        then:
        thrown(JobConfigurationMissingException)
    }

    def "create new config - config XML not provided (EMPTY)"() {
        // Should throw a "NewJobNameMissingException
        setup:
        setIgnoreWhitespace(Boolean.TRUE); // XMLUnit
        JobManagement jm = new JenkinsJobManagement()

        when:
        jm.createOrUpdateConfig("NEW-JOB-NAME", "")

        then:
        thrown(JobConfigurationMissingException)
    }

def minimalXml = '''
<project>
  <actions/>
  <description/>
  <keepDependencies>false</keepDependencies>
  <properties/>
</project>
'''

def updatedXml_keepDepIsTrue = '''
<project>
  <actions/>
  <description/>
  <keepDependencies>true</keepDependencies>
  <properties/>
</project>
'''

}