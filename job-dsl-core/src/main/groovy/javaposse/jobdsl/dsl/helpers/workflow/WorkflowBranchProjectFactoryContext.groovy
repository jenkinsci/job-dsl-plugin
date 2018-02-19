package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.Context

class WorkflowBranchProjectFactoryContext implements Context {
    String scriptPath = "Jenkinsfile"

    /**
     * Path to use for the workflow branch project pipeline script. Default is 'Jenkinsfile'.
     */
    void scriptPath(String scriptPath) {
        this.scriptPath = scriptPath
    }
}
