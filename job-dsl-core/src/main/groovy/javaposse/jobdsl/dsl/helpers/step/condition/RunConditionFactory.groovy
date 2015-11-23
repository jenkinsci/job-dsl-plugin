package javaposse.jobdsl.dsl.helpers.step.condition

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.helpers.step.RunConditionContext

class RunConditionFactory {
    static RunCondition of(JobManagement jobManagement, Closure conditionClosure) {
        RunConditionContext conditionContext = new RunConditionContext(jobManagement)
        ContextHelper.executeInContext(conditionClosure, conditionContext)
        Preconditions.checkNotNull(conditionContext?.condition, 'No condition specified')
        conditionContext.condition
    }
}
