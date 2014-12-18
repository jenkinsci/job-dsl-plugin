package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.Context

/**
 * DSL for the Clear Case plugin
 *
 * See http://wiki.jenkins-ci.org/display/JENKINS/ClearCase+Plugin
 */
class ClearCaseContext implements Context {
    List<String> loadRules = []
    List<String> mkviewOptionalParameter = []
    String viewName = 'Jenkins_${USER_NAME}_${NODE_NAME}_${JOB_NAME}${DASH_WORKSPACE_NUMBER}'
    String viewPath = 'view'
    List<String> configSpec = []

    void configSpec(String... configSpec) {
        this.configSpec.addAll(configSpec)
    }

    void loadRules(String... loadRules) {
        this.loadRules.addAll(loadRules)
    }

    void mkviewOptionalParameter(String... mkviewOptionalParameter) {
        this.mkviewOptionalParameter.addAll(mkviewOptionalParameter)
    }

    void viewName(String viewName) {
        this.viewName = viewName
    }

    void viewPath(String viewPath) {
        this.viewPath = viewPath
    }
}
