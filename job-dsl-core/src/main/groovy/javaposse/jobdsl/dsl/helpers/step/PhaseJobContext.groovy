package javaposse.jobdsl.dsl.helpers.step

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.common.DownstreamTriggerContext

class PhaseJobContext implements Context {
    private static final List<String> VALID_KILL_CONDITIONS = ['FAILURE', 'NEVER', 'UNSTABLE']

    private final JobManagement jobManagement

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
    String nodeLabelParam
    List<String> props = []
    boolean disableJob = false
    String killPhaseCondition = 'FAILURE'

    PhaseJobContext(JobManagement jobManagement, String jobName, boolean currentJobParameters, boolean exposedScm) {
        this.jobManagement = jobManagement
        this.jobName = jobName
        this.currentJobParameters = currentJobParameters
        this.exposedScm = exposedScm
    }

    void jobName(String jobName) {
        this.jobName = jobName
    }

    void currentJobParameters(boolean currentJobParameters = true) {
        this.currentJobParameters = currentJobParameters
        paramTrigger.currentBuild()
    }

    void exposedScm(boolean exposedScm = true) {
        this.exposedScm = exposedScm
    }

    void boolParam(String paramName, boolean defaultValue = false) {
        boolParams[paramName] = defaultValue
        paramTrigger.boolParam(paramName, defaultValue)
    }

    void fileParam(String propertyFile, boolean failTriggerOnMissing = false) {
        Preconditions.checkState(!fileParam, "File parameter already set with ${fileParam}")
        this.fileParam = propertyFile
        this.failTriggerOnMissing = failTriggerOnMissing
        paramTrigger.propertiesFile(propertyFile, failTriggerOnMissing)
    }

    void sameNode(boolean nodeParam = true) {
        this.nodeParam = nodeParam
        paramTrigger.sameNode(nodeParam)
    }

    void matrixParam(String filter) {
        Preconditions.checkState(!matrixFilter, "Matrix parameter already set with ${matrixFilter}")
        this.matrixFilter = filter
        paramTrigger.matrixSubset(filter)
    }

    void subversionRevision(boolean includeUpstreamParameters = false) {
        this.subversionRevision = includeUpstreamParameters
        paramTrigger.subversionRevision(includeUpstreamParameters)
    }

    void gitRevision(boolean combineQueuedCommits = false) {
        this.gitRevision = combineQueuedCommits
        paramTrigger.gitRevision(combineQueuedCommits)
    }

    void prop(Object key, Object value) {
        props << "${key}=${value}"
        paramTrigger.predefinedProp(key, value)
    }

    void props(Map<String, String> map) {
        map.entrySet().each {
            prop(it.key, it.value)
        }
        paramTrigger.predefinedProps(map)
    }

    void nodeLabel(String paramName, String nodeLabel)  {
        Preconditions.checkState(!this.nodeLabelParam, "nodeLabel parameter already set with ${this.nodeLabelParam}")
        this.nodeLabelParam = paramName
        paramTrigger.nodeLabel(paramName, nodeLabel)
    }

    Node configAsNode() {
        paramTrigger.createParametersNode()
    }

    boolean hasConfig() {
        !boolParams.isEmpty() || fileParam || nodeParam || matrixFilter || subversionRevision != null ||
                gitRevision != null || !props.isEmpty() || nodeLabelParam
    }

    void disableJob(boolean disableJob = true) {
        jobManagement.requireMinimumPluginVersion('jenkins-multijob-plugin', '1.11')
        this.disableJob = disableJob
    }

    void killPhaseCondition(String killPhaseCondition) {
        jobManagement.requireMinimumPluginVersion('jenkins-multijob-plugin', '1.11')

        Preconditions.checkArgument(
                VALID_KILL_CONDITIONS.contains(killPhaseCondition),
                "Kill Phase on Job Result Condition needs to be one of these values: ${VALID_KILL_CONDITIONS.join(',')}"
        )

        this.killPhaseCondition = killPhaseCondition
    }
}
