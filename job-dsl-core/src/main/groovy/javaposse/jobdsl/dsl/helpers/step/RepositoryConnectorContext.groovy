package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context

/**
 * <p>DSL Support for the repository-connector plugin.</p>
 * <p><a href="https://wiki.jenkins-ci.org/display/JENKINS/Repository+Connector+Plugin">Repository Connector Plugin</a></p>
 */
class RepositoryConnectorContext implements Context {

    /**
     * <p>Enumeration of available artifact checksum policies.</p>
     */
    public static enum RepositoryConnectorChecksumPolicy {

        WARN("warn"),

        FAIL("fail")

        private final String stringRepresentation

        RepositoryConnectorChecksumPolicy(String stringRepresentation) {
            this.stringRepresentation = stringRepresentation
        }

        @Override
        public String toString() {
            return this.stringRepresentation
        }

    }

    /**
     * <p>Enumeration of available repository update policies.</p>
     */
    public static enum RepositoryConnectorUpdatePolicy {

        DAILY("daily"),

        NEVER("never"),

        ALWAYS("always")

        private final String stringRepresentation

        RepositoryConnectorUpdatePolicy(String stringRepresentation) {
            this.stringRepresentation = stringRepresentation
        }

        @Override
        public String toString() {
            return this.stringRepresentation
        }
    }

    List<Node> artifactNodes = []

    String targetDirectory

    boolean failOnError = false
    boolean enableRepoLogging = false

    RepositoryConnectorUpdatePolicy snapshotUpdatePolicy = RepositoryConnectorUpdatePolicy.DAILY
    RepositoryConnectorUpdatePolicy releaseUpdatePolicy = RepositoryConnectorUpdatePolicy.DAILY
    RepositoryConnectorChecksumPolicy snapshotChecksumPolicy = RepositoryConnectorChecksumPolicy.WARN
    RepositoryConnectorChecksumPolicy releaseChecksumPolicy = RepositoryConnectorChecksumPolicy.WARN

    /**
     * <p>Defines the directory where the artifact gets stored.</p>
     * @param targetDirectory e.g. 'target'
     */
    def targetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory
    }

    /**
     * <p>Fails the build on the first artifact, which can not be retrieved.</p>
     * @param failOnError (default: false)
     */
    def failOnError(boolean failOnError = true) {
        this.failOnError = failOnError
    }

    /**
     * <p>Enables logging for repository and transfer.</p>
     * @param enableRepoLogging (default: false)
     */
    def enableRepoLogging(boolean enableRepoLogging = true) {
        this.enableRepoLogging = enableRepoLogging
    }

    /**
     * <p>Maven snapshot update policy.</p>
     * @param snapshotUpdatePolicy must be one of {@link RepositoryConnectorUpdatePolicy}:
     * <ul>
     *     <li>{@link RepositoryConnectorUpdatePolicy#DAILY} (default)</li>
     *     <li>{@link RepositoryConnectorUpdatePolicy#NEVER}</li>
     *     <li>{@link RepositoryConnectorUpdatePolicy#ALWAYS}</li>
     * </ul>
     */
    def snapshotUpdatePolicy(RepositoryConnectorUpdatePolicy snapshotUpdatePolicy) {
        this.snapshotUpdatePolicy = snapshotUpdatePolicy
    }

    /**
     * <p>Maven release update policy.</p>
     * @param releaseUpdatePolicy must be one of {@link RepositoryConnectorUpdatePolicy}:
     * <ul>
     *     <li>{@link RepositoryConnectorUpdatePolicy#DAILY} (default)</li>
     *     <li>{@link RepositoryConnectorUpdatePolicy#NEVER}</li>
     *     <li>{@link RepositoryConnectorUpdatePolicy#ALWAYS}</li>
     * </ul>
     */
    def releaseUpdatePolicy(RepositoryConnectorUpdatePolicy releaseUpdatePolicy) {
        this.releaseUpdatePolicy = releaseUpdatePolicy
    }

    /**
     * <p>What to do when verification of an snapshot artifact checksum fails.</p>
     * @param snapshotChecksumPolicy must be one of {@link RepositoryConnectorChecksumPolicy}:
     * <ul>
     *     <li>{@link RepositoryConnectorChecksumPolicy#WARN} (default)</li>
     *     <li>{@link RepositoryConnectorChecksumPolicy#FAIL}</li>
     * </ul>
     */
    def snapshotChecksumPolicy(RepositoryConnectorChecksumPolicy snapshotChecksumPolicy) {
        this.snapshotChecksumPolicy = snapshotChecksumPolicy
    }

    /**
     * <p>What to do when verification of an release artifact checksum fails.</p>
     * @param releaseChecksumPolicy must be one of {@link RepositoryConnectorChecksumPolicy}:
     * <ul>
     *     <li>{@link RepositoryConnectorChecksumPolicy#WARN} (default)</li>
     *     <li>{@link RepositoryConnectorChecksumPolicy#FAIL}</li>
     * </ul>
     */
    def releaseChecksumPolicy(RepositoryConnectorChecksumPolicy releaseChecksumPolicy) {
        this.releaseChecksumPolicy = releaseChecksumPolicy
    }

    /**
     * <p>Configures the resolution of an artifact from an repository using the repository-connector plugin.</p>
     * <pre>
     * {@code
     *     <artifacts>
     *       <org.jvnet.hudson.plugins.repositoryconnector.Artifact>
     *         <groupId>de.test.me</groupId>
     *         <artifactId>myTestArtifact</artifactId>
     *         <classifier/>
     *         <version>RELEASE</version>
     *         <extension>war</extension>
     *         <targetFileName>myTestArtifact.war</targetFileName>
     *       </org.jvnet.hudson.plugins.repositoryconnector.Artifact>
     *     </artifacts>
     *}
     * </pre>
     * @see <a href="https://wiki.jenkins-ci.org/display/JENKINS/Repository+Connector+Plugin">Repository Connector Plugin</a>
     */
    def artifact(Closure artifactClosure) {
        RepositoryConnectorArtifactContext context = new RepositoryConnectorArtifactContext()
        AbstractContextHelper.executeInContext(artifactClosure, context)

        def nodeBuilder = NodeBuilder.newInstance()
        Node artifactNode = nodeBuilder.'org.jvnet.hudson.plugins.repositoryconnector.Artifact' {
            groupId context.groupId
            artifactId context.artifactId
            classifier context.classifier
            version context.version
            extension context.extension
            targetFileName context.targetFileName
        }

        artifactNodes << artifactNode
    }
}
