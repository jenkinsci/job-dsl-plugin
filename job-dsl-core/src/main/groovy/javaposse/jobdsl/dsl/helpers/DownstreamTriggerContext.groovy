package javaposse.jobdsl.dsl.helpers
import static javaposse.jobdsl.dsl.helpers.DownstreamContext.THRESHOLD_COLOR_MAP
import static javaposse.jobdsl.dsl.helpers.DownstreamContext.THRESHOLD_ORDINAL_MAP

import com.google.common.base.Preconditions

class DownstreamTriggerContext implements Context {
    def blockingThresholdTypes = ['buildStepFailure', 'failure', 'unstable']
    def blockingThresholds = []

    String projects
    String condition
    boolean triggerWithNoParameters

    boolean usingCurrentBuild = false

    def currentBuild() {
        usingCurrentBuild = true
    }

    boolean usingPropertiesFile = false
    String propFile

    def propertiesFile(String propFile) {
        usingPropertiesFile = true
        this.propFile = propFile
    }

    boolean usingGitRevision = false
    boolean combineQueuedCommits = false

    def gitRevision(boolean combineQueuedCommits = false) {
        usingGitRevision = true
        this.combineQueuedCommits = combineQueuedCommits
    }

    boolean usingPredefined = false
    List<String> predefinedProps = []

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

    boolean usingMatrixSubset = false
    String matrixSubsetFilter

    def matrixSubset(String groovyFilter) {
        usingMatrixSubset = true
        matrixSubsetFilter = groovyFilter
    }

    boolean usingSubversionRevision = false

    def subversionRevision() {
        usingSubversionRevision = true
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
                || usingPredefined || usingPropertiesFile || usingSubversionRevision)
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
