package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

import static javaposse.jobdsl.dsl.helpers.step.RunConditionContext.BaseDir.WORKSPACE

class RunConditionContextSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    RunConditionContext context = new RunConditionContext(jobManagement, Mock(Item))

    def 'extension node added'() {
        setup:
        Node node = Mock(Node)

        when:
        context.addExtensionNode(node)

        then:
        context.condition == node
    }

    def 'always run condition'() {
        when:
        context.alwaysRun()

        then:
        with(context.condition) {
            name() == 'org.jenkins_ci.plugins.run_condition.core.AlwaysRun'
            children().size() == 0
        }
    }

    def 'never run condition'() {
        when:
        context.neverRun()

        then:
        with(context.condition) {
            name() == 'org.jenkins_ci.plugins.run_condition.core.NeverRun'
            children().size() == 0
        }
    }

    def 'boolean condition'() {
        when:
        context.booleanCondition('foo')

        then:
        with(context.condition) {
            name() == 'org.jenkins_ci.plugins.run_condition.core.BooleanCondition'
            children().size() == 1
            token[0].value() == 'foo'
        }
    }

    def 'strings match condition'() {
        when:
        context.stringsMatch('foo', 'bar', true)

        then:
        with(context.condition) {
            name() == 'org.jenkins_ci.plugins.run_condition.core.StringsMatchCondition'
            children().size() == 3
            arg1[0].value() == 'foo'
            arg2[0].value() == 'bar'
            ignoreCase[0].value() == true
        }
    }

    def 'cause condition'() {
        when:
        context.cause('foo', true)

        then:
        with(context.condition) {
            name() == 'org.jenkins_ci.plugins.run_condition.core.CauseCondition'
            children().size() == 2
            buildCause[0].value() == 'foo'
            exclusiveCause[0].value() == true
        }
    }

    def 'expression condition'() {
        when:
        context.expression('foo', 'bar')

        then:
        with(context.condition) {
            name() == 'org.jenkins_ci.plugins.run_condition.core.ExpressionCondition'
            children().size() == 2
            expression[0].value() == 'foo'
            label[0].value() == 'bar'
        }
    }

    def 'time condition validation'(def args) {
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
        when:
        context.time(9, 30, 10, 0, true)

        then:
        with(context.condition) {
            name() == 'org.jenkins_ci.plugins.run_condition.core.TimeCondition'
            children().size() == 5
            earliestHours[0].value() == 9
            earliestMinutes[0].value() == 30
            latestHours[0].value() == 10
            latestMinutes[0].value() == 0
            useBuildTime[0].value() == true
        }
    }

    def 'status condition validation'(String worst, String best) {
        when:
        context.status(worst, best)

        then:
        thrown(DslScriptException)

        where:
        worst       | best
        'SUCCESS'   | 'FOO'
        'FOO'       | 'UNSTABLE'
        null        | 'FAILURE'
        'NOT_BUILT' | null
        'ABORTED'   | ''
        ''          | 'ABORTED'
        'SUCCESS'   | 'ABORTED'
    }

    def 'status condition'() {
        when:
        context.status(worst, 'SUCCESS')

        then:
        with(context.condition) {
            name() == 'org.jenkins_ci.plugins.run_condition.core.StatusCondition'
            children().size() == 2
            worstResult[0].children().size() == 1
            worstResult[0].ordinal[0].value() == ordinal
            bestResult[0].children().size() == 1
            bestResult[0].ordinal[0].value() == 0
        }

        where:
        worst       | ordinal
        'SUCCESS'   | 0
        'UNSTABLE'  | 1
        'FAILURE'   | 2
        'NOT_BUILT' | 3
        'ABORTED'   | 4
    }

    def 'file exists'() {
        when:
        context.fileExists('some_file.txt', WORKSPACE)

        then:
        with(context.condition) {
            name() == 'org.jenkins_ci.plugins.run_condition.core.FileExistsCondition'
            children().size() == 2
            file[0].value() == 'some_file.txt'
            baseDir[0].value().empty
            baseDir[0].@class == 'org.jenkins_ci.plugins.run_condition.common.BaseDirectory$Workspace'
        }
    }

    def 'files match'() {
        when:
        context.filesMatch(/incl.*udes/, /excl.*udes/, WORKSPACE)

        then:
        with(context.condition) {
            name() == 'org.jenkins_ci.plugins.run_condition.core.FilesMatchCondition'
            children().size() == 3
            includes[0].value() == /incl.*udes/
            excludes[0].value() == /excl.*udes/
            baseDir[0].value().empty
            baseDir[0].@class == 'org.jenkins_ci.plugins.run_condition.common.BaseDirectory$Workspace'
        }
    }

    def 'allowed nodes'() {
        when:
        context.nodes(['foo', 'bar'])

        then:
        with(context.condition) {
            name() == 'org.jenkins_ci.plugins.run_condition.core.NodeCondition'
            children().size() == 1
            allowedNodes[0].children().size() == 2
            allowedNodes[0].string[0].value() == 'foo'
            allowedNodes[0].string[1].value() == 'bar'
        }
    }

    def 'shell condition'() {
        when:
        context.shell('foo')

        then:
        with(context.condition) {
            name() == 'org.jenkins_ci.plugins.run_condition.contributed.ShellCondition'
            children().size() == 1
            command[0].value() == 'foo'
        }
    }

    def 'batch file condition'() {
        when:
        context.batch('foo')

        then:
        with(context.condition) {
            name() == 'org.jenkins_ci.plugins.run_condition.contributed.BatchFileCondition'
            children().size() == 1
            command[0].value() == 'foo'
        }
    }

    def 'not condition'() {
        when:
        context.not {
            alwaysRun()
        }

        then:
        with(context.condition) {
            name() == 'org.jenkins_ci.plugins.run_condition.logic.Not'
            children().size() == 1
            condition[0].children().size() == 0
            condition[0].@class == 'org.jenkins_ci.plugins.run_condition.core.AlwaysRun'
        }
    }

    def 'and condition'() {
        when:
        context.and {
            alwaysRun()
        } {
            neverRun()
        }

        then:
        with(context.condition) {
            name() == 'org.jenkins_ci.plugins.run_condition.logic.And'
            children().size() == 1
            conditions[0].children().size() == 2
            with(conditions[0].'org.jenkins__ci.plugins.run__condition.logic.ConditionContainer'[0]) {
                children().size() == 1
                condition[0].children().size() == 0
                condition[0].@class == 'org.jenkins_ci.plugins.run_condition.core.AlwaysRun'
            }
            with(conditions[0].'org.jenkins__ci.plugins.run__condition.logic.ConditionContainer'[1]) {
                children().size() == 1
                condition[0].children().size() == 0
                condition[0].@class == 'org.jenkins_ci.plugins.run_condition.core.NeverRun'
            }
        }
    }

    def 'or condition'() {
        when:
        context.or {
            alwaysRun()
        } {
            neverRun()
        }

        then:
        with(context.condition) {
            name() == 'org.jenkins_ci.plugins.run_condition.logic.Or'
            children().size() == 1
            conditions[0].children().size() == 2
            with(conditions[0].'org.jenkins__ci.plugins.run__condition.logic.ConditionContainer'[0]) {
                children().size() == 1
                condition[0].children().size() == 0
                condition[0].@class == 'org.jenkins_ci.plugins.run_condition.core.AlwaysRun'
            }
            with(conditions[0].'org.jenkins__ci.plugins.run__condition.logic.ConditionContainer'[1]) {
                children().size() == 1
                condition[0].children().size() == 0
                condition[0].@class == 'org.jenkins_ci.plugins.run_condition.core.NeverRun'
            }
        }
    }
}
