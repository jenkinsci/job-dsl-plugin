package javaposse.jobdsl.dsl.helpers.step.condition

class BinaryLogicOperation extends AbstractLogicCondition {
    List<RunCondition> conditions

    BinaryLogicOperation(String operation, List<RunCondition> conditions) {
        this.conditions = conditions
        this.operation = operation
    }

    @Override
    void addArgs(NodeBuilder builder) {
        builder.conditions {
            conditions.each { runCondition ->
                builder.'org.jenkins__ci.plugins.run__condition.logic.ConditionContainer' {
                    condition(class: runCondition.conditionClass) {
                        runCondition.addArgs(builder)
                    }
                }
            }
        }
    }
}
