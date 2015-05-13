package javaposse.jobdsl.dsl.helpers.common

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.common.DownstreamTriggerContext.BlockingThreshold

class DownstreamContext extends AbstractContext {
    public static final Map<String, String> THRESHOLD_COLOR_MAP = [SUCCESS: 'BLUE', UNSTABLE: 'YELLOW', FAILURE: 'RED']
    public static final Map<String, Integer> THRESHOLD_ORDINAL_MAP = [SUCCESS: 0, UNSTABLE: 1, FAILURE: 2]
    private static final Set<String> VALID_DOWNSTREAM_CONDITIONS = [
            'SUCCESS', 'UNSTABLE', 'UNSTABLE_OR_BETTER', 'UNSTABLE_OR_WORSE', 'FAILED', 'ALWAYS'
    ]

    private final List<DownstreamTriggerContext> triggers = []

    DownstreamContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    void trigger(String projects, @DslContext(DownstreamTriggerContext) Closure downstreamTriggerClosure = null) {
        trigger(projects, null, downstreamTriggerClosure)
    }

    void trigger(String projects, String condition,
                 @DslContext(DownstreamTriggerContext) Closure downstreamTriggerClosure = null) {
        trigger(projects, condition, false, downstreamTriggerClosure)
    }

    void trigger(String projects, String condition, boolean triggerWithNoParameters,
                 @DslContext(DownstreamTriggerContext) Closure downstreamTriggerClosure = null) {
        trigger(projects, condition, triggerWithNoParameters, [:], downstreamTriggerClosure)
    }

    void trigger(String projects, String condition, boolean triggerWithNoParameters,
                 Map<String, String> blockingThresholds,
                 @DslContext(DownstreamTriggerContext) Closure downstreamTriggerClosure = null) {
        DownstreamTriggerContext downstreamTriggerContext = new DownstreamTriggerContext(jobManagement)
        downstreamTriggerContext.projects = projects
        downstreamTriggerContext.condition = condition ?: 'SUCCESS'
        downstreamTriggerContext.triggerWithNoParameters = triggerWithNoParameters
        downstreamTriggerContext.blockingThresholdsFromMap(blockingThresholds)

        ContextHelper.executeInContext(downstreamTriggerClosure, downstreamTriggerContext)

        // Validate this trigger
        Preconditions.checkArgument(
                VALID_DOWNSTREAM_CONDITIONS.contains(downstreamTriggerContext.condition),
                "Trigger condition has to be one of these values: ${VALID_DOWNSTREAM_CONDITIONS.join(',')}"
        )

        triggers << downstreamTriggerContext
    }

    Node createDownstreamNode(boolean isStep) {
        String nodeName
        String configName
        if (isStep) {
            nodeName = 'hudson.plugins.parameterizedtrigger.TriggerBuilder'
            configName = 'hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig'
        } else {
            nodeName = 'hudson.plugins.parameterizedtrigger.BuildTrigger'
            configName = 'hudson.plugins.parameterizedtrigger.BuildTriggerConfig'
        }

        NodeBuilder.newInstance()."${nodeName}" {
            configs {
                triggers.each { DownstreamTriggerContext trigger ->
                    "${configName}" {
                        projects(trigger.projects)
                        condition(isStep ? 'ALWAYS' : trigger.condition)
                        triggerWithNoParameters(trigger.triggerWithNoParameters)

                        if (trigger.hasParameter()) {
                            configs(trigger.createParametersNode().children())
                        } else {
                            configs('class': 'java.util.Collections$EmptyList')
                        }
                        if (isStep && !trigger.blockingThresholds.empty) {
                            block {
                                trigger.blockingThresholds.each { BlockingThreshold threshold ->
                                    "${threshold.thresholdType}Threshold" {
                                        name(threshold.thresholdName)
                                        ordinal(threshold.thresholdOrdinal)
                                        color(threshold.thresholdColor)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
