package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class NestedViewsContextSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final NestedViewsContext context = new NestedViewsContext(jobManagement)

    def 'extension'() {
        given:
        Node viewNode = new NodeBuilder().'my.custom.CustomView' {
            name('myView')
        }

        when:
        context.addExtensionNode(viewNode)

        then:
        context.views != null
        context.views.size() == 1
        context.views[0].name == 'myView'
        context.views[0].node == viewNode
    }
}
