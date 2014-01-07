package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import spock.lang.Specification
import spock.lang.Unroll

class StaticAnalysisPublisherContextSpec extends Specification {
    List<WithXmlAction> mockActions = Mock()
    PublisherContextHelper helper = new PublisherContextHelper(mockActions, JobType.Freeform)
    PublisherContextHelper.PublisherContext context = new PublisherContextHelper.PublisherContext()

    @Unroll
    def 'add #analysisTool with default values'(analysisTool, extraNodes) {
        when:
        context."${analysisTool}"('somewhere')

        then:
        context.publisherNodes.size() == 1
        def pmdNode = context.publisherNodes[0]
        assertValues(pmdNode, [],
                pattern: 'somewhere',
                healthy: null,
                unHealthy: null,
                thresholdLimit: 'low',
                defaultEncoding: '',
                canRunOnFailed: false,
                useStableBuildAsReference: false,
                useDeltaValues: false,
                shouldDetectModules: false,
                dontComputeNew: true,
                doNotResolveRelativePaths: true,
                thresholds: [],
                *:extraNodes
        )

        where:
        analysisTool      | extraNodes
        'pmd'             | [:]
        'findbugs'        | [isRankActivated: false]
        'ccm'             | [:]
        'dependencyCheck' | [:]
        'androidLint'     | [:]
        'checkstyle'      | [:]
        'jshint'          | [:]
        'dry'             | [highThreshold: 50, normalThreshold: 25]
        'tasks'           | [excludePattern: '', high: '', normal: '', low: '', ignoreCase: false]
    }

    def 'add warnings with default values'() {
        when:
        context.warnings(['Java Compiler (javac)'])

        then:
        context.publisherNodes.size() == 1
        def warningsNode = context.publisherNodes[0]
        assertValues(warningsNode, ['consoleParsers'],
                healthy: null,
                unHealthy: null,
                thresholdLimit: 'low',
                defaultEncoding: '',
                canRunOnFailed: false,
                useStableBuildAsReference: false,
                useDeltaValues: false,
                shouldDetectModules: false,
                dontComputeNew: true,
                doNotResolveRelativePaths: true,
                thresholds: [],
                parserConfigurations: [],
                includePattern: '',
                excludePattern: ''
        )

        def consoleParsers = warningsNode.consoleParsers.'hudson.plugins.warnings.ConsoleParser'
        assertValues(consoleParsers, parserName: 'Java Compiler (javac)')
    }


    @Unroll
    def 'add #analysisTool with all values'(analysisTool, nodeName, extraArgs, extraValues) {
        when:
        context."${analysisTool}"('somewhere', *extraArgs) {
            healthLimits 3, 20
            thresholdLimit 'high'
            defaultEncoding 'UTF-8'
            canRunOnFailed true
            useStableBuildAsReference true
            useDeltaValues true
            shouldDetectModules true
            thresholds(
                    unstableTotal: [all: 1, high: 2, normal: 3, low: 4],
                    failedTotal: [all: 5, high: 6, normal: 7, low: 8],
                    unstableNew: [all: 9, high: 10, normal: 11, low: 12],
                    failedNew: [all: 13, high: 14, normal: 15, low: 16]
            )
        }

        then:
        context.publisherNodes.size() == 1
        def analysisNode = context.publisherNodes[0]
        analysisNode.name() == nodeName
        assertValues(analysisNode, ['thresholds'],
                pattern: 'somewhere',
                healthy: 3, unHealthy: 20,
                thresholdLimit: 'high',
                defaultEncoding: 'UTF-8',
                canRunOnFailed: true,
                useStableBuildAsReference: true,
                useDeltaValues: true,
                shouldDetectModules: true,
                dontComputeNew: false,
                doNotResolveRelativePaths: true,
                *:extraValues
        )
        def thresholds = analysisNode.thresholds
        assertValues(thresholds,
                unstableTotalAll: 1, unstableTotalHigh: 2, unstableTotalNormal: 3, unstableTotalLow: 4,
                failedTotalAll: 5, failedTotalHigh: 6, failedTotalNormal: 7, failedTotalLow: 8,
                unstableNewAll: 9, unstableNewHigh: 10, unstableNewNormal: 11, unstableNewLow: 12,
                failedNewAll: 13, failedNewHigh: 14, failedNewNormal: 15, failedNewLow: 16
        )

        where:
        analysisTool      | nodeName                                                         | extraArgs | extraValues
        'pmd'             | 'hudson.plugins.pmd.PmdPublisher'                                | []        | [:]
        'findbugs'        | 'hudson.plugins.findbugs.FindBugsPublisher'                      |
                [true]                                                                                   |
                [isRankActivated: true]
        'checkstyle'      | 'hudson.plugins.checkstyle.CheckStylePublisher'                  | []        | [:]
        'jshint'   	      | 'hudson.plugins.jshint.CheckStylePublisher'    	                 | []        | [:]
        'ccm'             | 'hudson.plugins.ccm.CcmPublisher'                                | []        | [:]
        'dependencyCheck' | 'org.jenkinsci.plugins.DependencyCheck.DependencyCheckPublisher' | []        | [:]
        'androidLint'     | 'org.jenkinsci.plugins.android__lint.LintPublisher'              | []        | [:]
        'dry'             | 'hudson.plugins.dry.DryPublisher'                                |
                [60, 37]                                                                                 |
                [highThreshold: 60, normalThreshold: 37]
        'tasks'           | 'hudson.plugins.tasks.TasksPublisher'                            |
                ['**/*.xml', 'FIXME', 'TODO', 'LOW', true]                                               |
                [excludePattern: '**/*.xml', high: 'FIXME', normal: 'TODO', low: 'LOW', ignoreCase: true]
    }

