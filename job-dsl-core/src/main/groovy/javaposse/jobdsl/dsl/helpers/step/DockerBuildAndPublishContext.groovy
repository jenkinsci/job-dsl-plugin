package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class DockerBuildAndPublishContext implements Context {
    String repositoryName = ''
    String tag = ''
    String dockerHostURI
    String serverCredentials
    String dockerRegistryURL
    String registryCredentials
    boolean skipPush
    boolean noCache
    boolean forcePull = true
    String buildContext = ''
    boolean skipBuild
    boolean createFingerprints = true
    boolean skipDecorate
    boolean skipTagAsLatest
    String dockerfileDirectory = ''
    String buildAdditionalArgs = ''
    boolean forceTag = true

    /**
     * Specifies the name of the repository to build.
     */
    void repositoryName(String repositoryName) {
        this.repositoryName = repositoryName
    }

    /**
     * Sets a tag for the image.
     */
    void tag(String tag) {
        this.tag = tag
    }

    /**
     * Specifies the URI to the Docker host.
     */
    void dockerHostURI(String dockerHostURI) {
        this.dockerHostURI = dockerHostURI
    }

    /**
     * Sets the credentials to use for authenticating with the Docker host.
     */
    void serverCredentials(String serverCredentials) {
        this.serverCredentials = serverCredentials
    }

    /**
     * Sets the URL of the Docker registry.
     */
    void dockerRegistryURL(String dockerRegistryURL) {
        this.dockerRegistryURL = dockerRegistryURL
    }

    /**
     * Sets the credentials to use for authenticating with the Docker registry.
     */
    void registryCredentials(String registryCredentials) {
        this.registryCredentials = registryCredentials
    }

    /**
     * If set, does not push image to registry/index on successful completion. Defaults to {@code false}.
     */
    void skipPush(boolean skipPush = true) {
        this.skipPush = skipPush
    }

    /**
     * If set, forces a rebuild. Defaults to {@code false}.
     */
    void noCache(boolean noCache = true) {
        this.noCache = noCache
    }

    /**
     * If set, updates the source image before building. Defaults to {@code true}.
     */
    void forcePull(boolean forcePull = true) {
        this.forcePull = forcePull
    }

    /**
     * Specifies the project root path for the build. Defaults to the workspace root if not specified
     */
    void buildContext(String buildContext) {
        this.buildContext = buildContext
    }

    /**
     * If set, does not build the image. Defaults to {@code false}.
     */
    void skipBuild(boolean skipBuild = true) {
        this.skipBuild = skipBuild
    }

    /**
     * If set, creates fingerprints for the image. Defaults to {@code true}.
     */
    void createFingerprints(boolean createFingerprints = true) {
        this.createFingerprints = createFingerprints
    }

    /**
     * If set, does not decorate the build name. Defaults to {@code false}.
     */
    void skipDecorate(boolean skipDecorate = true) {
        this.skipDecorate = skipDecorate
    }

    /**
     * If set, does not tag the build as latest. Defaults to {@code false}.
     */
    void skipTagAsLatest(boolean skipTagAsLatest = true) {
        this.skipTagAsLatest = skipTagAsLatest
    }

    /**
     * Sets the directory containing the Dockerfile. Defaults to the workspace directory.
     */
    void dockerfileDirectory(String dockerfileDirectory) {
        this.dockerfileDirectory = dockerfileDirectory
    }

    /**
     * Specifies Additional build arguments passed to docker build.
     */
    void buildAdditionalArgs(String buildAdditionalArgs) {
        this.buildAdditionalArgs = buildAdditionalArgs
    }

    /**
     * If set, force tag replacement when tag already exists. Defaults to {@code true}.
     */
    void forceTag(boolean forceTag = true) {
        this.forceTag = forceTag
    }
}
