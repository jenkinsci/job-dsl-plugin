package javaposse.jobdsl.dsl;


class FileJobManagement extends AbstractJobManagement {
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

        if (jobName.isEmpty()) return '''
<project>
  <actions/>
  <description/>
  <keepDependencies>false</keepDependencies>
  <properties/>
</project>'''

        try {
            new File(root, jobName + ext).getText()
        } catch (IOException ioex) {
            throw new JobConfigurationNotFoundException(jobName)
        }
    }

    boolean createOrUpdateConfig(String jobName, String config) throws JobNameNotProvidedException, JobConfigurationMissingException {
        validateUpdateArgs(jobName, config);

        new File(jobName).write(config)
        return true
    }
}

