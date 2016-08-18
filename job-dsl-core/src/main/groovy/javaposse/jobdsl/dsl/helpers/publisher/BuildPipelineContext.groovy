package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.helpers.common.DownstreamTriggerParameterContext

class BuildPipelineContext extends AbstractContext {
    protected final Item item

    List<Node> parameterNodes = []

    BuildPipelineContext(JobManagement jobManagement, Item item) {
        super(jobManagement)
        this.item = item
    }

    /**
     * Adds parameter values for the projects to trigger.
     *
     * @since 1.23
     */
    @RequiresPlugin(id = 'parameterized-trigger', minimumVersion = '2.26')
    void parameters(@DslContext(DownstreamTriggerParameterContext) Closure closure) {
        DownstreamTriggerParameterContext context = new DownstreamTriggerParameterContext(jobManagement, item)
        ContextHelper.executeInContext(closure, context)
        parameterNodes.addAll(context.configs)
    }
}
