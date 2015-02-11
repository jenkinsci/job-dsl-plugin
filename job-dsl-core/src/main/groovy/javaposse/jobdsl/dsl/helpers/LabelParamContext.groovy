package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Context

import static com.google.common.base.Preconditions.checkArgument

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

    void defaultValue(String defaultValue) {
        this.defaultValue = defaultValue
    }

    void description(String description) {
        this.description = description
    }

    void allNodes(String trigger = 'allCases', String eligibility = 'AllNodeEligibility') {
        checkArgument(TRIGGERS.contains(trigger), "trigger must be one of ${TRIGGERS.join(', ')}")
        checkArgument(ELIGIBILITY.contains(eligibility), "eligibility must be one of ${ELIGIBILITY.join(', ')}")

        this.allNodes = true
        this.trigger = trigger
        this.eligibility = eligibility
    }
}
