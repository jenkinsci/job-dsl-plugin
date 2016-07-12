package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

class FlexiblePublisherContext extends AbstractContext {
    protected final Item item
    List<ConditionalActionsContext> conditionalActions = []

    FlexiblePublisherContext(JobManagement jobManagement, Item item) {
        super(jobManagement)
        this.item = item
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
}
