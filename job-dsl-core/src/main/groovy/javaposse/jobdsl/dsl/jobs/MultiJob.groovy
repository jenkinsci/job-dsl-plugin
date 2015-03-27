package javaposse.jobdsl.dsl.jobs

import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.step.MultiJobStepContext

class MultiJob extends Job {
    MultiJob(JobManagement jobManagement) {
        super(jobManagement)

        if (jobManagement.getPluginVersion('jenkins-multijob-plugin')?.isOlderThan(new VersionNumber('1.16'))) {
            jobManagement.logDeprecationWarning('support for MultiJob plugin versions 1.15 and earlier')
        }
    }

    void steps(@DslContext(MultiJobStepContext) Closure closure) {
        MultiJobStepContext context = new MultiJobStepContext(jobManagement)
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            context.stepNodes.each {
                project / 'builders' << it
            }
        }
    }
}
