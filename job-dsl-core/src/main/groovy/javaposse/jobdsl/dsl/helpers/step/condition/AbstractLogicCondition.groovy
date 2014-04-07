package javaposse.jobdsl.dsl.helpers.step.condition

abstract class AbstractLogicCondition implements RunCondition {
    String operation

    @Override
    String getConditionClass() {
        return "org.jenkins_ci.plugins.run_condition.logic.${operation}"
    }
}
