package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.helpers.step.ConditionalStepsContext
import javaposse.jobdsl.dsl.helpers.step.StepContext

class ConditionalActionsContext extends ConditionalStepsContext {
    protected final Item item

    List<Node> actions = []

    ConditionalActionsContext(JobManagement jobManagement, Item item) {
        super(jobManagement, new StepContext(jobManagement, item))
        this.item = item

        condition {
            alwaysRun()
        }
    }

    @RequiresPlugin(id = 'any-buildstep')
    @Override
    void steps(@DslContext(StepContext) Closure closure) {
        StepContext stepContext = new StepContext(jobManagement, item)
        ContextHelper.executeInContext(closure, stepContext)
        actions.addAll(stepContext.stepNodes)
    }

    /**
     * Adds one or more post-build actions which will be executed conditionally.
     */
    void publishers(@DslContext(PublisherContext) Closure closure) {
        PublisherContext publisherContext = new PublisherContext(jobManagement, item)
        ContextHelper.executeInContext(closure, publisherContext)
        actions.addAll(publisherContext.publisherNodes)
    }
}
