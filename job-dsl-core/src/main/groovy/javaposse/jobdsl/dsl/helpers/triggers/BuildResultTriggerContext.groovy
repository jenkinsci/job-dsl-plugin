package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context

import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

/**
 * Context for configuring the build results trigger functionality.
 */
class BuildResultTriggerContext implements Context {
    boolean combinedJobs
    final Map<String, BuildResult[]> buildResultInfos = [:]

    /**
     * Combine all jobs informations. Defaults to {@code false}.
     */
    void combinedJobs(boolean combinedJobs = true) {
        this.combinedJobs = combinedJobs
    }

    /**
     * Sets the jobs build results to check.
     */
    void triggerInfo(String jobNames, BuildResult... buildResults) {
        checkNotNullOrEmpty(jobNames, 'Jobs names are required')

        buildResultInfos[jobNames] = buildResults
    }

    enum BuildResult {
        SUCCESS, UNSTABLE, FAILURE, NOT_BUILT, ABORTED
    }
}
