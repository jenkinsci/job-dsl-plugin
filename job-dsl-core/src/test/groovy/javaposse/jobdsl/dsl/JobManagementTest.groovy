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

    def "create new config - name not provided (NULL)"() {
        // Should throw a "NewJobNameMissingException
        setup:
        setIgnoreWhitespace(Boolean.TRUE); // XMLUnit
        JobManagement jm = new StringJobManagement()

        when:
        jm.createOrUpdateConfig(null, updatedXml_keepDepIsTrue, false)

        then:
        thrown(NameNotProvidedException)
    }

    def "create new config - name not provided (EMPTY)"() {
        // Should throw a "NewJobNameMissingException
        setup:
        setIgnoreWhitespace(Boolean.TRUE); // XMLUnit
        JobManagement jm = new StringJobManagement()

        when:
        jm.createOrUpdateConfig("", updatedXml_keepDepIsTrue, false)

        then:
        thrown(NameNotProvidedException)
    }

    def "create new config - config XML not provided (NULL)"() {
        // Should throw a "NewJobNameMissingException
        setup:
        setIgnoreWhitespace(Boolean.TRUE); // XMLUnit
        JobManagement jm = new StringJobManagement()

        when:
        jm.createOrUpdateConfig("NEW-JOB-NAME", null, false)

        then:
        thrown(ConfigurationMissingException)
    }

    def "create new config - config XML not provided (EMPTY)"() {
        // Should throw a "NewJobNameMissingException
        setup:
        setIgnoreWhitespace(Boolean.TRUE); // XMLUnit
        JobManagement jm = new StringJobManagement()

        when:
        jm.createOrUpdateConfig("NEW-JOB-NAME", "", false)

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
