package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class ArchiveXUnitThresholdContext implements Context {
    Integer unstable = 0
    Integer unstableNew = 0
    Integer failure = 0
    Integer failureNew = 0

    /**
     * Sets the build to unstable if the number or percentage of test failures or skipped tests exceeds the threshold.
     * Defaults to 0.
     */
    void unstable(Integer unstable) {
        this.unstable = unstable
    }

    /**
     * Sets the build to unstable if the number or percentage of new test failures or skipped tests exceeds the
     * threshold. Defaults to 0.
     */
    void unstableNew(Integer unstableNew) {
        this.unstableNew = unstableNew
    }

    /**
     * Fails the build if the number or percentage of test failures or skipped tests exceeds the threshold.
     * Defaults to 0.
     */
    void failure(Integer failure) {
        this.failure = failure
    }

    /**
     * Fails the build if the number or percentage of test failures or skipped tests exceeds the threshold.
     * Defaults to 0.
     */
    void failureNew(Integer failureNew) {
        this.failureNew = failureNew
    }
}
