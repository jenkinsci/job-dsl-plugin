package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.step.MultiJobStepContext

class MultiJob extends Job {
    MultiJob(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    void steps(@DslContext(MultiJobStepContext) Closure closure) {
        MultiJobStepContext context = new MultiJobStepContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        configure { Node project ->
            context.stepNodes.each {
                project / 'builders' << it
            }
        }
    }
}
