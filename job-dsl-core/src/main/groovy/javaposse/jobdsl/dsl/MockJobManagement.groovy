package javaposse.jobdsl.dsl

/**
 * Abstract base class for all non-Jenkins implementations of {@link JobManagement}.
 */
abstract class MockJobManagement extends AbstractJobManagement {
    final Map<String, Object> parameters = [:]
    final Map<String, List<String>> permissions = [
            'hudson.security.AuthorizationMatrixProperty': [
                    'hudson.model.Item.Delete',
                    'hudson.model.Item.Configure',
                    'hudson.model.Item.Read',
                    'hudson.model.Item.Discover',
                    'hudson.model.Item.Build',
                    'hudson.model.Item.Workspace',
                    'hudson.model.Item.Cancel',
                    'hudson.model.Item.Release',
                    'hudson.model.Item.ExtendedRead',
                    'hudson.model.Run.Delete',
                    'hudson.model.Run.Update',
                    'hudson.scm.SCM.Tag'
            ]
    ]

    protected MockJobManagement() {
        super(System.out)
    }

    protected MockJobManagement(PrintStream out) {
        super(out)
    }

    @Override
    @Deprecated
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
    void logPluginDeprecationWarning(String pluginShortName, String minimumVersion) {
    }

    @Override
    void requirePlugin(String pluginShortName, boolean failIfMissing) {
    }

    @Override
    void requireMinimumPluginVersion(String pluginShortName, String version, boolean failIfMissing) {
    }

    @Override
    void requireMinimumCoreVersion(String version) {
    }

    @Override
    boolean isMinimumPluginVersionInstalled(String pluginShortName, String version) {
        false
    }

    @Override
    boolean isMinimumCoreVersion(String version) {
        false
    }

    @Override
    Integer getVSphereCloudHash(String name) {
        null
    }

    @Override
    @Deprecated
    String getConfigFileId(ConfigFileType type, String name) {
        null
    }

    @Override
    Set<String> getPermissions(String authorizationMatrixPropertyClassName) {
        permissions[authorizationMatrixPropertyClassName] ?: []
    }

    @Override
    Node callExtension(String name, Item item, Class<? extends ExtensibleContext> contextType,
                       Object... args) {
        null
    }

    @Override
    void createOrUpdateUserContent(UserContent userContent, boolean ignoreExisting) {
    }
}
