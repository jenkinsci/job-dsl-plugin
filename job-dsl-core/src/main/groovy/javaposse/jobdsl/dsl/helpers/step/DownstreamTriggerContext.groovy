package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.common.AbstractDownstreamTriggerContext

class DownstreamTriggerContext extends AbstractDownstreamTriggerContext {
    DownstreamTriggerBlockContext blockContext

    DownstreamTriggerContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    void block(@javaposse.jobdsl.dsl.DslContext(DownstreamTriggerBlockContext) Closure closure = null) {
        blockContext = blockContext ?: new DownstreamTriggerBlockContext()
        ContextHelper.executeInContext(closure, blockContext)
    }
}