    def 'add warnings with all values'() {
        when:
        context.warnings(['Java Compiler (javac)'], ['Java Compiler (javac)': '**/*.log']) {
            includePattern '.*include.*'
            excludePattern '.*exclude.*'
            resolveRelativePaths true
            healthLimits 3, 20
            thresholdLimit 'high'
            defaultEncoding 'UTF-8'
            canRunOnFailed true
            useStableBuildAsReference true
            useDeltaValues true
            shouldDetectModules true
            thresholds(
                    unstableTotal: [all: 1, high: 2, normal: 3, low: 4],
                    failedTotal: [all: 5, high: 6, normal: 7, low: 8],
                    unstableNew: [all: 9, high: 10, normal: 11, low: 12],
                    failedNew: [all: 13, high: 14, normal: 15, low: 16]
            )
        }

        then:
        context.publisherNodes.size() == 1
        def analysisNode = context.publisherNodes[0]
        analysisNode.name() == 'hudson.plugins.warnings.WarningsPublisher'
        assertValues(analysisNode, ['consoleParsers', 'parserConfigurations', 'thresholds'],
                includePattern: '.*include.*',
                excludePattern: '.*exclude.*',
                healthy: 3, unHealthy: 20,
                thresholdLimit: 'high',
                defaultEncoding: 'UTF-8',
                canRunOnFailed: true,
                useStableBuildAsReference: true,
                useDeltaValues: true,
                shouldDetectModules: true,
                dontComputeNew: false,
                doNotResolveRelativePaths: false,
        )
        def thresholds = analysisNode.thresholds
        assertValues(thresholds,
                unstableTotalAll: 1, unstableTotalHigh: 2, unstableTotalNormal: 3, unstableTotalLow: 4,
                failedTotalAll: 5, failedTotalHigh: 6, failedTotalNormal: 7, failedTotalLow: 8,
                unstableNewAll: 9, unstableNewHigh: 10, unstableNewNormal: 11, unstableNewLow: 12,
                failedNewAll: 13, failedNewHigh: 14, failedNewNormal: 15, failedNewLow: 16
        )

        def consoleParsers = analysisNode.consoleParsers.'hudson.plugins.warnings.ConsoleParser'
        assertValues(consoleParsers, parserName: 'Java Compiler (javac)')

        def parserConfigurations = analysisNode.parserConfigurations.'hudson.plugins.warnings.ParserConfiguration'
        assertValues(parserConfigurations,
                pattern: '**/*.log',
                parserName: 'Java Compiler (javac)'
        )
    }


    private void assertValues(Map map, baseNode, List notCheckedNodes = []) {
        map.each { key, expectedValue ->
            def nodeList = baseNode[key]
            assert nodeList.size() == 1
            def node = nodeList[0]
            assert node != null, "Key ${key} not present in XML-Structure"
            assert node.value() == expectedValue
        }
        def children = baseNode instanceof Node ? baseNode.children() : baseNode*.children().flatten()
        assert children*.name().toSet() == map.keySet() + notCheckedNodes
    }

}
