package javaposse.jobdsl.dsl.helpers.step

import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.WithXmlAction

class MultiJobStepContext extends StepContext {
    private static final List<String> VALID_CONTINUATION_CONDITIONS = ['SUCCESSFUL', 'UNSTABLE', 'COMPLETED', 'FAILURE']

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

        VersionNumber multiJobPluginVersion = jobManagement.getPluginVersion('jenkins-multijob-plugin')

        Set<String> validContinuationConditions = new HashSet<String>(VALID_CONTINUATION_CONDITIONS)
        if (multiJobPluginVersion?.isNewerThan(new VersionNumber('1.15'))) {
            validContinuationConditions << 'ALWAYS'
        }

        Preconditions.checkNotNullOrEmpty(phaseContext.phaseName, 'A phase needs a name')
        Preconditions.checkArgument(
                validContinuationConditions.contains(phaseContext.continuationCondition),
                "Continuation Condition needs to be one of these values: ${validContinuationConditions.join(', ')}"
        )

        stepNodes << new NodeBuilder().'com.tikal.jenkins.plugins.multijob.MultiJobBuilder' {
            phaseName phaseContext.phaseName
            delegate.continuationCondition(phaseContext.continuationCondition)
            phaseJobs {
                phaseContext.jobsInPhase.each { PhaseJobContext jobInPhase ->
                    Node phaseJobNode = 'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig' {
                        jobName jobInPhase.jobName
                        currParams jobInPhase.currentJobParameters
                        exposedSCM jobInPhase.exposedScm
                        disableJob jobInPhase.disableJob
                        killPhaseOnJobResultCondition jobInPhase.killPhaseCondition
                        if (multiJobPluginVersion?.isNewerThan(new VersionNumber('1.13'))) {
                            abortAllJob jobInPhase.abortAllJobs
                        }
                        configs(jobInPhase.paramTrigger.configs ?: [class: 'java.util.Collections$EmptyList'])
                    }

                    if (jobInPhase.configureClosure) {
                        WithXmlAction action = new WithXmlAction(jobInPhase.configureClosure)
                        action.execute(phaseJobNode)
                    }
                }
            }
        }
    }

    @Override
    protected StepContext newInstance() {
        new MultiJobStepContext(jobManagement, item)
    }
}
