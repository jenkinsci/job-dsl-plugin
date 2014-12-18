package javaposse.jobdsl.dsl

/**
 * Helper class for building nested DSL structures.
 */
class ContextHelper {
    private ContextHelper() {
    }

    static void executeInContext(Closure closure, Context freshContext) {
        if (closure) {
            closure.delegate = freshContext
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        }
    }
}
