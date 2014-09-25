package javaposse.jobdsl.dsl.helpers.promotions

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context

class PromotionContext implements Context {
    private final JobManagement jobManagement

    private final List<ConditionsContext> conditions = []

    private final List<Node> actions =  []

    private String icon

    private String restrict

    PromotionContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

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
        PromotionStepContext actionsContext = new PromotionStepContext(this.jobManagement)
        AbstractContextHelper.executeInContext(actionsClosure, actionsContext)
        actionsContext.stepNodes.each { actions << it }
    }
}
