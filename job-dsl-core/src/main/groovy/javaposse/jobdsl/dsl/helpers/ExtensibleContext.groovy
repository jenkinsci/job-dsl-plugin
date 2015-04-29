package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Context

/**
 * Marker interface to indicate that this is a {@link Context} which can be extended at runtime.
 */
interface ExtensibleContext extends Context {
}
