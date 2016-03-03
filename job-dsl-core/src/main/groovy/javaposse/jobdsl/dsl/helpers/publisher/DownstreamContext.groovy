package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

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

        configs << new NodeBuilder().'hudson.plugins.parameterizedtrigger.BuildTriggerConfig' {
            delegate.projects(projects)
            delegate.condition(context.condition)
            delegate.triggerWithNoParameters(context.triggerWithNoParameters)
            delegate.configs(context.parameterContext.configs ?: [class: 'java.util.Collections$EmptyList'])
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
}
