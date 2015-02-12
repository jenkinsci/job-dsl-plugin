package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement

class EnvironmentVariableContributorsContext implements Context {
    List<Node> contributors = []
    private final JobManagement jobManagement

    EnvironmentVariableContributorsContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    void populateToolInstallations() {
        jobManagement.requireMinimumPluginVersion('shared-objects', '0.1')
        Node node = new NodeBuilder().'org.jenkinsci.plugins.sharedobjects.ToolInstallationJobProperty' {
            delegate.populateToolInstallation(true)
        }
        contributors << node
    }
}
