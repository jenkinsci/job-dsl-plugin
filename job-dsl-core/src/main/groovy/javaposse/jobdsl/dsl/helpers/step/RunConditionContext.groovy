package javaposse.jobdsl.dsl.helpers.step

import com.google.common.base.Preconditions
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

    void alwaysRun() {
        this.condition = new AlwaysRunCondition()
    }

    void neverRun() {
        this.condition = new NeverRunCondition()
    }

    void booleanCondition(String token) {
        this.condition = new SimpleCondition(name: 'Boolean', args: ['token': token])
    }

    void stringsMatch(String arg1, String arg2, boolean ignoreCase) {
        this.condition = new SimpleCondition(
                name: 'StringsMatch',
                args: ['arg1': arg1, 'arg2': arg2, 'ignoreCase': ignoreCase.toString()])
    }

    void cause(String buildCause, boolean exclusiveCondition) {
        this.condition = new SimpleCondition(
                name: 'Cause',
                args: ['buildCause': buildCause, 'exclusiveCondition': exclusiveCondition.toString()])
    }

    void expression(String expression, String label) {
        this.condition = new SimpleCondition(
                name: 'Expression',
                args: ['expression': expression, 'label': label])
    }

    void time(int earliestHours, int earliestMinutes, int latestHours, int latestMinutes, boolean useBuildTime) {
        Preconditions.checkArgument((0..23).contains(earliestHours), 'earliestHours must be between 0 and 23')
        Preconditions.checkArgument((0..59).contains(earliestMinutes), 'earliestMinutes must be between 0 and 59')
        Preconditions.checkArgument((0..23).contains(latestHours), 'latestHours must be between 0 and 23')
        Preconditions.checkArgument((0..59).contains(latestMinutes), 'latestMinutes must be between 0 and 59')

        this.condition = new SimpleCondition(
                name: 'Time',
                args: ['earliestHours': earliestHours, 'earliestMinutes': earliestMinutes,
                       'latestHours': latestHours, 'latestMinutes': latestMinutes,
                       'useBuildTime': useBuildTime])
    }

    void status(String worstResult, String bestResult) {
        this.condition = new StatusCondition(worstResult, bestResult)
    }

    void shell(String command) {
        this.condition = new SimpleCondition(name: 'Shell', subPackage: 'contributed', args: [command: command])
    }

    void batch(String command) {
        this.condition = new SimpleCondition(name: 'BatchFile', subPackage: 'contributed', args: [command: command])
    }

    void fileExists(String file, BaseDir baseDir) {
        condition = new FileExistsCondition(file, baseDir)
    }

    void not(Closure conditionClosure) {
        this.condition = new NotCondition(RunConditionFactory.of(conditionClosure))
    }

    void and(Closure... conditionClosures) {
        List<RunCondition> conditions = conditionClosures.collect { RunConditionFactory.of(it) }
        this.condition = new BinaryLogicOperation('And', conditions)
    }

    void or(Closure... conditionClosures) {
        List<RunCondition> conditions = conditionClosures.collect { RunConditionFactory.of(it) }
        this.condition = new BinaryLogicOperation('Or', conditions)
    }
}
