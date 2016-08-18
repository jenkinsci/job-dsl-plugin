package javaposse.jobdsl.dsl.helpers.properties

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

@ContextType('com.cloudbees.hudson.plugins.folder.AbstractFolderProperty')
class FolderPropertiesContext extends AbstractExtensibleContext {
    List<Node> propertiesNodes = []

    FolderPropertiesContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    @Override
    protected void addExtensionNode(Node node) {
        propertiesNodes << node
    }
}
