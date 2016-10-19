package javaposse.jobdsl.dsl

import spock.lang.Specification

class MavenSettingsConfigFileSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    MavenSettingsConfigFile configFile = new MavenSettingsConfigFile(ConfigFileType.MavenSettings, jobManagement)

    def 'set replaceAll'() {
        expect:
        configFile.replaceAll == null

        when:
        configFile.replaceAll()

        then:
        configFile.replaceAll

        when:
        configFile.replaceAll(false)

        then:
        !configFile.replaceAll

        when:
        configFile.replaceAll(true)

        then:
        configFile.replaceAll
    }

    def 'set serverCredentials'() {
        expect:
        configFile.credentialsMapping.isEmpty()

        when:
        configFile.serverCredentials('foo', '123')

        then:
        configFile.credentialsMapping.size() == 1
        configFile.credentialsMapping['foo'] == '123'

        when:
        configFile.serverCredentials('bar', '456')

        then:
        configFile.credentialsMapping.size() == 2
        configFile.credentialsMapping['foo'] == '123'
        configFile.credentialsMapping['bar'] == '456'
    }
}
