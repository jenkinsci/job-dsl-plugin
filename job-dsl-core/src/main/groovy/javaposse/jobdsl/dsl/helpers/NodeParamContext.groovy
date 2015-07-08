package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Context

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class NodeParamContext implements Context {
    private static final List<String> ELIGIBILITY = [
            'AllNodeEligibility', 'IgnoreOfflineNodeEligibility', 'IgnoreTempOfflineNodeEligibility'
    ]
    private static final List<String> TRIGGERS = [
            'success', 'unstable', 'allCases', 'allowMultiSelectionForConcurrentBuilds', 'multiSelectionDisallowed'
    ]

    String description
    final List<String> allowedNodes = []
    final List<String> defaultNodes = []
    String trigger = 'multiSelectionDisallowed'
    boolean allowMultiNodeSelection = false
    boolean triggerConcurrentBuilds = false
    String eligibility = 'AllNodeEligibility'

    void description(String description) {
        this.description = description
    }

    void allowedNodes(List<String> nodes) {
        allowedNodes.addAll(nodes)
    }

    void defaultNodes(List<String> nodes) {
        defaultNodes.addAll(nodes)
    }

    void trigger(String trigger) {
        checkArgument(TRIGGERS.contains(trigger), "trigger must be one of ${TRIGGERS.join(', ')}")

        this.trigger = trigger
        switch (trigger) {
            case 'success':
            case 'unstable':
            case 'allCases':
                allowMultiNodeSelection = true
                triggerConcurrentBuilds = false
                break
            case 'allowMultiSelectionForConcurrentBuilds':
                allowMultiNodeSelection = true
                triggerConcurrentBuilds = true
                break
            case 'multiSelectionDisallowed':
                allowMultiNodeSelection = false
                triggerConcurrentBuilds = false
                break
        }
    }

    void eligibility(String eligibility) {
        checkArgument(ELIGIBILITY.contains(eligibility), "eligibility must be one of ${ELIGIBILITY.join(', ')}")
        this.eligibility = eligibility
    }
}
