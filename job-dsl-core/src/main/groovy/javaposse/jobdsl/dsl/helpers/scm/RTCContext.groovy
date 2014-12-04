package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.helpers.Context

/**
 * DSL for the Team Concert Plugin
 *
 * See https://wiki.jenkins-ci.org/display/JENKINS/Team+Concert+Plugin
 */
class RTCContext implements Context {

    String buildType = 'buildDefinition'
    String buildDefinition = ''
    String buildWorkspace = ''
    String credentialsId = ''
    boolean overrideGlobal = false
    int timeout = 0
	String buildTool = ''
	String serverURI = ''

    void buildType(String buildType) {
        this.buildType = buildType
    }

    void buildDefinition(String buildDefinition) {
        this.buildDefinition = buildDefinition
    }

    void buildWorkspace(String buildWorkspace) {
        this.buildWorkspace = buildWorkspace
    }

    void overrideGlobal(boolean overrideGlobal) {
        this.overrideGlobal = overrideGlobal
    }

    void credentialsId(String credentialsId) {
        this.credentialsId = credentialsId
    }

    void buildTool(String buildTool) {
        this.buildTool = buildTool
    }

    void serverURI(String serverURI) {
        this.serverURI = serverURI
    }

    void timeout(int timeout) {
        this.timeout = timeout
    }
}
