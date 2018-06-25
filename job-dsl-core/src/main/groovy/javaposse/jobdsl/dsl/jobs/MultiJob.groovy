package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Project
import javaposse.jobdsl.dsl.helpers.step.MultiJobStepContext

class MultiJob extends Project {
    MultiJob(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    /**
     * Triggers a build if an SCM change is detected in a subjob. Defaults to {@code false}.
     *
     * @since 1.64
     */
    void pollSubjobs(boolean pollSubjobs = true) {
        configure { Node project ->
            Node node = methodMissing('pollSubjobs', pollSubjobs)
            project / node
        }
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
