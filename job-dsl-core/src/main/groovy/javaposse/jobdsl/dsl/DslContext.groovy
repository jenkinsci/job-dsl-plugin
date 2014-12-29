package javaposse.jobdsl.dsl

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Marks a {@link Closure} parameter as nested DSL context.
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.PARAMETER])
@interface DslContext {
    Class<? extends Context> value()
}
