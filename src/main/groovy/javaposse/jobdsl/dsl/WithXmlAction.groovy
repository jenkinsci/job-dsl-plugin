package javaposse.jobdsl.dsl

import com.google.common.base.Preconditions
import java.util.logging.Logger
import java.util.logging.Level
import java.util.logging.LogManager

class WithXmlAction {
    private static final Logger LOGGER = Logger.getLogger(WithXmlAction.getName())

    private Closure closure

    WithXmlAction(Closure closure) {
        this.closure = Preconditions.checkNotNull(closure, "Closure has to be set during constructor")
    }

    def execute(Node root) {
        //println "${closure} ${closure.class}"
        closure.delegate = new MissingPropertyToStringDelegate()
        closure.resolveStrategy = Closure.OWNER_FIRST

        // Lock up the arguments with the closure
        def curried = closure.curry(root)

        use(NodeEnhancement) {
            //closure.call(root)
            curried.call()
        }
    }
}

@Category(Node)
class NodeEnhancement {
    private static final Logger LOGGER = Logger.getLogger(NodeEnhancement.getName())

    Node div(String childName) { // a.div(b)
        LOGGER.info("Looking for childName ${childName} ${LOGGER.getLevel()}")

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
        NodeBuilder b = new NodeBuilder();
        Node newNode = (Node) b.invokeMethod("dummyNode", c);
        return newNode.children();
    }

    Node leftShift(String appendChildName) {
        LOGGER.fine("Appending fresh node for ${appendChildName}")
        this.appendNode(appendChildName)
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
        List<Node> newChildren = buildChildren(configureBlock);
        newChildren.each { this.append(it) }
        return this
    }
}

class MissingPropertyToStringDelegate {
    private static final Logger LOGGER = Logger.getLogger(MissingPropertyToStringDelegate.getName())

    /**
     * Make string for div() to do lookup
     */
    def propertyMissing(String propertyName) {
        LOGGER.info("Missing ${propertyName}")
        return propertyName
    }

    String toXml(Node n) {
        def writer = new StringWriter()
        new XmlNodePrinter(new PrintWriter(writer)).print(n)
        writer.toString()
    }

    def methodMissing(String methodName, args) {
        LOGGER.fine("Method missing for ${methodName} ${args}")
        args.each {
            if (it instanceof Closure) {
                // Node Builder will make a better delegate than ourselves
                it.resolveStrategy = Closure.DELEGATE_FIRST
            }
        }
        NodeBuilder b = new NodeBuilder();
        Node newNode = (Node) b.invokeMethod(methodName, args);
        LOGGER.info("Missing ${methodName} created ${toXml(newNode)}")
        return newNode
    }
}



