package javaposse.jobdsl.dsl.helpers.wrapper

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.ConfigFileType
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.JobManagement

class ConfigFilesContext implements Context {
    private final JobManagement jobManagement

    List<ConfigFileContext> configFiles = []

    ConfigFilesContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    void file(String fileName, Closure configFileClosure = null) {
        String configFileId = jobManagement.getConfigFileId(ConfigFileType.Custom, fileName)
        Preconditions.checkNotNull(configFileId, "Custom config file with name '${fileName}' not found")

        ConfigFileContext configFileContext = new ConfigFileContext(configFileId)
        ContextHelper.executeInContext(configFileClosure, configFileContext)

        configFiles << configFileContext
    }
}
