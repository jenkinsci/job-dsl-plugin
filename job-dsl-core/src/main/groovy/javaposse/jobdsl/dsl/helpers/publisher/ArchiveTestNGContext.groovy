package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class ArchiveTestNGContext extends AbstractContext {
    boolean escapeTestDescription = true
    boolean escapeExceptionMessages = true
    boolean showFailedBuildsInTrendGraph = false
    boolean markBuildAsUnstableOnSkippedTests = false
    boolean markBuildAsFailureOnFailedConfiguration = false

    protected ArchiveTestNGContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Escape the description string associated with the test method in the report.
     * Defaults to {@code true}.
     */
    void escapeTestDescription(boolean escapeTestDescription = true) {
        this.escapeTestDescription = escapeTestDescription
    }

    /**
     * Escape the test method's exception messages. in the report.
     * Defaults to {@code true}.
     */
    void escapeExceptionMessages(boolean escapeExceptionMessages = true) {
        this.escapeExceptionMessages = escapeExceptionMessages
    }

    /**
     * Include results from failed builds in the trend graph.
     * Defaults to {@code false}.
     */
    void showFailedBuildsInTrendGraph(boolean showFailedBuildsInTrendGraph = false) {
        this.showFailedBuildsInTrendGraph = showFailedBuildsInTrendGraph
    }

    /**
     * Mark the build as unstable if skipped configuration or test methods are found in results.
     * If build result is worse that UNSTABLE, this option has no effect.
     * Defaults to {@code false}.
     */
    void markBuildAsUnstableOnSkippedTests(boolean markBuildAsUnstableOnSkippedTests = true) {
        this.markBuildAsUnstableOnSkippedTests = markBuildAsUnstableOnSkippedTests
    }

    /**
     * Distinguish between failing tests and failing configuration methods
     * Defaults to {@code false}.
     */
    void markBuildAsFailureOnFailedConfiguration(boolean markBuildAsFailureOnFailedConfiguration = true) {
        this.markBuildAsFailureOnFailedConfiguration = markBuildAsFailureOnFailedConfiguration
    }

}
