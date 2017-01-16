package javaposse.jobdsl.plugin

import javaposse.jobdsl.dsl.ConfigFile
import javaposse.jobdsl.dsl.ConfigFileType
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.MavenSettingsConfigFile
import javaposse.jobdsl.dsl.ParametrizedConfigFile
import org.jenkinsci.lib.configprovider.model.Config
import org.jenkinsci.plugins.configfiles.GlobalConfigFiles
import org.jenkinsci.plugins.configfiles.custom.CustomConfig
import org.jenkinsci.plugins.configfiles.maven.GlobalMavenSettingsConfig
import org.jenkinsci.plugins.configfiles.maven.MavenSettingsConfig
import org.jenkinsci.plugins.managedscripts.ScriptConfig
import org.junit.ClassRule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Shared
import spock.lang.Specification

import static java.util.UUID.randomUUID
import static javaposse.jobdsl.plugin.ConfigFileProviderHelper.createNewConfig
import static javaposse.jobdsl.plugin.ConfigFileProviderHelper.findConfig
import static javaposse.jobdsl.plugin.ConfigFileProviderHelper.findConfigProvider

class ConfigFileProviderHelperSpec extends Specification {
    @Shared
    @ClassRule
    JenkinsRule jenkinsRule = new JenkinsRule()

    @Shared
    Config customConfig

    @Shared
    Config mavenSettingsConfig

    @Shared
    Config globalMavenSettingsConfig

    @Shared
    Config scriptConfig

    def setupSpec() {
        GlobalConfigFiles globalConfigFiles =
                jenkinsRule.instance.getExtensionList(GlobalConfigFiles).get(GlobalConfigFiles)

        customConfig =
                new CustomConfig(randomUUID().toString(), 'custom', 'foo', 'bar')
        globalConfigFiles.save(customConfig)
        mavenSettingsConfig =
                new MavenSettingsConfig(randomUUID().toString(), 'mavenSettings', 'foo', 'bar', false, [])
        globalConfigFiles.save(mavenSettingsConfig)
        globalMavenSettingsConfig =
                new GlobalMavenSettingsConfig(randomUUID().toString(), 'globalMavenSettings', 'foo', 'bar', false, [])
        globalConfigFiles.save(globalMavenSettingsConfig)
        scriptConfig =
                new ScriptConfig(randomUUID().toString(), 'script', 'foo', 'bar', [])
        globalConfigFiles.save(scriptConfig)
    }

    def 'find config'() {
        expect:
        findConfig(customConfig.provider, 'custom') == customConfig
        findConfig(mavenSettingsConfig.provider, 'mavenSettings') == mavenSettingsConfig
        findConfig(globalMavenSettingsConfig.provider, 'globalMavenSettings') == globalMavenSettingsConfig
        findConfig(scriptConfig.provider, 'script') == scriptConfig

        findConfig(customConfig.provider, 'script') == null
    }

    def 'find config provider'() {
        expect:
        findConfigProvider(ConfigFileType.Custom) == customConfig.provider
        findConfigProvider(ConfigFileType.MavenSettings) == mavenSettingsConfig.provider
        findConfigProvider(ConfigFileType.GlobalMavenSettings) == globalMavenSettingsConfig.provider
        findConfigProvider(ConfigFileType.ManagedScript) == scriptConfig.provider
    }

    def 'create new custom config'() {
        setup:
        String id = randomUUID()
        ConfigFile configFile = new ConfigFile(ConfigFileType.Custom, Mock(JobManagement))
        configFile.name = 'newCustom'
        configFile.comment = 'bbb'
        configFile.content = 'aaa'

        when:
        Config config = createNewConfig(id, configFile)

        then:
        config instanceof CustomConfig
        config.id == id
        config.name == configFile.name
        config.comment == configFile.comment
        config.content == configFile.content
    }

    def 'create new Maven settings config'() {
        setup:
        String id = randomUUID()
        ConfigFile configFile = new MavenSettingsConfigFile(ConfigFileType.MavenSettings, Mock(JobManagement))
        configFile.name = 'newCustom'
        configFile.comment = 'bbb'
        configFile.content = 'aaa'
        configFile.replaceAll = true
        configFile.credentialsMapping['foo'] = 'bar'

        when:
        Config config = createNewConfig(id, configFile)

        then:
        config instanceof MavenSettingsConfig
        config.id == id
        config.name == configFile.name
        config.comment == configFile.comment
        config.content == configFile.content
        ((MavenSettingsConfig) config).isReplaceAll
        ((MavenSettingsConfig) config).serverCredentialMappings.size() == 1
        ((MavenSettingsConfig) config).serverCredentialMappings[0].serverId == 'foo'
        ((MavenSettingsConfig) config).serverCredentialMappings[0].credentialsId == 'bar'
    }

    def 'create new global Maven settings config'() {
        setup:
        String id = randomUUID()
        ConfigFile configFile = new MavenSettingsConfigFile(ConfigFileType.GlobalMavenSettings, Mock(JobManagement))
        configFile.name = 'newCustom'
        configFile.comment = 'bbb'
        configFile.content = 'aaa'
        configFile.replaceAll = true
        configFile.credentialsMapping['foo'] = 'bar'

        when:
        Config config = createNewConfig(id, configFile)

        then:
        config instanceof GlobalMavenSettingsConfig
        config.id == id
        config.name == configFile.name
        config.comment == configFile.comment
        config.content == configFile.content
        ((GlobalMavenSettingsConfig) config).isReplaceAll
        ((GlobalMavenSettingsConfig) config).serverCredentialMappings.size() == 1
        ((GlobalMavenSettingsConfig) config).serverCredentialMappings[0].serverId == 'foo'
        ((GlobalMavenSettingsConfig) config).serverCredentialMappings[0].credentialsId == 'bar'
    }

    def 'create new script config'() {
        setup:
        String id = randomUUID()
        ConfigFile configFile = new ParametrizedConfigFile(ConfigFileType.ManagedScript, Mock(JobManagement))
        configFile.name = 'newCustom'
        configFile.comment = 'bbb'
        configFile.content = 'aaa'
        configFile.arguments = ['one', 'two']

        when:
        Config config = createNewConfig(id, configFile)

        then:
        config instanceof ScriptConfig
        config.id == id
        config.name == configFile.name
        config.comment == configFile.comment
        config.content == configFile.content
        ((ScriptConfig) config).args.size() == 2
        ((ScriptConfig) config).args[0].name == 'one'
        ((ScriptConfig) config).args[1].name == 'two'
    }
}
