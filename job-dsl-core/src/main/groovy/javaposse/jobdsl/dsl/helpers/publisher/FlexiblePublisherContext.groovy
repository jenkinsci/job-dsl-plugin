package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.helpers.step.RunConditionContext
import javaposse.jobdsl.dsl.helpers.step.StepContext

class FlexiblePublisherContext extends AbstractContext {
    protected final Item item
    Node condition
    List<Node> actions = []
    List<ConditionalActionsContext> conditionalActions = []

    FlexiblePublisherContext(JobManagement jobManagement, Item item) {
        super(jobManagement)
        this.item = item

        RunConditionContext context = new RunConditionContext(jobManagement, item)
        context.alwaysRun()
        condition = context.condition
    }

    /**
     * Adds a conditional action. Can be called multiple times to add more actions.
     *
     * @since 1.42
     */
    void conditionalAction(@DslContext(ConditionalActionsContext) Closure closure) {
        ConditionalActionsContext context = new ConditionalActionsContext(jobManagement, item)
        ContextHelper.executeInContext(closure, context)
        conditionalActions << context
    }

    /**
     * Specifies the condition to evaluate before executing publishers or build steps.
     */
    @Deprecated
    void condition(@DslContext(RunConditionContext) Closure closure) {
        RunConditionContext context = new RunConditionContext(jobManagement, item)
        ContextHelper.executeInContext(closure, context)
        condition = context.condition
    }

    /**
     * Adds one or more build steps which will be executed conditionally.
     */
    @RequiresPlugin(id = 'any-buildstep')
    @Deprecated
    void step(@DslContext(StepContext) Closure closure) {
        StepContext stepContext = new StepContext(jobManagement, item)
        ContextHelper.executeInContext(closure, stepContext)
        actions.addAll(stepContext.stepNodes)
    }

    /**
     * Adds one or more post-build actions which will be executed conditionally.
     */
    @Deprecated
    void publisher(@DslContext(PublisherContext) Closure closure) {
        PublisherContext publisherContext = new PublisherContext(jobManagement, item)
        ContextHelper.executeInContext(closure, publisherContext)
        actions.addAll(publisherContext.publisherNodes)
    }
}
