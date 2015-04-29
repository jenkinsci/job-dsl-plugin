package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

abstract class AbstractExtensibleContext extends AbstractContext implements ExtensibleContext {
    protected AbstractExtensibleContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    Object methodMissing(String name, args) {
        Class<? extends ExtensibleContext> contextType = this.class as Class<? extends ExtensibleContext>
        Node node = jobManagement.callExtension(name, contextType, args)
        if (node == null) {
            throw new MissingMethodException(name, contextType, args)
        }
        addExtensionNode(node)
        null
    }

    protected abstract void addExtensionNode(Node node)
}
