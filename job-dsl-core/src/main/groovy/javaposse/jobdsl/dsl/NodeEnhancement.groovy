package javaposse.jobdsl.dsl

import java.util.logging.Logger

/**
 * Add div and leftShift operators to Node.
 * * div - Will return the first child that matches name, and if it doesn't exists, it creates
 * * leftShift - Take node (or configure block to create) and appends as child, as opposed to plus which appends as a
 *               peer
 */
@Category(Node)
class NodeEnhancement {
    private static final Logger LOGGER = Logger.getLogger(NodeEnhancement.name)

    Node div(Node orphan) {
        Node clonedOrphan = cloneNode(orphan)
        LOGGER.fine("Looking for child node ${clonedOrphan}")
        String childName = clonedOrphan.name()
        List children = this.children().findAll { child ->
            child instanceof Node && child.name() == childName &&
                    child.attributes().entrySet().containsAll(clonedOrphan.attributes().entrySet())
        }
        if (children.size() == 0) {
            LOGGER.fine("Creating node for ${childName}")
            // Create node using just name
            this.append(clonedOrphan)
            clonedOrphan
        } else {
            // Return first childName, that's the contract for div
            LOGGER.fine("Using first found childName for ${childName}")
            Node found = children[0] as Node

            // Copy over value and attribute from orphan if it has one.
            if (clonedOrphan.value() != null) {
                found.value = clonedOrphan.value()
            }
            clonedOrphan.attributes().each { k, v ->
                found.attributes().put(k, v)
            }

            found
        }
    }

    Node div(String childName) {
        LOGGER.fine("Looking for childName ${childName} ${LOGGER.level}")

        List children = this.children().findAll { child ->
            child instanceof Node && child.name() == childName
        }
        if (children.size() == 0) {
            LOGGER.fine("Creating node for ${childName}")
            // Create node using just name
            return this.appendNode(childName)
        } else {
            // Return first childName, that's the contract for div
            LOGGER.fine("Using first found childName for ${childName}")
            return children[0] as Node
        }
    }

    Node leftShift(boolean boolValue) {
        leftShift(boolValue ? 'true' : 'false')
    }

    Node leftShift(String appendChildName) {
        LOGGER.fine("Setting value of ${appendChildName} for ${this.name()}")
        this.value = appendChildName
        this
    }

    Node leftShift(Node child) {
        LOGGER.fine("Appending node ${child} to ${this}")
        this.append(cloneNode(child))
        this
    }

    Node leftShift(Closure configureBlock) {
        LOGGER.fine("Appending block from ${configureBlock}")
        configureBlock.resolveStrategy = Closure.DELEGATE_FIRST
        List<Node> newChildren = buildChildren(configureBlock)
        newChildren.each { this.append(it) }
        this
    }

    private static List<Node> buildChildren(Object c) {
        NodeBuilder b = new NodeBuilder()
        Node newNode = (Node) b.invokeMethod('dummyNode', c)
        newNode.children()
    }

    /**
     * Creates a new Node with the same name, no parent, shallow cloned attributes
     * and if the value is a NodeList, a (deep) clone of those nodes.
     *
     * @return the clone
     */
    // can be replaced by Node#clone() when using a Groovy release that contains fixes for GROOVY-5682 and GROOVY-7044
    private static Node cloneNode(Node node) {
        Object newValue = node.value()
        if (newValue instanceof List) {
            newValue = cloneNodeList((List) newValue)
        }
        Map attributes = node.attributes() ? new HashMap(node.attributes()) : [:]
        new Node(null, node.name(), attributes, newValue)
    }

    /**
     * Creates a new NodeList containing the same elements as the
     * original (but cloned in the case of Nodes).
     *
     * @return the clone
     */
    private static List cloneNodeList(List nodeList) {
        List result = nodeList instanceof NodeList ? new NodeList(nodeList.size()) : new ArrayList(nodeList.size())
        for (int i = 0; i < nodeList.size(); i++) {
            Object next = nodeList[i]
            if (next instanceof Node) {
                result << cloneNode(((Node) next))
            } else {
                result << next
            }
        }
        result
    }
}
