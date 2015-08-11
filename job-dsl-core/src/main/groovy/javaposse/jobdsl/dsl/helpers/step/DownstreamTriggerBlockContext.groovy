package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

import static javaposse.jobdsl.dsl.helpers.common.Threshold.THRESHOLD_COLOR_MAP

class DownstreamTriggerBlockContext implements Context {
    String buildStepFailure = 'FAILURE'
    String failure  = 'FAILURE'
    String unstable = 'UNSTABLE'

    void buildStepFailure(String threshold) {
        checkThreshold(threshold)
        this.buildStepFailure = threshold
    }

    void failure(String threshold) {
        checkThreshold(threshold)
        this.failure = threshold
    }

    void unstable(String threshold) {
        checkThreshold(threshold)
        this.unstable = threshold
    }

    private static void checkThreshold(String threshold) {
        Preconditions.checkArgument(
                THRESHOLD_COLOR_MAP.containsKey(threshold),
                "threshold must be one of ${THRESHOLD_COLOR_MAP.keySet().join(', ')}"
        )
    }
}
