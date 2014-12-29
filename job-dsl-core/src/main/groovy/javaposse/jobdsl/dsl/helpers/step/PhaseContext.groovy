package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

class PhaseContext implements Context {
    private final JobManagement jobManagement

    String phaseName
    String continuationCondition

    List<PhaseJobContext> jobsInPhase = []

    PhaseContext(JobManagement jobManagement, String phaseName, String continuationCondition) {
        this.jobManagement = jobManagement
        this.phaseName = phaseName
        this.continuationCondition = continuationCondition
    }

    void phaseName(String phaseName) {
        this.phaseName = phaseName
    }

    void continuationCondition(String continuationCondition) {
        this.continuationCondition = continuationCondition
    }

    void job(String jobName, @DslContext(PhaseJobContext) Closure phaseJobClosure = null) {
        job(jobName, true, true, phaseJobClosure)
    }

    void job(String jobName, boolean currentJobParameters,
             @DslContext(PhaseJobContext) Closure phaseJobClosure = null) {
        job(jobName, currentJobParameters, true, phaseJobClosure)
    }

    void job(String jobName, boolean currentJobParameters, boolean exposedScm,
             @DslContext(PhaseJobContext) Closure phaseJobClosure = null) {
        PhaseJobContext phaseJobContext = new PhaseJobContext(jobManagement, jobName, currentJobParameters, exposedScm)
        ContextHelper.executeInContext(phaseJobClosure, phaseJobContext)

        jobsInPhase << phaseJobContext
    }
}
