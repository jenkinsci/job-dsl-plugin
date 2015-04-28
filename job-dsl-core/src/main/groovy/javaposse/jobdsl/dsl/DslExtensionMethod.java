package javaposse.jobdsl.dsl;

import javaposse.jobdsl.dsl.helpers.ExtensibleContext;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
@Documented
public @interface DslExtensionMethod {
    Class<? extends ExtensibleContext> context();
}
