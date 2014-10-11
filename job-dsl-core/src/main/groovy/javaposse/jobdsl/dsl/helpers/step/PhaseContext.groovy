package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context

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

    def job(String jobName, Closure phaseJobClosure = null) {
        job(jobName, true, true, phaseJobClosure)
    }

    def job(String jobName, boolean currentJobParameters, Closure phaseJobClosure = null) {
        job(jobName, currentJobParameters, true, phaseJobClosure)
    }

    def job(String jobName, boolean currentJobParameters, boolean exposedScm, Closure phaseJobClosure = null) {
        PhaseJobContext phaseJobContext = new PhaseJobContext(jobManagement, jobName, currentJobParameters, exposedScm)
        AbstractContextHelper.executeInContext(phaseJobClosure, phaseJobContext)

        jobsInPhase << phaseJobContext

        phaseJobContext
    }
}
