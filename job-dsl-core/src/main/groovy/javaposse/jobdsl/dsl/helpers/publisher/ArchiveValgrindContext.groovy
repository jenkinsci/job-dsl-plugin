package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

class ArchiveValgrindContext extends AbstractContext {
    ArchiveValgrindThresholdContext failThresholdsContext = new ArchiveValgrindThresholdContext()
    ArchiveValgrindThresholdContext unstableThresholdsContext = new ArchiveValgrindThresholdContext()

    boolean publishResultsForAbortedBuilds
    boolean publishResultsForFailedBuilds
    boolean failBuildOnMissingReports
    boolean failBuildOnInvalidReports

    ArchiveValgrindContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Specifies thresholds for failed tests.
     */
    void failThresholds(@DslContext(ArchiveValgrindThresholdContext) Closure thresholdsClosure) {
        ContextHelper.executeInContext(thresholdsClosure, failThresholdsContext)
    }

    /**
     * Specifies thresholds for unstable tests.
     */
    void unstableThresholds(@DslContext(ArchiveValgrindThresholdContext) Closure thresholdsClosure) {
        ContextHelper.executeInContext(thresholdsClosure, unstableThresholdsContext)
    }

    /**
     * Mark build as failed if no report was found. Defaults to {@code false}.
     */
    void failOnMissingReports(boolean failMissing = true) {
        failBuildOnMissingReports = failMissing
    }

    /**
     * Mark build as failed on malformed xml reports. Defaults to {@code false}.
     */
    void failOnInvalidReports(boolean failInvalid = true) {
        failBuildOnInvalidReports = failInvalid
    }

    /**
     * Publish report when job is marked as aborted.
     * Defaults to {@code false}.
     */
    void publishAborted(boolean publish = true) {
        publishResultsForAbortedBuilds = publish
    }

    /**
     * Publish report when job is marked as failed.
     * Defaults to {@code false}.
     */
    void publishFailed(boolean publish = true) {
        publishResultsForFailedBuilds = publish
    }
}
