package javaposse.jobdsl.dsl

import com.google.common.base.Preconditions

import java.util.logging.Logger

class WithXmlAction {
    private static final Logger LOGGER = Logger.getLogger(WithXmlAction.class.name)

    private final Closure closure

    WithXmlAction(Closure closure) {
        this.closure = Preconditions.checkNotNull(closure, "Closure has to be set during constructor")
    }

    def execute(Node root) {
        Preconditions.checkNotNull(root)

        closure.delegate = new MissingPropertyToStringDelegate(root)

        use(NodeEnhancement) {
            closure.call(root)
        }
    }
}


