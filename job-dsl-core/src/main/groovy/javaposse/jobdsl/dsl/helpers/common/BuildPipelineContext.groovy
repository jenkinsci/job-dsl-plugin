package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper

class BuildPipelineContext implements Context {
    List<Node> parameterNodes = []

    void parameters(Closure closure) {
        DownstreamTriggerContext downstreamTriggerContext = new DownstreamTriggerContext()
        ContextHelper.executeInContext(closure, downstreamTriggerContext)
        parameterNodes.addAll(downstreamTriggerContext.createParametersNode().children())
    }
}
