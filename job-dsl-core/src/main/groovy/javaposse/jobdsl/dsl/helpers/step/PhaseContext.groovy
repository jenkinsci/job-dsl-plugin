package javaposse.jobdsl.dsl.helpers.step

import groovy.transform.Canonical
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context

@Canonical
class PhaseContext implements Context {
    String phaseName
    String continuationCondition

    List<PhaseJobContext> jobsInPhase = []

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
        PhaseJobContext phaseJobContext = new PhaseJobContext(jobName, currentJobParameters, exposedScm)
        AbstractContextHelper.executeInContext(phaseJobClosure, phaseJobContext)

        jobsInPhase << phaseJobContext

        phaseJobContext
    }
}
