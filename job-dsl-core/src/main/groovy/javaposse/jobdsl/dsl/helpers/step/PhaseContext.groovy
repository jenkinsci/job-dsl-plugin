package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

class PhaseContext extends AbstractContext {
    protected final Item item

    String phaseName
    String continuationCondition
    String executionType
    Boolean enableGroovyScript
    Boolean isUseScriptFile
    String scriptText
    String scriptPath

    List<PhaseJobContext> jobsInPhase = []

    PhaseContext(JobManagement jobManagement, Item item, String phaseName, String continuationCondition,
                 String executionType, Boolean enableGroovyScript, String scriptText, Boolean isUseScriptFile,
                 String scriptPath) {
        super(jobManagement)
        this.item = item
        this.phaseName = phaseName
        this.continuationCondition = continuationCondition
        this.executionType = executionType
        this.enableGroovyScript = enableGroovyScript
        this.scriptText = scriptText
        this.isUseScriptFile = isUseScriptFile
        this.scriptPath = scriptPath
    }

    /**
     * Defines the name of the MultiJob phase.
     */
    void phaseName(String phaseName) {
        this.phaseName = phaseName
    }

    /**
     * Defines how to decide the status of the whole MultiJob phase.
     */
    void continuationCondition(String continuationCondition) {
        this.continuationCondition = continuationCondition
    }

    /**
     * Defines how to run jobs in a phase: parallel or sequentially
     */
    void executionType(String executionType) {
        this.executionType = executionType
    }

    void groovyScript(String script) {
        if (null == enableGroovyScript) {
            this.enableGroovyScript = true
        }
        this.groovyScript = script
    }

    void enableGroovyScript(boolean enableGroovyScript) {
        this.enableGroovyScript = enableGroovyScript
    }

    /**
     * Defines groovy script
     * @param source
     * @param script
     */
    void groovyScript(String source, String script) {
        if (null == enableGroovyScript) {
            this.enableGroovyScript = true
        }
        if ('FILE' == source) {
            this.scriptPath = script
            this.isUseScriptFile = true
        } else if ('SCRIPT' == source) {
            this.scriptText = script
            this.isUseScriptFile = false
        } else {
            this.enableGroovyScript = false
            this.isUseScriptFile = false
        }
    }

    /**
     * Adds a job to the phase.
     */
    @Deprecated
    void job(String jobName, @DslContext(PhaseJobContext) Closure phaseJobClosure = null) {
        job(jobName, true, true, phaseJobClosure)
    }

    /**
     * Adds a job to the phase.
     */
    @Deprecated
    void job(String jobName, boolean currentJobParameters,
             @DslContext(PhaseJobContext) Closure phaseJobClosure = null) {
        job(jobName, currentJobParameters, true, phaseJobClosure)
    }

    /**
     * Adds a job to the phase.
     */
    @Deprecated
    void job(String jobName, boolean currentJobParameters, boolean exposedScm,
             @DslContext(PhaseJobContext) Closure phaseJobClosure = null) {
        jobManagement.logDeprecationWarning()

        PhaseJobContext phaseJobContext = new PhaseJobContext(
                jobManagement, item, jobName, currentJobParameters, exposedScm
        )
        ContextHelper.executeInContext(phaseJobClosure, phaseJobContext)

        jobsInPhase << phaseJobContext
    }

    /**
     * Adds a job to the phase.
     *
     * @since 1.39
     */
    void phaseJob(String jobName, @DslContext(PhaseJobContext) Closure phaseJobClosure = null) {
        PhaseJobContext phaseJobContext = new PhaseJobContext(jobManagement, item, jobName)
        ContextHelper.executeInContext(phaseJobClosure, phaseJobContext)

        jobsInPhase << phaseJobContext
    }
}
