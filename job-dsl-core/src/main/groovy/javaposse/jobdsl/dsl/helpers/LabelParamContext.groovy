package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Context

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class LabelParamContext implements Context {
    private static final List<String> ELIGIBILITY = [
            'AllNodeEligibility', 'IgnoreOfflineNodeEligibility', 'IgnoreTempOfflineNodeEligibility'
    ]
    private static final List<String> TRIGGERS = [
            'success', 'unstable', 'allCases'
    ]

    String defaultValue
    String description
    boolean allNodes = false
    String trigger = 'allCases'
    String eligibility = 'AllNodeEligibility'

    /**
     * Sets the default value for the parameter.
     */
    void defaultValue(String defaultValue) {
        this.defaultValue = defaultValue
    }

    /**
     * Sets a description for the parameter.
     */
    void description(String description) {
        this.description = description
    }

    /**
     * Defines in which case a build on the next node should be triggered.
     *
     * The {@code trigger} argument defines in which case a build on the next node should be triggered, must be one of
     * {@code 'allCases'}, {@code 'success'} or {@code 'unstable'}.
     *
     * The {@code eligibility} argument defines how selected offline nodes should be handled, must be one of
     * {@code 'AllNodeEligibility'}, {@code 'IgnoreOfflineNodeEligibility'} or
     * {@code 'IgnoreTempOfflineNodeEligibility'}.
     */
    void allNodes(String trigger = 'allCases', String eligibility = 'AllNodeEligibility') {
        checkArgument(TRIGGERS.contains(trigger), "trigger must be one of ${TRIGGERS.join(', ')}")
        checkArgument(ELIGIBILITY.contains(eligibility), "eligibility must be one of ${ELIGIBILITY.join(', ')}")

        this.allNodes = true
        this.trigger = trigger
        this.eligibility = eligibility
    }
}
