package javaposse.jobdsl.dsl

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

    NodeDelegate(String xml) {
        this(new XmlParser().parse(new StringReader(xml)))
    }

    private int nodesAlreadyPresent(String nodeName) {
        return node.get(nodeName).size()
    }

    /**
     * Mutates node. :-(
     */
    def findTargetNode(name) {
        def targetNode
        if (nodesAlreadyPresent(name)) {
            targetNode = node.get(name)[0]
        } else {
            targetNode = node.appendNode(name) // This will cause problems when trying to use ++
        }
        targetNode
    }

    /**
     * Add potential children as real nodes
     */
    def reconcileChildren() {

    }

    def plus(Object b) {
        if (b instanceof Node) {

        }
    }

    def minus(Object b) {

    }

    // Getter
    def propertyMissing(String name) {
        // Identify Node
        def targetNode = findTargetNode(name)
        targetNode.value
    }

    // Setter, always sets the value. Will be converted to a string
    // TODO How do we deal with attributes on the name
    def propertyMissing(String name, arg) {
        Node targetNode = findTargetNode(name)

        def value = arg
        if (arg instanceof Closure) {
            // block that wants to be configured
            def childClosure = arg
            childClosure.delegate = new NodeDelegate(targetNode)
            value = childClosure.call(targetNode)
        }
        targetNode.value = value.toString()
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
            Closure childClosure = args[0]
            childClosure.delegate = new NodeDelegate(targetNode)
            childClosure.resolveStrategy = Closure.DELEGATE_FIRST
            childClosure.call(childClosure.delegate)
        } else {
            // Default to setting direct value
            targetNode.value = args[0]
        }
        targetNode
    }

    /**
     * Access to the node, for direct manipulation. This is a pattern seen in gradle
     * @return Node from XmlBuilder
     */
    public Node asNode() {
        node
    }
}
