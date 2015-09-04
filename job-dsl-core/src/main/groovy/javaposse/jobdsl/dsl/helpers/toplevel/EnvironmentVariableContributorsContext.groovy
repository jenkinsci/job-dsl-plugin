package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class EnvironmentVariableContributorsContext extends AbstractContext {
    List<Node> contributors = []

    EnvironmentVariableContributorsContext(JobManagement jobManagement) {
        super(jobManagement)
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
}
