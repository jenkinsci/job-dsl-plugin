package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class RepositoryConnectorArtifactContext implements Context {
    String groupId
    String artifactId
    String classifier
    String version
    String extension = 'jar'
    String targetFileName

    /**
     * Id of the Maven group.
     *
     * @param groupId groupId of the Maven coordinates
     */
    void groupId(String groupId) {
        this.groupId = groupId
    }

    /**
     * Id of the Maven artifact.
     *
     * @param artifactId artifact id of the Maven coordinates
     */
    void artifactId(String artifactId) {
        this.artifactId = artifactId
    }

    /**
     * Optional Maven classifier.
     *
     * @param classifier classifier of the Maven artifact
     */
    void classifier(String classifier) {
        this.classifier = classifier
    }

    /**
     * Version of the artifact to download.
     *
     * @param version specific artifact version, <code>RELEASE</code>, <code>LATEST</code> or a version range
     *                (e.g. <code>[1.1,)</code>)
     */
    void version(String version) {
        this.version = version
    }

    /**
     * Artifact extension, defaults to <code>jar</code>.
     *
     * @param extension extension (e.g. <code>jar</code> or <code>war</code>)
     */
    void extension(String extension) {
        this.extension = extension
    }

    /**
     * Optional target file name.
     *
     * @param targetFileName the name of the downloaded file
     */
    void targetFileName(String targetFileName) {
        this.targetFileName = targetFileName
    }
}
