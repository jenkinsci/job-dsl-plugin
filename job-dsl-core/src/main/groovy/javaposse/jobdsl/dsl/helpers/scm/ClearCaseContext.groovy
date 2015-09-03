package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.Context

class ClearCaseContext implements Context {
    List<String> loadRules = []
    List<String> mkviewOptionalParameter = []
    String viewName = 'Jenkins_${USER_NAME}_${NODE_NAME}_${JOB_NAME}${DASH_WORKSPACE_NUMBER}'
    String viewPath = 'view'
    List<String> configSpec = []

    /**
     * Sets the config spec that will be used when creating the view.
     */
    void configSpec(String... configSpec) {
        this.configSpec.addAll(configSpec)
    }

    /**
     * Sets the load rules for the view, which specify the path names of directories and files to load from the VOB.
     */
    void loadRules(String... loadRules) {
        this.loadRules.addAll(loadRules)
    }

    /**
     * Specifies additional arguments when creating a snapshot view using {@code mkview}.
     */
    void mkviewOptionalParameter(String... mkviewOptionalParameter) {
        this.mkviewOptionalParameter.addAll(mkviewOptionalParameter)
    }

    /**
     * Creates a view in the workspace with the specified view tag.
     */
    void viewName(String viewName) {
        this.viewName = viewName
    }

    /**
     * Create a view in the workspace with the specified view path name. If left empty, the view tag will be used as
     * view path.
     */
    void viewPath(String viewPath) {
        this.viewPath = viewPath
    }
}
