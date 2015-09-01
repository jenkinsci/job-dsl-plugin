package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class RepositoryConnectorContext implements Context {
    private static final Set<String> UPDATE_POLICIES = ['daily', 'never', 'always']

    List<Node> artifactNodes = []
    String targetDirectory
    boolean failOnError = false
    boolean enableRepoLogging = false
    String snapshotUpdatePolicy = 'daily'
    String releaseUpdatePolicy = 'daily'

    /**
     * Defines the directory where the artifact gets stored. Not set by default.
     */
    void targetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory
    }

    /**
     * Fails the build on the first artifact, which can not be retrieved. Not enabled by default.
     */
    void failOnError(boolean failOnError = true) {
        this.failOnError = failOnError
    }

    /**
     * Enables logging for repository and transfer. Not enabled by default.
     */
    void enableRepoLogging(boolean enableRepoLogging = true) {
        this.enableRepoLogging = enableRepoLogging
    }

    /**
     * Maven snapshot update policy, defaults to <code>daily</code>.
     *
     * Must be one of <code>daily</code>, <code>never</code> or <code>always</code>.
     */
    void snapshotUpdatePolicy(String updatePolicy) {
        validateUpdatePolicy(updatePolicy)
        this.snapshotUpdatePolicy = updatePolicy
    }

    /**
     * Maven release update policy, defaults to <code>daily</code>.
     *
     * Must be one of <code>daily</code>, <code>never</code> or <code>always</code>.
     */
    void releaseUpdatePolicy(String updatePolicy) {
        validateUpdatePolicy(updatePolicy)
        this.releaseUpdatePolicy = updatePolicy
    }

    /**
     * Adds an artifact for resolution from a repository. Can be called multiple times to resolve more artifacts.
     */
    void artifact(@DslContext(RepositoryConnectorArtifactContext) Closure artifactClosure) {
        RepositoryConnectorArtifactContext context = new RepositoryConnectorArtifactContext()
        ContextHelper.executeInContext(artifactClosure, context)

        artifactNodes << new NodeBuilder().'org.jvnet.hudson.plugins.repositoryconnector.Artifact' {
            groupId context.groupId ?: ''
            artifactId context.artifactId ?: ''
            classifier context.classifier ?: ''
            version context.version ?: ''
            extension context.extension ?: ''
            targetFileName context.targetFileName ?: ''
        }
    }

    private static void validateUpdatePolicy(String updatePolicy) {
        checkArgument(
                UPDATE_POLICIES.contains(updatePolicy),
                "updatePolicy must be one of ${UPDATE_POLICIES.join(', ')}"
        )
    }
}
