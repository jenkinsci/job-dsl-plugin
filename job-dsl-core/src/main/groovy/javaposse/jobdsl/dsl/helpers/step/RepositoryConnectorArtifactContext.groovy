package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.helpers.Context

/**
 * <p>DSL Support for the repository-connector plugin's artifacts subsection.</p>
 * <p>
 *     <a href="https://wiki.jenkins-ci.org/display/JENKINS/Repository+Connector+Plugin">Repository Connector Plugin</a>
 * </p>
 */
class RepositoryConnectorArtifactContext implements Context {

    String groupId
    String artifactId
    String classifier = ''
    String version
    String extension
    String targetFileName

    /**
     * <p>Id of the Maven group.</p>
     * @param groupId groupId of the Maven coordinates
     */
    def groupId(String groupId) {
        this.groupId = groupId
    }

    /**
     * <p>Id of the Maven artefact</p>
     * @param artifactId artefact id of the maven coordinates
     */
    void artifactId(String artifactId) {
        this.artifactId = artifactId
    }

    /**
     * <p>Maven version classifier (optional)</p>
     * @param classifier version classifier which is appended
     * to the version separated by a dash
     * e.g. "javadoc" or "sources" (default: <empty>)
     */
    void classifier(String classifier) {
        this.classifier = classifier
    }

    /**
     * <p>Version of the artifact to download.</p>
     * @param version specific artifact version, RELEASE, LATEST or a
     * version range e.g. [0-SNAPSHOT,) are supported.
     */
    void version(String version) {
        this.version = version
    }

    /**
     * <p>Artifact extension</p>
     * @param extension e.g. jar, war, ear
     */
    void extension(String extension) {
        this.extension = extension
    }

    /**
     * <p>Target file name</p>
     * @param targetFileName the name of the downloaded file
     */
    void targetFileName(String targetFileName) {
        this.targetFileName = targetFileName
    }
}
