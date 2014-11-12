package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement

abstract class AbstractExtensibleContext implements ExtensibleContext {
    JobManagement jobManagement
    Job job

    protected AbstractExtensibleContext(JobManagement jobManagement, Job job) {
        this.jobManagement = jobManagement
        this.job = job
    }

    def methodMissing(String name, args) {
        Class<? extends ExtensibleContext> contextType = this.class as Class<? extends ExtensibleContext>
        Node node = jobManagement.callExtension(job.getId().toString(), name, contextType, args)
        if (node == null) {
            throw new MissingMethodException(name, contextType, args)
        }
        addExtensionNode(node)
    }

    protected abstract void addExtensionNode(Node node)
}
