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

    /**
     * Sets a description for the parameter.
     */
    void description(String description) {
        this.description = description
    }

    /**
     * Specifies the nodes available for selection when job gets triggered manually. Defaults to all nodes if omitted.
     */
    void allowedNodes(List<String> nodes) {
        allowedNodes.addAll(nodes)
    }

    /**
     * Specifies the nodes used when job gets triggered by anything else then manually. Empty by default.
     */
    void defaultNodes(List<String> nodes) {
        defaultNodes.addAll(nodes)
    }

    /**
     * Defines in which case a build on the next node should be triggered.
     *
     * Must be one of {@code 'allCases'}, {@code 'success'}, {@code 'unstable'},
     * {@code 'allowMultiSelectionForConcurrentBuilds'} or {@code 'multiSelectionDisallowed'} (default).
     */
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

    /**
     * Defines how selected offline nodes should be handled.
     *
     * Must be one of {@code 'AllNodeEligibility'} (default), {@code 'IgnoreOfflineNodeEligibility'} or
     * {@code 'IgnoreTempOfflineNodeEligibility'}.
     */
    void eligibility(String eligibility) {
        checkArgument(ELIGIBILITY.contains(eligibility), "eligibility must be one of ${ELIGIBILITY.join(', ')}")
        this.eligibility = eligibility
    }
}
