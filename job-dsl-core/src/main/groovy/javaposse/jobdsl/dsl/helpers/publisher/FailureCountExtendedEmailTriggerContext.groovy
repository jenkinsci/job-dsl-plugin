package javaposse.jobdsl.dsl.helpers.publisher

class FailureCountExtendedEmailTriggerContext extends ExtendedEmailTriggerContext {
    int failureCount = 3

    /**
     * Specifies the failure count.
     */
    void failureCount(int failureCount) {
        this.failureCount = failureCount
    }
}
