package javaposse.jobdsl.dsl.helpers.step.condition

class NotCondition extends AbstractLogicCondition {
    RunCondition condition

    NotCondition(RunCondition condition) {
        this.operation = 'Not'
        this.condition = condition
    }

    @Override
    void addArgs(NodeBuilder builder) {
        builder.condition(class: condition.conditionClass) {
            condition.addArgs(delegate)
        }
    }
}
