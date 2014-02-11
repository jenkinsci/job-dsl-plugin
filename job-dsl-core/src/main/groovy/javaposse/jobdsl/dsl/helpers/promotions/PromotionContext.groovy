package javaposse.jobdsl.dsl.helpers.promotions

import groovy.lang.Closure;

import java.util.List;

import javaposse.jobdsl.dsl.JobType;
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.Context;
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.step.StepContext;

import com.google.common.base.Preconditions

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
        ConditionsContext conditionContext = new ConditionsContext()
        AbstractContextHelper.executeInContext(conditionClosure, conditionContext)
        conditions << conditionContext
    }

    def actions(Closure actionsClosure) {
        StepContext actionsContext = new StepContext(JobType.Maven)
        AbstractContextHelper.executeInContext(actionsClosure, actionsContext)
        actionsContext.stepNodes.each { actions << it }
    }
}
