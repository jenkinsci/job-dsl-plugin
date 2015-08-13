package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.helpers.common.AbstractDownstreamTriggerContext

class DownstreamTriggerContext extends AbstractDownstreamTriggerContext {
    DownstreamTriggerBlockContext blockContext
    DownstreamTriggerParameterFactoryContext parameterFactoryContext = new DownstreamTriggerParameterFactoryContext()

    DownstreamTriggerContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    void block(@DslContext(DownstreamTriggerBlockContext) Closure closure = null) {
        blockContext = blockContext ?: new DownstreamTriggerBlockContext()
        ContextHelper.executeInContext(closure, blockContext)
    }

    @RequiresPlugin(id = 'parameterized-trigger', minimumVersion = '2.25')
    void parameterFactories(@DslContext(DownstreamTriggerParameterFactoryContext) Closure closure) {
        ContextHelper.executeInContext(closure, parameterFactoryContext)
    }
}
