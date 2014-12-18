package javaposse.jobdsl.dsl

import com.google.common.base.Preconditions

class WithXmlAction {
    private final Closure closure

    WithXmlAction(Closure closure) {
        this.closure = Preconditions.checkNotNull(closure, 'Closure has to be set during constructor')
    }

    void execute(Node root) {
        Preconditions.checkNotNull(root)

        closure.delegate = new MissingPropertyToStringDelegate(root)

        use(NodeEnhancement) {
            closure.call(root)
        }
    }

    static WithXmlAction create(Closure closure) {
        new WithXmlAction(closure)
    }
}
