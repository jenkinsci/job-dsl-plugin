package javaposse.jobdsl.dsl.helpers.common

/**
 * @since 1.38
 */
class Threshold {
    static final Map<String, String> THRESHOLD_COLOR_MAP = [
            SUCCESS: 'BLUE', UNSTABLE: 'YELLOW', FAILURE: 'RED', ABORTED: 'ABORTED'
    ]
    static final Map<String, Integer> THRESHOLD_ORDINAL_MAP = [
            SUCCESS: 0, UNSTABLE: 1, FAILURE: 2, ABORTED: 4
    ]

    /**
     * @since 1.61
     */
    static final Map<String, Boolean> THRESHOLD_COMPLETED_BUILD = [
            SUCCESS: true, UNSTABLE: true, FAILURE: true, ABORTED: false
    ]
}
