package javaposse.jobdsl.dsl.helpers.step.condition

class NeverRunCondition implements RunCondition {
    final String conditionClass = 'org.jenkins_ci.plugins.run_condition.core.NeverRun'

    @Override
    void addArgs(NodeBuilder builder) {
    }
}
