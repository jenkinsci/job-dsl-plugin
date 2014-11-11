package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement

class PropertiesContext extends AbstractExtensibleContext {
    List<Node> propertiesNodes = []

    PropertiesContext(JobManagement jobManagement, Job job) {
        super(jobManagement, job)
    }

    @Override
    protected void addExtensionNode(Node node) {
        propertiesNodes << node
    }
}
