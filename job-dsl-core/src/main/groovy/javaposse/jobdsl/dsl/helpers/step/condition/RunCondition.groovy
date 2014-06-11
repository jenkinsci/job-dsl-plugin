package javaposse.jobdsl.dsl.helpers.step.condition

public interface RunCondition {
    String getConditionClass()
    void addArgs(NodeBuilder builder)
}
