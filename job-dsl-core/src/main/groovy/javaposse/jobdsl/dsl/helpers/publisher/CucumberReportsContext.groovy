package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class CucumberReportsContext implements Context {
    String jsonReportPath
    String pluginUrlPath
    String fileIncludePattern
    String fileExcludePattern
    boolean failOnSkippedSteps
    boolean failOnPendingSteps
    boolean failOnUndefinedSteps
    boolean failOnMissingSteps
    boolean turnOffFlashCharts
    boolean ignoreFailedTests
    boolean parallelTesting

    /**
     * Sets the path to the JSON report directory in the workspace.
     */
    void jsonReportPath(String jsonReportPath) {
        this.jsonReportPath = jsonReportPath
    }

    /**
     * Sets the path to the Jenkins user content URL.
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
     * Fails the build if there were skipped steps. Defaults to {@code false}.
     */
    void failOnSkipSteps(boolean skippedFails = true) {
        this.failOnSkippedSteps = skippedFails
    }

    /**
     * Fails the build if there were pending steps. Defaults to {@code false}.
     */
    void failOnPendingSteps(boolean pendingFails = true) {
        this.failOnPendingSteps = pendingFails
    }

    /**
     * Fails the build if there were undefined steps. Defaults to {@code false}.
     */
    void failOnUndefinedSteps(boolean undefinedFails = true) {
        this.failOnUndefinedSteps = undefinedFails
    }

    /**
     * Fails the build if there were missing steps. Defaults to {@code false}.
     */
    void failOnMissingSteps(boolean missingFails = true) {
        this.failOnMissingSteps = missingFails
    }

    /**
     * If set, uses JavaScript charts instead of Flash charts. Defaults to {@code false}.
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
