package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
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

    PhaseJobContext(JobManagement jobManagement, String jobName) {
        super(jobManagement)
        this.jobName = jobName
    }

    @Deprecated
    PhaseJobContext(JobManagement jobManagement, String jobName, boolean currentJobParameters, boolean exposedScm) {
        this(jobManagement, jobName)
        this.currentJobParameters = currentJobParameters
        this.exposedScm = exposedScm
    }

    /**
     * Defines the name of the job.
     */
    @Deprecated
    void jobName(String jobName) {
        jobManagement.logDeprecationWarning()

        this.jobName = jobName
    }

    /**
     * Copies parameters from the current build, except for file parameters. Defaults to [@code true}.
     */
    void currentJobParameters(boolean currentJobParameters = true) {
        this.currentJobParameters = currentJobParameters
        paramTrigger.currentBuild()
    }

    /**
     * Defaults to {@code true}.
     */
    void exposedScm(boolean exposedScm = true) {
        this.exposedScm = exposedScm
    }

    /**
     * Adds parameter values for the job.
     *
     * @since 1.38
     */
    @RequiresPlugin(id = 'parameterized-trigger')
    void parameters(@DslContext(DownstreamTriggerParameterContext) Closure closure) {
        jobManagement.logPluginDeprecationWarning('parameterized-trigger', '2.26')

        ContextHelper.executeInContext(closure, paramTrigger)
    }

    /**
     * Adds a boolean parameter. Can be called multiple times to add more parameters.
     */
    @Deprecated
    void boolParam(String paramName, boolean defaultValue = false) {
        jobManagement.logDeprecationWarning()
        paramTrigger.boolParam(paramName, defaultValue)
    }

    /**
     * Reads parameters from a properties file.
     */
    @Deprecated
    void fileParam(String propertyFile, boolean failTriggerOnMissing = false) {
        jobManagement.logDeprecationWarning()
        paramTrigger.propertiesFile(propertyFile, failTriggerOnMissing)
    }

    /**
     * Uses the same node for the triggered builds that was used for this build.
     */
    @Deprecated
    void sameNode(boolean nodeParam = true) {
        jobManagement.logDeprecationWarning()
        paramTrigger.sameNode = nodeParam
    }

    /**
     * Specifies a Groovy filter expression that restricts the subset of combinations that the downstream project will
     * run.
     */
    @Deprecated
    void matrixParam(String filter) {
        jobManagement.logDeprecationWarning()
        paramTrigger.matrixSubset(filter)
    }

    /**
     * Passes the Subversion revisions that were used in this build to the downstream builds.
     */
    @Deprecated
    void subversionRevision(boolean includeUpstreamParameters = false) {
        jobManagement.logDeprecationWarning()
        paramTrigger.subversionRevision(includeUpstreamParameters)
    }

    /**
     * Passes the Git commit that was used in this build to the downstream builds.
     */
    @Deprecated
    void gitRevision(boolean combineQueuedCommits = false) {
        jobManagement.logDeprecationWarning()
        paramTrigger.gitRevision(combineQueuedCommits)
    }

    /**
     * Adds a parameter. Can be called multiple times to add more parameters.
     */
    @Deprecated
    void prop(Object key, Object value) {
        jobManagement.logDeprecationWarning()
        paramTrigger.predefinedProp(key, value)
    }

    /**
     * Adds parameters. Can be called multiple times to add more parameters.
     */
    @Deprecated
    void props(Map<String, String> map) {
        jobManagement.logDeprecationWarning()
        paramTrigger.predefinedProps(map)
    }

    /**
     * Defines where the target job should be executed, the value must match either a label or a node name.
     *
     * @since 1.26
     */
    @Deprecated
    void nodeLabel(String paramName, String nodeLabel) {
        jobManagement.logDeprecationWarning()
        paramTrigger.nodeLabel(paramName, nodeLabel)
    }

    /**
     * Disables the job. Defaults to {@code false}.
     *
     * @since 1.25
     */
    @RequiresPlugin(id = 'jenkins-multijob-plugin', minimumVersion = '1.11')
    void disableJob(boolean disableJob = true) {
        this.disableJob = disableJob
    }

    /**
     * Kills all sub jobs and the phase job if this sub job is killed. Defaults to {@code false}.
     *
     * @since 1.37
     */
    @RequiresPlugin(id = 'jenkins-multijob-plugin', minimumVersion = '1.14')
    void abortAllJobs(boolean abortAllJob = true) {
        this.abortAllJobs = abortAllJob
    }

    /**
     * Kills the phase when a condition is met.
     *
     * Must be one of {@code 'FAILURE'} (default), {@code 'NEVER'} or [@code 'UNSTABLE'}.
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
     * Allows direct manipulation of the generated XML. The {@code com.tikal.jenkins.plugins.multijob.PhaseJobsConfig}
     * node is passed into the configure block.
     *
     * @since 1.30
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    void configure(Closure configureClosure) {
        this.configureClosure = configureClosure
    }
}
