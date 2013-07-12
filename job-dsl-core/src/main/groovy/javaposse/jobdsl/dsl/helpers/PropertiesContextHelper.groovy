package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction

class PropertiesContextHelper extends AbstractContextHelper<PropertiesContext> {
    JobManagement jobManagement

    PropertiesContextHelper(List<WithXmlAction> withXmlActions, JobType jobType, JobManagement jobManagement) {
        super(withXmlActions, jobType)
        this.jobManagement = jobManagement
    }

    def properties(Closure closure) {
        execute(closure, new PropertiesContext(jobManagement))
    }

    Closure generateWithXmlClosure(PropertiesContext context) {
        return { Node project ->
            def propertiesNode
            if (project.'properties'.isEmpty()) {
                propertiesNode = project.appendNode('properties')
            } else {
                propertiesNode = project.'properties'[0]
            }
            context.propertiesNodes.each {
                propertiesNode << it
            }
        }
    }
}
