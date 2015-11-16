package javaposse.jobdsl.dsl.helpers.step.condition

/**
 * Generate config for a node condition.
 *
 * https://wiki.jenkins-ci.org/display/JENKINS/Run+Condition+Plugin
 */
class NodeCondition extends SimpleCondition {
    private final List<String> allowedNodes

    NodeCondition(List<String> allowedNodes) {
        this.name = 'Node'
	this.allowedNodes = allowedNodes
    }

    @Override
    void addArgs(NodeBuilder builder) {
        builder.'allowedNodes' {
	    allowedNodes.each { node ->
                'string' node
            }
	}
    }
}
