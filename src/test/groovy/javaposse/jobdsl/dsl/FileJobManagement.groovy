package javaposse.jobdsl.dsl;

import java.io.IOException;

import javaposse.jobdsl.dsl.JobManagement;

class FileJobManagement implements JobManagement {
    String getConfig(String jobName) throws IOException {
        new File(templateName).getText()
    }

    void createOrUpdateConfig(String jobName, String config) {
        new File(jobName).write(config)
    }
}

