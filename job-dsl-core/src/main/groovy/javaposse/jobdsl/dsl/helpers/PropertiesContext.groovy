package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

class PropertiesContext extends AbstractExtensibleContext {
    List<Node> propertiesNodes = []

    PropertiesContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    @Override
    protected void addExtensionNode(Node node) {
        propertiesNodes << node
    }
}
