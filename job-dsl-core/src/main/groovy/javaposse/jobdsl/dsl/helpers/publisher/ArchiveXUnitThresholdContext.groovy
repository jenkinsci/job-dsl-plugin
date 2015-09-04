package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class ArchiveXUnitThresholdContext implements Context {
    int unstable = 0
    int unstableNew = 0
    int failure = 0
    int failureNew = 0

    /**
     * Sets the build to unstable if the number or percentage of test failures or skiped tests exceeds the threshold.
     * Defaults to 0.
     */
    void unstable(int unstable) {
        this.unstable = unstable
    }

    /**
     * Sets the build to unstable if the number or percentage of new test failures or skiped tests exceeds the
     * threshold. Defaults to 0.
     */
    void unstableNew(int unstableNew) {
        this.unstableNew = unstableNew
    }

    /**
     * Fails the build if the number or percentage of test failures or skiped tests exceeds the threshold.
     * Defaults to 0.
     */
    void failure(int failure) {
        this.failure = failure
    }

    /**
     * Fails the build if the number or percentage of test failures or skiped tests exceeds the threshold.
     * Defaults to 0.
     */
    void failureNew(int failureNew) {
        this.failureNew = failureNew
    }
}
