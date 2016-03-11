package javaposse.jobdsl.dsl

@Deprecated
class WithXmlAction {
    private final Closure closure

    WithXmlAction(Closure closure) {
        Preconditions.checkNotNull(closure, 'Closure has to be set during constructor')
        this.closure = closure
    }

    void execute(Node root) {
        closure.delegate = new MissingPropertyToStringDelegate(root)

        use(NodeEnhancement) {
            closure.call(root)
        }
    }

    static WithXmlAction create(Closure closure) {
        new WithXmlAction(closure)
    }
}
