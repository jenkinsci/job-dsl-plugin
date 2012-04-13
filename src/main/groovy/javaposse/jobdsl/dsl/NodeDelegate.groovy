package javaposse.jobdsl.dsl

import groovy.util.Node;

/**
 * TODO Do some cleanup in the bodies of the missing methods
 * @author jryan
 *
 */
public class NodeDelegate {
    Node node

    NodeDelegate(Node node) {
        this.node = node
    }

    def findTargetNode(name) {
        def targetNode
        if (nodeAlreadyPresent(name)) {
            targetNode = node.get(name)[0]
        } else {
            targetNode = node.appendNode(name)
        }
        targetNode
    }

    def propertyMissing(String name) {
        // Identify Node
        def targetNode = findTargetNode(name)
        targetNode.value
    }

    def propertyMissing(String name, arg) {
        def targetNode = findTargetNode(name)

        def value = arg
        if (arg instanceof Closure) {
            // block that wants to be configured
            def childClosure = arg
            childClosure.delegate = new NodeDelegate(targetNode)
            value = childClosure.call(targetNode)
        }
        targetNode.value = value
    }

    def methodMissing(String name, args) {
        if (args.length == 0) {
            // TODO create node, with no body
            return // Not sure what to do with a method with no args in this context
        }

        // Identify Node
        def targetNode = findTargetNode(name)

        if (args[0] instanceof Closure) {
            // block that wants to be configured
            def childClosure = args[0]
            childClosure.delegate = new NodeDelegate(targetNode)
            childClosure.call(targetNode)
        } else {
            // Default to setting direct value
            targetNode.value = args[0]
        }

    }

    private boolean nodeAlreadyPresent(String nodeName) {
        return node.get(nodeName).size() > 0
    }

    /**
     * Access to the node, for direct manipulation. This is a pattern seen in gradle
     * @return Node from XmlBuilder
     */
    public Node asNode() {
        node
    }
}
