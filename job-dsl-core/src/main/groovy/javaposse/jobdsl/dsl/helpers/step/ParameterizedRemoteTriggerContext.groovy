package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class ParameterizedRemoteTriggerContext extends AbstractContext {
    Map<String, String> parameters = [:]
    boolean shouldNotFailBuild = false
    int pollInterval = 10
    boolean preventRemoteBuildQueue = false
    boolean blockBuildUntilComplete = false
    String token
    String credentialsIds
    String remoteJenkinsUrl
    boolean abortTriggeredJob = false
    int maxConn = 1
    boolean enhancedLogging = false
    boolean useCrumbCache = true
    boolean useJobInfoCache = true
    boolean disabled = false
    boolean overrideTrustAllCertificates = false
    boolean trustAllCertificates = false
    String parameterFile

    ParameterizedRemoteTriggerContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Adds a parameter value for the remote job. Can be called multiple times to add more parameter values.
     */
    void parameter(String name, String value) {
        this.parameters[name] = value
    }

    /**
     * Adds parameter values for the remote job. Can be called multiple times to add more parameter values.
     */
    void parameters(Map<String, String> parameters) {
        this.parameters.putAll(parameters)
    }

    /**
     * If set, a failure of the remote job will not fail this job. Defaults to {@code false}.
     */
    void shouldNotFailBuild(boolean shouldNotFailBuild = true) {
        this.shouldNotFailBuild = shouldNotFailBuild
    }

    /**
     * Sets the poll interval in seconds. Defaults to 10 seconds.
     *
     * @since 1.29
     */
    void pollInterval(int pollInterval) {
        this.pollInterval = pollInterval
    }

    /**
     * If set, waits to trigger remote builds until no other builds are running. Defaults to {@code false}.
     *
     * @since 1.29
     */
    boolean preventRemoteBuildQueue(boolean preventRemoteBuildQueue = true) {
        this.preventRemoteBuildQueue = preventRemoteBuildQueue
    }

    /**
     * Blocks the job until the remote triggered projects finish their builds. Defaults to {@code false}.
     *
     * @since 1.29
     */
    boolean blockBuildUntilComplete(boolean blockUntilBuildComplete = true) {
        this.blockBuildUntilComplete = blockUntilBuildComplete
    }

    /**
     * Sets a security token which is defined on the job of the remote Jenkins host.
     *
     * For security reasons, do not use a hard-coded token. See
     * <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/Handling-Credentials">Handling Credentials</a> for
     * details about handling credentials in DSL scripts.
     *
     * @since 1.38
     */
    void token(String token) {
        this.token = token
    }

    /**
     * Specifies credentials for authentication with the remote Jenkins host.
     *
     * @since 1.62
     */
    void overrideCredentials(String credentialsIds) {
        this.credentialsIds = credentialsIds
    }

    /**
     * If set, overrides the Remote Jenkins URL for this Job alone.
     *
     * @since 1.81
     */
    void remoteJenkinsUrl(String remoteJenkinsUrl) {
        this.remoteJenkinsUrl = remoteJenkinsUrl
    }

    /**
     * Abort remote job if current job was aborted. Defaults to {@code false}.
     *
     * @since 1.81
     */
    void abortTriggeredJob(boolean abortTriggeredJob = true) {
        this.abortTriggeredJob = abortTriggeredJob
    }

    /**
     * Set the max concurrent connections to the remote host, default is 1, max is 5.
     * It'll be 5 even if you set it greater than 5.
     *
     * Note: Set this field with caution, too many concurrent requests will not only fail your local jobs,
     * but also block the remote server.
     *
     * @since 1.81
     */
    void maxConn(int maxConn) {
        this.maxConn = maxConn
    }

    /**
     * If set, the console output of the remote job is also logged. Defaults to {@code false}.
     *
     * @since 1.81
     */
    void enhancedLogging(boolean enhancedLogging = true) {
        this.enhancedLogging = enhancedLogging
    }

    /**
     * Enable cache of the crumb of remote server. Defaults to {@code true}.
     *
     * It'll be more efficient for the local job execution & more stable for remote server when massive concurrent
     * jobs are triggered.
     * This cache will be cleared every 10 minutes.
     *
     * @since 1.81
     */
    void useCrumbCache(boolean useCrumbCache = true) {
        this.useCrumbCache = useCrumbCache
    }

    /**
     * Enable cache of the job info of remote server. Defaults to {@code true}.
     *
     * It'll be more efficient for the local job execution & more stable for remote server when massive concurrent
     * jobs are triggered.
     * This cache will be cleared every 10 minutes.
     *
     * @since 1.81
     */
    void useJobInfoCache(boolean useJobInfoCache = true) {
        this.useJobInfoCache = useJobInfoCache
    }

    /**
     * Disable the job step instead of removing it from job configuration. Defaults to {@code false}.
     *
     * @since 1.81
     */
    void disabled(boolean disabled = true) {
        this.disabled = disabled
    }

    /**
     * Specify, whether the {@link #trustAllCertificates()} option will be taken into account.
     * Defaults to {@code false}.
     *
     * @since 1.81
     */
    void overrideTrustAllCertificates(boolean overrideTrustAllCertificates = true) {
        this.overrideTrustAllCertificates = overrideTrustAllCertificates
    }

    /**
     * Override/rewrite the 'Trust all certificate'-setting for this Job alone. Defaults to {@code false}.
     *
     * Setting this checkbox to {@code true} will result in accepting all certificates for the given Job.
     * If the remote Jenkins host has a self-signed certificate or its certificate is not trusted,
     * you may want to enable this option. It will accept untrusted certificates for the given host.
     *
     * This is unsafe and should only be used for testing or if you trust the host.
     *
     * @since 1.81
     */
    void trustAllCertificates(boolean trustAllCertificates = true) {
        this.trustAllCertificates = trustAllCertificates
    }

    /**
     * Specify parameter path + name of an external file, which parameters should be loaded from.
     * All paths are relative to the current workspace.
     *
     * @since 1.81
     */
    void parameterFile(String parameterFile) {
        this.parameterFile = parameterFile
    }
}
