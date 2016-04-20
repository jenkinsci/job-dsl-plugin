package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions

class MultiJobStepContext extends StepContext {
    private static final List<String> VALID_CONTINUATION_CONDITIONS = [
            'SUCCESSFUL', 'UNSTABLE', 'COMPLETED', 'FAILURE', 'ALWAYS'
    ]

    private static final List<String> VALID_EXECUTION_TYPES = [
            'PARALLEL', 'SEQUENTIAL'
    ]

    MultiJobStepContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    /**
     * Adds a MultiJob phase.
     */
    void phase(@DslContext(PhaseContext) Closure phaseContext) {
        phase(null, 'SUCCESSFUL', 'PARALLEL', phaseContext)
    }

    /**
     * Adds a MultiJob phase.
     */
    void phase(String phaseName, @DslContext(PhaseContext) Closure phaseContext = null) {
        phase(phaseName, 'SUCCESSFUL', 'PARALLEL', phaseContext)
    }

    /**
     * Adds a MultiJob phase.
     */
    void phase(String phaseName, String continuationCondition, @DslContext(PhaseContext) Closure phaseContext = null) {
        phase(phaseName, continuationCondition, 'PARALLEL', phaseContext)
    }

    /**
     * Adds a MultiJob phase.
     *
     * {@code continuationCondition} must be one of {@code 'SUCCESSFUL'}, {@code 'UNSTABLE'}, {@code 'COMPLETED'} or
     * {@code 'FAILURE'}. When version 1.16 or later of the MultiJob plugin is installed, {@code continuationCondition}
     * can also be set to {@code 'ALWAYS'}.
     * {@code executionType} must be one of {@code 'PARALLEL'}, {@code 'SEQUENTIAL'}.
     */
    void phase(String name, String continuationCondition, String executionType,
               @DslContext(PhaseContext) Closure phaseClosure) {
        PhaseContext phaseContext = new PhaseContext(jobManagement, item, name, continuationCondition, executionType,
                null, '', null, '', '', false, false)
        ContextHelper.executeInContext(phaseClosure, phaseContext)

        Preconditions.checkNotNullOrEmpty(phaseContext.phaseName, 'A phase needs a name')
        Preconditions.checkArgument(
                VALID_CONTINUATION_CONDITIONS.contains(phaseContext.continuationCondition),
                "Continuation Condition needs to be one of these values: ${VALID_CONTINUATION_CONDITIONS.join(', ')}"
        )
        Preconditions.checkArgument(
                VALID_EXECUTION_TYPES.contains(phaseContext.executionType),
                "Execution type needs to be one of these values: ${VALID_EXECUTION_TYPES.join(', ')}"
        )
        stepNodes << new NodeBuilder().'com.tikal.jenkins.plugins.multijob.MultiJobBuilder' {
            phaseName phaseContext.phaseName
            delegate.continuationCondition(phaseContext.continuationCondition)
            delegate.executionType(phaseContext.executionType)
            delegate.enableGroovyScript(phaseContext.enableGroovyScript)
            delegate.scriptText(phaseContext.scriptText)
            delegate.isUseScriptFile(phaseContext.isUseScriptFile)
            delegate.scriptPath(phaseContext.scriptPath)
            delegate.bindings(phaseContext.bindings)
            delegate.isScriptOnSlave(phaseContext.isScriptOnSlave)
            delegate.isRunOnSlave(phaseContext.isRunOnSlave)
            phaseJobs {
                phaseContext.jobsInPhase.each { PhaseJobContext jobInPhase ->
                    Node phaseJobNode = 'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig' {
                        jobName jobInPhase.jobName
                        currParams jobInPhase.currentJobParameters
                        exposedSCM jobInPhase.exposedScm
                        disableJob jobInPhase.disableJob
                        killPhaseOnJobResultCondition jobInPhase.killPhaseCondition
                        abortAllJob jobInPhase.abortAllJobs
                        enableJobScript jobInPhase.enableJobScript
                        jobScript jobInPhase.jobScript
                        isUseScriptFile jobInPhase.isUseScriptFile
                        scriptPath jobInPhase.scriptPath
                        resumeCondition jobInPhase.resumeCondition
                        resumeScriptPath jobInPhase.resumeScriptPath
                        resumeScriptText jobInPhase.resumeScriptText
                        isUseResumeScriptFile jobInPhase.isUseResumeScriptFile
                        jobBindings jobInPhase.jobBindings
                        resumeBindings jobInPhase.resumeBindings
                        resumeConditions jobInPhase.resumeConditions
                        isRunJobScriptOnSlave jobInPhase.isRunJobScriptOnSlave
                        isRunResumeScriptOnSlave jobInPhase.isRunResumeScriptOnSlave
                        configs(jobInPhase.paramTrigger.configs ?: [class: 'java.util.Collections$EmptyList'])
                    }

                    ContextHelper.executeConfigureBlock(phaseJobNode, jobInPhase.configureBlock)
                }
            }
        }
    }

    @Override
    protected StepContext newInstance() {
        new MultiJobStepContext(jobManagement, item)
    }
}
