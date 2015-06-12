package javaposse.jobdsl.dsl.helpers.wrapper

import com.google.common.base.Preconditions
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

    void file(String fileName, @DslContext(ConfigFileContext) Closure configFileClosure = null) {
        custom(fileName, configFileClosure)
    }

    /**
     * @since 1.35
     */
    void custom(String fileName, @DslContext(ConfigFileContext) Closure configFileClosure = null) {
        configFile(fileName, ConfigFileType.Custom, configFileClosure)
    }

    /**
     * @since 1.35
     */
    void mavenSettings(String fileName, @DslContext(ConfigFileContext) Closure configFileClosure = null) {
        configFile(fileName, ConfigFileType.MavenSettings, configFileClosure)
    }

    private void configFile(String fileName, ConfigFileType type, Closure configFileClosure) {
        String configFileId = jobManagement.getConfigFileId(type, fileName)
        Preconditions.checkNotNull(configFileId, "${type} config file with name '${fileName}' not found")

        ConfigFileContext configFileContext = new ConfigFileContext(configFileId)
        ContextHelper.executeInContext(configFileClosure, configFileContext)

        configFiles << configFileContext
    }
}
