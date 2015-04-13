package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

class BuildPipelineContext implements Context {
    protected final JobManagement jobManagement
    List<Node> parameterNodes = []

    BuildPipelineContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    /**
     * @since 1.23
     */
    void parameters(@DslContext(DownstreamTriggerContext) Closure closure) {
        DownstreamTriggerContext downstreamTriggerContext = new DownstreamTriggerContext(jobManagement)
        ContextHelper.executeInContext(closure, downstreamTriggerContext)
        parameterNodes.addAll(downstreamTriggerContext.createParametersNode().children())
    }
}
