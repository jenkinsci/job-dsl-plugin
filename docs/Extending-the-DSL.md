Note: This page is intended for Jenkins plugin developers which want to provide DSL methods for their plugins. If you
are a DSL user, have a look at the [[configure block|The Configure Block]], the [[user power moves|User Power Moves]]
and at the [[examples|Real World Examples]] for hints about extending the DSL as a user.

Any Jenkins plugin can provide DSL methods for features they contribute to the job configuration by implementing the
Job DSL extension point. The extension point is available with Job DSL plugin version 1.35 or later.

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
                <version>1.46</version>
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
        optionalJenkinsPlugins 'org.jenkins-ci.plugins:job-dsl:1.46@jar'
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

* `javaposse.jobdsl.dsl.helpers.ScmContext` for the `scm` and `multiscm` contexts
* `javaposse.jobdsl.dsl.helpers.step.StepsContext` for the `steps` context
* `javaposse.jobdsl.dsl.helpers.triggers.TriggerContext` for the `triggers` context
* `javaposse.jobdsl.dsl.helpers.triggers.MultibranchWorkflowTriggerContext` for the `triggers` context, but only for
  multibranch workflow jobs
* `javaposse.jobdsl.dsl.helpers.properties.PropertiesContext` for the `properties` context
* `javaposse.jobdsl.dsl.helpers.publisher.PublisherContext` for the `publisher` context
* `javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext` for the `wrappers` context
* `javaposse.jobdsl.dsl.helpers.BuildParametersContext` for the `parameters` context
* `javaposse.jobdsl.dsl.helpers.publisher.MavenPublisherContext` for the `publishers` context, but only for Maven jobs
* `javaposse.jobdsl.dsl.helpers.triggers.MavenTriggerContext` for the `triggers` context, but only for Maven jobs
* `javaposse.jobdsl.dsl.helpers.wrapper.MavenWrapperContext` for the `wrappers` context, but only for Maven jobs 
* `javaposse.jobdsl.dsl.helpers.AxisContext` for the `axes` context of matrix jobs
* `javaposse.jobdsl.dsl.helpers.IvyBuilderContext` for the `ivyBuilder` context of Ivy jobs
* `javaposse.jobdsl.dsl.helpers.common.DownstreamTriggerParameterContext` for the `parameters` context of parameterized
  triggers
* `javaposse.jobdsl.dsl.helpers.toplevel.EnvironmentVariableContributorsContext` for the `contributors` context within
  the `environmentVariables` context

