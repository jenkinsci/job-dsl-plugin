package javaposse.jobdsl.dsl.helpers

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Preconditions.checkNotNull

class NodeParamContext implements Context {
    List<String> pDefaultNodes = []
    String pEligibility = 'AllNodeEligibility'
    String pTrigger = 'allCases'
    String pAllowMultiNodeSelection = 'true'
    String pTriggerConcurrentBuilds = 'false'
    final List<String> allowedNodes

    NodeParamContext(List<String> allowedNodes) {
        this.allowedNodes = allowedNodes
        pDefaultNodes = allowedNodes
    }

    def defaultSlaves(List<String> nodes) {
        checkNotNull(nodes)
        checkArgument(nodes.size() > 0, 'at least one default node must be specified')
        nodes.each {
            checkArgument(it in allowedNodes, it + ' not an allowed slave')
        }
        pDefaultNodes = nodes
    }

    def eligibility(String elig) {
        checkArgument(elig in ['AllNodeEligibility',
                               'IgnoreOfflineNodeEligibility',
                                'IgnoreTempOfflineNodeEligibility'],
                      'eligibility ' + elig + ' is invalid')
        pEligibility = elig
    }

    def trigger(String trigger) {
        pTrigger = trigger
        switch (trigger) {
        case 'success':
        case 'unstable':
        case 'allCases':
            pAllowMultiNodeSelection = 'true'
            pTtriggerConcurrentBuilds = 'false'
            break
        case 'allowMultiSelectionForConcurrentBuilds':
            pAllowMultiNodeSelection = 'true'
            pTriggerConcurrentBuilds = 'true'
            break
        case 'multiSelectionDisallowed':
            pAllowMultiNodeSelection = 'false'
            pTriggerConcurrentBuilds = 'false'
            break
        default:
            throw new IllegalArgumentException('trigger ' + trigger + ' is invalid')
        }
    }
}
