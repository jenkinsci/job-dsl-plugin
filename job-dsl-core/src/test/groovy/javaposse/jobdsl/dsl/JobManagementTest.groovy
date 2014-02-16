package javaposse.jobdsl.dsl

import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLUnit.setIgnoreWhitespace

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
        setup:
        JobManagement jm = new StringJobManagement()

        when:
        String xml = jm.getConfig("")

        then:
        thrown(JobConfigurationNotFoundException)
    }

//    //TODO: Enable this when I figure out how to mock what I need using Spock
//    def "lookup template job - job not found"() {
//        setup:
//        JobManagement jm = new JenkinsJobManagement()
//        jm.jenkins = Mock(Jenkins)
////        jm.lookupJob() >> ""
//
//        when:
//        String xml = jm.lookupJob("TEMPLATE_JOB_THAT_ISNT_THERE")
//
//        then:
////        thrown(NullPointerException)
//        assertEqual "", xml
//    }

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
        JobManagement jm = new StringJobManagement()

        when:
        jm.createOrUpdateConfig(null, updatedXml_keepDepIsTrue, null, false)

        then:
        thrown(NameNotProvidedException)
    }

    def "create new config - name not provided (EMPTY)"() {
        // Should throw a "NewJobNameMissingException
        setup:
        setIgnoreWhitespace(Boolean.TRUE); // XMLUnit
        JobManagement jm = new StringJobManagement()

        when:
        jm.createOrUpdateConfig("", updatedXml_keepDepIsTrue, null, false)

        then:
        thrown(NameNotProvidedException)
    }

    def "create new config - config XML not provided (NULL)"() {
        // Should throw a "NewJobNameMissingException
        setup:
        setIgnoreWhitespace(Boolean.TRUE); // XMLUnit
        JobManagement jm = new StringJobManagement()

        when:
        jm.createOrUpdateConfig("NEW-JOB-NAME", null, null, false)

        then:
        thrown(ConfigurationMissingException)
    }

    def "create new config - config XML not provided (EMPTY)"() {
        // Should throw a "NewJobNameMissingException
        setup:
        setIgnoreWhitespace(Boolean.TRUE); // XMLUnit
        JobManagement jm = new StringJobManagement()

        when:
        jm.createOrUpdateConfig("NEW-JOB-NAME", "", null, false)

        then:
        thrown(ConfigurationMissingException)
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
