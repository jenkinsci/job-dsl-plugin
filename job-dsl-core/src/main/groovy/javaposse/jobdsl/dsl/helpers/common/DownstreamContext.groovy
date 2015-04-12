package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

class DownstreamContext implements Context {
    public static final THRESHOLD_COLOR_MAP = ['SUCCESS': 'BLUE', 'UNSTABLE': 'YELLOW', 'FAILURE': 'RED']
    public static final THRESHOLD_ORDINAL_MAP = ['SUCCESS': 0, 'UNSTABLE': 1, 'FAILURE': 2]

    private final List<DownstreamTriggerContext> triggers = []
    protected final JobManagement jobManagement

    DownstreamContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
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
        assert validDownstreamConditionNames.contains(downstreamTriggerContext.condition),
                "Trigger condition has to be one of these values: ${validDownstreamConditionNames.join(',')}"

        triggers << downstreamTriggerContext
    }

    Node createDownstreamNode(boolean isStep = false) {
        NodeBuilder nodeBuilder = NodeBuilder.newInstance()

        String nodeName

        if (isStep) {
            nodeName = 'hudson.plugins.parameterizedtrigger.TriggerBuilder'
        } else {
            nodeName = 'hudson.plugins.parameterizedtrigger.BuildTrigger'
        }

        Node downstreamNode = nodeBuilder."${nodeName}" {
            configs {
                triggers.each { DownstreamTriggerContext trigger ->
                    String configName

                    if (isStep) {
                        configName = 'hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig'
                    } else {
                        configName = 'hudson.plugins.parameterizedtrigger.BuildTriggerConfig'
                    }
                    "${configName}" {
                        projects trigger.projects
                        condition trigger.condition
                        triggerWithNoParameters trigger.triggerWithNoParameters ? 'true' : 'false'

                        if (trigger.hasParameter()) {
                            configs(trigger.createParametersNode().children())
                        } else {
                            configs('class': 'java.util.Collections$EmptyList')
                        }
                        if (isStep && !trigger.blockingThresholds.isEmpty()) {
                            block {
                                trigger.blockingThresholds.each { t ->
                                    "${t.thresholdType}Threshold" {
                                        name t.thresholdName
                                        ordinal t.thresholdOrdinal
                                        color t.thresholdColor
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        downstreamNode
    }

    Set<String> validDownstreamConditionNames = [
            'SUCCESS', 'UNSTABLE', 'UNSTABLE_OR_BETTER', 'UNSTABLE_OR_WORSE', 'FAILED', 'ALWAYS'
    ]
}
