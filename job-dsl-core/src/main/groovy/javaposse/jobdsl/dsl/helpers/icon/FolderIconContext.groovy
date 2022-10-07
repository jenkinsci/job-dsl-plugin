package javaposse.jobdsl.dsl.helpers.icon

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

@ContextType('com.cloudbees.hudson.plugins.folder.FolderIcon')
class FolderIconContext extends AbstractExtensibleContext {
    Node icon

    FolderIconContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    @Override
    protected void addExtensionNode(Node node) {
        icon = ContextHelper.toNamedNode('icon', node)
    }
}
