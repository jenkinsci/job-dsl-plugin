package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.helpers.step.ConditionalStepsContext
import javaposse.jobdsl.dsl.helpers.step.RunConditionContext
import javaposse.jobdsl.dsl.helpers.step.StepContext
import javaposse.jobdsl.dsl.jobs.MatrixJob

import static javaposse.jobdsl.dsl.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.Preconditions.checkState

class ConditionalActionsContext extends ConditionalStepsContext {
    protected final Item item

    List<Node> actions = []
    Node aggregationCondition
    String aggregationRunner

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

    /**
     * Specifies the condition for matrix aggregation.
     *
     * @since 1.48
     */
    void aggregationCondition(@DslContext(RunConditionContext) Closure conditionClosure) {
        checkState(item instanceof MatrixJob, 'aggregationCondition can only be using in matrix jobs')

        RunConditionContext context = new RunConditionContext(jobManagement, item)
        ContextHelper.executeInContext(conditionClosure, context)
        aggregationCondition = context.condition
    }

    /**
     * Specifies the action to take if the evaluation of the aggregation condition fails. Must be one of {@code 'Fail'},
     * {@code 'Unstable'}, {@code 'RunUnstable'}, {@code 'Run'} or {@code 'DontRun'}. Defaults to {@code 'Fail'}.
     *
     * @since 1.48
     */
    void aggregationRunner(String runnerName) {
        checkState(item instanceof MatrixJob, 'aggregationRunner can only be using in matrix jobs')
        checkArgument(
                VALID_RUNNERS.contains(runnerName),
                "${runnerName} not a valid runner, must be one of ${VALID_RUNNERS.join(', ')}"
        )

        aggregationRunner = "org.jenkins_ci.plugins.run_condition.BuildStepRunner\$${runnerName}"
    }
}
