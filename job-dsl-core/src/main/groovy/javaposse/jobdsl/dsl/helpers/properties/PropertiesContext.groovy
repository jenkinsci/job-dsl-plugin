package javaposse.jobdsl.dsl.helpers.properties

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.AbstractExtensibleContext

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
