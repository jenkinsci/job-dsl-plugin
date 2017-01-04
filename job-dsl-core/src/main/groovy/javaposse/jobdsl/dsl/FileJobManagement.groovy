package javaposse.jobdsl.dsl

class FileJobManagement extends MockJobManagement {
    /**
     * Root of where to look for and write out job and view config files
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

    @Override
    boolean createOrUpdateConfig(Item item, boolean ignoreExisting) throws NameNotProvidedException {
        String jobName = item.name
        String config = item.xml

        validateNameArg(jobName)

        File file = new File(root, jobName + ext)
        file.parentFile.mkdirs()
        file.write(config)
        true
    }

    @Override
    void createOrUpdateView(String viewName, String config, boolean ignoreExisting) {
        validateUpdateArgs(viewName, config)

        File file = new File(root, viewName + ext)
        file.parentFile.mkdirs()
        file.write(config)
    }

    InputStream streamFileInWorkspace(String filePath) {
        new FileInputStream(new File(root, filePath))
    }

    @Override
    String readFileInWorkspace(String filePath) {
        new File(root, filePath).text
    }
}
