package javaposse.jobdsl.dsl

import org.apache.commons.codec.digest.DigestUtils

/**
 * In-memory JobManagement for testing.
 */
class MemoryJobManagement extends MockJobManagement {
    final Map<String, String> availableConfigs = [:]
    final Map<String, String> savedConfigs = [:]
    final Map<String, String> savedViews = [:]
    @Deprecated
    final Set<ConfigFile> savedConfigFiles = []
    final Map<String, String> availableFiles = [:]

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
    boolean createOrUpdateConfig(Item item, boolean ignoreExisting) throws NameNotProvidedException {
        String jobName = item.name
        String config = item.xml

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
    @Deprecated
    String createOrUpdateConfigFile(ConfigFile configFile, boolean ignoreExisting) {
        validateNameArg(configFile.name)
        savedConfigFiles << configFile
        createConfigFileId(configFile)
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
    @Deprecated
    String getConfigFileId(ConfigFileType type, String name) {
        ConfigFile configFile = savedConfigFiles.find { it.type == type && it.name == name }
        configFile == null ? null : createConfigFileId(configFile)
    }

    private static String createConfigFileId(ConfigFile configFile) {
        DigestUtils.md5Hex(configFile.name)
    }
}
