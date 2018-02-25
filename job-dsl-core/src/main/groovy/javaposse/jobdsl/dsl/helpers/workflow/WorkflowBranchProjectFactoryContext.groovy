package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class WorkflowBranchProjectFactoryContext extends AbstractContext {
    String scriptPath = 'Jenkinsfile'

    WorkflowBranchProjectFactoryContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Path to use for the workflow branch project pipeline script. Defaults to `Jenkinsfile`.
     *
     * @since 1.69
     */
    @RequiresPlugin(id = 'workflow-multibranch', minimumVersion = '2.15')
    void scriptPath(String scriptPath) {
        this.scriptPath = scriptPath
    }
}
