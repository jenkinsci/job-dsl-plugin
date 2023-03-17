package javaposse.jobdsl.dsl;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// this has to be written in Java because of https://jira.codehaus.org/browse/GROOVY-6019

/**
 * Container annotation for declaring multiple {@link RequiresPlugin} annotations.
 *
 * @since 1.51
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPlugins {
    /**
     * A list of {@link RequiresPlugin} annotations to indicate that more than one plugin must be installed to use the
     * features provided by the annotated DSL method.
     *
     * @return a list of {@link RequiresPlugin} annotations
     */
    RequiresPlugin[] value();
}
