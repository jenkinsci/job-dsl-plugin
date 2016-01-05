package javaposse.jobdsl.dsl

import org.custommonkey.xmlunit.XMLUnit
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class FileJobManagementSpec extends Specification {
    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    private final File tempFolder = temporaryFolder.newFolder()
    private final FileJobManagement jobManagement = new FileJobManagement(tempFolder)

    def setup() {
        XMLUnit.ignoreWhitespace = true
    }

    def cleanup() {
        new File('foo.xml').delete()
    }

    def 'getConfig returns dummy XML when job name is empty'() {
        when:
        String config = jobManagement.getConfig('')

        then:
        XMLUnit.compareXML(
                '<project><actions/><description/><keepDependencies>false</keepDependencies><properties/></project>',
                config
        ).similar()
    }

    def 'getConfig throws exception then config not found'() {
        when:
        jobManagement.getConfig('foo')

        then:
        Exception e = thrown(JobConfigurationNotFoundException)
        e.message.contains('foo')
    }

    def 'getConfig returns config'() {
        setup:
        new File(jobManagement.root, 'foo.xml').write('bar')

        when:
        String config = jobManagement.getConfig('foo')

        then:
        config == 'bar'
    }

    def 'createOrUpdateConfig complains about missing name'(String name) {
        when:
        jobManagement.createOrUpdateConfig(name, 'bar', false)

        then:
        thrown(NameNotProvidedException)

        where:
        name << [null, '']
    }

    def 'createOrUpdateConfig complains about missing config'(String config) {
        when:
        jobManagement.createOrUpdateConfig('foo', config, false)

        then:
        thrown(ConfigurationMissingException)

        where:
        config << [null, '']
    }

    def 'createOrUpdateConfig creates config'() {
        when:
        boolean result = jobManagement.createOrUpdateConfig('foo', 'bar', false)

        then:
        result
        new File(tempFolder, 'foo.xml').text == 'bar'
    }

    def 'createOrUpdateConfig creates config in folder'() {
        when:
        boolean result = jobManagement.createOrUpdateConfig('foo/bar', 'baz', false)

        then:
        result
        new File(tempFolder, 'foo/bar.xml').text == 'baz'
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
        new File(tempFolder, 'foo.xml').text == 'bar'
    }

    def 'createOrUpdateView creates config in folder'() {
        when:
        jobManagement.createOrUpdateView('foo/bar', 'baz', false)

        then:
        new File(tempFolder, 'foo/bar.xml').text == 'baz'
    }

    def 'createOrUpdateConfigFile is not supported'() {
        when:
        jobManagement.createOrUpdateConfigFile(Mock(ConfigFile), false)

        then:
        thrown(UnsupportedOperationException)
    }

    def 'readFileInWorkspace returns file content'() {
        setup:
        new File(jobManagement.root, 'foo').write('bar')

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
        new File(jobManagement.root, 'foo').write('bar')

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

    def 'isMinimumPluginVersionInstalled returns null'() {
        when:
        boolean result = jobManagement.isMinimumPluginVersionInstalled('foo', '1.0')

        then:
        !result
    }

    def 'getPluginVersion returns null'() {
        when:
        String id = jobManagement.getPluginVersion('foo')

        then:
        id == null
    }

    def 'getVSphereCloudHash returns null'() {
        when:
        String id = jobManagement.getVSphereCloudHash('foo')

        then:
        id == null
    }

    def 'getConfigFileId returns null when config file not found'() {
        when:
        String id = jobManagement.getConfigFileId(ConfigFileType.Custom, 'foo')

        then:
        id == null
    }
}
