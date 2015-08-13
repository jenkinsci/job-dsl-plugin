package javaposse.jobdsl.dsl.helpers.common

/**
 * @since 1.38
 */
class Threshold {
    static final Map<String, String> THRESHOLD_COLOR_MAP = [SUCCESS: 'BLUE', UNSTABLE: 'YELLOW', FAILURE: 'RED']
    static final Map<String, Integer> THRESHOLD_ORDINAL_MAP = [SUCCESS: 0, UNSTABLE: 1, FAILURE: 2]
}
