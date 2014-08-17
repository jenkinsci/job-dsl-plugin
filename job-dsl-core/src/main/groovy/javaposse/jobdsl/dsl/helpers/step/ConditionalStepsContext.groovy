package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.step.condition.RunCondition
import javaposse.jobdsl.dsl.helpers.step.condition.RunConditionFactory

import static com.google.common.base.Preconditions.checkArgument

class ConditionalStepsContext extends AbstractStepContext {

    RunCondition runCondition
    String runnerClass

    ConditionalStepsContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    def condition(Closure conditionClosure) {
        this.runCondition = RunConditionFactory.of(conditionClosure)
    }

    def runner(String runnerName) {
        checkArgument(EvaluationRunners.find(runnerName) != null, "${runnerName} not a valid runner.")
        runnerClass = EvaluationRunners.find(runnerName).longForm
    }

    def runner(EvaluationRunners runner) {
        runnerClass = runner.longForm
    }

    protected Node createSingleStepNode() {
        def nodeBuilder = new NodeBuilder()

        nodeBuilder.'org.jenkinsci.plugins.conditionalbuildstep.singlestep.SingleConditionalBuilder' {
            delegate.condition(class: runCondition.conditionClass) {
                runCondition.addArgs(delegate)
            }
            runner(class: runnerClass)
            buildStep(class: stepNodes[0].name()) {
                stepNodes[0].children().each { c ->
                    "${c.name()}"(c.attributes(), c.value())
                }
            }
        }
    }

    protected Node createMultiStepNode() {
        def nodeBuilder = new NodeBuilder()

        nodeBuilder.'org.jenkinsci.plugins.conditionalbuildstep.ConditionalBuilder' {
            runCondition(class: runCondition.conditionClass) {
                runCondition.addArgs(delegate)
            }
            runner(class: runnerClass)
            conditionalbuilders(stepNodes)
        }
    }

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
