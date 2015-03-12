package javaposse.jobdsl.dsl

import hudson.util.VersionNumber
import org.apache.commons.codec.digest.DigestUtils

/**
 * In-memory JobManagement for testing.
 */
class MemoryJobManagement extends AbstractJobManagement {
    final Map<String, String> availableConfigs = [:]
    final Map<String, String> savedConfigs = [:]
    final Map<String, String> savedViews = [:]
    final Set<ConfigFile> savedConfigFiles = []
    final Map<String, String> availableFiles = [:]

    final Map<String, String> parameters = [:]
    final List<String> scheduledJobs = []

    MemoryJobManagement() {
    }

    MemoryJobManagement(PrintStream out) {
        super(out)
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
        true
    }

    @Override
    void createOrUpdateView(String viewName, String config, boolean ignoreExisting) {
        validateUpdateArgs(viewName, config)

        savedViews[viewName] = config
    }

    @Override
    String createOrUpdateConfigFile(ConfigFile configFile, boolean ignoreExisting) {
        validateNameArg(configFile.name)
        savedConfigFiles << configFile
        createConfigFileId(configFile)
    }

    @Override
    void renameJobMatching(String previousNames, String destination) throws IOException {
    }

    @Override
    void queueJob(String jobName) throws NameNotProvidedException {
        scheduledJobs << jobName
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
        ConfigFile configFile = savedConfigFiles.find { it.type == type && it.name == name }
        configFile == null ? null : createConfigFileId(configFile)
    }

    @Override
    void createOrUpdatePromotionConfig(String jobName, String promotionName, String xml) {
    }

    private static String createConfigFileId(ConfigFile configFile) {
        DigestUtils.md5Hex(configFile.name)
    }
}
