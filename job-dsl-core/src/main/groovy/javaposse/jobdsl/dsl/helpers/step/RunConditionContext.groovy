package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.helpers.step.condition.AlwaysRunCondition
import javaposse.jobdsl.dsl.helpers.step.condition.BinaryLogicOperation
import javaposse.jobdsl.dsl.helpers.step.condition.FileExistsCondition
import javaposse.jobdsl.dsl.helpers.step.condition.FileExistsCondition.BaseDir
import javaposse.jobdsl.dsl.helpers.step.condition.FilesMatchCondition
import javaposse.jobdsl.dsl.helpers.step.condition.NeverRunCondition
import javaposse.jobdsl.dsl.helpers.step.condition.NotCondition
import javaposse.jobdsl.dsl.helpers.step.condition.RunCondition
import javaposse.jobdsl.dsl.helpers.step.condition.RunConditionFactory
import javaposse.jobdsl.dsl.helpers.step.condition.SimpleCondition
import javaposse.jobdsl.dsl.helpers.step.condition.StatusCondition
import javaposse.jobdsl.dsl.helpers.step.condition.NodeCondition

class RunConditionContext extends AbstractContext {
    RunCondition condition

    RunConditionContext(JobManagement jobManagement) {
        super(jobManagement)
    }
/**
     * Runs the build steps no matter what.
     */
    void alwaysRun() {
        this.condition = new AlwaysRunCondition()
    }

    /**
     * Does not run the build steps.
     */
    void neverRun() {
        this.condition = new NeverRunCondition()
    }

    /**
     * Expands the Token Macro and run the build step if it evaluates to true.
     */
    void booleanCondition(String token) {
        this.condition = new SimpleCondition(name: 'Boolean', args: ['token': token])
    }

    /**
     * Runs the build steps if the two strings are the same.
     */
    void stringsMatch(String arg1, String arg2, boolean ignoreCase) {
        this.condition = new SimpleCondition(
                name: 'StringsMatch',
                args: ['arg1': arg1, 'arg2': arg2, 'ignoreCase': ignoreCase.toString()])
    }

    /**
     * Runs the build steps if the current build has a specific cause.
     */
    void cause(String buildCause, boolean exclusiveCondition) {
        this.condition = new SimpleCondition(
                name: 'Cause',
                args: ['buildCause': buildCause, 'exclusiveCondition': exclusiveCondition.toString()])
    }

    /**
     * Runs the build steps if the expression matches the label.
     */
    void expression(String expression, String label) {
        this.condition = new SimpleCondition(
                name: 'Expression',
                args: ['expression': expression, 'label': label])
    }

    /**
     * Only runs the build steps during a certain period of the day.
     */
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

    /**
     * Runs the build steps if the current build status is within the configured range.
     *
     * The values must be one of {@code 'SUCCESS'}, {@code 'UNSTABLE'}, {@code 'FAILURE'}, {@code 'NOT_BUILT'} or
     * {@code 'ABORTED'}.
     */
    void status(String worstResult, String bestResult) {
        this.condition = new StatusCondition(worstResult, bestResult)
    }

    /**
     * Run only on selected nodes.
     *
     * @since 1.41
     */
    @RequiresPlugin(id = 'run-condition', minimumVersion = '1.0')
    void nodes(Iterable<String> allowedNodes) {
        this.condition = new NodeCondition(allowedNodes)
    }

    /**
     * Runs a shell script for checking the condition.
     *
     * Use {@link javaposse.jobdsl.dsl.DslFactory#readFileFromWorkspace(java.lang.String) readFileFromWorkspace} to read
     * the script from a file.
     *
     * @since 1.23
     */
    void shell(String command) {
        this.condition = new SimpleCondition(name: 'Shell', subPackage: 'contributed', args: [command: command])
    }

    /**
     * Runs a Windows batch script for checking the condition.
     *
     * Use {@link javaposse.jobdsl.dsl.DslFactory#readFileFromWorkspace(java.lang.String) readFileFromWorkspace} to read
     * the script from a file.
     *
     * @since 1.23
     */
    void batch(String command) {
        this.condition = new SimpleCondition(name: 'BatchFile', subPackage: 'contributed', args: [command: command])
    }

    /**
     * Runs the build steps if the file exists.
     *
     * @since 1.23
     */
    void fileExists(String file, BaseDir baseDir) {
        condition = new FileExistsCondition(file, baseDir)
    }

    /**
     * Runs the build steps if one or more files match the selectors.
     *
     * @since 1.36
     */
    void filesMatch(String includes, String excludes = '', BaseDir baseDir = BaseDir.WORKSPACE) {
        condition = new FilesMatchCondition(includes, excludes, baseDir)
    }

    /**
     * Inverts the result of the selected condition.
     *
     * @since 1.23
     */
    void not(@DslContext(RunConditionContext) Closure conditionClosure) {
        this.condition = new NotCondition(RunConditionFactory.of(jobManagement, conditionClosure))
    }

    /**
     * Runs the build steps if all of the contained conditions would run.
     *
     * @since 1.23
     */
    void and(@DslContext(RunConditionContext) Closure... conditionClosures) {
        List<RunCondition> conditions = conditionClosures.collect { RunConditionFactory.of(jobManagement, it) }
        this.condition = new BinaryLogicOperation('And', conditions)
    }

    /**
     * Runs the build steps if any of the contained conditions would run.
     *
     * @since 1.23
     */
    void or(@DslContext(RunConditionContext) Closure... conditionClosures) {
        List<RunCondition> conditions = conditionClosures.collect { RunConditionFactory.of(jobManagement, it) }
        this.condition = new BinaryLogicOperation('Or', conditions)
    }
}
