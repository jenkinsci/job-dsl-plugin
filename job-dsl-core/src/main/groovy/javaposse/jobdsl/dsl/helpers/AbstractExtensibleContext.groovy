package javaposse.jobdsl.dsl.helpers

import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder
import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

abstract class AbstractExtensibleContext extends AbstractContext implements ExtensibleContext {
    protected final Item item

    protected AbstractExtensibleContext(JobManagement jobManagement, Item item) {
        super(jobManagement)
        this.item = item
    }

    Object methodMissing(String name, args) {
        Class<? extends ExtensibleContext> contextType = this.class as Class<? extends ExtensibleContext>
        Node node = jobManagement.callExtension(name, item, contextType, args)
        if (node == null) {
            throw new MissingMethodException(name, contextType, args)
        }
        if (node != JobManagement.NO_VALUE) {
            addExtensionNode(node)
        }
        null
    }

    protected abstract void addExtensionNode(Node node)

    protected static Node toNamedNode(String name, Node node) {
        Node namedNode = new Node(null, name, node.attributes(), node.children())
        namedNode.attributes()['class'] = new XmlFriendlyNameCoder().decodeAttribute(node.name().toString())
        namedNode
    }
}
