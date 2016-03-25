package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class LogParserContext implements Context {
    String globalRules
    String projectRules
    boolean unstableOnWarning = false
    boolean failBuildOnError = false
    boolean showGraphs = false

    /**
     * Path to rule file in global setup.
     */
    void globalRules(String globalRules) {
        this.globalRules = globalRules
    }

    /**
     * Path to rule file in workspace.
     */
    void projectRules(String projectRules) {
        this.projectRules = projectRules
    }

    /**
     * If set, marks the build Unstable if any warnings are found. Defaults to
     * {@code false}.
     */
    void unstableOnWarning(boolean unstableOnWarning = true) {
        this.unstableOnWarning = unstableOnWarning
    }

    /**
     * If set, marks the build Failed if any errors are found. Defaults to
     * {@code false}.
     */
    void failBuildOnError(boolean failBuildOnError = true) {
        this.failBuildOnError = failBuildOnError
    }

    /**
     * If set, shows Log parser graphs on project page. Defaults to
     * {@code false}.
     */
    void showGraphs(boolean showGraphs = true) {
        this.showGraphs = showGraphs
    }
}
