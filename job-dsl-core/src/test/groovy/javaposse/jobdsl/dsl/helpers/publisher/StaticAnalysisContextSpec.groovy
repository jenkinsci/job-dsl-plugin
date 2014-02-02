package javaposse.jobdsl.dsl.helpers.publisher

import spock.lang.Specification

class StaticAnalysisContextSpec extends Specification {

    StaticAnalysisContext context = new StaticAnalysisContext()

    def 'computeNew is set correctly when setting thresholds'(thresholds, dontComputeNew) {
        when:
        context.thresholds(thresholds)
        then:
        context.dontComputeNew == dontComputeNew
        where:
        thresholds                                         || dontComputeNew
        [unstableNew: [low: 15], unstableTotal: [high: 5]] |  false
        [unstableTotal: [low: 15], failedTotal: [all: 3]]  |  true
    }

    def 'failure on unknown threshold configuration'(thresholds) {
        when:
        context.thresholds(thresholds)
        then:
        AssertionError e = thrown()
        e.message.contains(thresholds.toString())
        where:
        thresholds  << [
            [unstableNew: [low: 15], unstableTotalWhatever: [high: 5]],
            [unstableTotalOld: [low: 15], failedTotal: [all: 3]],
            [unstableTotal: [veryLow: 15], failedTotal: [all: 3]]
        ]
    }

}
