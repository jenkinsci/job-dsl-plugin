package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class ArchiveXUnitThresholdContext implements Context {
    int unstable = 0
    int unstableNew = 0
    int failure = 0
    int failureNew = 0

    void unstable(int unstable) {
        this.unstable = unstable
    }

    void unstableNew(int unstableNew) {
        this.unstableNew = unstableNew
    }

    void failure(int failure) {
        this.failure = failure
    }

    void failureNew(int failureNew) {
        this.failureNew = failureNew
    }
}
