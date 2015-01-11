package javaposse.jobdsl.dsl

import spock.lang.Specification
import static org.custommonkey.xmlunit.XMLUnit.setIgnoreWhitespace

class JobManagementSpec extends Specification {
    def 'get non-existent config'() {
        setup:
        JobManagement jm = new FileJobManagement(new File('src/test/resources'))

        when:
        jm.getConfig('TMPL-NOT-THERE')

        then:
        thrown(JobConfigurationNotFoundException)
    }

    def 'get config - no name provided'() {
        setup:
        JobManagement jm = new StringJobManagement()

        when:
        jm.getConfig('')

        then:
        thrown(JobConfigurationNotFoundException)
    }

    def 'create new config - name not provided (NULL)'() {
        setup:
        setIgnoreWhitespace(Boolean.TRUE); // XMLUnit
        JobManagement jm = new StringJobManagement()

        when:
        JobConfig config = new JobConfig()
        config.setMainConfig(minimalXml)
        jm.createOrUpdateConfig(null, config, false)

        then:
        thrown(NameNotProvidedException)
    }

    def 'create new config - name not provided (EMPTY)'() {
        setup:
        setIgnoreWhitespace(Boolean.TRUE); // XMLUnit
        JobManagement jm = new StringJobManagement()

        when:
        JobConfig config = new JobConfig()
        config.setMainConfig(minimalXml)
        jm.createOrUpdateConfig('', config, false)

        then:
        thrown(NameNotProvidedException)
    }

    def 'create new config - config XML not provided (NULL)'() {
        setup:
        setIgnoreWhitespace(Boolean.TRUE); // XMLUnit
        JobManagement jm = new StringJobManagement()

        when:
        JobConfig config = new JobConfig()
        config.setMainConfig(null)
        jm.createOrUpdateConfig('NEW-JOB-NAME', config, false)

        then:
        thrown(ConfigurationMissingException)
    }

    def 'create new config - config XML not provided (EMPTY)'() {
        setup:
        setIgnoreWhitespace(Boolean.TRUE); // XMLUnit
        JobManagement jm = new StringJobManagement()

        when:
        JobConfig config = new JobConfig()
        config.setMainConfig('')
        jm.createOrUpdateConfig('NEW-JOB-NAME', config, false)

        then:
        thrown(ConfigurationMissingException)
    }

    private final minimalXml = '''
<project>
  <actions/>
  <description/>
  <keepDependencies>false</keepDependencies>
  <properties/>
</project>
'''
}
