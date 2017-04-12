package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class StashNotifierContext extends AbstractContext {
    String serverBaseUrl
    String credentialsId
    String commitSha1
    boolean keepRepeatedBuilds
    boolean ignoreUnverifiedSSLCertificates

    protected StashNotifierContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Sets the base URL of the Stash server to notify.
     *
     * @since 1.61
     */
    void serverBaseUrl(String serverBaseUrl) {
        this.serverBaseUrl = serverBaseUrl
    }

    /**
     * Sets credentials for authentication with the Stash server.
     *
     * @since 1.61
     */
    @RequiresPlugin(id = 'stashNotifier', minimumVersion = '1.9.0')
    void credentialsId(String credentialsId) {
        this.credentialsId = credentialsId
    }

    /**
     * Attaches the notification to a specific commit in Stash.
     */
    void commitSha1(String commitSha1) {
        this.commitSha1 = commitSha1
    }

    /**
     * If set, results of repeated builds of the same commit will show up in Stash as a list of builds. Defaults to
     * {@code false}.
     */
    void keepRepeatedBuilds(boolean keepRepeatedBuilds = true) {
        this.keepRepeatedBuilds = keepRepeatedBuilds
    }

    /**
     * If set, ignores invalid or self-signed SSL certificates. Defaults to {@code false}.
     *
     * @sincde 1.61
     */
    void ignoreUnverifiedSSLCertificates(boolean ignoreUnverifiedSSLCertificates = true) {
        this.ignoreUnverifiedSSLCertificates = ignoreUnverifiedSSLCertificates
    }
}
