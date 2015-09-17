package javaposse.jobdsl.plugin

import hudson.ExtensionList
import javaposse.jobdsl.dsl.ConfigFile
import javaposse.jobdsl.dsl.ConfigFileType
import jenkins.model.Jenkins
import org.jenkinsci.lib.configprovider.ConfigProvider
import org.jenkinsci.lib.configprovider.model.Config
import org.jenkinsci.plugins.configfiles.custom.CustomConfig
import org.jenkinsci.plugins.configfiles.maven.GlobalMavenSettingsConfig
import org.jenkinsci.plugins.configfiles.maven.MavenSettingsConfig

class ConfigFileProviderHelper {
    private static final Map<ConfigFileType, Class<? extends ConfigProvider>> CONFIG_PROVIDERS = [
            (ConfigFileType.Custom)             : CustomConfig.CustomConfigProvider,
            (ConfigFileType.MavenSettings)      : MavenSettingsConfig.MavenSettingsConfigProvider,
            (ConfigFileType.GlobalMavenSettings): GlobalMavenSettingsConfig.GlobalMavenSettingsConfigProvider,
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
            default:
                return null
        }
    }
}
