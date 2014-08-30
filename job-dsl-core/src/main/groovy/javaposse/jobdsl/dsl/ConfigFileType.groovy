package javaposse.jobdsl.dsl

enum ConfigFileType {
    Custom(ConfigFile),
    MavenSettings(ConfigFile)

    final Class<? extends ConfigFile> configFileClass

    ConfigFileType(Class<? extends ConfigFile> configFileClass) {
        this.configFileClass = configFileClass
    }
}
