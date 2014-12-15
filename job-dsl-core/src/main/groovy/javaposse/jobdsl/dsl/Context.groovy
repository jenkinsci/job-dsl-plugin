package javaposse.jobdsl.dsl

/**
 * Marker interface to indicate that implementations are used as DSL contexts.
 *
 * Use {@link ContextHelper#executeInContext(groovy.lang.Closure, javaposse.jobdsl.dsl.Context)} to call a
 * {@link Closure} in a DSL context.
 */
interface Context {
}
