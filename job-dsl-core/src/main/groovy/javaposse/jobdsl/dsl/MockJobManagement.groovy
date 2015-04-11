package javaposse.jobdsl.dsl

import hudson.util.VersionNumber

/**
 * Abstract base class for all non-Jenkins implementations of {@link JobManagement}.
 */
abstract class MockJobManagement extends AbstractJobManagement {
    final Map<String, String> parameters = [:]

    protected MockJobManagement() {
        super(System.out)
    }

    protected MockJobManagement(PrintStream out) {
        super(out)
    }

    @Override
    String createOrUpdateConfigFile(ConfigFile configFile, boolean ignoreExisting) {
        throw new UnsupportedOperationException()
    }

    @Override
    void renameJobMatching(String previousNames, String destination) throws IOException {
    }

    @Override
    void queueJob(String jobName) throws NameNotProvidedException {
        validateNameArg(jobName)
    }

    @Override
    String readFileInWorkspace(String jobName, String filePath) throws IOException {
        throw new UnsupportedOperationException()
    }

    @Override
    void requirePlugin(String pluginShortName) {
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
    Set<String> getPermissions(String authorizationMatrixPropertyClassName) {
        []
    }
}
