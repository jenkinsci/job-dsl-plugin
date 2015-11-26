package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class CucumberReportsContext implements Context {
    String jsonReportPath
    String pluginUrlPath
    String fileIncludePattern
    String fileExcludePattern
    boolean skippedFails = false
    boolean pendingFails = false
    boolean undefinedFails = false
    boolean missingFails = false
    boolean turnOffFlashCharts = false
    boolean ignoreFailedTests = false
    boolean parallelTesting = false

    /**
     * Sets the path to the Json Report directory in the workspace.
     */
    void jsonReportPath(String jsonReportPath) {
        this.jsonReportPath = jsonReportPath
    }

    /**
     * Sets the path to the jenkins user content url.
     */
    void pluginUrlPath(String pluginUrlPath) {
        this.pluginUrlPath = pluginUrlPath
    }

    /**
     * Specifies files that will be shown.
     */
    void fileIncludePattern(String fileIncludePattern) {
        this.fileIncludePattern = fileIncludePattern
    }

    /**
     * Specifies files that will not be shown.
     */
    void fileExcludePattern(String fileExcludePattern) {
        this.fileExcludePattern = fileExcludePattern
    }

    /**
     * Fails builds if there were skipped steps. Defaults to {@code false}.
     */
    void skippedFails(boolean skippedFails = true) {
        this.skippedFails = skippedFails
    }

    /**
     * Fails builds if there were pending steps. Defaults to {@code false}.
     */
    void pendingFails(boolean pendingFails = true) {
        this.pendingFails = pendingFails
    }

    /**
     * Fails builds if there were undefined steps. Defaults to {@code false}.
     */
    void undefinedFails(boolean undefinedFails = true) {
        this.undefinedFails = undefinedFails
    }

    /**
     * Fails builds if there were missing steps. Defaults to {@code false}.
     */
    void missingFails(boolean missingFails = true) {
        this.missingFails = missingFails
    }

    /**
     * If set, uses javascript charts instead of flash charts. Defaults to {@code false}.
     */
    void turnOffFlashCharts(boolean turnOffFlashCharts = true) {
        this.turnOffFlashCharts = turnOffFlashCharts
    }

    /**
     * Fails builds if these tests fail. Defaults to {@code false}.
     */
    void ignoreFailedTests(boolean ignoreFailedTests = true) {
        this.ignoreFailedTests = ignoreFailedTests
    }

    /**
     * If set, runs the same test in parallel for multiple devices. Defaults to {@code false}.
     */
    void parallelTesting(boolean parallelTesting = true) {
        this.parallelTesting = parallelTesting
    }
}
