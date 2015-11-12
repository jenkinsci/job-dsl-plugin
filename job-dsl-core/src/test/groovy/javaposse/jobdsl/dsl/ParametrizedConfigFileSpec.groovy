package javaposse.jobdsl.dsl

import spock.lang.Specification

class ParametrizedConfigFileSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    ParametrizedConfigFile configFile = new ParametrizedConfigFile(ConfigFileType.Custom, jobManagement)

    def 'set arguments'() {
        when:
        configFile.arguments()

        then:
        configFile.arguments == []

        when:
        configFile.arguments('foo')

        then:
        configFile.arguments == ['foo']

        when:
        configFile.arguments('bar', 'baz')

        then:
        configFile.arguments == ['foo', 'bar', 'baz']
    }
}
