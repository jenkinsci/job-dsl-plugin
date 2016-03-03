package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.helpers.common.Threshold.THRESHOLD_COLOR_MAP
import static javaposse.jobdsl.dsl.helpers.common.Threshold.THRESHOLD_ORDINAL_MAP

class DownstreamContext extends AbstractContext {
    protected final Item item

    List<Node> configs = []

    DownstreamContext(JobManagement jobManagement, Item item) {
        super(jobManagement)
        this.item = item
    }

    /**
     * Adds a trigger for parametrized builds. Can be called multiple times to add more triggers.
     */
    void trigger(String projects, @DslContext(DownstreamTriggerContext) Closure downstreamTriggerClosure = null) {
        DownstreamTriggerContext context = new DownstreamTriggerContext(jobManagement, item)
        ContextHelper.executeInContext(downstreamTriggerClosure, context)

        configs << new NodeBuilder().'hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig' {
            delegate.projects(projects)
            condition('ALWAYS')
            triggerWithNoParameters(false)
            delegate.configs(context.parameterContext.configs ?: [class: 'java.util.Collections$EmptyList'])
            if (context.parameterFactoryContext.configFactories) {
                configFactories(context.parameterFactoryContext.configFactories)
            }
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

    /**
     * Adds a trigger for parametrized builds. Can be called multiple times to add more triggers.
     *
     * @since 1.39
     */
    void trigger(List<String> projects, @DslContext(DownstreamTriggerContext) Closure downstreamTriggerClosure = null) {
        trigger(projects.join(', '), downstreamTriggerClosure)
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
