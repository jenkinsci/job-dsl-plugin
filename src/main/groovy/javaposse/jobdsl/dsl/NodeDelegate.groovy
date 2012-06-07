package javaposse.jobdsl.dsl

/**
 * TODO Do some cleanup in the bodies of the missing methods
 * @author jryan
 *
 */
public class NodeDelegate {
    NodeDelegate parent
    Node node
    Queue<Action> possibleChildren = new LinkedList<Action>()
    List<NodeDelegate> referencedChildren = new ArrayList<NodeDelegate>()

    class Action {
        boolean append
        Node node
        NodeDelegate nodeDelegate
        String name

        Action(NodeDelegate nodeDelegate) {
            this.nodeDelegate = nodeDelegate
            this.node = nodeDelegate.node
            this.name = node.name()
            this.append = false
        }
    }

    NodeDelegate(Node node, NodeDelegate parent) {
        this.node = node
        this.parent = parent
    }

    NodeDelegate(String xml) {
        this(new XmlParser().parse(new StringReader(xml)), null)
    }

    private int childrenAlreadyPresent(String nodeName) {
        return node.get(nodeName).size()
    }

    private int nodesAlreadyPresent(String nodeName) {
        return node.get(nodeName).size() + possibleChildren.findAll({ it.name == nodeName }).size()
    }

    /**
     * Locate child. Create one if not found.  We don't currently support matching based on attributes.
     */
    def findChild(String name) {
        return findChild(name, null)
    }

    def NodeDelegate findChild(String name, Map attributes) {
        int childrenCount = nodesAlreadyPresent(name)
        def potentialNode
        def targetNodeDelegate
        if (childrenCount == 1) { // TODO We're return existing nodes, which are going to be appended :-(
            // Found single child, this is it
            if (childrenAlreadyPresent(name) == 1) {
                potentialNode = node.get(name)[0] // Just grab node
            } else {
                potentialNode = possibleChildren.find { it.node.name() == name }.node // Find it in the pile, albeit small pile
            }
            targetNodeDelegate = new NodeDelegate(potentialNode, this)
        } else {
            // No existing children, add node as a potential
            // OR Multiple children, we don't have support to access multiple children. We going to either append or remove,
            // pretty similar from our point of view
            potentialNode = new Node(null, name, attributes)
            targetNodeDelegate = new NodeDelegate(potentialNode, this)
            possibleChildren.add(new Action(targetNodeDelegate))
        }
        // Including ourselves as the parent since we might be actually added via + to another parent, in which case
        // we'll have to cleanup this one


        referencedChildren.add(targetNodeDelegate)
        return targetNodeDelegate
    }

    def processActions() {
        possibleChildren.each { Action action ->
            if (!action.append) {
                // Implies that we need to remove previous nodes
                for (def child : node.get(action.name)) {
                    node.remove(child)
                }
            }
            node.append(action.node)
        }

        // Make sure every child gets a chance to reconcile
        referencedChildren.each {
            it.processActions()
        }
    }

    def plus(Object b) {
        if ( !(b instanceof NodeDelegate)) {
            throw new RuntimeException("Can only add other nodes")
        }

        NodeDelegate nd = (NodeDelegate) b
        // See if we're the parent
        Action activeAction
        if (nd.parent == this) { // TODO Is this equals going to cut it?
            activeAction = possibleChildren.find { it.nodeDelegate == nd }
        } else {
            // Remove from parent and add to this one
            activeAction = nd.parent.possibleChildren.find { it.nodeDelegate == nd }
            nd.parent.possibleChildren.remove(activeAction)
            possibleChildren.append(activeAction)
            referencedChildren.add(activeAction.nodeDelegate)
        }
        // Find action and flip append bit
        activeAction.append = true

        return nd
    }

    def minus(Object b) {
        throw new RuntimeException("Not supported yet")
    }

    /**
     * Getter. Returns another delegate, so that calls can be chained.
     * @param name
     * @return
     */
    def propertyMissing(String name) {
        // Identify Node
        def targetNode = findChild(name)
        targetNode
    }

    /**
     * Setter to set the text value of a child. Will always be converted to a string
     * @param name
     * @param arg
     * @return
     */
    def propertyMissing(String name, arg) {
        NodeDelegate targetNode = findChild(name)

        def value = arg
        if (arg instanceof Closure) {
            // block that wants to be configured
            def childClosure = arg
            childClosure.delegate = targetNode
            childClosure.resolveStrategy = Closure.DELEGATE_FIRST
            value = childClosure.call(targetNode)
        }

        // Force to a string when setting a value
        targetNode.node.value = value.toString()
    }

    def methodMissing(String name, args) {

        NodeDelegate targetNode
        if (args.length == 0) {
            // Just finding the name, created it
            targetNode = findChild(name)
        } else if (args.length == 1) {
            def arg = args[0]
            if (arg instanceof Map) {
                // Attributes, so just looking can create
                targetNode = findChild(name, (Map) arg)

            } else if (arg instanceof Closure) {
                // Identify Node
                targetNode = findChild(name)

                Closure childClosure = arg
                childClosure.delegate = targetNode
                childClosure.resolveStrategy = Closure.DELEGATE_FIRST
                childClosure.call(targetNode)
            } else {
                targetNode = findChild(name)
                targetNode.node.value = arg.toString()
            }
        } else if (args.length == 2) {
            def arg1 = args[0]
            def arg2 = args[1]
            if ( arg1 instanceof Map && arg2 instanceof Closure) {
                targetNode = findChild(name, (Map) arg1)

                Closure childClosure = (Closure) arg2
                childClosure.delegate = targetNode
                childClosure.resolveStrategy = Closure.DELEGATE_FIRST
                childClosure.call(targetNode)
            } else {
                throw new IllegalArgumentException("Two arguments needs to be a Map and a Closure")
            }
        } else {
            throw new IllegalArgumentException("Not sure what to do with this many args")
        }

        return targetNode
    }

    /**
     * Access to the node, for direct manipulation. This is a pattern seen in gradle
     * @return Node from XmlBuilder
     */
    public Node asNode() {
        node
    }
}
