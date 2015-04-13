package javaposse.jobdsl.dsl

import hudson.util.VersionNumber

/**
 * Interface to manage jobs, which the DSL needs to do.
 */
interface JobManagement {
    /**
     * Gets (loads) the job configuration for the Jenkins job with the specified name.  If no name is supplied, an empty
     * configuration is returned.
     *
     * @param jobName the name of the job to look up
     * @return the job configuration as XML
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
     */
    boolean createOrUpdateConfig(String jobName, String config, boolean ignoreExisting)
            throws NameNotProvidedException, ConfigurationMissingException

    /**
     * Creates or updates the view config for the named Jenkins view with the config provided.
     *
     * @param viewName the name of the new / updated view
     * @param config the new / updated view config
     * @param ignoreExisting do not update existing view
     * @throws NameNotProvidedException if the viewName is null or blank
     * @throws ConfigurationMissingException if the config xml is null or blank
     */
    void createOrUpdateView(String viewName, String config, boolean ignoreExisting)
            throws NameNotProvidedException, ConfigurationMissingException

    /**
     * Creates or updates the managed config file.
     *
     * @param configFile the config file to create or update
     * @param ignoreExisting do not update existing config files
     * @return the id of the created or updated config file
     */
    String createOrUpdateConfigFile(ConfigFile configFile, boolean ignoreExisting)

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
     */
    void renameJobMatching(String previousNames, String destination) throws IOException

    /**
     * Queue a job to run. Useful for running jobs after they've been created.
     */
    void queueJob(String jobName) throws NameNotProvidedException

    InputStream streamFileInWorkspace(String filePath) throws IOException

    String readFileInWorkspace(String filePath) throws IOException

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
     */
    String getCredentialsId(String credentialsDescription)

    /**
     * Logs a deprecation warning for the calling method.
     */
    void logDeprecationWarning()

    /**
     * Logs a deprecation warning for the calling method with the given subject.
     */
    void logDeprecationWarning(String subject)

    /**
     * Logs a deprecation warning for the given subject and source position.
     */
    void logDeprecationWarning(String subject, String scriptName, int lineNumber)

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
     * Returns the currently installed version of the given plugin or <code>null<code> if the plugin is not installed.
     */
    VersionNumber getPluginVersion(String pluginShortName)

    /**
     * Return the hash of the vSphere cloud with the given name.
     * @param name name of the vSphere cloud
     * @return hash of the vSphere cloud or <code>null</code> if a cloud with the given name does not exist
     */
    Integer getVSphereCloudHash(String name)

    /**
     * Return the id of the config file with the given type and name.
     *
     * @param type type of the config file
     * @param name name of the config file
     * @return the config ID of the config file or <code>null</code> if no config file with the given type and name can
     *         be found
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
}
