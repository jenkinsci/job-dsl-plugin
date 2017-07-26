package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

@ContextType('jenkins.branch.MultiBranchProjectFactory')
class MultiBranchProjectFactoryContext extends AbstractExtensibleContext {
    final List<Node> projectFactoryNodes = []

    MultiBranchProjectFactoryContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    @Override
    protected void addExtensionNode(final Node node) {
        projectFactoryNodes << node
    }
}
