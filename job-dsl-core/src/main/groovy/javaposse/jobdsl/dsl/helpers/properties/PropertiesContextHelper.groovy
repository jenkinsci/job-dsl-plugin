package javaposse.jobdsl.dsl.helpers.properties

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper

class PropertiesContextHelper extends AbstractContextHelper<PropertiesContext> {
    PropertiesContextHelper(List<WithXmlAction> withXmlActions, JobType jobType) {
        super(withXmlActions, jobType)
    }

    def properties(Closure closure) {
        execute(closure, new PropertiesContext())
    }

    Closure generateWithXmlClosure(PropertiesContext context) {
        return { Node project ->
            Node propertiesNode
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
