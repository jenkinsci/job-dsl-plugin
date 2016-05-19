package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class ConditionalStepsContext extends AbstractContext {
    private static final Set<String> VALID_RUNNERS = ['Fail', 'Unstable', 'RunUnstable', 'Run', 'DontRun']

    Node runCondition
    String runnerClass
    final StepContext stepContext

    ConditionalStepsContext(JobManagement jobManagement, StepContext stepContext) {
        super(jobManagement)
        this.stepContext = stepContext
        runner('Fail')
    }

    /**
     * Specifies the condition to evaluate before executing the build steps.
     */
    void condition(@DslContext(RunConditionContext) Closure conditionClosure) {
        RunConditionContext context = new RunConditionContext(jobManagement, stepContext.item)
        ContextHelper.executeInContext(conditionClosure, context)
        this.runCondition = context.condition
    }

    /**
     * Specifies the action to take if the evaluation of a run condition fails. Must be one of {@code 'Fail'},
     * {@code 'Unstable'}, {@code 'RunUnstable'}, {@code 'Run'} or {@code 'DontRun'}. Defaults to {@code 'Fail'}.
     */
    void runner(String runnerName) {
        checkArgument(
                VALID_RUNNERS.contains(runnerName),
                "${runnerName} not a valid runner, must be one of ${VALID_RUNNERS.join(', ')}"
        )
        runnerClass = "org.jenkins_ci.plugins.run_condition.BuildStepRunner\$${runnerName}"
    }

    /**
     * Adds one or more build steps which will be executed conditionally.
     */
    void steps(@DslContext(StepContext) Closure stepContextClosure) {
        ContextHelper.executeInContext(stepContextClosure, stepContext)
    }
}
