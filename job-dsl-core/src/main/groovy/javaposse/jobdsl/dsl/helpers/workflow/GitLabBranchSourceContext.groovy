package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class GitLabBranchSourceContext extends AbstractContext {
    String id
    String serverName
    String credentialsId
    String projectOwner
    String projectPath

    GitLabBranchSourceContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Specifies a unique ID for this branch source.
     */
    void id(String id) {
        this.id = id
    }

    /**
     * Sets the defined connection to GitLab server.
     */
    void serverName(String serverName) {
        this.serverName = serverName
    }

    /**
     * Sets checkout credentials for authentication with GitLab.
     */
    void credentialsId(String credentialsId) {
        this.credentialsId = credentialsId
    }

    /**
     * Sets the name of the GitLab Group or GitLab User.
     */
    void projectOwner(String projectOwner) {
        this.projectOwner = projectOwner
    }

    /**
     * Sets the full path of the GitLab project.
     */
    void projectPath(String projectPath) {
        this.projectPath = projectPath
    }
}