The parameters of the `@DslExtensionMethod` annotated method are the same parameters that will be available in the DSL.
Have a look at the [DSL Design](https://github.com/jenkinsci/job-dsl-plugin/blob/master/CONTRIBUTING.md#dsl-design)
section of the Job DSL contributing guide on how to use methods parameters.

The method can return an object that will be saved as part of the job configuration. Usually that will be
the `hudson.tasks.Builder`, `hudson.tasks.Publisher` or `hudson.tasks.BuildWrapper` subclass which is provided by the
plugin. If the method should not contribute to the job configuration, it can simply return `null`.

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
    import javaposse.jobdsl.dsl.helpers.step.StepContext;
    import javaposse.jobdsl.plugin.ContextExtensionPoint;
    import javaposse.jobdsl.plugin.DslExtensionMethod;
    
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
    import javaposse.jobdsl.dsl.helpers.step.StepContext;
    import javaposse.jobdsl.plugin.ContextExtensionPoint;
    import javaposse.jobdsl.plugin.DslExtensionMethod;
    
    @Extension(optional = true)
    public class ExampleJobDslExtension extends ContextExtensionPoint {
        @DslExtensionMethod(context = StepContext.class)
        public Object example(Runnable closure) {
            ExampleDslContext context = new ExampleDslContext();
            executeInContext(closure, context);
    
            return new ExampleBuilder(context.optionA, context.optionB);
        }
    }

In cases where it's necessary to perform additional actions after a job has been created or updated, the
`ContextExtensionPoint` can act as a listener. Subclasses can override the `notifyItemCreated` and `notifyItemUpdated`
methods to be notified about job creation or update events. The created or updated job is passed into the methods as
`Item` along with a `DslEnvironment`. The `DslEnvironment` can be used to transfer state from a `@DslExtensionMethod`
to the listener methods, which is useful when the listener methods needs access to the parameters of the
`@DslExtensionMethod`. The `@DslExtensionMethod` needs to declare the `DslEnvironment` as one of it's parameters to get
access to the `DslEnvironment`. The `DslEnvironment` parameter will not be exposed to the DSL.

`DslEnvironment` implements a `java.util.Map<String, Object>` interface and can store arbitrary data. Each job has a
separate `DslEnvironment` and the `DslEnvironment` for a job will be shared between all `ContextExtensionPoint`
instances, so a `ContextExtensionPoint` instance is able to see values stored by other instances. Subclasses of
`ContextExtensionPoint` should choose unique keys to avoid collisions with other extensions.

The following example will show how to use the `DslEnvironment` to create an additional configuration file in the job's
directory. The `readFileFromWorkspace` DSL method is used to read the content of the configuration file from
the workspace of the seed job.

    job('example') {
        steps {
            example('foo', readFileFromWorkspace('example-1.json'))
            example('bar', readFileFromWorkspace('example-2.json'))
        }
    }
    
The `@DslExtensionMethod` uses the environment to store the content of the config file, so that is can be retrieved in
the listener methods and be written to a file. The implementation does not need to distinguish between creation and
update, so it delegates the creation event to `notifyItemUpdated`. A unique key for the `DslEnvironment` must be chosen
because build steps can be added multiple times.  
 
    package org.jenkinsci.plugins.example;
    
    import hudson.Extension;
    import hudson.model.Item;
    import javaposse.jobdsl.dsl.helpers.step.StepContext;
    import javaposse.jobdsl.plugin.ContextExtensionPoint;
    import javaposse.jobdsl.plugin.DslEnvironment;
    import javaposse.jobdsl.plugin.DslExtensionMethod;
    import org.apache.commons.io.FileUtils;

    import java.io.File;
    import java.io.IOException;
    import java.util.Map;
    
    @Extension(optional = true)
    public class ExampleJobDslExtension extends ContextExtensionPoint {
        private static final String PREFIX = "example.";

        @DslExtensionMethod(context = StepContext.class)
        public Object example(String optionA, String configJson,
                              DslEnvironment dslEnvironment) {
            dslEnvironment.put(PREFIX + optionA, configJson);
            return new ExampleBuilder(optionA);
        }

        @Override
        public void notifyItemCreated(Item item,
                                      DslEnvironment dslEnvironment) {
            notifyItemUpdated(item, dslEnvironment);
        }

        @Override
        public void notifyItemUpdated(Item item,
                                      DslEnvironment dslEnvironment) {
            for (Map.Entry<String, Object> entry : dslEnvironment.entrySet()) {
                String key = entry.getKey();  
                if (key.startsWith(PREFIX)) {
                    String fileName = key.substring(PREFIX.length()) + ".json";
                    File configFile = new File(item.getRootDir(), fileName);
                    try {
                        FileUtils.write(configFile, (String) entry.getValue());
                    } catch (IOException e) {
                        // handle exception
                    }
                }
            }
        }
    }

Instances of `ContextExtensionPoint` must be thread-safe. Each subclass will be instantiated only once and reused for
all seed jobs. Since multiple seed jobs can run in parallel, any `@DslExtensionMethod` and the listener methods can be
called in parallel.

To debug problems with Job DSL extensions, set the logger for `javaposse.jobdsl` to level `FINE`. The Jenkins log
configuration can be changed at Manage Jenkins > System Log, e.g. by adding a new log recorder for `javaposse.jobdsl`.

The following plugins implement the extension point and serve as examples:

* [JGiven Plugin](https://wiki.jenkins-ci.org/display/JENKINS/JGiven+Plugin)
