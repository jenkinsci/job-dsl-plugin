package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.helpers.Context

class ExecutionStrategyContext implements Context {
    enum RequiredResult {
        SUCCESS('BLUE'), UNSTABLE('YELLOW')

        String color

        RequiredResult(color) {
            this.color = color
        }
    }

    def runSequentially = false
    def String touchStoneCombinationFilter
    def RequiredResult touchStoneRequiredResult

    def runSequentially(boolean runSequentially = true) {
        this.runSequentially = runSequentially
    }

    def touchStoneCombinationFilter(String combinationFilter) {
        this.touchStoneCombinationFilter = combinationFilter
    }

    def touchStoneResultCondition(RequiredResult requiredResult) {
        this.touchStoneRequiredResult = requiredResult
    }
}
