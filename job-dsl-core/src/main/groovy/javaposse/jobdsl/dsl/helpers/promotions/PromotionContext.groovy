package javaposse.jobdsl.dsl.helpers.promotions

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.helpers.ContextHelper

class PromotionContext implements Context {
    private final JobManagement jobManagement

    private final List<ConditionsContext> conditions = []

    private final List<Node> actions =  []

    private String icon

    private String restrict

    PromotionContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    void icon(String icon) {
        this.icon = icon
    }

    void restrict(String restrict) {
        this.restrict = restrict
    }

    List<ConditionsContext> conditions(Closure conditionClosure) {
        // delegate to ConditionsContext
        ConditionsContext conditionContext = new ConditionsContext()
        ContextHelper.executeInContext(conditionClosure, conditionContext)
        conditions << conditionContext
    }

    List<Node> actions(Closure actionsClosure) {
        // delegate to ConditionsContext
        PromotionStepContext actionsContext = new PromotionStepContext(this.jobManagement)
        ContextHelper.executeInContext(actionsClosure, actionsContext)
        actionsContext.stepNodes.each { actions << it }
    }
}
