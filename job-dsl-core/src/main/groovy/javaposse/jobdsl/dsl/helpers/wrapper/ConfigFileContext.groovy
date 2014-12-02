package javaposse.jobdsl.dsl.helpers.wrapper

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.ConfigFileType
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.Context

/**
 * @author Johannes Graf - graf.johannes@gmail.com
 */
class ConfigFileContext implements Context {

    private final JobManagement jobManagement

    String fileId
    String targetLocation
    String variable

    ConfigFileContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    def fileName(String fileName) {
        String fileId = jobManagement.getConfigFileId(ConfigFileType.Custom, fileName)
        Preconditions.checkNotNull fileId, "Custom config file with name '${fileName}' not found"

        this.fileId = fileId
    }

    def targetLocation(String targetLocation) {
        this.targetLocation = targetLocation
    }

    def variable(String variable) {
        this.variable = variable
    }
}
