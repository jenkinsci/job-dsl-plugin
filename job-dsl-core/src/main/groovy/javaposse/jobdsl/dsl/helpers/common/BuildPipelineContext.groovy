package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.helpers.ContextHelper

class BuildPipelineContext implements Context {
    List<Node> parameterNodes = []

    def parameters(Closure closure) {
        DownstreamTriggerContext downstreamTriggerContext = new DownstreamTriggerContext()
        ContextHelper.executeInContext(closure, downstreamTriggerContext)
        parameterNodes.addAll(downstreamTriggerContext.createParametersNode().children())
    }
}
