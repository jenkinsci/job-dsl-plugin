package javaposse.jobdsl.dsl

import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.helpers.ExtensibleContext

/**
 * Testing JobManagement which will deal with a single template and single saved job. Useful for testing
 * since it can be prodded with the expected value.
 */
class StringJobManagement extends AbstractJobManagement {
    Map<String, String> availableConfigs = [:]
    Map<String, String> savedConfigs = [:]
    Map<String, String> savedViews = [:]
    Map<String, String> availableFiles = [:]

    Map<String, String> params = [:]
    List<String> jobScheduled = []

    StringJobManagement(PrintStream out) {
        super(out)
    }

    StringJobManagement() {
    }

    void addConfig(String jobName, String xml) {
        availableConfigs[jobName] = xml
    }

    String getConfig(String jobName) {
        if (availableConfigs.containsKey(jobName)) {
            return availableConfigs[jobName]
        } else {
            throw new JobConfigurationNotFoundException("No config found for ${jobName}")
        }
    }

    @Override
    boolean createOrUpdateConfig(String jobName, String config, boolean ignoreExisting)
            throws NameNotProvidedException, ConfigurationMissingException {
        validateUpdateArgs(jobName, config)

        savedConfigs[jobName] = config
        false
    }

    @Override
    void createOrUpdateView(String viewName, String config, boolean ignoreExisting) {
        validateUpdateArgs(viewName, config)

        savedViews[viewName] = config
    }

    @Override
    String createOrUpdateConfigFile(ConfigFile configFile, boolean ignoreExisting) {
        validateNameArg(configFile.name)
        UUID.randomUUID().toString()
    }

    @Override
    Map<String, String> getParameters() {
        params
    }

    @Override
    void queueJob(String jobName) throws NameNotProvidedException {
        jobScheduled << jobName
    }

    @Override
    InputStream streamFileInWorkspace(String filePath) {
        new ByteArrayInputStream(readFileInWorkspace(filePath).bytes)
    }

    @Override
    String readFileInWorkspace(String filePath) {
        String body = availableFiles[filePath]
        if (body == null) {
            throw new FileNotFoundException(filePath)
        }
        body
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

    @Override
    Node callExtension(Job job, String name, Class<? extends ExtensibleContext> contextType, Object... args) {
        null
    }
}

