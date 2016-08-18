package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.helpers.common.AbstractDownstreamTriggerContext

class DownstreamTriggerContext extends AbstractDownstreamTriggerContext {
    private static final Set<String> VALID_DOWNSTREAM_CONDITIONS = [
            'SUCCESS', 'UNSTABLE', 'UNSTABLE_OR_BETTER', 'UNSTABLE_OR_WORSE', 'FAILED', 'ALWAYS', 'FAILED_OR_BETTER'
    ]

    String condition = 'SUCCESS'
    boolean triggerWithNoParameters

    DownstreamTriggerContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    /**
     * Determines for which results of the current build, the new build(s) will be triggered.
     *
     * Must be one of {@code 'SUCCESS'}, {@code 'UNSTABLE'}, {@code 'UNSTABLE_OR_BETTER'}, {@code 'UNSTABLE_OR_WORSE'},
     * {@code 'FAILED'}, {@code 'FAILED_OR_BETTER'} or {@code 'ALWAYS'}.
     *
     * @since 1.38
     */
    void condition(String condition) {
        Preconditions.checkArgument(
                VALID_DOWNSTREAM_CONDITIONS.contains(condition),
                "Trigger condition has to be one of these values: ${VALID_DOWNSTREAM_CONDITIONS.join(',')}"
        )

        this.condition = condition
    }

    /**
     * Triggers a build even when there are currently no parameters defined.
     *
     * @since 1.38
     */
    void triggerWithNoParameters(boolean triggerWithNoParameters = true) {
        this.triggerWithNoParameters = triggerWithNoParameters
    }
}
