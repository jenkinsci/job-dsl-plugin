package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context

import static com.google.common.base.Preconditions.checkArgument

class RundeckTriggerContext implements Context {
    private static final Set<String> VALID_EXECUTION_STATUSES = [
        'SUCCEEDED', 'FAILED', 'ABORTED'
    ]

    boolean filterJobs = false
    List<String> jobsIdentifiers = []
    List<String> executionStatuses = []

    void filterJobs(boolean filterJobs = true) {
        this.filterJobs = filterJobs
    }

    void jobsIdentifiers(String... jobsIdentifiers) {
        this.jobsIdentifiers.addAll(jobsIdentifiers)
    }

    void executionStatuses(String... executionStatuses) {
        for (String status : executionStatuses) {
            checkArgument(VALID_EXECUTION_STATUSES.contains(status),
                "executionStatus must be one of ${VALID_EXECUTION_STATUSES.join(', ')}")
            this.executionStatuses.add(status)
        }
    }
}
