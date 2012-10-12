package javaposse.jobdsl.dsl

import java.util.logging.Logger

/**
 * Works like NodeBuilder, but in the context of a parent node. Used as the delegate for WithXmlAction closure.
 */
class MissingPropertyToStringDelegate {
    private static final Logger LOGGER = Logger.getLogger(MissingPropertyToStringDelegate.getName())
    Node root

    MissingPropertyToStringDelegate(Node root) {
        this.root = root
    }
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

        // TODO Investigate re-using root's children for names. Problems with closure and non-left aligned assignments
//        def children = this.children().findAll { child -> // HAVE TO GIVE IT A NAME, OR ELSE IT WON'T WORK
//            child instanceof Node && child.name() == methodName
//        }
//
//        if(children.size() == 1) {
//            // Single child with the exact name, let's re-use it.
//            return children[0]
//        }

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
