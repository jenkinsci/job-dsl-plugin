package javaposse.jobdsl.dsl;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

// this has to be written in Java because of https://jira.codehaus.org/browse/GROOVY-6019

/**
 * Indicates that a DSL method needs a certain minimum version of Jenkins core.
 *
 * @since 1.54
 */
@Target(ElementType.METHOD)
@Documented
public @interface RequiresCore {
    /**
     * The least acceptable version of Jenkins core.
     *
     * @return the least acceptable version of Jenkins core
     */
    String minimumVersion();
}
