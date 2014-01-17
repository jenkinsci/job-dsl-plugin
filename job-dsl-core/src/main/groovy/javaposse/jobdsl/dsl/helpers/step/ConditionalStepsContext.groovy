package javaposse.jobdsl.dsl.helpers.step

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context

class ConditionalStepsContext extends AbstractStepContext {

    RunConditionContext conditionContext
    String runnerClass

    ConditionalStepsContext() {
        super()
    }

    ConditionalStepsContext(String runnerName, Closure conditionClosure) {
        this(runnerName, [], conditionClosure)
    }

    ConditionalStepsContext(String runnerName, List<Node> stepNodes, Closure conditionClosure) {
        super(stepNodes)
        Preconditions.checkArgument(EvaluationRunners.find(runnerName) != null, "${runnerName} not a valid evaluation runner.")

        condition(conditionClosure)

        this.runnerClass = EvaluationRunners.find(runnerName)
    }

    def condition(Closure conditionClosure) {
        conditionContext = new RunConditionContext()

        AbstractContextHelper.executeInContext(conditionClosure, conditionContext)

        Preconditions.checkNotNull(conditionContext.conditionName, "No condition name specified")
    }

    def runner(String runnerName) {
        Preconditions.checkArgument(EvaluationRunners.find(runnerName) != null, "${runnerName} not a valid runner.")
        runnerClass = EvaluationRunners.find(runnerName).longForm
    }

    def runner(EvaluationRunners runner) {
        runnerClass = runner.longForm
    }

    protected def createSingleStepNode() {
        def nodeBuilder = new NodeBuilder()

        return nodeBuilder.'org.jenkinsci.plugins.conditionalbuildstep.singlestep.SingleConditionalBuilder' {
            condition(class: conditionContext.conditionClass()) {
                conditionContext.conditionArgs.each { k, v ->
                    "${k}" v
                }
            }
            runner(class: runnerClass)
            buildStep(class: stepNodes[0].name()) {
                stepNodes[0].children().each { c ->
                    "${c.name()}"(c.attributes(), c.value())
                }
            }
        }
    }

    protected def createMultiStepNode() {
        def nodeBuilder = new NodeBuilder()

        return nodeBuilder.'org.jenkinsci.plugins.conditionalbuildstep.ConditionalBuilder' {
            runCondition(class: conditionContext.conditionClass()) {
                conditionContext.conditionArgs.each { k, v ->
                    "${k}" v
                }
            }
            runner(class: runnerClass)
            conditionalBuilders(stepNodes)
        }
    }

    public static enum EvaluationRunners {
        Fail('org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail'),
        Unstable('org.jenkins_ci.plugins.run_condition.BuildStepRunner$Unstable'),
        RunUnstable('org.jenkins_ci.plugins.run_condition.BuildStepRunner$RunUnstable'),
        Run('org.jenkins_ci.plugins.run_condition.BuildStepRunner$Run'),
        DontRun('org.jenkins_ci.plugins.run_condition.BuildStepRunner$DontRun')

        final String longForm

        EvaluationRunners(String longForm) {
            this.longForm = longForm
        }

        public static find(String enumName) {
            values().find { it.name().toLowerCase() == enumName.toLowerCase() }
        }

    }

    public static class RunConditionContext implements Context {
        String conditionName
        public Map<String, String> conditionArgs = [:]

        def conditionClass() {
            return "org.jenkins_ci.plugins.run_condition.core.${conditionName}Condition"
        }

        def alwaysRun() {
            this.conditionName = "AlwaysRun"
            this.conditionArgs = [:]
        }

        def neverRun() {
            this.conditionName = "NeverRun"
            this.conditionArgs = [:]
        }

        def booleanCondition(String token) {
            this.conditionName = "Boolean"
            this.conditionArgs = ['token': token]
        }

        def stringsMatch(String arg1, String arg2, boolean ignoreCase) {
            this.conditionName = "StringsMatch"
            this.conditionArgs = ['arg1': arg1, 'arg2': arg2, 'ignoreCase': ignoreCase ? "true" : "false"]
        }

        def cause(String buildCause, boolean exclusiveCondition) {
            this.conditionName = "Cause"
            this.conditionArgs = ['buildCause': buildCause, 'exclusiveCondition': exclusiveCondition ? "true" : "false"]
        }

        def expression(String expression, String label) {
            this.conditionName = "Expression"
            this.conditionArgs = ['expression': expression, 'label': label]
        }

        def time(String earliest, String latest, boolean useBuildTime) {
            this.conditionName = "Time"
            this.conditionArgs = ['earliest': earliest, 'latest': latest, 'useBuildTime': useBuildTime ? 'true' : 'false']
        }
    }
}
