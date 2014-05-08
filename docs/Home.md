Welcome to the jenkins-job-dsl wiki!

Highly recommended starting point is the tutorial, [[Tutorial - Using the Jenkins Job DSL]]

Once you know how to create a "seed" job from the tutorial, start looking at the [[Real World Examples]] for examples to steal from.  **For formal documentation, the [[Job DSL Commands]]** page has what is available directly in the DSL at this time, and there are also some [[User Power Moves]] you can try to make your life easier.

After you get familiar with some of the commands, try them out at the [Job DSL Playground](http://job-dsl.herokuapp.com/).

If you want to get fancy you'll want to read up on the _configure_ block which gives you direct access to the config.xml, read [[configure block|The Configure Block]]. It's also possible (and easy) to [[define your own DSL commands|Extending the DSL from your Job Scripts]] using monkey patching.

There is a great load of information on [the forum](https://groups.google.com/forum/#!forum/job-dsl-plugin), but some stuff is also making its way into a [[FAQ|Frequently Asked Questions]].

And finally, if you want to get more involved, [here's how...](https://github.com/jenkinsci/job-dsl-plugin/blob/master/CONTRIBUTING.md)

## Release Notes
* 1.23 (unreleased)
 * Fixed support for the Description Setter Plugin
 * new parameter for CopyArtifact's StatusBuildSelector to consider only stable builds
 * Limited [Extra Columns Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Extra+Columns+Plugin) support
 * [Flowdock Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Flowdock+Plugin)
 * More conditions for the Conditional BuildStep Plugin supported
 * Initial support for the [Stash Notifier Plugin](https://wiki.jenkins-ci.org/display/JENKINS/StashNotifier+Plugin)
* 1.22 (Apr 05 2014)
 * Fixed support for the Conditional BuildStep Plugin
 * [Github Pull Request Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin)
 * [Workspace Cleanup Plugin](https://github.com/jenkinsci/job-dsl-plugin/wiki/Job-reference#wiki-workspace-cleanup-plugin)
 * [Parameterized Remote Trigger Plugin](https://github.com/jenkinsci/job-dsl-plugin/wiki/Job-reference#wiki-parameterized-remote-trigger)
 * [Git Publisher](https://github.com/jenkinsci/job-dsl-plugin/wiki/Job-reference#wiki-git-publisher)
 * Support for the `changeBuildStatus` element for [Jacoco Code Coverage](wiki/Job-reference#wiki-jacoco) publisher
 * [Jenkins Release Plugin](https://github.com/jenkinsci/job-dsl-plugin/wiki/Job-reference#wiki-release)
 * Support for buildnumbers as strings in [Copy Artifacts](https://github.com/jenkinsci/job-dsl-plugin/wiki/Job-reference#wiki-copy-artifacts)
* 1.21 (Mar 06 2014)
 * [Build Flow](https://wiki.jenkins-ci.org/display/JENKINS/Build+Flow+Plugin) job type
 * [Execute concurrent builds](wiki/Job-reference#wiki-execute-concurrent-builds) job option
 * [Github Commit Notifier](wiki/Job-reference#wiki-github-commit-notifier) publisher
 * [Build Pipeline Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Pipeline+Plugin)
 * [Views](wiki/Job-DSL-Commands#wiki-view)
 * [Publish Robot Framework reports](wiki/Job-reference#wiki-robot-framework-reports)
 * Templates within folders can be used
 * [Tool Environment Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Tool+Environment+Plugin)
* 1.20 (Jan 27 2014)
 * [Allow to set the Maven installation](wiki/Job-reference#maven-installation)
 * [Maven step supports more options](wiki/Job-reference#maven-1)
 * [Maven pre/post build steps supported](wiki/Job-reference#maven-pre-and-post-build-steps)
 * [Conditional BuildStep Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Conditional+BuildStep+Plugin)
 * [Added allowEmpty option to archiveArtifacts](wiki/Job-reference#archive-artifacts)
 * [Throttle Concurrent Builds Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Throttle+Concurrent+Builds+Plugin)
 * Some implementation classes have been moved to avoid problems with Groovy closures (**BREAKING CHANGE**, see [[Migration]])
 * [Parameterized Trigger Plugin for build steps](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Trigger+Plugin)
 * [Associated Files Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Associated+Files+Plugin)
 * [JSHint Checkstyle Plugin](https://wiki.jenkins-ci.org/display/JENKINS/JSHint+Checkstyle+Plugin)
 * [Emma Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Emma+Plugin)
 * [Added support for advanced Git SCM options](wiki/Job-reference#git)
 * [Added ItemDiscover, ItemCancel, and ScmTag permissions to the Permissions enum](https://github.com/jenkinsci/job-dsl-plugin/pull/97)
* 1.19 (Dec 19 2013)
 * [Javadoc Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Javadoc+Plugin)
 * Added support to allow seed jobs in folders. [#109](https://github.com/jenkinsci/job-dsl-plugin/pull/109)
 * [Groovy Postbuild Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Groovy+Postbuild+Plugin)
 * [XVNC Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Xvnc+Plugin)
 * [Prerequisite Build Step](https://wiki.jenkins-ci.org/display/JENKINS/Prerequisite+build+step+plugin)
 * Aggregate Downstream Test Results
 * [Post Build Task](https://wiki.jenkins-ci.org/display/JENKINS/Post+build+task)
 * [AnsiColor Plugin](https://wiki.jenkins-ci.org/display/JENKINS/AnsiColor+Plugin)
 * new wrappers block, containing all build wrapper methods (**BREAKING CHANGE**, see [[Migration]])
 * [Timestamper](https://wiki.jenkins-ci.org/display/JENKINS/Timestamper)
 * [JENKINS-20284](https://issues.jenkins-ci.org/browse/JENKINS-20284)
 * [Text Finder](https://wiki.jenkins-ci.org/display/JENKINS/Text-finder+Plugin)
* 1.17
 * Static Analysis Plugins (PMD, Checkstyle, Findbugs, DRY, Tasks, CCM, Lint, OWASP Dependency Check, Compiler warnings, 
 * Description Setter Plugin
 * Keep Dependencies and Fingerprinting
 * Mailer
 * SSH Agent
 * General Improvements (Filter for Run Build Parameter, support Folders)
 * Isolated Local Maven Repositories
 * Node Stalker Plugin
 * JaCoCo Plugin
 * Claim plugin
* 1.16
 * Clone Workspace SCM
 * Block on upstream/downstream jobs
 * Job DSL Plugin (to create DSL build steps in a job)
 * Build Timeout Plugin
 * Cobertura Plugin
 * Port Allocator Plugin
 * RVM Plugin
 * URL Trigger
 * Better Gerrit support
 * SBT Plugin
 * Update blockOn call
 * [[Help method to read files from the workspace|Job-DSL-Commands#reading-files-from-workspace]]
 * Queue jobs after execution of the DSL
 * MultiJob Support
* 1.15
 * [[@Grab Support|Grab Support]]
 * Build Parameters
 * GitHub SCM Context
 * IRC Publisher
 * Environment Variables From Groovy Script
 * Priority Sorter Plugin
* 1.14
 * Environment Variables
 * Groovy Build Steps
 * System Groovy Build Steps
 * Maven Project Support
* 1.13
 * Make it possible to forget generated jobs
 * JENKINS-16931, JENKINS-16998
* 1.12
 * Copy Artifacts Plugin
 * Batch File Build Step
 * Violations Publisher Plugin
* 1.11
 * Able to specify description
 * Build Blocker Plugin
 * Downstream Extended - Extended version of downstream that can also pass in complex parameters
 * Downstream - Specify a downstream job
 * SCP Publisher
 * Jabber Publisher - Publish builds to Jabber
 * Archive Junit - Archiving the results from JUnit
 * Ant - Apache Ant Build Step
 * logRotator - How long to keep builds
 * publishHtml - Publish HTML Files
 * archiveArtifacts - Archive artifacts into the build
* 1.10
 * Encrypt P4 Passwords
 * Start building onejar
* 1.9
 * Fix label() to force canRoam to false
* 1.8
 * Bug fixes
* 1.7
 * Move to GroovyEngine, to look at Workspace for other Groovy scripts that can be used for re-usable helper classes
* 1.6
 * Refactored DSL from Plugin, so that they're in different modules
* 1.5
 * Fix for #39
 * label() - Assign which labels this job can run on
 * timeout(Integer timeoutInMinutes, boolean shouldFailBuild) - Build Timeout Plugin
 * chucknorris() - Chuck Norris Plugin
* 1.4
 * Parameters and Environment variables are binded to the script and can be used directly
 * svn(svnUrl, closure)
 * p4(viewspec, user, password, closure)
* 1.3
 * Support for Node as argument to div(), meaning that more complex structures can be used in div statements.
 * multiscm(closure) - Supoprt multi-scm plugin, alternative to scm tag to allow multiple SCMs
* 1.2
 * permissionAll(String userName) - Add all available permissions for a user
* 1.1
 * extendedEmail(recipients, subject, content, closure) - Configure email-ext plugin
 * gerrit(closure) - Configure Gerrit Trigger plugin
* 1.0
 * Initial release