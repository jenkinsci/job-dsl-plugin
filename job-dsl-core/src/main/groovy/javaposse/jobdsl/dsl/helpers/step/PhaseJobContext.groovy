package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.helpers.common.DownstreamTriggerParameterContext

class PhaseJobContext extends AbstractContext {
    private static final List<String> VALID_KILL_CONDITIONS = ['FAILURE', 'NEVER', 'UNSTABLE']

    String jobName
    boolean currentJobParameters = true
    boolean exposedScm = true
    DownstreamTriggerParameterContext paramTrigger = new DownstreamTriggerParameterContext(jobManagement)
    boolean disableJob = false
    boolean abortAllJobs = false
    String killPhaseCondition = 'FAILURE'
    Closure configureClosure

    PhaseJobContext(JobManagement jobManagement, String jobName, boolean currentJobParameters, boolean exposedScm) {
        super(jobManagement)
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

    @RequiresPlugin(id = 'parameterized-trigger')
    void parameters(@javaposse.jobdsl.dsl.DslContext(DownstreamTriggerParameterContext) Closure closure) {
        jobManagement.logPluginDeprecationWarning('parameterized-trigger', '2.25')

        ContextHelper.executeInContext(closure, paramTrigger)
    }

    @Deprecated
    void boolParam(String paramName, boolean defaultValue = false) {
        jobManagement.logDeprecationWarning()
        paramTrigger.boolParam(paramName, defaultValue)
    }

    @Deprecated
    void fileParam(String propertyFile, boolean failTriggerOnMissing = false) {
        jobManagement.logDeprecationWarning()
        paramTrigger.propertiesFile(propertyFile, failTriggerOnMissing)
    }

    @Deprecated
    void sameNode(boolean nodeParam = true) {
        jobManagement.logDeprecationWarning()
        paramTrigger.sameNode = nodeParam
    }

    @Deprecated
    void matrixParam(String filter) {
        jobManagement.logDeprecationWarning()
        paramTrigger.matrixSubset(filter)
    }

    @Deprecated
    void subversionRevision(boolean includeUpstreamParameters = false) {
        jobManagement.logDeprecationWarning()
        paramTrigger.subversionRevision(includeUpstreamParameters)
    }

    @Deprecated
    void gitRevision(boolean combineQueuedCommits = false) {
        jobManagement.logDeprecationWarning()
        paramTrigger.gitRevision(combineQueuedCommits)
    }

    @Deprecated
    void prop(Object key, Object value) {
        jobManagement.logDeprecationWarning()
        paramTrigger.predefinedProp(key, value)
    }

    @Deprecated
    void props(Map<String, String> map) {
        jobManagement.logDeprecationWarning()
        paramTrigger.predefinedProps(map)
    }

    /**
     * @since 1.26
     */
    @Deprecated
    void nodeLabel(String paramName, String nodeLabel)  {
        jobManagement.logDeprecationWarning()
        paramTrigger.nodeLabel(paramName, nodeLabel)
    }

    /**
     * @since 1.25
     */
    @RequiresPlugin(id = 'jenkins-multijob-plugin', minimumVersion = '1.11')
    void disableJob(boolean disableJob = true) {
        this.disableJob = disableJob
    }

    /**
     * @since 1.37
     */
    @RequiresPlugin(id = 'jenkins-multijob-plugin', minimumVersion = '1.14')
    void abortAllJobs(boolean abortAllJob = true) {
        this.abortAllJobs = abortAllJob
    }

    /**
     * @since 1.25
     */
    @RequiresPlugin(id = 'jenkins-multijob-plugin', minimumVersion = '1.11')
    void killPhaseCondition(String killPhaseCondition) {
        Preconditions.checkArgument(
                VALID_KILL_CONDITIONS.contains(killPhaseCondition),
                "Kill Phase on Job Result Condition needs to be one of these values: ${VALID_KILL_CONDITIONS.join(',')}"
        )

        this.killPhaseCondition = killPhaseCondition
    }

    /**
     * @since 1.30
     */
    void configure(Closure configureClosure) {
        this.configureClosure = configureClosure
    }
}
