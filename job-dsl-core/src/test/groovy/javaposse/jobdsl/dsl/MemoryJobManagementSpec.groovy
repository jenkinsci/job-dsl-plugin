package javaposse.jobdsl.dsl

import spock.lang.Specification

class MemoryJobManagementSpec extends Specification {
    private final MemoryJobManagement jobManagement = new MemoryJobManagement()

    def 'getConfig throws Exception when config not available'() {
        when:
        jobManagement.getConfig('foo')

        then:
        Exception e = thrown(JobConfigurationNotFoundException)
        e.message.contains('foo')
    }

    def 'getConfig returns config'() {
        setup:
        jobManagement.availableConfigs['foo'] = 'bar'

        when:
        String config = jobManagement.getConfig('foo')

        then:
        config == 'bar'
    }

    def 'createOrUpdateConfig complains about missing name'(String name) {
        setup:
        Item item = Mock(Item)
        item.name >> name

        when:
        jobManagement.createOrUpdateConfig(item, false)

        then:
        thrown(NameNotProvidedException)

        where:
        name << [null, '']
    }

    def 'createOrUpdateConfig complains about missing config'(String config) {
        setup:
        Item item = Mock(Item, constructorArgs: [jobManagement, 'foo'])
        item.xml >> config

        when:
        jobManagement.createOrUpdateConfig(item, false)

        then:
        thrown(ConfigurationMissingException)

        where:
        config << [null, '']
    }

    def 'createOrUpdateConfig creates config'() {
        setup:
        Item item = Mock(Item, constructorArgs: [jobManagement, 'foo'])
        item.xml >> 'bar'

        when:
        boolean result = jobManagement.createOrUpdateConfig(item, false)

        then:
        result
        jobManagement.savedConfigs['foo'] == 'bar'
    }

    def 'createOrUpdateView complains about missing name'(String name) {
        when:
        jobManagement.createOrUpdateView(name, 'bar', false)

        then:
        thrown(NameNotProvidedException)

        where:
        name << [null, '']
    }

    def 'createOrUpdateView complains about missing config'(String config) {
        when:
        jobManagement.createOrUpdateView('foo', config, false)

        then:
        thrown(ConfigurationMissingException)

        where:
        config << [null, '']
    }

    def 'createOrUpdateView creates config'() {
        when:
        jobManagement.createOrUpdateView('foo', 'bar', false)

        then:
        jobManagement.savedViews['foo'] == 'bar'
    }

    def 'queueJob schedules job'() {
        when:
        jobManagement.queueJob('foo')

        then:
        jobManagement.scheduledJobs.contains('foo')
    }

    def 'readFileInWorkspace returns file content'() {
        setup:
        jobManagement.availableFiles['foo'] = 'bar'

        when:
        String file = jobManagement.readFileInWorkspace('foo')

        then:
        file == 'bar'
    }

    def 'readFileInWorkspace throws exception when file not found'() {
        when:
        jobManagement.readFileInWorkspace('foo')

        then:
        Exception e = thrown(FileNotFoundException)
        e.message.contains('foo')
    }

    def 'streamFileInWorkspace returns file content'() {
        setup:
        jobManagement.availableFiles['foo'] = 'bar'

        when:
        InputStream file = jobManagement.streamFileInWorkspace('foo')

        then:
        file.bytes == 'bar'.bytes
    }

    def 'streamFileInWorkspace throws exception when file not found'() {
        when:
        jobManagement.streamFileInWorkspace('foo')

        then:
        Exception e = thrown(FileNotFoundException)
        e.message.contains('foo')
    }

    def 'isMinimumPluginVersionInstalled returns false'() {
        when:
        boolean result = jobManagement.isMinimumPluginVersionInstalled('foo', '0.1')

        then:
        !result
    }

    def 'getVSphereCloudHash returns null'() {
        when:
        String id = jobManagement.getVSphereCloudHash('foo')

        then:
        id == null
    }

    def 'outputStream'() {
        setup:
        PrintStream out = Mock(PrintStream)

        when:
        JobManagement jobManagement = new MemoryJobManagement(out)

        then:
        jobManagement.outputStream == out
    }
}
