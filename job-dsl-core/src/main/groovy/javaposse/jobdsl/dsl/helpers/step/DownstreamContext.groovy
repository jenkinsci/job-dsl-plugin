package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions

import static javaposse.jobdsl.dsl.helpers.common.Threshold.THRESHOLD_COLOR_MAP
import static javaposse.jobdsl.dsl.helpers.common.Threshold.THRESHOLD_ORDINAL_MAP

class DownstreamContext extends AbstractContext {
    private static final Set<String> BLOCKING_THRESHOLD_TYPES = ['buildStepFailure', 'failure', 'unstable']

    List<Node> configs = []

    DownstreamContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    void trigger(String projects, @DslContext(DownstreamTriggerContext) Closure downstreamTriggerClosure = null) {
        addTrigger(projects, null, false, downstreamTriggerClosure)
    }

    @Deprecated
    @SuppressWarnings(['UnusedMethodParameter', 'GroovyUnusedDeclaration'])
    void trigger(String projects, String condition,
                 @DslContext(DownstreamTriggerContext) Closure downstreamTriggerClosure = null) {
        jobManagement.logDeprecationWarning()
        addTrigger(projects, null, false, downstreamTriggerClosure)
    }

    @Deprecated
    @SuppressWarnings(['UnusedMethodParameter', 'GroovyUnusedDeclaration'])
    void trigger(String projects, String condition, boolean triggerWithNoParameters,
                 @DslContext(DownstreamTriggerContext) Closure downstreamTriggerClosure = null) {
        jobManagement.logDeprecationWarning()
        addTrigger(projects, null, triggerWithNoParameters, downstreamTriggerClosure)
    }

    @Deprecated
    @SuppressWarnings(['UnusedMethodParameter', 'GroovyUnusedDeclaration'])
    void trigger(String projects, String condition, boolean triggerWithNoParameters,
                 Map<String, String> blockingThresholds,
                 @DslContext(DownstreamTriggerContext) Closure downstreamTriggerClosure = null) {
        jobManagement.logDeprecationWarning()
        addTrigger(projects, blockingThresholds, triggerWithNoParameters, downstreamTriggerClosure)
    }

    private void addTrigger(String projects, Map<String, String> blockingThresholds, boolean triggerWithNoParameters,
                            @DslContext(DownstreamTriggerContext) Closure downstreamTriggerClosure = null) {
        DownstreamTriggerContext context = new DownstreamTriggerContext(jobManagement)
        ContextHelper.executeInContext(downstreamTriggerClosure, context)

        if (blockingThresholds) {
            blockingThresholds.each { String thresholdType, String threshold ->
                Preconditions.checkArgument(BLOCKING_THRESHOLD_TYPES.contains(thresholdType),
                        "thresholdType must be one of these values: ${BLOCKING_THRESHOLD_TYPES}")
                context.block {
                    "${thresholdType}"(threshold)
                }
            }
        }

        configs << new NodeBuilder().'hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig' {
            delegate.projects(projects)
            condition('ALWAYS')
            delegate.triggerWithNoParameters(triggerWithNoParameters)
            delegate.configs(context.parameterContext.configs ?: [class: 'java.util.Collections$EmptyList'])
            if (context.blockContext) {
                Node node = block()
                if (context.blockContext.buildStepFailure != 'never') {
                    node.append(createThresholdNode('buildStepFailure', context.blockContext.buildStepFailure))
                }
                if (context.blockContext.unstable != 'never') {
                    node.append(createThresholdNode('unstable', context.blockContext.unstable))
                }
                if (context.blockContext.failure != 'never') {
                    node.append(createThresholdNode('failure', context.blockContext.failure))
                }
            }
        }
    }

    private Node createThresholdNode(String thresholdName, String threshold) {
        new NodeBuilder()."${thresholdName}Threshold" {
            name(threshold)
            ordinal(THRESHOLD_ORDINAL_MAP[threshold])
            color(THRESHOLD_COLOR_MAP[threshold])
            completeBuild(true)
        }
    }
}
