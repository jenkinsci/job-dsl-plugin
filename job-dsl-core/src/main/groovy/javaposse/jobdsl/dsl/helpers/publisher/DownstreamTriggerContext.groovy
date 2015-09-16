package javaposse.jobdsl.dsl.helpers.publisher

import hudson.util.VersionNumber
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

    /**
     * Determines for which results of the current build, the new build(s) will be triggered.
     *
     * Must be one of {@code 'SUCCESS'}, {@code 'UNSTABLE'}, {@code 'UNSTABLE_OR_BETTER'}, {@code 'UNSTABLE_OR_WORSE'},
     * {@code 'FAILED'} or {@code 'ALWAYS'}. If version 2.26 or newer of the Parameterized Trigger Plugin is installed,
     * {@code 'FAILED_OR_BETTER'} can be used as well.
     *
     * @since 1.38
     */
    void condition(String condition) {
        Set<String> validConditions = new HashSet<>(VALID_DOWNSTREAM_CONDITIONS)
        if (!jobManagement.getPluginVersion('parameterized-trigger')?.isOlderThan(new VersionNumber('2.26'))) {
            validConditions << 'FAILED_OR_BETTER'
        }

        Preconditions.checkArgument(
                validConditions.contains(condition),
                "Trigger condition has to be one of these values: ${validConditions.join(',')}"
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
