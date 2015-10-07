package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class DeployArtifactsContext implements Context {
    String repositoryUrl
    String repositoryId
    boolean uniqueVersion = true
    boolean evenIfUnstable

    /**
     * Specifies the URL of the Maven repository to deploy artifacts to.
     *
     * @since 1.40
     */
    void repositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl
    }

    /**
     * Specifies the ID that is used to retrieve additional configuration from the Maven settings file.
     *
     * @since 1.40
     */
    void repositoryId(String repositoryId) {
        this.repositoryId = repositoryId
    }

    /**
     * If set, assigns timestamp-based unique version number to the deployed artifacts, when their versions end with
     * {@code -SNAPSHOT}. Defaults to {@code true}.
     */
    void uniqueVersion(boolean uniqueVersion = true) {
        this.uniqueVersion = uniqueVersion
    }

    /**
     * If set, deploys even if the build is unstable. Defaults to {@code false}.
     */
    void evenIfUnstable(boolean evenIfUnstable = true) {
        this.evenIfUnstable = evenIfUnstable
    }
}
