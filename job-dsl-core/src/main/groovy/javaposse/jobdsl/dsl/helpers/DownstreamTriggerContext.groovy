package javaposse.jobdsl.dsl.helpers
import static javaposse.jobdsl.dsl.helpers.DownstreamContext.THRESHOLD_COLOR_MAP
import static javaposse.jobdsl.dsl.helpers.DownstreamContext.THRESHOLD_ORDINAL_MAP

import com.google.common.base.Preconditions

class DownstreamTriggerContext implements Context {
    def blockingThresholdTypes = ['buildStepFailure', 'failure', 'unstable']

    def blockingThresholds = []
    List<String> predefinedProps = []
    String propFile
    String projects
    String condition
    String matrixSubsetFilter
    Map<String, Boolean> boolParams = [:]

    boolean triggerWithNoParameters
    boolean failTriggerOnMissing = false
    boolean includeUpstreamParameters = false
    boolean usingSubversionRevision = false
    boolean usingCurrentBuild = false
    boolean usingPropertiesFile = false
    boolean usingGitRevision = false
    boolean combineQueuedCommits = false
    boolean usingPredefined = false
    boolean usingMatrixSubset = false
    boolean sameNode = false

    def currentBuild() {
        usingCurrentBuild = true
    }

    def propertiesFile(String propFile, boolean failTriggerOnMissing = false) {
        usingPropertiesFile = true
        this.failTriggerOnMissing = failTriggerOnMissing
        this.propFile = propFile
    }

    def gitRevision(boolean combineQueuedCommits = false) {
        usingGitRevision = true
        this.combineQueuedCommits = combineQueuedCommits
    }

    def predefinedProp(String key, String value) {
        usingPredefined = true
        this.predefinedProps << "${key}=${value}"
    }

    def predefinedProps(Map<String, String> predefinedPropsMap) {
        usingPredefined = true
        def props = predefinedPropsMap.collect { "${it.key}=${it.value}" }
        this.predefinedProps.addAll(props)
    }

    def predefinedProps(String predefinedProps) { // Newline separated
        usingPredefined = true
        this.predefinedProps.addAll(predefinedProps.split('\n'))
    }

    def matrixSubset(String groovyFilter) {
        usingMatrixSubset = true
        matrixSubsetFilter = groovyFilter
    }

    def subversionRevision(boolean includeUpstreamParameters = false) {
        this.includeUpstreamParameters = includeUpstreamParameters
        usingSubversionRevision = true
    }

    def boolParam(String paramName, boolean defaultValue = false) {
        boolParams[paramName] = defaultValue
    }

    def sameNode(boolean sameNode = true) {
        this.sameNode = sameNode
    }

    def blockingThresholdsFromMap(Map<String, String> thresholdMap) {
        thresholdMap.each { type, name ->
            blockingThreshold(type, name)
        }
    }

    def blockingThreshold(String thresholdType, String thresholdName) {
        Preconditions.checkArgument(blockingThresholdTypes.contains(thresholdType),
                "thresholdType must be one of these values: ${blockingThresholdTypes}")
        Preconditions.checkArgument(THRESHOLD_COLOR_MAP.containsKey(thresholdName),
                "thresholdName must be one of these values: ${THRESHOLD_COLOR_MAP.keySet().join(',')}")

        blockingThresholds << new BlockingThreshold(thresholdType, thresholdName)
    }

    boolean hasParameter() {
        return (usingCurrentBuild || usingGitRevision || usingMatrixSubset
                || usingPredefined || usingPropertiesFile || usingSubversionRevision
                || !boolParams.isEmpty() || sameNode)
    }

    def createParametersNode() {
        def nodeBuilder = NodeBuilder.newInstance()

        return nodeBuilder.'configs' {
            if (usingCurrentBuild) {
                'hudson.plugins.parameterizedtrigger.CurrentBuildParameters'(plugin:'parameterized-trigger@2.17')
            }

            if (usingPropertiesFile) {
                'hudson.plugins.parameterizedtrigger.FileBuildParameters'(plugin:'parameterized-trigger@2.17') {
                    delegate.createNode('propertiesFile', propFile)
                    delegate.createNode('failTriggerOnMissing', failTriggerOnMissing?'true':'false')
                }
            }

            if (usingGitRevision) {
                'hudson.plugins.git.GitRevisionBuildParameters'(plugin:'parameterized-trigger@2.17') {
                    'combineQueuedCommits' combineQueuedCommits ? 'true' : 'false'
                }
            }

            if (usingPredefined) {
                'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'(plugin:'parameterized-trigger@2.17') {
                    delegate.createNode('properties', predefinedProps.join('\n'))
                }
            }

            if (usingMatrixSubset) {
                'hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters'(plugin:'parameterized-trigger@2.17') {
                    filter matrixSubsetFilter
                }
            }

            if (usingSubversionRevision) {
                'hudson.plugins.parameterizedtrigger.SubversionRevisionBuildParameters'(plugin:'parameterized-trigger@2.17') {
                    delegate.createNode('includeUpstreamParameters', includeUpstreamParameters?'true':'false')
                }
            }

            if (sameNode) {
                'hudson.plugins.parameterizedtrigger.NodeParameters'(plugin:'parameterized-trigger@2.17')
            }

            if (!boolParams.isEmpty()) {
                'hudson.plugins.parameterizedtrigger.BooleanParameters'(plugin:'parameterized-trigger@2.17')  {
                    configs {
                        boolParams.each { k, v ->
                            def boolConfigNode = 'hudson.plugins.parameterizedtrigger.BooleanParameterConfig' {
                                value(v?'true':'false')
                            }
                            boolConfigNode.appendNode('name', k)
                        }
                    }
                }
            }
        }
    }

    static class BlockingThreshold {
        String thresholdType
        String thresholdName
        int thresholdOrdinal
        String thresholdColor

        BlockingThreshold(String thresholdType, String thresholdName) {
            this.thresholdType = thresholdType
            this.thresholdName = thresholdName
            this.thresholdOrdinal = THRESHOLD_ORDINAL_MAP[thresholdName]
            this.thresholdColor = THRESHOLD_COLOR_MAP[thresholdName]
        }
    }
}
