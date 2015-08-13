package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.helpers.common.AbstractDownstreamTriggerContext

class DownstreamTriggerContext extends AbstractDownstreamTriggerContext {
    private static final Set<String> VALID_DOWNSTREAM_CONDITIONS = [
            'SUCCESS', 'UNSTABLE', 'UNSTABLE_OR_BETTER', 'UNSTABLE_OR_WORSE', 'FAILED', 'ALWAYS'
    ]

    String condition = 'SUCCESS'
    boolean triggerWithNoParameters

    DownstreamTriggerContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    void condition(String condition) {
        Preconditions.checkArgument(
                VALID_DOWNSTREAM_CONDITIONS.contains(condition),
                "Trigger condition has to be one of these values: ${VALID_DOWNSTREAM_CONDITIONS.join(',')}"
        )

        this.condition = condition
    }

    void triggerWithNoParameters(boolean triggerWithNoParameters = true) {
        this.triggerWithNoParameters = triggerWithNoParameters
    }
}
