package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction

class BuildWrapperContextHelper extends AbstractContextHelper<BuildWrapperContext> {

    BuildWrapperContextHelper(List<WithXmlAction> withXmlActions, JobType jobType) {
        super(withXmlActions, jobType)
    }

    def buildWrappers(Closure closure) {
        execute(closure, new BuildWrapperContext(type))
    }

    Closure generateWithXmlClosure(BuildWrapperContext context) {
        return { Node project ->
            def buildWrappersNode
            if (project.buildWrappers.isEmpty()) {
                buildWrappersNode = project.appendNode('buildWrappers')
            } else {
                buildWrappersNode = project.buildWrappers[0]
            }
            context.buildWrapperNodes.each {
                buildWrappersNode << it
            }
        }
    }
}
