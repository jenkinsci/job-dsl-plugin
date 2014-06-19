package javaposse.jobdsl.dsl.helpers.common

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.helpers.Context

import static DownstreamContext.THRESHOLD_COLOR_MAP

class DownstreamTriggerContext implements Context {
    def blockingThresholdTypes = ['buildStepFailure', 'failure', 'unstable']

    def blockingThresholds = []
    List<String> predefinedProps = []
    String propFile
    String projects
    String condition
    String matrixSubsetFilter
    String nodeLabelParam
    String nodeLabel
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
    boolean usingNodeLabel = false
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

    def nodeLabel(String paramName, String nodeLabel) {
        usingNodeLabel = true
        this.nodeLabelParam = paramName
        this.nodeLabel = nodeLabel
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
        usingCurrentBuild || usingGitRevision || usingMatrixSubset || usingPredefined || usingPropertiesFile ||
                usingSubversionRevision || !boolParams.isEmpty() || sameNode
    }

    Node createParametersNode() {
        def nodeBuilder = NodeBuilder.newInstance()

        nodeBuilder.'configs' {
            if (usingCurrentBuild) {
                'hudson.plugins.parameterizedtrigger.CurrentBuildParameters'()
            }

            if (usingPropertiesFile) {
                'hudson.plugins.parameterizedtrigger.FileBuildParameters' {
                    delegate.createNode('propertiesFile', propFile)
                    delegate.createNode('failTriggerOnMissing', failTriggerOnMissing)
                }
            }

            if (usingGitRevision) {
                'hudson.plugins.git.GitRevisionBuildParameters' {
                    'combineQueuedCommits' combineQueuedCommits
                }
            }

            if (usingPredefined) {
                'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters' {
                    delegate.createNode('properties', predefinedProps.join('\n'))
                }
            }

            if (usingMatrixSubset) {
                'hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters' {
                    filter matrixSubsetFilter
                }
            }

            if (usingSubversionRevision) {
                'hudson.plugins.parameterizedtrigger.SubversionRevisionBuildParameters' {
                    delegate.createNode('includeUpstreamParameters', includeUpstreamParameters)
                }
            }

            if (usingNodeLabel) {
                'org.jvnet.jenkins.plugins.nodelabelparameter.parameterizedtrigger.NodeLabelBuildParameter' {
                    name nodeLabelParam
                    nodeLabel nodeLabel
                }
            }

            if (sameNode) {
                'hudson.plugins.parameterizedtrigger.NodeParameters'()
            }

            if (!boolParams.isEmpty()) {
                'hudson.plugins.parameterizedtrigger.BooleanParameters'  {
                    configs {
                        boolParams.each { k, v ->
                            def boolConfigNode = 'hudson.plugins.parameterizedtrigger.BooleanParameterConfig' {
                                value(v)
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
            this.thresholdOrdinal = DownstreamContext.THRESHOLD_ORDINAL_MAP[thresholdName]
            this.thresholdColor = DownstreamContext.THRESHOLD_COLOR_MAP[thresholdName]
        }
    }
}
