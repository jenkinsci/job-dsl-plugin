package javaposse.jobdsl.dsl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// this has to be written in Java because of https://jira.codehaus.org/browse/GROOVY-6019

/**
 * Indicates that a method should not be included in the API documentation.
 *
 * @since 1.38
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoDoc {
}
