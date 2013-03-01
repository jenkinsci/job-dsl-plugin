package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context

class DownstreamContext implements Context {
    private List<DownstreamTriggerContext> triggers = []

    def trigger(String projects, Closure downstreamTriggerClosure = null) {
        trigger(projects, null, downstreamTriggerClosure)
    }

    def trigger(String projects, String condition, Closure downstreamTriggerClosure = null) {
        trigger(projects, condition, false, downstreamTriggerClosure)
    }

    def trigger(String projects, String condition, boolean triggerWithNoParameters, Closure downstreamTriggerClosure = null) {
        DownstreamTriggerContext downstreamTriggerContext = new DownstreamTriggerContext()
        downstreamTriggerContext.projects = projects
        downstreamTriggerContext.condition = condition ?: 'SUCCESS'
        downstreamTriggerContext.triggerWithNoParameters = triggerWithNoParameters
        AbstractContextHelper.executeInContext(downstreamTriggerClosure, downstreamTriggerContext)

        // Validate this trigger
        assert validDownstreamConditionNames.contains(downstreamTriggerContext.condition), "Trigger condition has to be one of these values: ${validDownstreamConditionNames.join(',')}"

        triggers << downstreamTriggerContext
    }

    def validDownstreamConditionNames = ['SUCCESS', 'UNSTABLE', 'UNSTABLE_OR_BETTER', 'UNSTABLE_OR_WORSE', 'FAILED', 'ALWAYS']
}
