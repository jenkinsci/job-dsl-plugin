# Contributing to the job dsl plugin Project

We are always happy for folks to help us out on this project.  Here's the ways you can help us out:

* Documentation
    * Small Changes - fixes to spelling, inaccuracies, errors - Just do it
    * Larger Changes - Whole new sections / pages - send a mail to the group with your proposal at https://groups.google.com/forum/?fromgroups#!forum/job-dsl-plugin
* New Features
    * Feature Requests - Send a mail to the group with your request at https://groups.google.com/forum/?fromgroups#!forum/job-dsl-plugin - if you can include an example snippet of the config.xml so much the better.
    * Even better than this is an "New Feature" issue on the [[Jenkins JIRA|https://issues.jenkins-ci.org/secure/Dashboard.jspa]]. Remember and add the "job-dsl-plugin" component. Then, send us a mail to the newsgroup telling us about it.
    * Feature Implementations - Even better than both of these is an implementation.  Simply fork our repo, create a branch (named after the JIRA "New Feature" you created earlier), implement it yourself and submit a Pull Request.  Get started with the (light touch) [[Architecture Docs|Jenkins Job DSL Architecture]] and  [["How to Build"|Building the Jenkins Job DSL]] notes and the "Our Git Protocol" section below
* Bugs
    * Bug Reports - Send a mail to the group with your request at https://groups.google.com/forum/?fromgroups#!forum/job-dsl-plugin - The more information the better
    * Even better than this is a new "Bug" issue on the [[Jenkins JIRA|https://issues.jenkins-ci.org/secure/Dashboard.jspa]]. Remember and add the "job-dsl-plugin" component. Then, send us a mail to the newsgroup telling us about it.
    * Bug Fixes - Even better than both of these is a fix.   Simply fork our repo, create a branch (named after the JIRA "Bug" you created earlier), implement it yourself and submit a Pull Request.  Remember to follow the "Our Git Protocol" section below

## Project Developer Docs
_Not required for users of the DSL. And like any developer docs, they're probably out of date the moment they're written._

## Our Git Protocol
If you want to make a change to the code on jenkinsci/job-dsl-plugin, here's the protocol we follow (you need a github account in order to do this):

1. Fork the jenkinsci/job-dsl-plugin repository to your account.
2. On your local machine, clone your copy of the job-dsl-plugin repo.
3. Again on your local machine, create a branch, ideally named after a JIRA issue you're created for the work.
4. Switch to the local branch and make your changes. Commit them as you go, and when you're happy, push them to your repo branch.
5. Also update the documentation, see below.
6. Then, on the GitHub website, find the branch you created for your work, and submit a Pull Request. This will then poke us and we'll take a look at it. We might ask you to rebase (if the trunk has moved on and there are some conflicts) or we might suggest some more changes.
7. If the all looks good, we'll merge the Pull Request.

Try to focus. It's not required to add all options for a certain plugin to get the pull request merged. In fact, it may
even delay the merge until someone finds time to review a huge change. Only implement the options you really need and
leave room so that the remaining options can be added when needed.

## Our Basic Design Decisions / Conventions
1. Use com.google.common.base.Preconditions for argument validation. E.g. Preconditions.checkArgument(name, "Channel name for irc channel is required!")
1. We write tests using [Spock](http://code.google.com/p/spock/), so if (for example) you add a new Helper (e.g. ScmHelper), then add a corresponding ScmHelperSpec in the tests directory tree

## DSL Design
* Every option should have the same defaults as the UI.
* Use context closures instead of long parameter lists.
* Use private or protected access modifiers for context and helper methods that should not be exposed to DSL users.
* Use enum values where appropriate, e.g. when the UI displays a chooser. The enum should be an inner class of the
context which uses the enum. Use conventions for constants for naming enum values. Add the enum to the implicit imports
in `DslScriptLoader.createCompilerConfiguration`.

```groovy
class FooContext {
    FooOptions option = FooOptions.FIRST

    def option(FooOptions option) {
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

    def foo(boolean foo = true) {
        this.foo = foo
    }
}
```

* Offer convenience methods for list or key-value options.

```groovy
class FooContext {
    Map<String, String> jvmOptions = [:]
    List<String> args = []

    def jvmOption(String key, String value) {
        jvmOptions[key] = value
    }

    def jvmOptions(Map<String, String> options) {
        jvmOptions.putAll(options)
    }

    def arg(String arg) {
        args << arg
    }

    def args(String... args) {
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
* Include an example of the generated XML in the GroovyDoc comment of DSL methods.

```groovy
/**
 * <project>
 *     <buildWrappers>
 *         <hudson.plugins.foo.FooWrapper>
 *             <option>bar</option>
 *         </hudson.plugins.foo.FooWrapper>
 *     </buildWrappers>
 * </project>
 */
def foo(String optionArg) {
    ...
}
```

## Documentation
* Add an entry to the [Release Notes](docs/Home.md#release-notes).
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