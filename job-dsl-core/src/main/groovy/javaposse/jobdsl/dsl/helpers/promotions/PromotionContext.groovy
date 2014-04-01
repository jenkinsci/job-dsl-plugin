package javaposse.jobdsl.dsl.helpers.promotions

import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.helpers.step.AbstractStepContext

class PromotionContext implements Context {

    private List<ConditionsContext> conditions = []

    private List<Node> actions =  []

    private String icon

    private String restrict

    def icon(String icon) {
        this.icon = icon
    }

    def restrict(String restrict) {
        this.restrict = restrict
    }

    def conditions(Closure conditionClosure) {
        // delegate to ConditionsContext
        ConditionsContext conditionContext = new ConditionsContext()
        AbstractContextHelper.executeInContext(conditionClosure, conditionContext)
        conditions << conditionContext
    }

    def actions(Closure actionsClosure) {
        // delegate to ConditionsContext
        AbstractStepContext actionsContext = new AbstractStepContext()
        AbstractContextHelper.executeInContext(actionsClosure, actionsContext)
        actionsContext.stepNodes.each { actions << it }
    }
}
