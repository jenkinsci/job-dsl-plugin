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

    static void executeConfigureBlock(Node node, Closure configureBlock) {
        if (configureBlock) {
            configureBlock.delegate = new MissingPropertyToStringDelegate(node)

            use(NodeEnhancement) {
                configureBlock.call(node)
            }
        }
    }

    static void executeConfigureBlocks(Node node, List<Closure> configureBlocks) {
        configureBlocks.each { executeConfigureBlock(node, it) }
    }
}
