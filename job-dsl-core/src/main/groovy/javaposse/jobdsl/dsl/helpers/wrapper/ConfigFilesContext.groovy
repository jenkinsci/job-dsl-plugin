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
        String configFileId = jobManagement.getConfigFileId(ConfigFileType.Custom, fileName)
        Preconditions.checkNotNull(configFileId, "Custom config file with name '${fileName}' not found")

        ConfigFileContext configFileContext = new ConfigFileContext(configFileId)
        ContextHelper.executeInContext(configFileClosure, configFileContext)

        configFiles << configFileContext
    }
}
