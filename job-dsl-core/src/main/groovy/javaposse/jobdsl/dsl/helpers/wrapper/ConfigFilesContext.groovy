package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ConfigFileType
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions

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
    void file(String fileName, @DslContext(ConfigFileContext) Closure configFileClosure = null) {
        custom(fileName, configFileClosure)
    }

    /**
     * Makes a custom file available to the build.
     *
     * @since 1.35
     */
    void custom(String fileName, @DslContext(ConfigFileContext) Closure configFileClosure = null) {
        configFile(fileName, ConfigFileType.Custom, configFileClosure)
    }

    /**
     * Makes a Maven settings file available to the build.
     *
     * @since 1.35
     */
    void mavenSettings(String fileName, @DslContext(ConfigFileContext) Closure configFileClosure = null) {
        configFile(fileName, ConfigFileType.MavenSettings, configFileClosure)
    }

    /**
     * Makes a global Maven settings file available to the build.
     *
     * @since 1.39
     */
    void globalMavenSettings(String fileName, @DslContext(ConfigFileContext) Closure configFileClosure = null) {
        configFile(fileName, ConfigFileType.GlobalMavenSettings, configFileClosure)
    }

    private void configFile(String fileName, ConfigFileType type, Closure configFileClosure) {
        String configFileId = jobManagement.getConfigFileId(type, fileName)
        Preconditions.checkNotNull(configFileId, "${type} config file with name '${fileName}' not found")

        ConfigFileContext configFileContext = new ConfigFileContext(configFileId)
        ContextHelper.executeInContext(configFileClosure, configFileContext)

        configFiles << configFileContext
    }
}
