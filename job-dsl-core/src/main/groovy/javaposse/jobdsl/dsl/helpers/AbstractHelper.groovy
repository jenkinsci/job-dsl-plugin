package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction

/**
 * Base for all helpers, providing an easy ability to create WithXmlActions
 */
class AbstractHelper implements Helper {

    /**
     * Global list of all withXmlActions. Helper should append to it as they get called. They could prepend to the list,
     * that is not advised. They can scan the list so fat to look special implementations of WithXmlAction, which might
     * effect their behavior.
     */
    List<WithXmlAction> withXmlActions

    JobType type

    AbstractHelper(List<WithXmlAction> withXmlActions, JobType type) {
        this.withXmlActions = withXmlActions
        this.type = type
    }

    WithXmlAction execute(Closure rootClosure) {
        rootClosure.resolveStrategy = Closure.DELEGATE_FIRST
        def action = new WithXmlAction(rootClosure)
        withXmlActions << action
        return action
    }

    static void appendOrReplaceNode(Node node, String name, Object value) {
        node.children().removeAll { it instanceof Node && it.name() == name }
        node.appendNode name, value
    }

}
