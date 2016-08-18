package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

abstract class AbstractDownstreamTriggerContext extends AbstractContext {
    DownstreamTriggerParameterContext parameterContext

    protected AbstractDownstreamTriggerContext(JobManagement jobManagement, Item item) {
        super(jobManagement)
        parameterContext = new DownstreamTriggerParameterContext(jobManagement, item)
    }

    /**
     * Adds parameter values for the projects to trigger.
     *
     * @since 1.38
     */
    void parameters(@DslContext(DownstreamTriggerParameterContext) Closure closure) {
        ContextHelper.executeInContext(closure, parameterContext)
    }
}
