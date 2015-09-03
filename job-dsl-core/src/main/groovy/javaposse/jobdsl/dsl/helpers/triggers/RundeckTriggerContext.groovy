package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class RundeckTriggerContext implements Context {
    private static final Set<String> VALID_EXECUTION_STATUSES = [
        'SUCCEEDED', 'FAILED', 'ABORTED'
    ]

    boolean filterJobs
    List<String> jobIdentifiers = []
    Set<String> executionStatuses = []

    /**
     * Only triggers a new build if the Rundeck execution at the origin of the notification matches one of the following
     * Rundeck job identifier. The identifier can be either a job UUID or a job reference.
     */
    void jobIdentifiers(String... jobIdentifiers) {
        this.filterJobs = true
        this.jobIdentifiers.addAll(jobIdentifiers)
    }

    /**
     *  Only triggers a build if the status of the Rundeck execution at the origin of the notification matches one of
     *  the given statuses.
     *
     * Possible values for are {@code 'SUCCEEDED'}, {@code 'FAILED'} and {@code 'ABORTED'}.
     */
    void executionStatuses(String... executionStatuses) {
        executionStatuses.each { String status ->
            checkArgument(
                    VALID_EXECUTION_STATUSES.contains(status),
                    "executionStatuses must be one of ${VALID_EXECUTION_STATUSES.join(', ')}"
            )
            this.executionStatuses.add(status)
        }
    }
}
