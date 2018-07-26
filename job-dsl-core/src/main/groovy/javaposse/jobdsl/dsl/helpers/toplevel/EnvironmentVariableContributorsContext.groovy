package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class EnvironmentVariableContributorsContext extends AbstractExtensibleContext {
    List<Node> contributors = []

    EnvironmentVariableContributorsContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    /**
     * Populates the locations of installed tools as environment variables.
     */
    @RequiresPlugin(id = 'shared-objects', minimumVersion = '0.1')
    void populateToolInstallations() {
        Node node = new NodeBuilder().'org.jenkinsci.plugins.sharedobjects.ToolInstallationJobProperty' {
            delegate.populateToolInstallation(true)
        }
        contributors << node
    }

    @Override
    protected void addExtensionNode(Node node) {
        contributors << node
    }
}
