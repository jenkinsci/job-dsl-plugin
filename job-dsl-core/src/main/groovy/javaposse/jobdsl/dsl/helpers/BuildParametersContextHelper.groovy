package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction

class BuildParametersContextHelper extends AbstractContextHelper<BuildParametersContext> {

    BuildParametersContextHelper(List<WithXmlAction> withXmlActions, JobType jobType) {
        super(withXmlActions, jobType)
    }

    def parameters(Closure closure) {
        execute(closure, new BuildParametersContext())
    }

    Closure generateWithXmlClosure(BuildParametersContext context) {
        return { Node project ->
            def node = project / 'properties' / 'hudson.model.ParametersDefinitionProperty' / parameterDefinitions
            context.buildParameterNodes.values().each {
                node << it
            }
        }
    }
}
