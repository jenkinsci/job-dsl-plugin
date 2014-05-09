package javaposse.jobdsl.dsl.helpers.axis

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper

class AxisContextHelper extends AbstractContextHelper<AxisContext> {
    AxisContextHelper(List<WithXmlAction> withXmlActions, JobType jobType) {
        super(withXmlActions, jobType)
    }

    def axes(Closure closure) {
        execute(closure, new AxisContext())
    }

    @Override
    Closure generateWithXmlClosure(AxisContext context) {
        return { Node project ->
            def axesNode
            if (project.axes.isEmpty()) {
                axesNode = project.appendNode('axes')
            } else {
                axesNode = project.axes[0]
            }
            context.axesNode.each {
                axesNode << it
            }
        }
    }
}
