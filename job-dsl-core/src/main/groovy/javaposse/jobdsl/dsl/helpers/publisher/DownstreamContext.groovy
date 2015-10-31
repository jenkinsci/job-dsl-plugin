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
        addTrigger(projects, null, false, downstreamTriggerClosure)
    }

    /**
     * Adds a trigger for parametrized builds. Can be called multiple times to add more triggers.
     *
     * @since 1.39
     */
    void trigger(List<String> projects, @DslContext(DownstreamTriggerContext) Closure downstreamTriggerClosure = null) {
        addTrigger(projects.join(', '), null, false, downstreamTriggerClosure)
    }

    /**
     * Adds a trigger for parametrized builds. Can be called multiple times to add more triggers.
     */
    @Deprecated
    void trigger(String projects, String condition,
                 @DslContext(DownstreamTriggerContext) Closure downstreamTriggerClosure = null) {
        jobManagement.logDeprecationWarning()
        addTrigger(projects, condition, false, downstreamTriggerClosure)
    }

    /**
     * Adds a trigger for parametrized builds. Can be called multiple times to add more triggers.
     */
    @Deprecated
    void trigger(String projects, String condition, boolean triggerWithNoParameters,
                 @DslContext(DownstreamTriggerContext) Closure downstreamTriggerClosure = null) {
        jobManagement.logDeprecationWarning()
        addTrigger(projects, condition, triggerWithNoParameters, downstreamTriggerClosure)
    }

    /**
     * Adds a trigger for parametrized builds. Can be called multiple times to add more triggers.
     */
    @Deprecated
    @SuppressWarnings(['UnusedMethodParameter', 'GroovyUnusedDeclaration'])
    void trigger(String projects, String condition, boolean triggerWithNoParameters,
                 Map<String, String> blockingThresholds,
                 @DslContext(DownstreamTriggerContext) Closure downstreamTriggerClosure = null) {
        jobManagement.logDeprecationWarning()
        addTrigger(projects, condition, triggerWithNoParameters, downstreamTriggerClosure)
    }

    private void addTrigger(String projects, String condition, boolean triggerWithNoParameters,
                            @DslContext(DownstreamTriggerContext) Closure downstreamTriggerClosure = null) {
        DownstreamTriggerContext context = new DownstreamTriggerContext(jobManagement, item)
        ContextHelper.executeInContext(downstreamTriggerClosure, context)

        if (condition) {
            context.condition(condition)
        }
        if (triggerWithNoParameters) {
            context.triggerWithNoParameters()
        }

        configs << new NodeBuilder().'hudson.plugins.parameterizedtrigger.BuildTriggerConfig' {
            delegate.projects(projects)
            delegate.condition(context.condition)
            delegate.triggerWithNoParameters(context.triggerWithNoParameters)
            delegate.configs(context.parameterContext.configs ?: [class: 'java.util.Collections$EmptyList'])
        }
    }
}
