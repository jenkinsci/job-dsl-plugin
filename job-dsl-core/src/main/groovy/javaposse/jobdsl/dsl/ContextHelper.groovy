package javaposse.jobdsl.dsl

import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder

/**
 * Helper class for building nested DSL structures.
 */
class ContextHelper {
    private ContextHelper() {
    }

    static void executeInContext(Closure closure, Context freshContext) {
        if (closure) {
            closure.delegate = freshContext
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        }
    }

    static void executeConfigureBlock(Node node, Closure configureBlock) {
        if (configureBlock) {
            configureBlock.delegate = new MissingPropertyToStringDelegate(node)

            use(NodeEnhancement) {
                configureBlock.call(node)
            }
        }
    }

    static void executeConfigureBlocks(Node node, List<Closure> configureBlocks) {
        configureBlocks.each { executeConfigureBlock(node, it) }
    }

    static Node toNamedNode(String name, Node node) {
        Node namedNode = new Node(null, name, node.attributes(), node.children())
        namedNode.attributes()['class'] = new XmlFriendlyNameCoder().decodeAttribute(node.name().toString())
        namedNode
    }
}
