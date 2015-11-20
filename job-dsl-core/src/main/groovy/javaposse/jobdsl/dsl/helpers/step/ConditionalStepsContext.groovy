package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.step.condition.RunCondition
import javaposse.jobdsl.dsl.helpers.step.condition.RunConditionFactory

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class ConditionalStepsContext extends AbstractContext {
    private static final Set<String> VALID_RUNNERS = ['Fail', 'Unstable', 'RunUnstable', 'Run', 'DontRun']

    RunCondition runCondition
    String runnerClass
    final StepContext stepContext

    ConditionalStepsContext(JobManagement jobManagement, StepContext stepContext) {
        super(jobManagement)
        this.stepContext = stepContext
        runner('Fail')
    }

    Object methodMissing(String name, args) {
        Object result
        if (stepContext.respondsTo(name)) {
            result = stepContext."$name"(*args)
        } else {
            result = stepContext.methodMissing(name, args)
        }
        jobManagement.logDeprecationWarning('using build steps outside the nested steps context of conditionalSteps')
        result
    }

    /**
     * Specifies the condition to evaluate before executing the build steps.
     */
    void condition(@DslContext(RunConditionContext) Closure conditionClosure) {
        this.runCondition = RunConditionFactory.of(jobManagement, conditionClosure)
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
