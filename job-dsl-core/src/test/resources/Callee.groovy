import javaposse.jobdsl.dsl.JobParent

/**
 * External class referenced by a Caller
 */
class Callee {
    static def makeJob(JobParent jobParent, String nameOfJob) {
        jobParent.freeStyleJob(nameOfJob) {
        }
    }
}
