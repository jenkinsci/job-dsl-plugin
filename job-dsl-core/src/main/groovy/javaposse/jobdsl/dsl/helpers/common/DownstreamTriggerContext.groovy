package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin

import static javaposse.jobdsl.dsl.helpers.common.DownstreamContext.THRESHOLD_COLOR_MAP
import static javaposse.jobdsl.dsl.helpers.common.DownstreamContext.THRESHOLD_ORDINAL_MAP

class DownstreamTriggerContext extends AbstractContext {
    private static final Set<String> BLOCKING_THRESHOLD_TYPES = ['buildStepFailure', 'failure', 'unstable']

    List<BlockingThreshold> blockingThresholds = []
    List<String> predefinedProps = []
    String propFile
    String projects
    String condition
    String matrixSubsetFilter
    String nodeLabelParam
    String nodeLabel
    Map<String, Boolean> boolParams = [:]
    List<String[]> binaryFileParameterFactory = []

    boolean triggerWithNoParameters
    boolean failTriggerOnMissing
    boolean includeUpstreamParameters
    boolean usingSubversionRevision
    boolean usingCurrentBuild
    boolean usingPropertiesFile
    boolean usingGitRevision
    boolean combineQueuedCommits
    boolean usingPredefined
    boolean usingMatrixSubset
    boolean usingNodeLabel
    boolean sameNode

    DownstreamTriggerContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    void currentBuild() {
        usingCurrentBuild = true
    }

    void propertiesFile(String propFile, boolean failTriggerOnMissing = false) {
        usingPropertiesFile = true
        this.failTriggerOnMissing = failTriggerOnMissing
        this.propFile = propFile
    }

    @RequiresPlugin(id = 'git')
    void gitRevision(boolean combineQueuedCommits = false) {
        usingGitRevision = true
        this.combineQueuedCommits = combineQueuedCommits
    }

    void predefinedProp(String key, String value) {
        usingPredefined = true
        this.predefinedProps << "${key}=${value}"
    }

    void predefinedProps(Map<String, String> predefinedPropsMap) {
        usingPredefined = true
        List<String> props = predefinedPropsMap.collect { "${it.key}=${it.value}" }
        this.predefinedProps.addAll(props)
    }

    void predefinedProps(String predefinedProps) {
        usingPredefined = true
        this.predefinedProps.addAll(predefinedProps.split('\n'))
    }

    void matrixSubset(String groovyFilter) {
        usingMatrixSubset = true
        matrixSubsetFilter = groovyFilter
    }

    void subversionRevision(boolean includeUpstreamParameters = false) {
        this.includeUpstreamParameters = includeUpstreamParameters
        usingSubversionRevision = true
    }

    void boolParam(String paramName, boolean defaultValue = false) {
        boolParams[paramName] = defaultValue
    }

    void sameNode(boolean sameNode = true) {
        this.sameNode = sameNode
    }

    void forMatchingFiles(String paramName, String filePattern, String action = 'SKIP') {
        binaryFileParameterFactory << [paramName, filePattern, action]
    }

    /**
     * @since 1.26
     */
    @RequiresPlugin(id = 'nodelabelparameter')
    void nodeLabel(String paramName, String nodeLabel) {
        usingNodeLabel = true
        this.nodeLabelParam = paramName
        this.nodeLabel = nodeLabel
    }

    void blockingThresholdsFromMap(Map<String, String> thresholdMap) {
        thresholdMap.each { type, name ->
            blockingThreshold(type, name)
        }
    }

    void blockingThreshold(String thresholdType, String thresholdName) {
        Preconditions.checkArgument(BLOCKING_THRESHOLD_TYPES.contains(thresholdType),
                "thresholdType must be one of these values: ${BLOCKING_THRESHOLD_TYPES}")
        Preconditions.checkArgument(THRESHOLD_COLOR_MAP.containsKey(thresholdName),
                "thresholdName must be one of these values: ${THRESHOLD_COLOR_MAP.keySet().join(',')}")

        blockingThresholds << new BlockingThreshold(thresholdType, thresholdName)
    }

    boolean hasParameter() {
        usingCurrentBuild || usingGitRevision || usingMatrixSubset || usingPredefined || usingPropertiesFile ||
                usingSubversionRevision || !boolParams.isEmpty() || sameNode || usingNodeLabel
    }

    boolean hasFactories() {
        !binaryFileParameterFactory.isEmpty()
    }

    Node createParametersNode() {
        NodeBuilder nodeBuilder = NodeBuilder.newInstance()

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
                    delegate.createNode('name', nodeLabelParam)
                    delegate.createNode('nodeLabel', nodeLabel)
                }
            }

            if (sameNode) {
                'hudson.plugins.parameterizedtrigger.NodeParameters'()
            }

            if (!boolParams.isEmpty()) {
                'hudson.plugins.parameterizedtrigger.BooleanParameters' {
                    configs {
                        boolParams.each { k, v ->
                            Node boolConfigNode = 'hudson.plugins.parameterizedtrigger.BooleanParameterConfig' {
                                value(v)
                            }
                            boolConfigNode.appendNode('name', k)
                        }
                    }
                }
            }
        }
    }

    Node createFactoriesNode() {
        NodeBuilder nodeBuilder = NodeBuilder.newInstance()

        nodeBuilder.'configFactories' {
            if (!binaryFileParameterFactory.isEmpty()) {
                binaryFileParameterFactory.each { entry ->
                    'hudson.plugins.parameterizedtrigger.BinaryFileParameterFactory' {
                        delegate.createNode('parameterName', entry[0])
                        delegate.createNode('filePattern', entry[1])
                        delegate.createNode('noFilesFoundAction', entry[2])
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
