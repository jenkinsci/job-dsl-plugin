package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.step.condition.FileExistsCondition
import javaposse.jobdsl.dsl.helpers.step.condition.FilesMatchCondition
import javaposse.jobdsl.dsl.helpers.step.condition.NodeCondition
import javaposse.jobdsl.dsl.helpers.step.condition.SimpleCondition
import spock.lang.Specification

import static javaposse.jobdsl.dsl.helpers.step.condition.FileExistsCondition.BaseDir.WORKSPACE

class RunConditionContextSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)

    def 'time condition validation'(def args) {
        setup:
        RunConditionContext context = new RunConditionContext(jobManagement)

        when:
        context.time(*args)

        then:
        thrown(DslScriptException)

        where:
        args << [
                [-1, 0, 0, 0, true],
                [0, -1, 0, 0, true],
                [0, 0, -1, 0, true],
                [0, 0, 0, -1, true],
                [24, 0, 0, 0, true],
                [0, 60, 0, 0, true],
                [0, 0, 24, 0, true],
                [0, 0, 0, 60, true],
        ]
    }

    def 'time condition'() {
        setup:
        RunConditionContext context = new RunConditionContext(jobManagement)

        when:
        context.time(9, 30, 10, 0, true)

        then:
        context.condition instanceof SimpleCondition
        with(context.condition as SimpleCondition) {
            name == 'Time'
            args == [
                    'earliestHours': 9,
                    'earliestMinutes': 30,
                    'latestHours': 10,
                    'latestMinutes': 0,
                    'useBuildTime': true,
            ]
        }
    }

    def 'file exists'() {
        setup:
        RunConditionContext context = new RunConditionContext(jobManagement)

        when:
        context.fileExists('some_file.txt', WORKSPACE)

        then:
        context.condition instanceof FileExistsCondition
        with(context.condition as FileExistsCondition) {
            name == 'FileExists'
            file == 'some_file.txt'
            baseDir == WORKSPACE
        }
    }

    def 'files match'() {
        setup:
        RunConditionContext context = new RunConditionContext(jobManagement)

        when:
        context.filesMatch(/incl.*udes/, /excl.*udes/, WORKSPACE)

        then:
        context.condition instanceof FilesMatchCondition
        with(context.condition as FilesMatchCondition) {
            name == 'FilesMatch'
            includes == /incl.*udes/
            excludes == /excl.*udes/
            baseDir == WORKSPACE
        }
    }

    def 'allowed nodes'() {
        setup:
        RunConditionContext context = new RunConditionContext(jobManagement)

        when:
        context.nodes(['foo', 'bar'])

        then:
        context.condition instanceof NodeCondition
        with(context.condition as NodeCondition) {
            name == 'Node'
            allowedNodes == ['foo', 'bar']
        }
    }
}
