package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class RTCContext extends AbstractContext {
    String buildType
    String buildDefinition
    String buildWorkspace
    String credentialsId
    boolean overrideGlobal
    int timeout
    String buildTool
    String serverURI

    RTCContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Use a build definition for RTC integration.
     */
    void buildDefinition(String buildDefinition) {
        this.buildDefinition = buildDefinition
        this.buildType = 'buildDefinition'
    }

    /**
     * Fetch from a build workspace.
     */
    void buildWorkspace(String buildWorkspace) {
        this.buildWorkspace = buildWorkspace
        this.buildType = 'buildWorkspace'
    }

    /**
     * Overrides the global RTC repository connection.
     */
    void connection(String buildTool, String credentials, String serverURI, int timeout) {
        this.overrideGlobal = true
        this.buildTool = buildTool
        this.credentialsId = credentials
        this.serverURI = serverURI
        this.timeout = timeout
    }
}
