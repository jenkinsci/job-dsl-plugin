package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction

class WrapperContextHelper extends AbstractContextHelper<WrapperContext> {

    WrapperContextHelper(List<WithXmlAction> withXmlActions, JobType jobType) {
        super(withXmlActions, jobType)
    }

    def wrappers(Closure closure) {
        execute(closure, new WrapperContext(type))
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
