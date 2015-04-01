package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

class MavenPublisherContext extends PublisherContext {
    MavenPublisherContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    void deployArtifacts(@DslContext(DeployArtifactsContext) Closure closure = null) {
        DeployArtifactsContext context = new DeployArtifactsContext()
        ContextHelper.executeInContext(closure, context)

        publisherNodes << new NodeBuilder().'hudson.maven.RedeployPublisher' {
            id()
            uniqueVersion(context.uniqueVersion)
            evenIfUnstable(context.evenIfUnstable)
        }
    }
}
