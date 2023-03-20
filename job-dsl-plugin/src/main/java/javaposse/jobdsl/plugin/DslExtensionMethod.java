package javaposse.jobdsl.plugin;

import javaposse.jobdsl.dsl.ExtensibleContext;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Any methods of {@link ContextExtensionPoint} marked with this annotation will be exposed to the DSL.
 *
 * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/Extending-the-DSL">Extending the DSL</a>
 * @since 1.33
 */
@Retention(RUNTIME)
@Target(METHOD)
@Documented
public @interface DslExtensionMethod {
    Class<? extends ExtensibleContext> context();
}
