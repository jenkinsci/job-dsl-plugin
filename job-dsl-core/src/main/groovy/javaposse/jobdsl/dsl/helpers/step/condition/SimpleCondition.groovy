package javaposse.jobdsl.dsl.helpers.step.condition

class SimpleCondition implements RunCondition {
    String name
    String subPackage = 'core'
    Map<String, String> args = [:]

    @Override
    String getConditionClass() {
        "org.jenkins_ci.plugins.run_condition.${subPackage}.${name}Condition"
    }

    @Override
    void addArgs(NodeBuilder builder) {
        args.each { k, v ->
            builder."${k}" v
        }
    }
}
