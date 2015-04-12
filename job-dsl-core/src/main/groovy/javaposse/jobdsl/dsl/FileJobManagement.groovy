package javaposse.jobdsl.dsl

class FileJobManagement extends MockJobManagement {
    /**
     * Root of where to look for job config files
     */
    File root

    /**
     * Extension to append to job name when looking at the filesystem
     */
    String ext = '.xml'

    FileJobManagement(File root) {
        this.root = root
    }

    String getConfig(String jobName) throws JobConfigurationNotFoundException {
        if (jobName.isEmpty()) {
            return '''
<project>
  <actions/>
  <description/>
  <keepDependencies>false</keepDependencies>
  <properties/>
</project>'''
        }

        try {
            new File(root, jobName + ext).text
        } catch (IOException ignored) {
            throw new JobConfigurationNotFoundException(jobName)
        }
    }

    boolean createOrUpdateConfig(String jobName, String config, boolean ignoreExisting)
        throws NameNotProvidedException, ConfigurationMissingException {
        validateUpdateArgs(jobName, config)

        new File(jobName + ext).write(config)
        true
    }

    @Override
    void createOrUpdateView(String viewName, String config, boolean ignoreExisting) {
        validateUpdateArgs(viewName, config)

        new File(viewName + ext).write(config)
    }

    InputStream streamFileInWorkspace(String filePath) {
        new FileInputStream(new File(root, filePath))
    }

    @Override
    String readFileInWorkspace(String filePath) {
        new File(root, filePath).text
    }
}
