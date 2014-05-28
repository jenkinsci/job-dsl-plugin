package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context

class BuildPipelineContext implements Context {
    List<Node> parameterNodes = []

    def parameters(Closure closure) {
        DownstreamTriggerContext downstreamTriggerContext = new DownstreamTriggerContext()
        AbstractContextHelper.executeInContext(closure, downstreamTriggerContext)
        parameterNodes.addAll(downstreamTriggerContext.createParametersNode().children())
    }
}
