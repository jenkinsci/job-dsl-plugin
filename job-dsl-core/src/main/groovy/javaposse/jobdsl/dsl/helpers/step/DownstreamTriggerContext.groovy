package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.common.AbstractDownstreamTriggerContext

class DownstreamTriggerContext extends AbstractDownstreamTriggerContext {
    DownstreamTriggerBlockContext blockContext
    DownstreamTriggerParameterFactoryContext parameterFactoryContext

    DownstreamTriggerContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
        parameterFactoryContext = new DownstreamTriggerParameterFactoryContext(jobManagement, item)
    }

    /**
     * Blocks until the triggered projects finish their builds.
     *
     * @since 1.38
     */
    void block(@DslContext(DownstreamTriggerBlockContext) Closure closure = null) {
        blockContext = blockContext ?: new DownstreamTriggerBlockContext()
        ContextHelper.executeInContext(closure, blockContext)
    }

    /**
     * Adds parameters factories.
     *
     * @since 1.38
     */
    void parameterFactories(@DslContext(DownstreamTriggerParameterFactoryContext) Closure closure) {
        ContextHelper.executeInContext(closure, parameterFactoryContext)
    }
}
