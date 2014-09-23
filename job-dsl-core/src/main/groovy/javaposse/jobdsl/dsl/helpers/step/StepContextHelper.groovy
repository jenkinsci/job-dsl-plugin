package javaposse.jobdsl.dsl.helpers.step

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper

class StepContextHelper extends AbstractContextHelper<AbstractStepContext> {
    private final JobManagement jobManagement

    StepContextHelper(List<WithXmlAction> withXmlActions, JobType jobType, JobManagement jobManagement) {
        super(withXmlActions, jobType)
        this.jobManagement = jobManagement
    }

    def steps(Closure closure) {
        Preconditions.checkState(type != JobType.Maven, 'steps cannot be applied for Maven jobs')
        execute(closure, new AbstractStepContext(jobManagement))
    }

    Closure generateWithXmlClosure(AbstractStepContext context) {
        return { Node project ->
            def buildersNode
            if (project.builders.isEmpty()) {
                buildersNode = project.appendNode('builders')
            } else {
                buildersNode = project.builders[0]
            }
            context.stepNodes.each {
                buildersNode << it
            }
        }
    }
}
