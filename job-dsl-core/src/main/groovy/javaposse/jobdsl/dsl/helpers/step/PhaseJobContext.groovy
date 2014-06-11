package javaposse.jobdsl.dsl.helpers.step

import com.google.common.base.Preconditions
import groovy.transform.Canonical
import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.helpers.common.DownstreamTriggerContext

@Canonical
class PhaseJobContext implements Context {
    String jobName
    boolean currentJobParameters = true
    boolean exposedScm = true
    DownstreamTriggerContext paramTrigger = new DownstreamTriggerContext()
    Map<String, Boolean> boolParams = [:]
    String fileParam
    boolean failTriggerOnMissing
    boolean nodeParam = false
    String matrixFilter
    Boolean subversionRevision
    Boolean gitRevision
    def props = []

    void jobName(String jobName) {
        this.jobName = jobName
    }

    def currentJobParameters(boolean currentJobParameters = true) {
        this.currentJobParameters = currentJobParameters
        paramTrigger.currentBuild()
    }

    def exposedScm(boolean exposedScm = true) {
        this.exposedScm = exposedScm
    }

    def boolParam(String paramName, boolean defaultValue = false) {
        boolParams[paramName] = defaultValue
        paramTrigger.boolParam(paramName, defaultValue)
    }

    def fileParam(String propertyFile, boolean failTriggerOnMissing = false) {
        Preconditions.checkState(!fileParam, "File parameter already set with ${fileParam}")
        this.fileParam = propertyFile
        this.failTriggerOnMissing = failTriggerOnMissing
        paramTrigger.propertiesFile(propertyFile, failTriggerOnMissing)
    }

    def sameNode(boolean nodeParam = true) {
        this.nodeParam = nodeParam
        paramTrigger.sameNode(nodeParam)
    }

    def matrixParam(String filter) {
        Preconditions.checkState(!matrixFilter, "Matrix parameter already set with ${matrixFilter}")
        this.matrixFilter = filter
        paramTrigger.matrixSubset(filter)
    }

    def subversionRevision(boolean includeUpstreamParameters = false) {
        this.subversionRevision = includeUpstreamParameters
        paramTrigger.subversionRevision(includeUpstreamParameters)
    }

    def gitRevision(boolean combineQueuedCommits = false) {
        this.gitRevision = combineQueuedCommits
        paramTrigger.gitRevision(combineQueuedCommits)
    }

    def prop(Object key, Object value) {
        props << "${key}=${value}"
        paramTrigger.predefinedProp(key, value)
    }

    def props(Map<String, String> map) {
        map.entrySet().each {
            prop(it.key, it.value)
        }
        paramTrigger.predefinedProps(map)
    }

    def configAsNode() {
        return paramTrigger.createParametersNode()
    }

    def hasConfig() {
        return !boolParams.isEmpty() || fileParam || nodeParam || matrixFilter || subversionRevision != null || gitRevision != null || !props.isEmpty()
    }
}
