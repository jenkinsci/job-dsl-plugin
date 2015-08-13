package javaposse.jobdsl.dsl

import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.helpers.ExtensibleContext

/**
 * Interface to manage jobs, which the DSL needs to do.
 */
interface JobManagement {
    /**
     * Gets (loads) the job configuration for the Jenkins job with the specified name.  If no name is supplied, an empty
     * configuration is returned.
     *
     * @param jobName the name of the job to look up
     * @return the job configuration as XML, never {@code null}
     * @throws JobConfigurationNotFoundException
     */
    String getConfig(String jobName) throws JobConfigurationNotFoundException

    /**
     * Creates or updates the job config for the named Jenkins job with the config provided.
     *
     * @param jobName the name of the new / updated job
     * @param config the new / updated job config
     * @param ignoreExisting do not update existing jobs
     * @throws NameNotProvidedException if the jobName is null or blank
     * @throws ConfigurationMissingException if the config xml is null or blank
     * @deprecated use {@link #createOrUpdateConfig(javaposse.jobdsl.dsl.Item, boolean)} instead
     */
    @Deprecated
    boolean createOrUpdateConfig(String jobName, String config, boolean ignoreExisting)
            throws NameNotProvidedException, ConfigurationMissingException

    /**
     * Creates or updates the Jenkins job or folder with the provided configuration.
     *
     * @param item the item to create or update
     * @param ignoreExisting do not update existing jobs
     * @throws NameNotProvidedException if the jobName is null or blank
     * @throws ConfigurationMissingException if the config xml is null or blank
     * @since 1.33
     */
    boolean createOrUpdateConfig(Item item, boolean ignoreExisting) throws NameNotProvidedException

    /**
     * Creates or updates the view config for the named Jenkins view with the config provided.
     *
     * @param viewName the name of the new / updated view
     * @param config the new / updated view config
     * @param ignoreExisting do not update existing view
     * @throws NameNotProvidedException if the viewName is null or blank
     * @throws ConfigurationMissingException if the config xml is null or blank
     * @since 1.21
     */
    void createOrUpdateView(String viewName, String config, boolean ignoreExisting)
            throws NameNotProvidedException, ConfigurationMissingException

    /**
     * Creates or updates the managed config file.
     *
     * @param configFile the config file to create or update
     * @param ignoreExisting do not update existing config files
     * @return the id of the created or updated config file
     * @since 1.25
     */
    String createOrUpdateConfigFile(ConfigFile configFile, boolean ignoreExisting)

    /**
     * Uploads the given <a href="https://wiki.jenkins-ci.org/display/JENKINS/User+Content">user content</a>.
     *
     * @param userContent the user content to be uploaded
     * @param ignoreExisting do not update existing user content
     * @since 1.33
     */
    void createOrUpdateUserContent(UserContent userContent, boolean ignoreExisting)

    /**
     * Renames a Job with name matching previousNames to the destination name.
     *
     * If destination matches the currently found job name, then nothing happens.
     *
     * @param previousNames a regular Expression how the job was called earlier.
     *        Needs to match the full Name (with path) of the job.
     * @param destination the new name of the job
     * @throws IOException if renaming failed
     * @throws IllegalArgumentException if there are multiple jobs matching the previousNames
     * @since 1.29
     */
    void renameJobMatching(String previousNames, String destination) throws IOException

    /**
     * Queue a job to run. Useful for running jobs after they've been created.
     *
     * @since 1.16
     */
    void queueJob(String jobName) throws NameNotProvidedException

    /**
     * Streams a file from the workspace of the seed job.
     *
     * @param filePath path of the file relative to the workspace root
     * @return content of the file
     * @throws IOException if the file could not be read
     * @since 1.16
     */
    InputStream streamFileInWorkspace(String filePath) throws IOException

