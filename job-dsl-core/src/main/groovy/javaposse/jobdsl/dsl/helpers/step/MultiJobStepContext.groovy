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

    MultiJobStepContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    /**
     * Adds a MultiJob phase.
     */
    void phase(@DslContext(PhaseContext) Closure phaseContext) {
        phase(null, 'SUCCESSFUL', phaseContext)
    }

    /**
     * Adds a MultiJob phase.
     */
    void phase(String phaseName, @DslContext(PhaseContext) Closure phaseContext = null) {
        phase(phaseName, 'SUCCESSFUL', phaseContext)
    }

    /**
     * Adds a MultiJob phase.
     *
     * {@code continuationCondition} must be one of {@code 'SUCCESSFUL'}, {@code 'UNSTABLE'}, {@code 'COMPLETED'} or
     * {@code 'FAILURE'}. When version 1.16 or later of the MultiJob plugin is installed, {@code continuationCondition}
     * can also be set to {@code 'ALWAYS'}.
     */
    void phase(String name, String continuationCondition, @DslContext(PhaseContext) Closure phaseClosure) {
        PhaseContext phaseContext = new PhaseContext(jobManagement, item, name, continuationCondition)
        ContextHelper.executeInContext(phaseClosure, phaseContext)

        Preconditions.checkNotNullOrEmpty(phaseContext.phaseName, 'A phase needs a name')
        Preconditions.checkArgument(
                VALID_CONTINUATION_CONDITIONS.contains(phaseContext.continuationCondition),
                "Continuation Condition needs to be one of these values: ${VALID_CONTINUATION_CONDITIONS.join(', ')}"
        )

        stepNodes << new NodeBuilder().'com.tikal.jenkins.plugins.multijob.MultiJobBuilder' {
            phaseName phaseContext.phaseName
            delegate.continuationCondition(phaseContext.continuationCondition)
            delegate.executionType(phaseContext.executionType)
            phaseJobs {
                phaseContext.jobsInPhase.each { PhaseJobContext jobInPhase ->
                    Node phaseJobNode = 'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig' {
                        jobName jobInPhase.jobName
                        currParams jobInPhase.currentJobParameters
                        exposedSCM jobInPhase.exposedScm
                        disableJob jobInPhase.disableJob
                        killPhaseOnJobResultCondition jobInPhase.killPhaseCondition
                        abortAllJob jobInPhase.abortAllJobs
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
