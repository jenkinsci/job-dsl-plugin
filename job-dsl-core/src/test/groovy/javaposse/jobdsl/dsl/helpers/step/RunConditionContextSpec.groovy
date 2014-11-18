package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.helpers.step.condition.SimpleCondition
import spock.lang.Specification

class RunConditionContextSpec extends Specification {
    def 'time condition validation'(def args) {
        setup:
        RunConditionContext context = new RunConditionContext()

        when:
        context.time(*args)

        then:
        thrown(IllegalArgumentException)

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
        RunConditionContext context = new RunConditionContext()

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
}
