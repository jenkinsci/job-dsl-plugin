package javaposse.jobdsl.dsl.helpers.step

import com.google.common.base.Preconditions
import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction

class MultiJobStepContext extends StepContext {
    private static final List<String> VALID_CONTINUATION_CONDITIONS = ['SUCCESSFUL', 'UNSTABLE', 'COMPLETED']

    MultiJobStepContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * phaseName will have to be provided in the closure
     *
     * <com.tikal.jenkins.plugins.multijob.MultiJobBuilder>
     *   <phaseName>name-of-phase</phaseName>
     *   <phaseJobs>
     *     <com.tikal.jenkins.plugins.multijob.PhaseJobsConfig>
     *       <jobName>job-in-phase</jobName>
     *       <currParams>true</currParams>
     *       <exposedSCM>false</exposedSCM>
     *       <disableJob>false</disableJob>
     *       <configs class="empty-list"/>
     *       <killPhaseOnJobResultCondition>FAILURE</killPhaseOnJobResultCondition>
     *     </com.tikal.jenkins.plugins.multijob.PhaseJobsConfig>
     *   </phaseJobs>
     *   <continuationCondition>COMPLETED</continuationCondition>
     * </com.tikal.jenkins.plugins.multijob.MultiJobBuilder>
     */
    void phase(@DslContext(PhaseContext) Closure phaseContext) {
        phase(null, 'SUCCESSFUL', phaseContext)
    }

    void phase(String phaseName, @DslContext(PhaseContext) Closure phaseContext = null) {
        phase(phaseName, 'SUCCESSFUL', phaseContext)
    }

    void phase(String name, String continuationConditionArg, @DslContext(PhaseContext) Closure phaseClosure) {
        PhaseContext phaseContext = new PhaseContext(jobManagement, name, continuationConditionArg)
        ContextHelper.executeInContext(phaseClosure, phaseContext)

        VersionNumber multiJobPluginVersion = jobManagement.getPluginVersion('jenkins-multijob-plugin')

        Set<String> validContinuationConditions = new HashSet<String>(VALID_CONTINUATION_CONDITIONS)
        if (multiJobPluginVersion?.isNewerThan(new VersionNumber('1.10'))) {
            validContinuationConditions << 'FAILURE'
        }
        if (multiJobPluginVersion?.isNewerThan(new VersionNumber('1.15'))) {
            validContinuationConditions << 'ALWAYS'
        }

        Preconditions.checkArgument(phaseContext.phaseName as Boolean, 'A phase needs a name')
        Preconditions.checkArgument(
                validContinuationConditions.contains(phaseContext.continuationCondition),
                "Continuation Condition needs to be one of these values: ${validContinuationConditions.join(', ')}"
        )

        stepNodes << new NodeBuilder().'com.tikal.jenkins.plugins.multijob.MultiJobBuilder' {
            phaseName phaseContext.phaseName
            continuationCondition phaseContext.continuationCondition
            phaseJobs {
                phaseContext.jobsInPhase.each { PhaseJobContext jobInPhase ->
                    Node phaseJobNode = 'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig' {
                        jobName jobInPhase.jobName
                        currParams jobInPhase.currentJobParameters
                        exposedSCM jobInPhase.exposedScm
                        if (multiJobPluginVersion?.isNewerThan(new VersionNumber('1.10'))) {
                            disableJob jobInPhase.disableJob
                            killPhaseOnJobResultCondition jobInPhase.killPhaseCondition
                        }
                        if (jobInPhase.hasConfig()) {
                            configs(jobInPhase.configAsNode().children())
                        } else {
                            configs('class': 'java.util.Collections$EmptyList')
                        }
                    }

                    if (jobInPhase.configureClosure) {
                        WithXmlAction action = new WithXmlAction(jobInPhase.configureClosure)
                        action.execute(phaseJobNode)
                    }
                }
            }
        }
    }
}
