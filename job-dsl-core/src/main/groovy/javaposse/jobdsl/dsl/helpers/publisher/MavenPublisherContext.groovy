package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

class MavenPublisherContext extends PublisherContext {
    MavenPublisherContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    /**
     * Deploys artifacts to a Maven repository.
     *
     * @since 1.31
     */
    void deployArtifacts(@DslContext(DeployArtifactsContext) Closure closure = null) {
        DeployArtifactsContext context = new DeployArtifactsContext()
        ContextHelper.executeInContext(closure, context)

        publisherNodes << new NodeBuilder().'hudson.maven.RedeployPublisher' {
            id(context.repositoryId ?: '')
            if (context.repositoryUrl) {
                url(context.repositoryUrl)
            }
            if (context.releaseEnvVar) {
                releaseEnvVar(context.releaseEnvVar)
            }
            uniqueVersion(context.uniqueVersion)
            evenIfUnstable(context.evenIfUnstable)
        }
    }
}
