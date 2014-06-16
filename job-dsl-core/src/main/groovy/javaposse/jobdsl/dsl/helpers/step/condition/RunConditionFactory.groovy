package javaposse.jobdsl.dsl.helpers.step.condition

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.step.RunConditionContext

class RunConditionFactory {
    static RunCondition of(Closure conditionClosure) {
        RunConditionContext conditionContext = new RunConditionContext()
        AbstractContextHelper.executeInContext(conditionClosure, conditionContext)
        Preconditions.checkNotNull(conditionContext?.condition, 'No condition specified')
        conditionContext.condition
    }
}
