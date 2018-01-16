package javaposse.jobdsl.dsl

import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

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
        JobManagement jm = new MemoryJobManagement()

        when:
        jm.getConfig('')

        then:
        thrown(JobConfigurationNotFoundException)
    }

    def 'create new config - name not provided (NULL)'() {
        setup:
        XMLUnit.ignoreWhitespace = true
        JobManagement jm = new MemoryJobManagement()
        Item item = Mock(Item)
        item.name >> null
        item.xml >> minimalXml

        when:
        jm.createOrUpdateConfig(item, false)

        then:
        thrown(NameNotProvidedException)
    }

    def 'create new config - name not provided (EMPTY)'() {
        setup:
        XMLUnit.ignoreWhitespace = true
        JobManagement jm = new MemoryJobManagement()
        Item item = Mock(Item)
        item.name >> ''
        item.xml >> minimalXml

        when:
        jm.createOrUpdateConfig(item, false)

        then:
        thrown(NameNotProvidedException)
    }

    def 'create new config - config XML not provided (NULL)'() {
        setup:
        XMLUnit.ignoreWhitespace = true
        JobManagement jm = new MemoryJobManagement()
        Item item = Mock(Item, constructorArgs: [jm, 'NEW-JOB-NAME'])
        item.xml >> null

        when:
        jm.createOrUpdateConfig(item, false)

        then:
        thrown(ConfigurationMissingException)
    }

    def 'create new config - config XML not provided (EMPTY)'() {
        setup:
        XMLUnit.ignoreWhitespace = true
        JobManagement jm = new MemoryJobManagement()
        Item item = Mock(Item, constructorArgs: [jm, 'NEW-JOB-NAME'])
        item.xml >> ''

        when:
        jm.createOrUpdateConfig(item, false)

        then:
        thrown(ConfigurationMissingException)
    }

    private final String minimalXml = '''
<project>
  <actions/>
  <description/>
  <keepDependencies>false</keepDependencies>
  <properties/>
</project>
'''
}
