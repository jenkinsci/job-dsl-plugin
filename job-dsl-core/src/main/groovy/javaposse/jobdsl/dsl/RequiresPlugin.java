package javaposse.jobdsl.dsl;

import java.lang.annotation.*;

// this has to be written in Java because of https://jira.codehaus.org/browse/GROOVY-6019

/**
 * Indicates that a plugin must be installed to use the features provided by the annotated DSL method. A minimum
 * version can be specified as a lower bound for the version of the required plugin.
 *
 * @since 1.31
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPlugin {
    /**
     * The Plugin ID or short name of the required plugin.
     */
    String id();

    /**
     * The least acceptable version of the required plugin. Optional, any version will be accepted if none is given.
     */
    String minimumVersion() default "";
}
