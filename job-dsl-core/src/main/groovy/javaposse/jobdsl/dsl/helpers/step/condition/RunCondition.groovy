package javaposse.jobdsl.dsl.helpers.step.condition

interface RunCondition {
    String getConditionClass()

    void addArgs(NodeBuilder builder)
}
