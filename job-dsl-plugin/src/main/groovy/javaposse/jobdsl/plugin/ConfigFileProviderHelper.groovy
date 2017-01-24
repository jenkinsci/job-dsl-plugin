package javaposse.jobdsl.plugin

import hudson.ExtensionList
import javaposse.jobdsl.dsl.ConfigFile
import javaposse.jobdsl.dsl.ConfigFileType
import javaposse.jobdsl.dsl.MavenSettingsConfigFile
import javaposse.jobdsl.dsl.ParametrizedConfigFile
import jenkins.model.Jenkins
import org.jenkinsci.lib.configprovider.ConfigProvider
import org.jenkinsci.lib.configprovider.model.Config
import org.jenkinsci.plugins.configfiles.ConfigFiles
import org.jenkinsci.plugins.configfiles.custom.CustomConfig
import org.jenkinsci.plugins.configfiles.maven.GlobalMavenSettingsConfig
import org.jenkinsci.plugins.configfiles.maven.MavenSettingsConfig
import org.jenkinsci.plugins.configfiles.maven.security.ServerCredentialMapping
import org.jenkinsci.plugins.managedscripts.ScriptConfig

@Deprecated
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
        ConfigFiles.getConfigsInContext(Jenkins.instance, configProvider.class).find { it.name == name }
    }

    static ConfigProvider findConfigProvider(ConfigFileType configFileType) {
        Jenkins jenkins = Jenkins.instance
        ExtensionList<ConfigProvider> extensionList = jenkins.getExtensionList(CONFIG_PROVIDERS[configFileType])
        extensionList.empty ? null : extensionList[0]
    }

    @Deprecated
    static Config createNewConfig(Config oldConfig, ConfigFile configFile) {
        createNewConfig(oldConfig.id, configFile)
    }

    static Config createNewConfig(String id, ConfigFile configFile) {
        switch (configFile.type) {
            case ConfigFileType.Custom:
                return new CustomConfig(id, configFile.name, configFile.comment, configFile.content)
            case ConfigFileType.MavenSettings:
                return new MavenSettingsConfig(
                        id,
                        configFile.name,
                        configFile.comment,
                        configFile.content,
                        ((MavenSettingsConfigFile) configFile).replaceAll,
                        toServerCredentialMapping(((MavenSettingsConfigFile) configFile).credentialsMapping)
                )
            case ConfigFileType.GlobalMavenSettings:
                return new GlobalMavenSettingsConfig(
                        id,
                        configFile.name,
                        configFile.comment,
                        configFile.content,
                        ((MavenSettingsConfigFile) configFile).replaceAll,
                        toServerCredentialMapping(((MavenSettingsConfigFile) configFile).credentialsMapping)
                )
            case ConfigFileType.ManagedScript:
                return new ScriptConfig(
                        id,
                        configFile.name,
                        configFile.comment,
                        configFile.content,
                        ((ParametrizedConfigFile) configFile).arguments.collect { new ScriptConfig.Arg(it) }
                )
            default:
                return null
        }
    }

    private static List<ServerCredentialMapping> toServerCredentialMapping(Map<String, String> mapping) {
        mapping.collect { new ServerCredentialMapping(it.key, it.value) }
    }
}
