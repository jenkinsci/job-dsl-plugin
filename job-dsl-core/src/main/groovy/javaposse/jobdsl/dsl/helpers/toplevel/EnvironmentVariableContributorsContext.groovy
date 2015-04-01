package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class EnvironmentVariableContributorsContext implements Context {
    List<Node> contributors = []
    private final JobManagement jobManagement

    EnvironmentVariableContributorsContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    @RequiresPlugin(id = 'shared-objects', minimumVersion = '0.1')
    void populateToolInstallations() {
        Node node = new NodeBuilder().'org.jenkinsci.plugins.sharedobjects.ToolInstallationJobProperty' {
            delegate.populateToolInstallation(true)
        }
        contributors << node
    }
}
