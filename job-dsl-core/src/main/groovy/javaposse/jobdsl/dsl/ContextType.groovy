package javaposse.jobdsl.dsl

import java.lang.annotation.ElementType
import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * @since 1.46
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE])
@Inherited
@interface ContextType {
    String value()
}
