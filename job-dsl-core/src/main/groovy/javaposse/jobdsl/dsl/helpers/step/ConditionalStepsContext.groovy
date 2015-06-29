package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.step.condition.RunCondition
import javaposse.jobdsl.dsl.helpers.step.condition.RunConditionFactory

import static com.google.common.base.Preconditions.checkArgument

class ConditionalStepsContext extends AbstractContext {
    RunCondition runCondition
    String runnerClass
    StepContext stepContext

    ConditionalStepsContext(JobManagement jobManagement, StepContext freshStepContext) {
        super(jobManagement)
        this.stepContext = freshStepContext
    }

    Object methodMissing(String name, args) {
        if (stepContext.respondsTo(name)) {
            stepContext."$name"(*args)
        } else {
            stepContext.methodMissing(name, args)
        }
    }

    Object propertyMissing(String name) {
        if (stepContext.hasProperty(name)) {
            stepContext."$name"
        } else {
            throw new MissingPropertyException(name, ConditionalStepsContext)
        }
    }

    void condition(@DslContext(RunConditionContext) Closure conditionClosure) {
        this.runCondition = RunConditionFactory.of(conditionClosure)
    }

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
}
