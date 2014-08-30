package javaposse.jobdsl.dsl.helpers.step.condition

class AlwaysRunCondition implements RunCondition {
    final String conditionClass = 'org.jenkins_ci.plugins.run_condition.core.AlwaysRun'

    @Override
    void addArgs(NodeBuilder builder) {
    }
}
