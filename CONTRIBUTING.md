# Contributing to the job dsl plugin Project

## Ways To Help
We are always happy for folk to help us out on this project.  Here are some ways how:

### Documentation

* Small Changes (fixes to spelling, inaccuracies, errors) - just do it.
* Larger Changes (whole new sections / pages) - send a mail to [the group](https://groups.google.com/forum/?fromgroups#!forum/job-dsl-plugin) with your proposal at 
    
### New Features
* Feature Requests - Send a mail to [the group](https://groups.google.com/forum/?fromgroups#!forum/job-dsl-plugin) with your request at - if you can include an example snippet of the `config.xml`, so much the better.
* Even better than this is a "New Feature" issue on the [Jenkins JIRA](https://issues.jenkins-ci.org/secure/Dashboard.jspa). Remember to add the `job-dsl-plugin` component. Then, send us a mail to the newsgroup telling us about it.
* Feature Implementations - Even better than both of these is an implementation.  Simply fork our repo, create a branch (named after the JIRA "New Feature" you created earlier), implement it yourself and submit a Pull Request.  Get started with the (light touch) [Architecture Docs](docs/Jenkins-Job-DSL-Architecture.md) and  [How to Build](docs/Building-the-Jenkins-Job-DSL.md) notes and the _Our Git Protocol_ section below
    
### Bugs
* Bug Reports - Send a mail to [the group](https://groups.google.com/forum/?fromgroups#!forum/job-dsl-plugin) with your request - the more information the better.
* Even better than this is a new "Bug" issue on the [Jenkins JIRA](https://issues.jenkins-ci.org/secure/Dashboard.jspa). Remember to add the `job-dsl-plugin` component. Then, send us a mail to the newsgroup telling us about it.
* Bug Fixes - Even better than both of these is a fix.   Simply fork our repo, create a branch (named after the JIRA "Bug" you created earlier), implement it yourself and submit a Pull Request.  Remember to follow the _Our Git Protocol_ section below.

## Project Developer Docs
_Not required for users of the DSL. And like any developer docs, they're probably out of date the moment they're written._

## Our Git Protocol
If you want to make a change to the code on `jenkinsci/job-dsl-plugin`, here's the protocol we follow (you need a Github account in order to do this):

1. Fork the `jenkinsci/job-dsl-plugin` repository to your account.
2. On your local machine, clone your copy of the `job-dsl-plugin` repo.
3. Again on your local machine, create a branch, ideally named after a JIRA issue you've created for the work.
4. Switch to the local branch and make your changes. Commit them as you go, and when you're happy, push them to your repo branch.
5. Also update the documentation, see below.
6. Then, on the GitHub website, find the branch you created for your work, and submit a Pull Request. This will then poke us and we'll take a look at it. We might ask you to rebase (if the trunk has moved on and there are some conflicts) or we might suggest some more changes.
7. If the all looks good, we'll merge the Pull Request.

Try to focus. It's not required to add all options for a certain plugin to get the pull request merged. In fact, it may
even delay the merge until someone finds time to review a huge change. Only implement the options you really need and
leave room so that the remaining options can be added when needed.

## Our Basic Design Decisions / Conventions
1. Use `javaposse.jobdsl.dsl.Preconditions` for argument validation. E.g. `Preconditions.checkArgument(name, "Channel name for irc channel is required!")`
1. We write tests using [Spock](http://code.google.com/p/spock/), so if (for example) you add a new Helper (e.g. `ScmHelper`), then add a corresponding ScmHelperSpec in the tests directory tree.

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

* Use private or protected access modifiers for context and helper methods that should not be exposed to DSL users.
* Add the `@RequiresPlugin` annotation if the feature needs a specific plugin to be installed in Jenkins.

```groovy
@RequiresPlugin(id = 'foo', minimumVersion = '1.2')
void foo(String fooOption) {
    // your implementation goes here
}
```

* Use enum values where appropriate, e.g. when the UI displays a chooser. The enum should be an inner class of the
context which uses the enum. Use conventions for constants for naming enum values. Add the enum to the implicit imports
in `DslScriptLoader.createCompilerConfiguration`.

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
* We roughly follow the [Java](http://www.oracle.com/technetwork/java/javase/documentation/codeconvtoc-136057.html) and [Groovy](http://groovy.codehaus.org/Groovy+style+and+language+feature+guidelines+for+Java+developers) style guidelines.
* When using IntelliJ IDEA, use the default code style, but disable '*' imports for Java and Groovy.
* Add a CRLF at the end of a file.

## Documentation
* Add an entry to the [Release Notes](docs/Home.md#release-notes).
* Make sure the DSL methods
   * are annotated with `@RequiresPlugin`, including the plugin ID and a minimum version, if applicable.
   * are given a `@since <version>` in the GroovyDoc.
   * have a short description in the GroovyDoc.

```groovy
    /**
     * Generate configuration for Mercurial.
     *
     * @since 1.33
     */
    @RequiresPlugin(id = 'mercurial', minimumVersion = '1.50.1')
    void hg(String url, @DslContext(HgContext) Closure hgClosure) {
    }
```

* Add at least one example for each DSL method to `job-dsl-core/src/main/docs/examples`.
* Update the [DSL Overview](docs/Job-DSL-Commands.md#dsl-methods) if necessary.
* Make sure that the Job Reference page contains a formal reference, a short description including a link to the plugin, at least one example and the version which added the feature.

```
    ## Foo

    ```groovy
    job {
        wrappers {
            foo(String option) // optional
        }
    }
    ```

    Does some foo. Requires the [Foo Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Foo).

    ```groovy
    job {
        wrappers {
            foo('bar')
        }
    }
    ```

    (Since 1.15)
```
