package javaposse.jobdsl.dsl

import java.util.logging.Logger

/**
 * Add div and leftShift operators to Node.
 * * div - Will return the first child that matches name, and if it doesn't exists, it creates
 * * leftShift - Take node (or configure block to create) and appends as child, as opposed to plus which appends as a peer
 */
@Category(Node)
class NodeEnhancement {
    private static final Logger LOGGER = Logger.getLogger(NodeEnhancement.name)

    Node div(Node orphan) {
        LOGGER.fine("Looking for child node ${orphan}")
        def childName = orphan.name()
        def children = this.children().findAll { child -> // HAVE TO GIVE IT A NAME, OR ELSE IT WON'T WORK
            child instanceof Node && child.name() == childName && child.attributes().entrySet().containsAll(orphan.attributes().entrySet())
        }
        if (children.size() == 0) {
            LOGGER.fine("Creating node for ${childName}")
            // Create node using just name
            this.append(orphan)
            orphan
        } else {
            // Return first childName, that's the contract for div
            LOGGER.fine("Using first found childName for ${childName}")
            Node found = children[0]

            // Copy over value and attribute from orphan if it has one.
            if (orphan.value() != null) {
                found.setValue(orphan.value())
            }
            orphan.attributes().each { k, v ->
                found.attributes().put(k, v)
            }

            found
        }
    }

    Node div(String childName) { // a.div(b)
        LOGGER.fine("Looking for childName ${childName} ${LOGGER.level}")

        def children = this.children().findAll { child -> // HAVE TO GIVE IT A NAME, OR ELSE IT WON'T WORK
            child instanceof Node && child.name() == childName
        }
        if (children.size() == 0) {
            LOGGER.fine("Creating node for ${childName}")
            // Create node using just name
            return this.appendNode(childName)
        } else {
            // Return first childName, that's the contract for div
            LOGGER.fine("Using first found childName for ${childName}")
            return children[0]
        }
    }

    private static List<Node> buildChildren(c) {
        NodeBuilder b = new NodeBuilder()
        Node newNode = (Node) b.invokeMethod("dummyNode", c)
        return newNode.children()
    }

    Node leftShift(boolean boolValue) {
        leftShift(boolValue ? 'true' : 'false')
    }

    Node leftShift(String appendChildName) {
        LOGGER.fine("Setting value of ${appendChildName} for ${this.name()}")
        this.setValue(appendChildName)
        return this
    }

    Node leftShift(Node child) {
        LOGGER.fine("Appending node ${child} to ${this}")
        this.append(child)
        return this
    }

    Node leftShift(Closure configureBlock) {
        LOGGER.fine("Appending block from ${configureBlock}")
        configureBlock.resolveStrategy = Closure.DELEGATE_FIRST
        List<Node> newChildren = buildChildren(configureBlock)
        newChildren.each { this.append(it) }
        return this
    }
}
