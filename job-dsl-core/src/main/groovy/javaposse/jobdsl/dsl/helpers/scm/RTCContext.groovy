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

    void buildDefinition(String buildDefinition) {
        this.buildDefinition = buildDefinition
        if (buildDefinition != '') {
            buildType = 'buildDefinition'
        }
    }

    void buildWorkspace(String buildWorkspace) {
        this.buildWorkspace = buildWorkspace
        if (buildWorkspace != '' && this.buildDefinition == '') {
            buildType = 'buildWorkspace'
        }
    }

    void credentialsId(String credentialsId) {
        this.credentialsId = credentialsId
        if (credentialsId != '') {
            this.overrideGlobal = true
        }
    }

    void buildTool(String buildTool) {
        this.buildTool = buildTool
        if (buildTool != '') {
            this.overrideGlobal = true
        }
    }

    void serverURI(String serverURI) {
        this.serverURI = serverURI
        if (serverURI != '') {
            this.overrideGlobal = true
        }
    }

    void timeout(int timeout) {
        this.timeout = timeout
        if (timeout != 0) {
            this.overrideGlobal = true
        }
    }
}
