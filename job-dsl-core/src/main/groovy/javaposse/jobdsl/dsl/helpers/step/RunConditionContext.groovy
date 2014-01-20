package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.helpers.Context


class RunConditionContext implements Context {
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