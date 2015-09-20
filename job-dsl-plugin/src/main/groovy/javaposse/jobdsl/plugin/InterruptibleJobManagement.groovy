package javaposse.jobdsl.plugin

import groovy.transform.ThreadInterrupt
import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.ConfigFile
import javaposse.jobdsl.dsl.ConfigFileType
import javaposse.jobdsl.dsl.ConfigurationMissingException
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobConfigurationNotFoundException
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.NameNotProvidedException
import javaposse.jobdsl.dsl.UserContent
import javaposse.jobdsl.dsl.helpers.ExtensibleContext

/**
 * Checks the thread's {@link Thread#interrupted() interrupted} flag before delegating each call and throws an
 * {@link InterruptedException} if the thread has been interrupted.
 *
 * This class only exists because the {@link ThreadInterrupt} can not be applied to {@link JenkinsJobManagement}
 * since that is a Java class. It will be removed when {@link JenkinsJobManagement} has been ported to Groovy.
 */
@ThreadInterrupt
class InterruptibleJobManagement implements JobManagement {
    // can't use @Delegate because @ThreadInterrupt is evaluated before @Delegate
    private final JobManagement delegate

    InterruptibleJobManagement(JobManagement delegate) {
        this.delegate = delegate
    }

    @Override
    String getConfig(String jobName) throws JobConfigurationNotFoundException {
        delegate.getConfig(jobName)
    }

    @Override
    boolean createOrUpdateConfig(String jobName, String config, boolean ignoreExisting) throws NameNotProvidedException,
            ConfigurationMissingException {
        delegate.createOrUpdateConfig(jobName, config, ignoreExisting)
    }

    @Override
    boolean createOrUpdateConfig(Item item, boolean ignoreExisting) throws NameNotProvidedException {
        delegate.createOrUpdateConfig(item, ignoreExisting)
    }

    @Override
    void createOrUpdateView(String viewName, String config, boolean ignoreExisting) throws NameNotProvidedException,
            ConfigurationMissingException {
        delegate.createOrUpdateView(viewName, config, ignoreExisting)
    }

    @Override
    String createOrUpdateConfigFile(ConfigFile configFile, boolean ignoreExisting) {
        delegate.createOrUpdateConfigFile(configFile, ignoreExisting)
    }

    @Override
    void createOrUpdateUserContent(UserContent userContent, boolean ignoreExisting) {
        delegate.createOrUpdateUserContent(userContent, ignoreExisting)
    }

    @Override
    void renameJobMatching(String previousNames, String destination) throws IOException {
        delegate.renameJobMatching(previousNames, destination)
    }

    @Override
    void queueJob(String jobName) throws NameNotProvidedException {
        delegate.queueJob(jobName)
    }

    @Override
    InputStream streamFileInWorkspace(String filePath) throws IOException {
        delegate.streamFileInWorkspace(filePath)
    }

    @Override
    String readFileInWorkspace(String filePath) throws IOException {
        delegate.readFileInWorkspace(filePath)
    }

    @Override
    String readFileInWorkspace(String jobName, String filePath) throws IOException {
        delegate.readFileInWorkspace(jobName, filePath)
    }

    @Override
    PrintStream getOutputStream() {
        delegate.outputStream
    }

    @Override
    Map<String, String> getParameters() {
        delegate.parameters
    }

    @Override
    String getCredentialsId(String credentialsDescription) {
        delegate.getCredentialsId(credentialsDescription)
    }

    @Override
    void logDeprecationWarning() {
        delegate.logDeprecationWarning()
    }

    @Override
    void logDeprecationWarning(String subject) {
        delegate.logDeprecationWarning(subject)
    }

    @Override
    void logDeprecationWarning(String subject, String scriptName, int lineNumber) {
        delegate.logDeprecationWarning(subject, scriptName, lineNumber)
    }

    @Override
    void logPluginDeprecationWarning(String pluginShortName, String minimumVersion) {
        delegate.logPluginDeprecationWarning(pluginShortName, minimumVersion)
    }

    @Override
    void requirePlugin(String pluginShortName) {
        delegate.requirePlugin(pluginShortName)
    }

    @Override
    void requireMinimumPluginVersion(String pluginShortName, String version) {
        delegate.requireMinimumPluginVersion(pluginShortName, version)
    }

    @Override
    void requireMinimumCoreVersion(String version) {
        delegate.requireMinimumCoreVersion(version)
    }

    @Override
    VersionNumber getPluginVersion(String pluginShortName) {
        delegate.getPluginVersion(pluginShortName)
    }

    @Override
    VersionNumber getJenkinsVersion() {
        delegate.jenkinsVersion
    }

    @Override
    Integer getVSphereCloudHash(String name) {
        delegate.getVSphereCloudHash(name)
    }

    @Override
    String getConfigFileId(ConfigFileType type, String name) {
        delegate.getConfigFileId(type, name)
    }

    @Override
    Set<String> getPermissions(String authorizationMatrixPropertyClassName) {
        delegate.getPermissions(authorizationMatrixPropertyClassName)
    }

    @Override
    Node callExtension(String name, Item item, Class<? extends ExtensibleContext> contextType, Object... args) {
        delegate.callExtension(name, item, contextType, args)
    }
}
