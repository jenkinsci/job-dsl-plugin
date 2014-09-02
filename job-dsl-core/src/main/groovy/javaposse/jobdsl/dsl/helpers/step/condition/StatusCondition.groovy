package javaposse.jobdsl.dsl.helpers.step.condition

import static com.google.common.base.Preconditions.checkArgument

/**
 * Generate config for a status condition.
 *
 * https://wiki.jenkins-ci.org/display/JENKINS/Run+Condition+Plugin
 *
 * <condition class="org.jenkins_ci.plugins.run_condition.core.StatusCondition">
 *   <worstResult>
 *     <ordinal>2</ordinal>
 *   </worstResult>
 *   <bestResult>
 *     <ordinal>2</ordinal>
 *   </bestResult>
 * </condition>
 */
class StatusCondition extends SimpleCondition {

    static statuses = [
        'SUCCESS',
        'UNSTABLE',
        'FAILURE',
        'NOT_BUILT',
        'ABORTED',
    ]

    final String worstResult
    final String bestResult

    StatusCondition(String worstResult, String bestResult) {
        this.name = 'Status'
        this.worstResult = worstResult
        this.bestResult = bestResult

        checkArgument(statuses.contains(worstResult))
        checkArgument(statuses.contains(bestResult))
        checkArgument(statuses.findIndexOf { it == worstResult } >= statuses.findIndexOf { it == bestResult })
    }

    @Override
    void addArgs(NodeBuilder builder) {
        builder.worstResult {
            ordinal statuses.findIndexOf { it == worstResult }
        }
        builder.bestResult {
            ordinal statuses.findIndexOf { it == bestResult }
        }
    }
}
