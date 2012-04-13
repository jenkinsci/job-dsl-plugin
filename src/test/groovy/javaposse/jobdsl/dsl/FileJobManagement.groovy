package javaposse.jobdsl.dsl;

import java.io.File;
import java.io.IOException;

import javaposse.jobdsl.dsl.JobConfigurationNotFoundException

class FileJobManagement implements JobManagement {
    /**
     * Root of where to look for job config files
     */
    File root

    /**
     * Extension to append to job name when looking at the filesystem
     */
    String ext
    
    public FileJobManagement(File root, String ext = ".xml") {
        this.root = root
        this.ext = ext
    }

    String getConfig(String jobName) throws JobConfigurationNotFoundException {
        try {
            new File(root, jobName + ext).getText()
        } catch (IOException ioex) {
            throw new JobConfigurationNotFoundException(jobName)
        } 
    }

    void createOrUpdateConfig(String jobName, String config) {
        new File(jobName).write(config)
    }
}

