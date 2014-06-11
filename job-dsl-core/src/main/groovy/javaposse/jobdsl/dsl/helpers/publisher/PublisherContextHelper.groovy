package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper

class PublisherContextHelper extends AbstractContextHelper<PublisherContext> {
    private final JobManagement jobManagement

    PublisherContextHelper(List<WithXmlAction> withXmlActions, JobType jobType, JobManagement jobManagement) {
        super(withXmlActions, jobType)
        this.jobManagement = jobManagement
    }

    def publishers(Closure closure) {
        execute(closure, new PublisherContext(jobManagement))
    }

    Closure generateWithXmlClosure(PublisherContext context) {
        return { Node project ->
            def publishersNode
            if (project.publishers.isEmpty()) {
                publishersNode = project.appendNode('publishers')
            } else {
                publishersNode = project.publishers[0]
            }
            context.publisherNodes.each {
                publishersNode << it
            }
        }
    }
}
