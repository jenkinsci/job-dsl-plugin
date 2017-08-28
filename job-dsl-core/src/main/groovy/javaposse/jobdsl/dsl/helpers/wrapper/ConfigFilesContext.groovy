package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.AbstractContext
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
    void file(String fileId, @DslContext(ConfigFileContext) Closure configFileClosure = null) {
        custom(fileId, configFileClosure)
    }

    /**
     * Makes a custom file available to the build.
     *
     * @since 1.35
     */
    void custom(String fileId, @DslContext(ConfigFileContext) Closure configFileClosure = null) {
        configFile(fileId, configFileClosure)
    }

    /**
     * Makes a Maven settings file available to the build.
     *
     * @since 1.35
     */
    void mavenSettings(String fileId, @DslContext(ConfigFileContext) Closure configFileClosure = null) {
        configFile(fileId, configFileClosure)
    }

    /**
     * Makes a global Maven settings file available to the build.
     *
     * @since 1.39
     */
    void globalMavenSettings(String fileId, @DslContext(ConfigFileContext) Closure configFileClosure = null) {
        configFile(fileId, configFileClosure)
    }

    private void configFile(String fileId, Closure configFileClosure) {
        ConfigFileContext configFileContext = new ConfigFileContext(fileId)
        ContextHelper.executeInContext(configFileClosure, configFileContext)

        configFiles << configFileContext
    }
}
