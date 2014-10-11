package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.helpers.step.condition.AlwaysRunCondition
import javaposse.jobdsl.dsl.helpers.step.condition.BinaryLogicOperation
import javaposse.jobdsl.dsl.helpers.step.condition.FileExistsCondition
import javaposse.jobdsl.dsl.helpers.step.condition.FileExistsCondition.BaseDir
import javaposse.jobdsl.dsl.helpers.step.condition.NeverRunCondition
import javaposse.jobdsl.dsl.helpers.step.condition.NotCondition
import javaposse.jobdsl.dsl.helpers.step.condition.RunCondition
import javaposse.jobdsl.dsl.helpers.step.condition.RunConditionFactory
import javaposse.jobdsl.dsl.helpers.step.condition.SimpleCondition
import javaposse.jobdsl.dsl.helpers.step.condition.StatusCondition

class RunConditionContext implements Context {

    RunCondition condition

    def alwaysRun() {
        this.condition = new AlwaysRunCondition()
    }

    def neverRun() {
        this.condition = new NeverRunCondition()
    }

    def booleanCondition(String token) {
        this.condition = new SimpleCondition(name: 'Boolean', args: ['token': token])
    }

    def stringsMatch(String arg1, String arg2, boolean ignoreCase) {
        this.condition = new SimpleCondition(
                name: 'StringsMatch',
                args: ['arg1': arg1, 'arg2': arg2, 'ignoreCase': ignoreCase.toString()])
    }

    def cause(String buildCause, boolean exclusiveCondition) {
        this.condition = new SimpleCondition(
                name: 'Cause',
                args: ['buildCause': buildCause, 'exclusiveCondition': exclusiveCondition.toString()])
    }

    def expression(String expression, String label) {
        this.condition = new SimpleCondition(
                name: 'Expression',
                args: ['expression': expression, 'label': label])
    }

    def time(String earliest, String latest, boolean useBuildTime) {
        this.condition = new SimpleCondition(
                name: 'Time',
                args: ['earliest': earliest, 'latest': latest, 'useBuildTime': useBuildTime ? 'true' : 'false'])
    }

    def status(String worstResult, String bestResult) {
        this.condition = new StatusCondition(worstResult, bestResult)
    }

    def shell(String command) {
        this.condition = new SimpleCondition(name: 'Shell', subPackage: 'contributed', args: [command: command])
    }

    def batch(String command) {
        this.condition = new SimpleCondition(name: 'BatchFile', subPackage: 'contributed', args: [command: command])
    }

    def fileExists(String file, BaseDir baseDir) {
        condition = new FileExistsCondition(file, baseDir)
    }

    def not(Closure conditionClosure) {
        this.condition = new NotCondition(RunConditionFactory.of(conditionClosure))
    }

    def and(Closure... conditionClosures) {
        List<RunCondition> conditions = conditionClosures.collect { RunConditionFactory.of(it) }
        this.condition = new BinaryLogicOperation('And', conditions)
    }

    def or(Closure... conditionClosures) {
        List<RunCondition> conditions = conditionClosures.collect { RunConditionFactory.of(it) }
        this.condition = new BinaryLogicOperation('Or', conditions)
    }
}
