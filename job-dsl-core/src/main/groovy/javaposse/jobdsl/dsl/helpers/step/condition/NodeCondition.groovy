package javaposse.jobdsl.dsl.helpers.step.condition

class NodeCondition extends SimpleCondition {
    final Iterable<String> allowedNodes

    NodeCondition(Iterable<String> allowedNodes) {
        this.name = 'Node'
        this.allowedNodes = allowedNodes
    }

    @Override
    void addArgs(NodeBuilder builder) {
        builder.'allowedNodes' {
            allowedNodes.each { node ->
                'string'(node)
            }
        }
    }
}