    /**
     * Streams a file from the workspace of the seed job.
     *
     * @param filePath path of the file relative to the workspace root
     * @return content of the file
     * @throws IOException if the file could not be read
     * @since 1.16
     */
    String readFileInWorkspace(String filePath) throws IOException

    /**
     * Reads a file from the workspace of a job.
     *
     * @param jobName the job from which to read a file
     * @param filePath path of the file relative to the workspace root
     * @return content of the file
     * @throws IOException if the file could not be read
     * @since 1.25
     */
    String readFileInWorkspace(String jobName, String filePath) throws IOException

    /**
     * Stream to write to, for stdout.
     */
    PrintStream getOutputStream()

    /**
     * Map of variables that should be available to the script.
     */
    Map<String, String> getParameters()

    /**
     * Returns the id of a Credentials object.
     *
     * @param credentialsDescription the description of the credentials to lookup
     * @return id of Credentials or <code>null</code> if no credentials could be found
     * @since 1.17
     */
    @Deprecated
    String getCredentialsId(String credentialsDescription)

    /**
     * Logs a deprecation warning for the calling method.
     *
     * @since 1.23
     */
    void logDeprecationWarning()

    /**
     * Logs a deprecation warning for the calling method with the given subject.
     *
     * @since 1.30
     */
    void logDeprecationWarning(String subject)

    /**
     * Logs a deprecation warning for the given subject and source position.
     * @since 1.29
     */
    void logDeprecationWarning(String subject, String scriptName, int lineNumber)

    /**
     * Logs a deprecation warning for the given plugin if the installed plugin version is older then the given version.
     * @since 1.36
     */
    void logPluginDeprecationWarning(String pluginShortName, String minimumVersion)

    /**
     * Logs a warning and sets the build status to unstable if given plugin is not installed.
     *
     * @since 1.31
     */
    void requirePlugin(String pluginShortName)

    /**
     * Logs a warning and sets the build status to unstable if the installed version of the given plugin is older than
     * the given version.
     */
    void requireMinimumPluginVersion(String pluginShortName, String version)

    /**
     * Logs a warning and sets the build status to unstable if the version of Jenkins core is older than the given
     * version.
     *
     * @since 1.33
     */
    void requireMinimumCoreVersion(String version)

    /**
     * Returns the currently installed version of the given plugin or <code>null<code> if the plugin is not installed.
     */
    VersionNumber getPluginVersion(String pluginShortName)

    /**
     * Returns the version of Jenkins.
     *
     * @since 1.33
     */
    VersionNumber getJenkinsVersion()

    /**
     * Return the hash of the vSphere cloud with the given name.
     * @param name name of the vSphere cloud
     * @return hash of the vSphere cloud or <code>null</code> if a cloud with the given name does not exist
     * @since 1.25
     */
    Integer getVSphereCloudHash(String name)

    /**
     * Return the id of the config file with the given type and name.
     *
     * @param type type of the config file
     * @param name name of the config file
     * @return the config ID of the config file or <code>null</code> if no config file with the given type and name can
     *         be found
     * @since 1.25
     */
    String getConfigFileId(ConfigFileType type, String name)

    /**
     * Return all applicable permissions for the given authorization matrix property.
     *
     * @param authorizationMatrixPropertyClassName class name of the authorization matrix property
     * @return all applicable permissions for the given authorization matrix property
     * @since 1.31
     */
    Set<String> getPermissions(String authorizationMatrixPropertyClassName)

    /**
     * Tries to find and call an DSL extension method for the given context and returns a node object which will be
     * appended to the given context.
     *
     * @param name name of the DSL extension method to be called
     * @param item the {@link Item} which is being built
     * @param contextType type of the context which is extended by the method to be called
     * @param args arguments for the method to be called
     * @return a node to be appended to the given context or <code>null</code> if no extension has been found
     * @since 1.33
     */
    Node callExtension(String name, Item item, Class<? extends ExtensibleContext> contextType, Object... args)
}
