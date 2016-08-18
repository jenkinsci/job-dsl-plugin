package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class LogParserContext implements Context {
    String globalRules
    String projectRules
    boolean unstableOnWarning = false
    boolean failBuildOnError = false
    boolean showGraphs = false

    /**
     * Sets the path to a global rule file.
     */
    void globalRules(String globalRules) {
        this.globalRules = globalRules
    }

    /**
     * Sets the path to a rule file in the workspace.
     */
    void projectRules(String projectRules) {
        this.projectRules = projectRules
    }

    /**
     * If set, marks the build as unstable if any warnings are found. Defaults to {@code false}.
     */
    void unstableOnWarning(boolean unstableOnWarning = true) {
        this.unstableOnWarning = unstableOnWarning
    }

    /**
     * If set, marks the build as failed if any errors are found. Defaults to {@code false}.
     */
    void failBuildOnError(boolean failBuildOnError = true) {
        this.failBuildOnError = failBuildOnError
    }

    /**
     * If set, shows log parser graphs on the project page. Defaults to {@code false}.
     */
    void showGraphs(boolean showGraphs = true) {
        this.showGraphs = showGraphs
    }
}
