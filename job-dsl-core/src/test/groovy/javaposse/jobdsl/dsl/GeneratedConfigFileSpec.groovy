package javaposse.jobdsl.dsl

import spock.lang.Specification

class GeneratedConfigFileSpec extends Specification {
    def 'id is null (deprecated)'() {
        when:
        new GeneratedConfigFile(null, 'foo')

        then:
        thrown(IllegalArgumentException)
    }

    def 'name is null (deprecated)'() {
        when:
        new GeneratedConfigFile('235421345', (String) null)

        then:
        thrown(IllegalArgumentException)
    }

    def 'id is null'() {
        when:
        def foo = new ConfigFile(ConfigFileType.Custom, new MemoryJobManagement()).with { name = 'foo'; it }
        new GeneratedConfigFile(null, foo)

        then:
        thrown(IllegalArgumentException)
    }

    def 'name is null'() {
        when:
        new GeneratedConfigFile('235421345', (ConfigFile) null)

        then:
        thrown(IllegalArgumentException)
    }

    def 'get name and id'() {
        when:
        def foo = new ConfigFile(ConfigFileType.Custom, new MemoryJobManagement()).with { name = 'foo'; it }
        GeneratedConfigFile configFile = new GeneratedConfigFile('235421345', foo)

        then:
        configFile.id == '235421345'
        configFile.name == 'foo'
    }

    @SuppressWarnings('ChangeToOperator')
    def 'only id is relevant for hashCode and equals'() {
        when:
        def foo = new ConfigFile(ConfigFileType.Custom, new MemoryJobManagement()).with { name = 'foo'; it }
        def newName = new ConfigFile(ConfigFileType.Custom, new MemoryJobManagement()).with { name = 'new name'; it }
        def other = new ConfigFile(ConfigFileType.Custom, new MemoryJobManagement()).with { name = 'other'; it }
        GeneratedConfigFile configFile1 = new GeneratedConfigFile('235421345', foo)
        GeneratedConfigFile configFile2 = new GeneratedConfigFile('235421345', newName)
        GeneratedConfigFile configFile3 = new GeneratedConfigFile('235421346', other)

        then:
        configFile1.hashCode() == configFile2.hashCode()
        configFile2.hashCode() != configFile3.hashCode()
        configFile1.equals(configFile2)
        !configFile2.equals(configFile3)
    }

    def 'test toString'() {
        when:
        def foo = new ConfigFile(ConfigFileType.Custom, new MemoryJobManagement()).with { name = 'foo'; it }
        GeneratedConfigFile configFile = new GeneratedConfigFile('235421345', foo)

        then:
        configFile.toString() == "GeneratedConfigFile{name='foo', id='235421345'}"
    }

    @SuppressWarnings('ChangeToOperator')
    def 'test compare'() {
        when:
        GeneratedConfigFile configFile1 = new GeneratedConfigFile('235421345', 'foo')
        GeneratedConfigFile configFile2 = new GeneratedConfigFile('235421345', 'new name')

        then:
        configFile1.compareTo(configFile1) == 0
        configFile1.compareTo(configFile2) < 0
        configFile2.compareTo(configFile1) > 0
    }
}
