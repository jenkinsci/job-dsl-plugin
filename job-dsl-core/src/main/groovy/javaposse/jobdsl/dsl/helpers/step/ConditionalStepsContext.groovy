package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.step.condition.RunCondition
import javaposse.jobdsl.dsl.helpers.step.condition.RunConditionFactory

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class ConditionalStepsContext extends AbstractContext {
    RunCondition runCondition
    String runnerClass
    final StepContext stepContext

    ConditionalStepsContext(JobManagement jobManagement, StepContext stepContext) {
        super(jobManagement)
        this.stepContext = stepContext
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
        this.runCondition = RunConditionFactory.of(conditionClosure)
    }

    /**
     * Specifies the action to take if the evaluation of a run condition fails. Must be one of {@code 'Fail'},
     * {@code 'Unstable'}, {@code 'RunUnstable'}, {@code 'Run'} or {@code 'DontRun'}.
     */
    void runner(String runnerName) {
        checkArgument(EvaluationRunners.find(runnerName) != null, "${runnerName} not a valid runner.")
        runnerClass = EvaluationRunners.find(runnerName).longForm
    }

    @Deprecated
    void runner(EvaluationRunners runner) {
        jobManagement.logDeprecationWarning()
        runnerClass = runner.longForm
    }

    @Deprecated
    static enum EvaluationRunners {
        Fail('org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail'),
        Unstable('org.jenkins_ci.plugins.run_condition.BuildStepRunner$Unstable'),
        RunUnstable('org.jenkins_ci.plugins.run_condition.BuildStepRunner$RunUnstable'),
        Run('org.jenkins_ci.plugins.run_condition.BuildStepRunner$Run'),
        DontRun('org.jenkins_ci.plugins.run_condition.BuildStepRunner$DontRun')

        final String longForm

        EvaluationRunners(String longForm) {
            this.longForm = longForm
        }

        static find(String enumName) {
            values().find { it.name().toLowerCase() == enumName.toLowerCase() }
        }
    }

    /**
     * Adds one or more build steps which will be executed conditionally.
     */
    void steps(@DslContext(StepContext) Closure stepContextClosure) {
        ContextHelper.executeInContext(stepContextClosure, stepContext)
    }
}
