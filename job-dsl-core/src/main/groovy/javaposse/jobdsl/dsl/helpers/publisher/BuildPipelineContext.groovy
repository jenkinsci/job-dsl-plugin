package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.common.DownstreamTriggerParameterContext

class BuildPipelineContext extends AbstractContext {
    List<Node> parameterNodes = []

    BuildPipelineContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * @since 1.23
     */
    void parameters(@DslContext(DownstreamTriggerParameterContext) Closure closure) {
        DownstreamTriggerParameterContext context = new DownstreamTriggerParameterContext(jobManagement)
        ContextHelper.executeInContext(closure, context)
        parameterNodes.addAll(context.configs)
    }
}
