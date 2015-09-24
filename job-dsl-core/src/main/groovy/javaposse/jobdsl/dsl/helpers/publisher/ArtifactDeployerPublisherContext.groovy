package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.helpers.common.ArtifactDeployerContext

class ArtifactDeployerPublisherContext implements Context {
    List<ArtifactDeployerContext> entries = []
    boolean deployIfFailed

    /**
     * Adds more artifacts to deploy. Can be called multiple times to add more artifacts.
     */
    void artifactsToDeploy(@DslContext(ArtifactDeployerContext) Closure closure) {
        ArtifactDeployerContext context = new ArtifactDeployerContext()
        ContextHelper.executeInContext(closure, context)
        entries << context
    }

    /**
     * If set, deploys artifacts even if the build failed. Defaults to {@code false}.
     */
    void deployIfFailed(boolean deployIfFailed = true) {
        this.deployIfFailed = deployIfFailed
    }
}
