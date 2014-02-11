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

1. Fork the jenkinsci/job-dsl-plugin repository to your account
2. On your local machine, clone your copy of the job-dsl-plugin repo
3. Again on your local machine, create a branch, ideally named after a JIRA issue you're created for the work
4. Switch to the local branch and make your changes.  Commit them as you go,m and when you're happy, push them to your repo branch
5. Then, on the github website, find the branch you created for your work, and submit a Pull Request.  This will then poke us and we'll take a look at it. We might ask you to rebase (if the trunk has moved on and there are some conflicts) or we might suggest some more changes.
6. If things are all good, we'll ask you to update the documentation. Add an entry to the [Release Notes](https://github.com/jenkinsci/job-dsl-plugin/wiki#release-notes), update the [DSL Overview](https://github.com/jenkinsci/job-dsl-plugin/wiki/Job-DSL-Commands#dsl-methods) and the [Job Reference](https://github.com/jenkinsci/job-dsl-plugin/wiki/Job-reference) pages if necessary. Make sure to add an example or two on the Job Reference page.
7. If the documentation looks good, we'll merge the Pull Request.

## Our Basic Design Decisions / Conventions
1. Use com.google.common.base.Preconditions for argument validaton. E.g. Preconditions.checkArgument(name, "Channel name for irc channel is required!")
1. Use default parameters where appropriate. E.g. def hg(String url, String branch = null, Closure configure = null)
1. We write tests using [Spock](http://code.google.com/p/spock/), so if (for example) you add a new Helper (e.g. ScmHelper), then add a corresponding ScmHelperSpec in the tests directory tree
1. When the configuration value is a class name, use the unique parts of the possible classnames for brevity and build the FQCN adding the common parts. An example: hudson.plugins.im.build_notify.DefaultBuildToChatNotifier should be "Default".
1. For enum type options use the values from the config.xml instead of the GUI text, for example 'FAILURE_AND_FIXED' instead of 'failure and fixed'.
1. We don't yet have a standard way of taking what could be an Enum as a command argument.  However, a nice style tip can be found in [this thread on the forum](https://groups.google.com/forum/#!msg/job-dsl-plugin/imL88hLX0Cw/_XYDmo8t1M4J).
1. Neither do we have a standard way of coping with Job Commands which take a _lot_ of parameters. However, another nice style tip can be see in [this pull request](https://github.com/jenkinsci/job-dsl-plugin/pull/70/files).

## Code Style
1. Indentation: use 4 spaces, no tabs.
1. We roughly follow the [Java](http://www.oracle.com/technetwork/java/javase/documentation/codeconvtoc-136057.html) and [Groovy](http://groovy.codehaus.org/Groovy+style+and+language+feature+guidelines+for+Java+developers) style guidelines.
1. When using IntelliJ IDEA, use the default code style, but disable '*' imports for Java and Groovy.
