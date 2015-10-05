package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

import static javaposse.jobdsl.dsl.helpers.common.Threshold.THRESHOLD_COLOR_MAP

class DownstreamTriggerBlockContext implements Context {
    private static final Set<String> VALID_THRESHOLDS = ['never'] + THRESHOLD_COLOR_MAP.keySet()

    String buildStepFailure = 'never'
    String failure = 'never'
    String unstable = 'never'

    /**
     * Fails the build step if the triggered build is worse or equal to the threshold.
     *
     * Must be of {@code 'SUCCESS'}, {@code 'UNSTABLE'}, {@code 'FAILURE'} or {@code 'never'}.
     */
    void buildStepFailure(String threshold) {
        checkThreshold(threshold)
        this.buildStepFailure = threshold
    }

    /**
     * Marks this build as failure if the triggered build is worse or equal to the threshold.
     *
     * Must be of {@code 'SUCCESS'}, {@code 'UNSTABLE'}, {@code 'FAILURE'} or {@code 'never'}.
     */
    void failure(String threshold) {
        checkThreshold(threshold)
        this.failure = threshold
    }

    /**
     * Mark this build as unstable if the triggered build is worse or equal to the threshold.
     *
     * Must be of {@code 'SUCCESS'}, {@code 'UNSTABLE'}, {@code 'FAILURE'} or {@code 'never'}.
     */
    void unstable(String threshold) {
        checkThreshold(threshold)
        this.unstable = threshold
    }

    private static void checkThreshold(String threshold) {
        Preconditions.checkArgument(
                VALID_THRESHOLDS.contains(threshold),
                "threshold must be one of ${VALID_THRESHOLDS.join(', ')}"
        )
    }
}
