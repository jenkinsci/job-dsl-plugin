package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobManagement

class PropertiesContext extends AbstractExtensibleContext {
    List<Node> propertiesNodes = []

    PropertiesContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    @Override
    protected void addExtensionNode(Node node) {
        propertiesNodes << node
    }
}
