package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobManagement;

abstract class AbstractExtensibleContext implements ExtensibleContext {
    JobManagement jobManagement

    AbstractExtensibleContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    def methodMissing(String name, args) {
        Class<? extends ExtensibleContext> contextType = this.class as Class<? extends ExtensibleContext>
        Node node = jobManagement.callExtension(name, contextType, args)
        if (node == null) {
            throw new MissingMethodException(name, contextType, args)
        }
        addExtensionNode(node)
    }

    protected abstract void addExtensionNode(Node node)
}
