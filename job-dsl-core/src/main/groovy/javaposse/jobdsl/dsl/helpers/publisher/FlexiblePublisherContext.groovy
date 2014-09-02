package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.step.AbstractStepContext
import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.JobManagement

import javaposse.jobdsl.dsl.helpers.step.condition.RunCondition
import javaposse.jobdsl.dsl.helpers.step.condition.RunConditionFactory

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Preconditions.checkState

class FlexiblePublisherContext implements Context {
    final JobManagement jobManagement

    RunCondition condition
    AbstractStepContext stepContext
    PublisherContext publisherContext

    FlexiblePublisherContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    def condition(Closure closure) {
        condition = RunConditionFactory.of(closure)
    }

    def step(Closure closure) {
        checkState(publisherContext == null, 'Only 1 of step or publisher can be provided')

        stepContext = new AbstractStepContext(jobManagement)
        AbstractContextHelper.executeInContext(closure, stepContext)
        checkArgument(stepContext.stepNodes.size() == 1, 'Only 1 build step action allowed')
    }

    def publisher(Closure closure) {
        checkState(stepContext == null, 'Only 1 of step or publisher can be provided')
        /* We pass null as the 'jobManagement' parameter because it appears to be unused. */
        publisherContext = new PublisherContext(jobManagement)
        AbstractContextHelper.executeInContext(closure, publisherContext)
        checkArgument(publisherContext.publisherNodes.size() == 1, 'Only 1 publish action allowed')
    }
}
