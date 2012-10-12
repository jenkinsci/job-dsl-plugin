package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.WithXmlAction

abstract class AbstractHelper<T extends Context> implements Helper {
    /**
     * Global list of all withXmlActions. Helper should append to it as they get called. They could prepend to the list,
     * that is not advised. They can scan the list so fat to look special implementations of WithXmlAction, which might
     * effect their behavior.
     *
     */
    List<WithXmlAction> withXmlActions

    static def executeInContext(Closure closure, Context freshContext) {
        if(closure) {
            closure.delegate = freshContext
            closure.resolveStrategy = Closure.DELEGATE_FIRST
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
    }

    WithXmlAction generateWithXmlAction(T context) {
        // Closure to be run later, in this context we're given the root node with the WithXmlAction magic
        Closure withXmlClosure = generateWithXmlClosure(context)
        withXmlClosure.resolveStrategy = Closure.DELEGATE_ONLY // So call don't come back to us

        return new WithXmlAction(withXmlClosure)
    }

    abstract Closure generateWithXmlClosure(T context);

    /*
    <buildWrappers>
      <hudson.plugins.build__timeout.BuildTimeoutWrapper>
        <timeoutMinutes>15</timeoutMinutes>
        <failBuild>true</failBuild>
      </hudson.plugins.build__timeout.BuildTimeoutWrapper>
    </buildWrappers>
    */

    /*
    <hudson.plugins.chucknorris.CordellWalkerRecorder>
      <factGenerator/>
    </hudson.plugins.chucknorris.CordellWalkerRecorder>
     */
}

