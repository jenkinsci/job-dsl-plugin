package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper

class WrapperContextHelper extends AbstractContextHelper<WrapperContext> {

    JobManagement jobManagement

    WrapperContextHelper(List<WithXmlAction> withXmlActions, JobType jobType, JobManagement jobManagement) {
        super(withXmlActions, jobType)
        this.jobManagement = jobManagement
    }

    def wrappers(Closure closure) {
        execute(closure, new WrapperContext(type, jobManagement))
    }

    Closure generateWithXmlClosure(WrapperContext context) {
        return { Node project ->
            def wrapperNode
            if (project.buildWrappers.isEmpty()) {
                wrapperNode = project.appendNode('buildWrappers')
            } else {
                wrapperNode = project.buildWrappers[0]
            }
            context.wrapperNodes.each {
                wrapperNode << it
            }
        }
    }
}
