package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions

class PhaseContext extends AbstractContext {
    private static final List<String> VALID_EXECUTION_TYPES = ['PARALLEL', 'SEQUENTIALLY']

    protected final Item item

    String phaseName
    String continuationCondition
    String executionType = 'PARALLEL'

    List<PhaseJobContext> jobsInPhase = []

    PhaseContext(JobManagement jobManagement, Item item, String phaseName, String continuationCondition) {
        super(jobManagement)
        this.item = item
        this.phaseName = phaseName
        this.continuationCondition = continuationCondition
        this.executionType = executionType
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
     * Defines how to run the whole MultiJob phase. Must be either {@code 'PARALLEL'} or {@code 'SEQUENTIALLY'}.
     * Defaults to {@code 'PARALLEL'}.
     *
     * @since 1.52
     */
    void executionType(String executionType) {
        Preconditions.checkArgument(
                VALID_EXECUTION_TYPES.contains(executionType),
                "Execution Type needs to be one of these values: ${VALID_EXECUTION_TYPES.join(', ')}"
        )
        this.executionType = executionType
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
