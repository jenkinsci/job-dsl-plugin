package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction

/**
 * Each helper has essentially two parts. First they run a closure in executeWithContext right away to build a context
 * object. Once we have the actually root, we run again via the generateWithXmlClosure.
 * @param < T >
 */
abstract class AbstractContextHelper<T extends Context> extends AbstractHelper {

    protected AbstractContextHelper(List<WithXmlAction> withXmlActions, JobType jobType) {
        super(withXmlActions, jobType)
    }

    static def executeInContext(Closure closure, Context freshContext) {
        if(closure) {
            closure.delegate = freshContext
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
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

        return new WithXmlAction(withXmlClosure)
    }

    abstract Closure generateWithXmlClosure(T context);

}

