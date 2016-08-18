package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification
import spock.lang.Unroll

class StaticAnalysisPublisherContextSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    Item item = Mock(Item)
    PublisherContext context = new PublisherContext(jobManagement, item)

    @Unroll
    def 'add #analysisTool with default values'(String analysisTool, Map extraNodes, String pluginId) {
        when:
        context."${analysisTool}"('somewhere')

        then:
        context.publisherNodes.size() == 1
        def pmdNode = context.publisherNodes[0]
        assertValues(pmdNode, [], extraNodes,
                pattern: 'somewhere',
                healthy: '',
                unHealthy: '',
                thresholdLimit: 'low',
                defaultEncoding: '',
                canRunOnFailed: false,
                useStableBuildAsReference: false,
                useDeltaValues: false,
                shouldDetectModules: false,
                dontComputeNew: true,
                doNotResolveRelativePaths: true,
                thresholds: []
        )
        1 * jobManagement.requirePlugin(pluginId)

        where:
        analysisTool      | extraNodes                                                             | pluginId
        'pmd'             | [:]                                                                    | 'pmd'
        'findbugs'        | [isRankActivated: false]                                               | 'findbugs'
        'ccm'             | [:]                                                                    | 'ccm'
        'dependencyCheck' | [:]                                                      | 'dependency-check-jenkins-plugin'
        'androidLint'     | [:]                                                                    | 'android-lint'
        'checkstyle'      | [:]                                                                    | 'checkstyle'
        'dry'             | [highThreshold: 50, normalThreshold: 25]                               | 'dry'
    }

    def 'add warnings with default values'() {
        when:
        context.warnings(['Java Compiler (javac)'])

        then:
        context.publisherNodes.size() == 1
        def warningsNode = context.publisherNodes[0]
        assertValues(warningsNode, ['consoleParsers'], [:],
                healthy: '',
                unHealthy: '',
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

        1 * jobManagement.requireMinimumPluginVersion('warnings', '4.0')
    }

    @Unroll
    def 'add #analysisTool with all values'(String analysisTool, String nodeName, List extraArgs, Map extraValues) {
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
        assertValues(analysisNode, ['thresholds'], extraValues,
                pattern: 'somewhere',
                healthy: 3, unHealthy: 20,
                thresholdLimit: 'high',
                defaultEncoding: 'UTF-8',
                canRunOnFailed: true,
                useStableBuildAsReference: true,
                useDeltaValues: true,
                shouldDetectModules: true,
                dontComputeNew: false,
                doNotResolveRelativePaths: true
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
        'ccm'             | 'hudson.plugins.ccm.CcmPublisher'                                | []        | [:]
        'dependencyCheck' | 'org.jenkinsci.plugins.DependencyCheck.DependencyCheckPublisher' | []        | [:]
        'androidLint'     | 'org.jenkinsci.plugins.android__lint.LintPublisher'              | []        | [:]
        'dry'             | 'hudson.plugins.dry.DryPublisher'                                |
                [60, 37]                                                                                 |
                [highThreshold: 60, normalThreshold: 37]
        'tasks'           | 'hudson.plugins.tasks.TasksPublisher'                            |
                ['**/*.xml', 'FIXME', 'TODO', 'LOW', true]                                               |
                [excludePattern: '**/*.xml', high: 'FIXME', normal: 'TODO', low: 'LOW', ignoreCase: true,
                 asRegexp: false]
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

        1 * jobManagement.requireMinimumPluginVersion('warnings', '4.0')
    }

    def 'add analysis collector with default values'() {
        when:
        context.analysisCollector()

        then:
        context.publisherNodes.size() == 1
        def analysisCollectorNode = context.publisherNodes[0]
        assertValues(
                analysisCollectorNode,
                healthy: '',
                unHealthy: '',
                thresholdLimit: 'low',
                defaultEncoding: '',
                canRunOnFailed: false,
                useStableBuildAsReference: false,
                useDeltaValues: false,
                shouldDetectModules: false,
                dontComputeNew: true,
                doNotResolveRelativePaths: true,
                thresholds: [],
                isCheckStyleDeactivated: true,
                isDryDeactivated: true,
                isFindBugsDeactivated: true,
                isPmdDeactivated: true,
                isOpenTasksDeactivated: true,
                isWarningsDeactivated: true
        )
        1 * jobManagement.requirePlugin('analysis-collector')
    }

    def 'add analysis collector with all values'() {
        when:
        context.analysisCollector {
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
            checkstyle true
            dry true
            findbugs true
            pmd true
            tasks true
            warnings true
        }

        then:
        context.publisherNodes.size() == 1
        def analysisCollectorNode = context.publisherNodes[0]
        analysisCollectorNode.name() == 'hudson.plugins.analysis.collector.AnalysisPublisher'
        assertValues(
                analysisCollectorNode,
                ['thresholds'],
                healthy: 3,
                unHealthy: 20,
                thresholdLimit: 'high',
                defaultEncoding: 'UTF-8',
                canRunOnFailed: true,
                useStableBuildAsReference: true,
                useDeltaValues: true,
                shouldDetectModules: true,
                dontComputeNew: false,
                doNotResolveRelativePaths: true,
                isCheckStyleDeactivated: false,
                isDryDeactivated: false,
                isFindBugsDeactivated: false,
                isPmdDeactivated: false,
                isOpenTasksDeactivated: false,
                isWarningsDeactivated: false,
        )
        assertValues(
                analysisCollectorNode.thresholds,
                unstableTotalAll: 1, unstableTotalHigh: 2, unstableTotalNormal: 3, unstableTotalLow: 4,
                failedTotalAll: 5, failedTotalHigh: 6, failedTotalNormal: 7, failedTotalLow: 8,
                unstableNewAll: 9, unstableNewHigh: 10, unstableNewNormal: 11, unstableNewLow: 12,
                failedNewAll: 13, failedNewHigh: 14, failedNewNormal: 15, failedNewLow: 16,
        )
        1 * jobManagement.requirePlugin('analysis-collector')
    }

    def 'task scanner with minimal options'() {
        when:
        context.tasks('foo')

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            children().size() == 18
            pattern[0].value() == 'foo'
            high[0].value().empty
            normal[0].value().empty
            low[0].value().empty
            ignoreCase[0].value() == false
            excludePattern[0].value().empty
            healthy[0].value() == ''
            unHealthy[0].value() == ''
            thresholdLimit[0].value() == 'low'
            defaultEncoding[0].value().empty
            thresholds[0].value().empty
            canRunOnFailed[0].value() == false
            useStableBuildAsReference[0].value() == false
            useDeltaValues[0].value() == false
            shouldDetectModules[0].value() == false
            dontComputeNew[0].value() == true
            doNotResolveRelativePaths[0].value() == true
            asRegexp[0].value() == false
        }
        1 * jobManagement.requireMinimumPluginVersion('tasks', '4.41')
    }

    def 'task scanner with extra options'() {
        when:
        context.tasks('foo', 'bar', 'one', 'two', 'three', true) {
            regularExpression()
        }

        then:
        context.publisherNodes.size() == 1
        with(context.publisherNodes[0]) {
            children().size() == 18
            pattern[0].value() == 'foo'
            high[0].value() == 'one'
            normal[0].value() == 'two'
            low[0].value() == 'three'
            ignoreCase[0].value() == true
            excludePattern[0].value() == 'bar'
            healthy[0].value() == ''
            unHealthy[0].value() == ''
            thresholdLimit[0].value() == 'low'
            defaultEncoding[0].value().empty
            thresholds[0].value().empty
            canRunOnFailed[0].value() == false
            useStableBuildAsReference[0].value() == false
            useDeltaValues[0].value() == false
            shouldDetectModules[0].value() == false
            dontComputeNew[0].value() == true
            doNotResolveRelativePaths[0].value() == true
            asRegexp[0].value() == true
        }
        1 * jobManagement.requireMinimumPluginVersion('tasks', '4.41')
    }

    private static void assertValues(Map map, baseNode, List notCheckedNodes = [], Map extraNodes = [:]) {
        (map + extraNodes).each { String key, expectedValue ->
            def nodeList = baseNode[key]
            assert nodeList.size() == 1
            def node = nodeList[0]
            assert node != null, "Key ${key} not present in XML-Structure"
            assert node.value() == expectedValue
        }
        def children = baseNode instanceof Node ? baseNode.children() : baseNode*.children().flatten()
        assert children*.name().toSet() == (map + extraNodes).keySet() + notCheckedNodes
    }
}
