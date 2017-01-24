package javaposse.jobdsl.dsl

@Deprecated
class MavenSettingsConfigFile extends ConfigFile {
    Boolean replaceAll
    Map<String, String> credentialsMapping = [:]

    MavenSettingsConfigFile(ConfigFileType type, JobManagement jobManagement) {
        super(type, jobManagement)
    }

    /**
     * If set, replaces all server credentials in this Maven {@code settings.xml} file with the ones configured here.
     * Defaults to {@code true}.
     *
     * @since 1.52
     */
    void replaceAll(boolean replaceAll = true) {
        this.replaceAll = replaceAll
    }

    /**
     * Adds server credentials for this {@code settings.xml}. Can be called multiple times to add more server
     * credentials.
     *
     * If at least one credential mapping is added to this {@code settings.xml}, then all existing server definitions
     * in the {@code settings.xml} will be removed and replaced by the ones defined here.
     *
     * @since 1.52
     */
    void serverCredentials(String serverId, String credentials) {
        this.credentialsMapping[serverId] = credentials
    }
}
