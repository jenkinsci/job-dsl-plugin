package javaposse.jobdsl.dsl

import spock.lang.Specification

class ConfigFileSpec extends Specification {
    private final ConfigFile configFile = new ConfigFile(ConfigFileType.Custom)

    def 'get type'() {
        when:
        ConfigFile configFile = new ConfigFile(ConfigFileType.Custom)

        then:
        configFile.type == ConfigFileType.Custom
    }

    def 'set name'() {
        when:
        configFile.name('foo')

        then:
        configFile.name == 'foo'
    }

    def 'set comment'() {
        when:
        configFile.comment('lorem ipsum')

        then:
        configFile.comment == 'lorem ipsum'
    }

    def 'set comment with null value'() {
        when:
        configFile.comment(null)

        then:
        thrown(IllegalArgumentException)
    }

    def 'set content'() {
        when:
        configFile.content('<test/>')

        then:
        configFile.content == '<test/>'
    }

    def 'set content with null value'() {
        when:
        configFile.content(null)

        then:
        thrown(IllegalArgumentException)
    }
}
