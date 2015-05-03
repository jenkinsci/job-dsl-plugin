package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context

import static com.google.common.base.Preconditions.checkArgument

class RundeckTriggerContext implements Context {
    private static final Set<String> VALID_EXECUTION_STATUSES = [
        'SUCCEEDED', 'FAILED', 'ABORTED'
    ]

    boolean filterJobs
    List<String> jobIdentifiers = []
    Set<String> executionStatuses = []

    void jobIdentifiers(String... jobIdentifiers) {
        this.filterJobs = true
        this.jobIdentifiers.addAll(jobIdentifiers)
    }

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
