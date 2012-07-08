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

    def execute(Closure closure, T freshContext) {
        // Reset context
        closure.delegate = freshContext
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call() // No args

        // TODO Create callback to concrete classes, so that they can "enhance" the closure, e.g. with static imports

        // Queue up our action
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

