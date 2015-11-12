package javaposse.jobdsl.plugin

import hudson.ExtensionList
import javaposse.jobdsl.dsl.ConfigFile
import javaposse.jobdsl.dsl.ConfigFileType
import javaposse.jobdsl.dsl.ParametrizedConfigFile
import jenkins.model.Jenkins
import org.jenkinsci.lib.configprovider.ConfigProvider
import org.jenkinsci.lib.configprovider.model.Config
import org.jenkinsci.plugins.configfiles.maven.GlobalMavenSettingsConfig
import org.jenkinsci.plugins.configfiles.maven.MavenSettingsConfig
import org.jenkinsci.plugins.managedscripts.ScriptConfig

class ConfigFileProviderHelper {
    private static final Map<ConfigFileType, String> CONFIG_PROVIDERS = [
            (ConfigFileType.Custom):
                  'org.jenkinsci.plugins.configfiles.custom.CustomConfig$CustomConfigProvider',
            (ConfigFileType.MavenSettings):
                  'org.jenkinsci.plugins.configfiles.maven.MavenSettingsConfig$MavenSettingsConfigProvider',
            (ConfigFileType.GlobalMavenSettings):
                  'org.jenkinsci.plugins.configfiles.maven.GlobalMavenSettingsConfig$GlobalMavenSettingsConfigProvider',
            (ConfigFileType.ManagedScript):
                  'org.jenkinsci.plugins.managedscripts.ScriptConfig$ScriptConfigProvider',
    ]

    static Config findConfig(ConfigProvider configProvider, String name) {
        configProvider.allConfigs.find { it.name == name }
    }

    static ConfigProvider findConfigProvider(ConfigFileType configFileType) {
        Jenkins jenkins = Jenkins.instance
        ExtensionList<ConfigProvider> extensionList = jenkins.getExtensionList(CONFIG_PROVIDERS[configFileType])
        extensionList.empty ? null : extensionList[0]
    }

    static Config createNewConfig(Config oldConfig, ConfigFile configFile) {
        switch (configFile.type) {
            case ConfigFileType.Custom:
                return new Config(oldConfig.id, configFile.name, configFile.comment, configFile.content)
            case ConfigFileType.MavenSettings:
                return new MavenSettingsConfig(
                        oldConfig.id,
                        configFile.name,
                        configFile.comment,
                        configFile.content,
                        null,
                        null
                )
            case ConfigFileType.GlobalMavenSettings:
                return new GlobalMavenSettingsConfig(
                        oldConfig.id,
                        configFile.name,
                        configFile.comment,
                        configFile.content,
                        null,
                        null
                )
            case ConfigFileType.ManagedScript:
                return new ScriptConfig(
                        oldConfig.id,
                        configFile.name,
                        configFile.comment,
                        configFile.content,
                        ((ParametrizedConfigFile) configFile).arguments.collect { new ScriptConfig.Arg(it) }
                )
            default:
                return null
        }
    }
}
