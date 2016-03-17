package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context

import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

/**
 * Context for configuring the build results trigger functionality.
 */
class BuildResultTriggerContext implements Context {
    String crontab = ''
    boolean combinedJobs
    final Map<String, List<BuildResult>> buildResultInfos = [:]

    BuildResultTriggerContext(String cron = null) {
        if (cron) {
            this.crontab = cron
        }
    }

    /**
     * Sets the cron schedule. Defaults to {@code null}.
     */
    void cron(String cron) {
        this.crontab = cron
    }

    /**
     * Combine all jobs informations.
     */
    void combinedJobs(boolean combinedJobs = true) {
        this.combinedJobs = combinedJobs
    }

    /**
     * Sets the jobs build results to check.
     * Defaults 'buildResults' to {@code 'BuildResult.SUCCESS'}.
     */
    void triggerInfo(String jobNames, BuildResult... buildResults = [BuildResult.SUCCESS]) {
        checkNotNullOrEmpty(jobNames, 'Jobs names are required!')

        buildResultInfos[jobNames] = buildResults ?: [BuildResult.SUCCESS]
    }

    enum BuildResult {
        SUCCESS, UNSTABLE, FAILURE, NOT_BUILT, ABORTED
    }
}
