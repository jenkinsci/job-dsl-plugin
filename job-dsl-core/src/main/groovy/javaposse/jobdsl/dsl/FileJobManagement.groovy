package javaposse.jobdsl.dsl

import hudson.util.VersionNumber

class FileJobManagement extends AbstractJobManagement {
    /**
     * Root of where to look for job config files
     */
    File root

    /**
     * Extension to append to job name when looking at the filesystem
     */
    String ext = '.xml'

    /**
     * map to store job parameters from System properties and
     * Environment variables.
     */
    protected Map params = [:]
    private final RenameHelper renameHelper = new RenameHelper() {
        @Override
        Set<String> allJobNames() {
            root.listFiles()*.name
        }

        @Override
        void renameJob(String from, String to) throws IOException {
            boolean renamed = new File(root, from).renameTo(new File(root, to))
            if (!renamed) {
                throw new IOException("Could not rename Job file ${from} to ${to}")
            }
        }
    }

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

    @Override
    String createOrUpdateConfigFile(ConfigFile configFile, boolean ignoreExisting) {
        throw new UnsupportedOperationException()
    }

    @Override
    void renameJobMatching(String previousNames, String destination) throws IOException {
        renameHelper.renameJobMatching(previousNames, destination)
    }

    @Override
    Map<String, String> getParameters() {
        params
    }

    @Override
    InputStream streamFileInWorkspace(String filePath) {
        new FileInputStream(new File(root, filePath))
    }

    @Override
    String readFileInWorkspace(String filePath) {
        new File(root, filePath).text
    }

    @Override
    void requireMinimumPluginVersion(String pluginShortName, String version) {
    }

    @Override
    String getCredentialsId(String credentialsDescription) {
        null
    }

    @Override
    VersionNumber getPluginVersion(String pluginShortName) {
        null
    }

    @Override
    Integer getVSphereCloudHash(String name) {
        null
    }

    @Override
    String getConfigFileId(ConfigFileType type, String name) {
        null
    }
}
