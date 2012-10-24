package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.WithXmlAction

/**
 * Each helper has essentially two parts. First they run a closure in executeWithContext right away to build a context
 * object. Once we have the actually root, we run again via the generateWithXmlClosure.
 * @param < T >
 */
abstract class AbstractHelper<T extends Context> implements Helper {
    /**
     * Global list of all withXmlActions. Helper should append to it as they get called. They could prepend to the list,
     * that is not advised. They can scan the list so fat to look special implementations of WithXmlAction, which might
     * effect their behavior.
     *
     */
    List<WithXmlAction> withXmlActions

    AbstractHelper(List<WithXmlAction> withXmlActions) {
        this.withXmlActions = withXmlActions
    }

    static def executeInContext(Closure closure, Context freshContext) {
        if(closure) {
            closure.delegate = freshContext
            //closure.resolveStrategy = Closure.DELEGATE_FIRST
            def result = closure.call() // No args

            // TODO Create callback to concrete classes, so that they can "enhance" the closure, e.g. with static imports
        }
    }
    /**
     * Make assumption that we're creating top level xml elements
     * @param closure
     * @param freshContext
     * @return
     */
    def execute(Closure closure, T freshContext) {
        // Execute context, which we expect will just establish some state
        executeInContext(closure, freshContext)

        // Queue up our action, using the concrete classes logic
        withXmlActions << generateWithXmlAction(freshContext)

        return freshContext
    }

    WithXmlAction generateWithXmlAction(T context) {
        // Closure to be run later, in this context we're given the root node with the WithXmlAction magic
        Closure withXmlClosure = generateWithXmlClosure(context)
        //withXmlClosure.resolveStrategy = Closure.DELEGATE_FIRST

        return new WithXmlAction(withXmlClosure)
    }

    abstract Closure generateWithXmlClosure(T context);

}

