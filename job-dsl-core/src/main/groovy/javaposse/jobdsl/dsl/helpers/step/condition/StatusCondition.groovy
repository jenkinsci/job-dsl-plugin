package javaposse.jobdsl.dsl.helpers.step.condition

import static com.google.common.base.Preconditions.checkArgument

/**
 * Generate config for a status condition.
 *
 * https://wiki.jenkins-ci.org/display/JENKINS/Run+Condition+Plugin
 */
class StatusCondition extends SimpleCondition {
    private static final STATUSES = [
            'SUCCESS',
            'UNSTABLE',
            'FAILURE',
            'NOT_BUILT',
            'ABORTED',
    ]

    final int worstResult
    final int bestResult

    StatusCondition(String worstResult, String bestResult) {
        this.name = 'Status'
        this.worstResult = STATUSES.findIndexOf { it == worstResult }
        this.bestResult = STATUSES.findIndexOf { it == bestResult }

        checkArgument(this.worstResult > -1, "worstResult must be one of ${STATUSES.join(',')}")
        checkArgument(this.bestResult > -1, "bestResult must be one of ${STATUSES.join(',')}")
        checkArgument(this.worstResult >= this.bestResult, 'worstResult must be equal or worse than bestResult')
    }

    @Override
    void addArgs(NodeBuilder builder) {
        builder.worstResult {
            ordinal worstResult
        }
        builder.bestResult {
            ordinal bestResult
        }
    }
}
