package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.helpers.common.DownstreamTriggerParameterContext

class PhaseJobContext extends AbstractContext {
    private static final List<String> VALID_KILL_CONDITIONS = ['FAILURE', 'NEVER', 'UNSTABLE']

    String jobName
    boolean currentJobParameters = true
    boolean exposedScm = true
    DownstreamTriggerParameterContext paramTrigger
    boolean disableJob = false
    boolean abortAllJobs = false
    String killPhaseCondition = 'FAILURE'
    Closure configureBlock

    PhaseJobContext(JobManagement jobManagement, Item item, String jobName) {
        super(jobManagement)
        this.jobName = jobName
        this.paramTrigger = new DownstreamTriggerParameterContext(jobManagement, item)
    }

    /**
     * Copies parameters from the current build, except for file parameters. Defaults to [@code true}.
     */
    void currentJobParameters(boolean currentJobParameters = true) {
        this.currentJobParameters = currentJobParameters
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
    @RequiresPlugin(id = 'parameterized-trigger', minimumVersion = '2.26')
    void parameters(@DslContext(DownstreamTriggerParameterContext) Closure closure) {
        ContextHelper.executeInContext(closure, paramTrigger)
    }

    /**
     * Disables the job. Defaults to {@code false}.
     *
     * @since 1.25
     */
    void disableJob(boolean disableJob = true) {
        this.disableJob = disableJob
    }

    /**
     * Kills all sub jobs and the phase job if this sub job is killed. Defaults to {@code false}.
     *
     * @since 1.37
     */
    void abortAllJobs(boolean abortAllJob = true) {
        this.abortAllJobs = abortAllJob
    }

    /**
     * Kills the phase when a condition is met.
     *
     * Must be one of {@code 'FAILURE'} (default), {@code 'NEVER'} or {@code 'UNSTABLE'}.
     * @since 1.25
     */
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
    void configure(Closure configureBlock) {
        this.configureBlock = configureBlock
    }
}
