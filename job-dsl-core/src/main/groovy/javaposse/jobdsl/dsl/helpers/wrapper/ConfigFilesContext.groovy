package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ConfigFileType
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

class ConfigFilesContext extends AbstractContext {
    List<ConfigFileContext> configFiles = []

    ConfigFilesContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * An alias for {@link #custom(java.lang.String, groovy.lang.Closure) custom}.
     *
     * @see #custom(java.lang.String, groovy.lang.Closure)
     */
    void file(String fileIdOrName, @DslContext(ConfigFileContext) Closure configFileClosure = null) {
        custom(fileIdOrName, configFileClosure)
    }

    /**
     * Makes a custom file available to the build.
     *
     * @since 1.35
     */
    void custom(String fileIdOrName, @DslContext(ConfigFileContext) Closure configFileClosure = null) {
        configFile(fileIdOrName, ConfigFileType.Custom, configFileClosure)
    }

    /**
     * Makes a Maven settings file available to the build.
     *
     * @since 1.35
     */
    void mavenSettings(String fileIdOrName, @DslContext(ConfigFileContext) Closure configFileClosure = null) {
        configFile(fileIdOrName, ConfigFileType.MavenSettings, configFileClosure)
    }

    /**
     * Makes a global Maven settings file available to the build.
     *
     * @since 1.39
     */
    void globalMavenSettings(String fileIdOrName, @DslContext(ConfigFileContext) Closure configFileClosure = null) {
        configFile(fileIdOrName, ConfigFileType.GlobalMavenSettings, configFileClosure)
    }

    private void configFile(String fileIdOrName, ConfigFileType type, Closure configFileClosure) {
        String configFileId = jobManagement.getConfigFileId(type, fileIdOrName)

        ConfigFileContext configFileContext = new ConfigFileContext(configFileId ?: fileIdOrName)
        ContextHelper.executeInContext(configFileClosure, configFileContext)

        configFiles << configFileContext
    }
}
