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
        LOGGER.fine("Missing ${propertyName}")
        return propertyName
    }

    String toXml(Node n) {
        def writer = new StringWriter()
        new XmlNodePrinter(new PrintWriter(writer)).print(n)
        writer.toString()
    }

    Node methodMissing(String methodName, args) {
        LOGGER.fine("Method missing for ${methodName} ${args}")

        args.each {
            if (it instanceof Closure) {
                // Node Builder will make a better delegate than ourselves
                it.resolveStrategy = Closure.DELEGATE_FIRST
            }
        }
        NodeBuilder b = new NodeBuilder();
        Node newNode = (Node) b.invokeMethod(methodName, args);
        LOGGER.fine("Missing ${methodName} created ${toXml(newNode)}")
        return newNode
    }
}
