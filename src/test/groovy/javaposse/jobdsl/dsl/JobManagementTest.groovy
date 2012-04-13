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
        assertXMLEqual "<?xml version=\"1.0\" encoding=\"UTF-8\"?><project><actions/><description/><keepDependencies>false</keepDependencies><properties/></project>", xml  //'<?xml version="1.0" encoding="UTF-8"?>' + minimalXml, xml
    }

    def "create new config"() {
        // TODO: implement me
        // Should create a new job as expected
    }

    def "update existing config"() {
        // TODO: implement me
        // Should update job as expected
    }

    def "create new config - name not provided"() {
        // TODO: implement me
        // Should throw a "NewJobNameMissingException (or something like this)
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