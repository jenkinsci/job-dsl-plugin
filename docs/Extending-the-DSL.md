Note: This page is intended for Jenkins plugin developers which want to provide DSL methods for their plugins. If you
are a DSL user, have a look at the [[configure block|The Configure Block]], the [[user power moves|User Power Moves]]
and at the [[examples|Real World Examples]] for hints about extending the DSL as a user.

Any Jenkins plugin can provide DSL methods for features they contribute to the job configuration by implementing the
Job DSL extension point. The extension point is available with Job DSL plugin version 1.33 or later.

To be able to use the Job DSL extension point, the Job DSL plugin has to be added to the plugin's dependencies. The
dependency should be marked as optional, so that the plugin can still be used without the Job DSL plugin.

Maven:

    <project xmlns="http://maven.apache.org/POM/4.0.0" ...>
        ...
        <dependencies>
            ...
            <dependency>
                <groupId>org.jenkins-ci.plugins</groupId>
                <artifactId>job-dsl</artifactId>
                <version>1.33</version>
                <optional>true</optional>
            </dependency>
            ...
        </dependencies>
        ...
    </project>

Gradle:

    ...
    dependencies {
        ...
        optionalJenkinsPlugins 'org.jenkins-ci.plugins:job-dsl:1.33@jar'
        ...
    }
    ...

A class extending `javaposse.jobdsl.plugin.ContextExtensionPoint` should contain all methods that the plugin wants to
contribute to the DSL. The class should be marked as optional by using the `@Extension(optional = true)` annotation to
gracefully disable the extension if the user is not using the Job DSL plugin.

Each method that should be available in the DSL must be marked with the `@DslExtensionMethod` annotation. The mandatory
context attribute of the annotation specifies which DSL context should provide the method. Only DSL context classes
implementing `javaposse.jobdsl.dsl.helpers.ExtensibleContext` can be extended, which includes the following classes and
contexts:

* `javaposse.jobdsl.dsl.helpers.step.StepsContext` for the `steps` context
* `javaposse.jobdsl.dsl.helpers.publisher.PublisherContext` for the `publisher` context
* `javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext` for the `wrappers` context
* `javaposse.jobdsl.dsl.helpers.publisher.MavenPublisherContext` for the `publishers` context, but only for Maven jobs
* `javaposse.jobdsl.dsl.helpers.wrapper.MavenWrapperContext` for the `wrappers` context, but only for Maven jobs 

The parameters of the `@DslExtensionMethod` annotated method are the same parameters that will be available in the DSL.
Have a look at the [DSL Design](https://github.com/jenkinsci/job-dsl-plugin/blob/master/CONTRIBUTING.md#dsl-design)
section of the Job DSL contributing guide on how to use methods parameters.

The method must return an object that will be saved as part of the job configuration. Usually that will be
the `hudson.tasks.Builder`, `hudson.tasks.Publisher` or `hudson.tasks.BuildWrapper` subclass which is provided by the
plugin.

In the following example, a plugin provides an `ExampleBuilder` build step with two options, one with a string value and
another one with an integer value. The Job DSL should be extended with an `example` method in the `steps` context so
that DSL users can easily add the build step to their jobs.

    job('example') {
        steps {
            example('foo', 42)
        }
    }

To implement this DSL extension, a `ContextExtensionPoint` subclass must contains an `example` method with a matching
signature. The method must return a pre-configured `ExampleBuilder`.

    package org.jenkinsci.plugins.example;
    
    import hudson.Extension;
    import javaposse.jobdsl.dsl.DslExtensionMethod;
    import javaposse.jobdsl.dsl.helpers.step.StepContext;
    import javaposse.jobdsl.plugin.ContextExtensionPoint;
    
    @Extension(optional = true)
    public class ExampleJobDslExtension extends ContextExtensionPoint {
        @DslExtensionMethod(context = StepContext.class)
        public Object example(String optionA, int optionB) {
            return new ExampleBuilder(optionA, optionB);
        }
    }

It is also possible to add nested DSL contexts from an extension point. The DSL uses Groovy closures to implement the
contexts, but is possible to implement contexts without using Groovy. The closure object of the nested context is passed
as a `Runnable` to the last parameter of the `@DslExtensionMethod` annotated method.

The closure must be executed in a DSL context, which is a class implementing the `javaposse.jobdsl.dsl.Context` marker
interface and which will provide the DSL methods for the nested context. The `executeInContext` method inherited from
`ContextExtensionPoint` should be used the run the closure in the nested context.

The example above can be changed to use a nested DSL context for the `ExampleBuilder` options.

    job('example') {
        steps {
            example {
                optionA('foo')
                optionB(42)
            }
        }
    }
    
A class implementing `Context` will provide the methods for the nested DSL context. 

    package org.jenkinsci.plugins.example;
    
    import javaposse.jobdsl.dsl.Context;
    
    public class ExampleDslContext implements Context {
        String optionA
        int optionB
    
        public void optionA(String value) {
            optionA = value;
        }

        public void optionB(int value) {
            optionB = value;
        }
    }
    
The `@DslExtensionMethod` annotated method in the `ContextExtensionPoint` subclass has a single `Runnable` parameter,
which will be executed using the custom `Context` class. It returns a new `ExampleBuilder` configured with values from
the nested context.

    package org.jenkinsci.plugins.example;
    
    import hudson.Extension;
    import javaposse.jobdsl.dsl.DslExtensionMethod;
    import javaposse.jobdsl.dsl.helpers.step.StepContext;
    import javaposse.jobdsl.plugin.ContextExtensionPoint;
    
    @Extension(optional = true)
    public class ExampleJobDslExtension extends ContextExtensionPoint {
        @DslExtensionMethod(context = StepContext.class)
        public Object example(Runnable closure) {
            ExampleDslContext context = new ExampleDslContext();
            executeInContext(closure, context);
    
            return new ExampleBuilder(context.optionA, context.optionB);
        }
    }
