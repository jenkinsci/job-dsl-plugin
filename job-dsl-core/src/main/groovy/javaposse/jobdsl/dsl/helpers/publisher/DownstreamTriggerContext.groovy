package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

class DownstreamTriggerContext implements Context {
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

    boolean hasParameter() {
        return usingCurrentBuild || usingGitRevision || usingMatrixSubset || usingPredefined || usingPropertiesFile || usingSubversionRevision
    }
}
