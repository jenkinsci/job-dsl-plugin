# Contributing to the job-dsl-plugin Project

## Ways To Help

We are always happy for folk to help us out on this project. Please follow these guidelines.

### Documentation

* Small changes (fixes to spelling, inaccuracies, errors) - just do it, send a pull request.
* Larger Changes (whole new sections / pages) - send a mail to
  [the group](https://groups.google.com/forum/?fromgroups#!forum/job-dsl-plugin) with your proposal.
    
### New Features

* Feature Requests - Create a "New Feature" issue on the
  [Jenkins JIRA](https://issues.jenkins-ci.org/browse/JENKINS/component/16720/). Remember to add the `job-dsl-plugin`
  component.
* Feature Implementations - Even better than a JIRA issue is an implementation. If the implementation requires design
  or architectural changes or would need refactoring, send a mail to the
  [the group](https://groups.google.com/forum/?fromgroups#!forum/job-dsl-plugin) with a proposal. Otherwise simply fork
  the repo, create a branch (named after the JIRA "New Feature" you created earlier), implement it yourself and submit a
  Pull Request.
    
### Bugs

* Bug Reports - Create a new "Bug" issue on the
  [Jenkins JIRA](https://issues.jenkins-ci.org/browse/JENKINS/component/16720/). Remember to add the `job-dsl-plugin`
  component.
* Bug Fixes - Even better than a JIRA issue is a fix. Simply fork our repo, create a branch (named after the JIRA "Bug"
  you created earlier), implement it yourself and submit a Pull Request.

## Our Git Protocol

If you want to make a change to the code on `jenkinsci/job-dsl-plugin`, here's the protocol we follow (you need a
GitHub account in order to do this):

1. Fork the `jenkinsci/job-dsl-plugin` repository to your account.
2. On your local machine, clone your copy of the `job-dsl-plugin` repo.
3. Again on your local machine, create a branch, ideally named after a JIRA issue you've created for the work.
4. Switch to the local branch and make your changes. When you're happy, commit them and push them to your repo branch.
5. Also update the documentation (including examples) and add tests as part of your changes.
6. Then, on the GitHub website, find the branch you created for your work, and submit a Pull Request. This will then
   poke us and we'll take a look at it. We might ask you to rebase (if the trunk has moved on and there are some
   conflicts) or we might suggest some more changes.
7. If the all looks good, we'll merge the Pull Request.

Try to focus. It's not required to add all options for a certain plugin to get the pull request merged. In fact, it may
even delay the merge until someone finds time to review a huge change. Only implement the options you really need and
leave room so that the remaining options can be added when needed.

## Our Basic Design Decisions / Conventions

* When creating new classes, use Groovy.
* Use `javaposse.jobdsl.dsl.Preconditions` for argument validation.

```groovy
Preconditions.checkArgument(name, "Channel name for irc channel is required!")
```

* We write tests using [Spock](http://spockframework.org).

## DSL Design

* Every option should have the same defaults as the UI.
* Use method parameters for mandatory options and context closures for optional settings.

```groovy
job {
    publishers {
        foo(String mandatoryA, int mandatoryB) {
            optionalC(String value)
            optionalD(boolean value = true)
        }
    }
}
```

* Add the `@DslContext` annotation for context closure parameters.

```groovy
void remote(@DslContext(RemoteContext) Closure remoteClosure) {
    RemoteContext remoteContext = new RemoteContext()
    executeInContext(remoteClosure, remoteContext)
    
    // ...
}
```

* Use private or protected access modifiers for context and helper methods that should not be exposed to DSL users.
* Add the `@RequiresPlugin` annotation if the feature needs a specific plugin to be installed in Jenkins.

```groovy
@RequiresPlugin(id = 'foo', minimumVersion = '1.2')
void foo(String fooOption) {
    // your implementation goes here
}
```

* Use enum values where appropriate, e.g. when the UI displays a chooser. The enum should be an inner class of the
  context which uses the enum. Use conventions for constants for naming enum values. Add the enum to the implicit
  imports in `DslScriptLoader.createCompilerConfiguration`.

```groovy
class FooContext {
    FooOptions option = FooOptions.FIRST

    void option(FooOptions option) {
        this.option = option
    }

    enum FooOptions {
        FIRST,
        SECOND
    }
}
```

* Set the default parameter values for boolean options to true, so that a user can write `enableSomeOption()` instead
  of `enableSomeOption(true)`.

```groovy
class FooContext {
    boolean foo

    void foo(boolean foo = true) {
        this.foo = foo
    }
}
```

* Offer convenience methods for list or key-value options.

```groovy
class FooContext {
    Map<String, String> jvmOptions = [:]
    List<String> args = []

    void jvmOption(String key, String value) {
        jvmOptions[key] = value
    }

    void jvmOptions(Map<String, String> options) {
        jvmOptions.putAll(options)
    }

    void arg(String arg) {
        args << arg
    }

    void args(String... args) {
        this.args.addAll(args)
    }
}
```

## Code Style

* Indentation: use 4 spaces, no tabs.
* Use a maximum line length of 120 characters.
* We roughly follow the [Java](http://www.oracle.com/technetwork/java/javase/documentation/codeconvtoc-136057.html) and
  [Groovy](http://groovy-lang.org/style-guide.html) style
  guidelines.
* When using IntelliJ IDEA, use the default code style, but disable star imports for Java and Groovy.
* Add a CRLF at the end of a file.

## Documentation

* Add an entry to the [Release Notes](docs/Home.md#release-notes).
* Make sure the DSL methods
   * are annotated with `@RequiresPlugin`, including the plugin ID and a minimum version, if applicable.
   * are given a `@since <version>` in the GroovyDoc.
   * have a short description in the GroovyDoc.
   * mention the default value, e.g. `Defaults to {@code true}.`

```groovy
/**
 * Clean up the workspace before every checkout. Defaults to {@code false}.
 *
 * @since 1.37
 */
@RequiresPlugin(id = 'clean', minimumVersion = '1.3.4')
void clean(boolean clean = true) {
    // ...
}
```

* Add at least one example for each DSL method to `job-dsl-core/src/main/docs/examples`.

## Pull Request Checklist

1. Code style and indentation
2. GroovyDoc for each public method
    1. `@since` annotation
    2. default value mentioned for DSL methods, e.g. `Defaults to {@code true}.`
3. `@RequiresPlugin` annotation including minimum version
4. `@DslContext` annotation for closure parameters
5. DSL methods have default values for boolean parameters, e.g. `void foo(boolean foo = true) { ... }`
6. Examples for each DSL method
7. Tests for all methods
8. Manual test in Jenkins
