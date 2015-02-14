Welcome to the jenkins-job-dsl wiki!

Highly recommended starting point is the tutorial, [[Tutorial - Using the Jenkins Job DSL]]

Once you know how to create a "seed" job from the tutorial, start looking at the [[Real World Examples]] for examples to steal from.  **For formal documentation, the [[Job DSL Commands]]** page has what is available directly in the DSL at this time, and there are also some [[User Power Moves]] you can try to make your life easier.

After you get familiar with some of the commands, try them out at the [Job DSL Playground](http://job-dsl.herokuapp.com/).

If you want to get fancy you'll want to read up on the _configure_ block which gives you direct access to the config.xml, read [[configure block|The Configure Block]]. It's also possible (and easy) to [[define your own DSL commands|Extending the DSL from your Job Scripts]] using monkey patching.

There is a great load of information on [the forum](https://groups.google.com/forum/#!forum/job-dsl-plugin), but some stuff is also making its way into a [[FAQ|Frequently Asked Questions]].

Have a look at the [Jenkins Job DSL Gradle Example](https://github.com/sheehan/job-dsl-gradle-example) to see how to
organize a SCM repository for Job DSL scripts.

And finally, if you want to get more involved, [here's how...](https://github.com/jenkinsci/job-dsl-plugin/blob/master/CONTRIBUTING.md)

## Release Notes
* 1.30 (unreleased)
 * Added support for [Custom Tools Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Custom+Tools+Plugin)
 * Added configure block to the Multijob job context
 * Added strategy option for the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
 * Added strategy build chooser for the [Gerrit Trigger](https://wiki.jenkins-ci.org/display/JENKINS/Gerrit+Trigger)
 * Enhanced support for the [Subversion Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Subversion+Plugin)
 * Added `abortBuild` action for the [Build Timeout](https://wiki.jenkins-ci.org/display/JENKINS/Build-timeout+Plugin)
 * Added support for label parameters from the [NodeLabel Parameter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/NodeLabel+Parameter+Plugin)
 * Added populateToolInstallations and overrideBuildVariables options for the [EnvInject Plugin](https://wiki.jenkins-ci.org/display/JENKINS/EnvInject+Plugin)
 * Added groovy option in the wrappers context for the [EnvInject Plugin](https://wiki.jenkins-ci.org/display/JENKINS/EnvInject+Plugin)
* 1.29 (February 05 2015)
 * Show seed job and template job info in the generated jobs
 * Added [CoreMirror](https://codemirror.net/) support for the DSL script input field
 * Added support for renaming existing Jobs based on a regular expression
 * Added support for the [Repository Connector Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Repository+Connector+Plugin)
 * Added support for the [Workflow Plugin](https://github.com/jenkinsci/workflow-plugin)
 * Added support for the [View Job Filters Plugin](https://wiki.jenkins-ci.org/display/JENKINS/View+Job+Filters)
 * Enhanced support for the [Parameterized Remote Trigger Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Remote+Trigger+Plugin)
 * Enhanced support for the [Claim Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Claim+plugin)
 * Enhanced DSL support for the Job DSL plugin
 * Fixed support for JARs in the "Additional classpath" option of the "Process Job DSLs" build step
 * Allow Ant-style patterns in the "Additional classpath" option of the "Process Job DSLs" build step
 * Support for the `@Grab` and `@Grapes` annotations is deprecated, see [[Migration]]
 * Deprecated `perModuleEmail` option for Maven jobs ([JENKINS-26284](https://issues.jenkins-ci.org/browse/JENKINS-26284))
 * Removed deprecated build timeout methods, see [[Migration|Migration#migrating-to-124]]
* 1.28 (January 01 2015)
 * Added support for the [Credentials Binding Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Credentials+Binding+Plugin)
 * Added support for the [HTTP Request Plugin](https://wiki.jenkins-ci.org/display/JENKINS/HTTP+Request+Plugin)
 * Added support for the [Build Monitor Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Monitor+Plugin)
 * Added support for the [Publish Over SSH Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Publish+Over+SSH+Plugin)
 * Added support for the [Team Concert Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Team+Concert+Plugin)
 * Enhanced support for the [Config File Provider Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Config+File+Provider+Plugin)
 * Added allow missing option for the [HTML Publisher Plugin](https://wiki.jenkins-ci.org/display/JENKINS/HTML+Publisher+Plugin)
 * Added clone timeout option for the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
 * Support more trigger for [Email-ext Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Email-ext+plugin)
 * Fixed documentation for the [Release Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Release+Plugin) ([JENKINS-25945](https://issues.jenkins-ci.org/browse/JENKINS-25945))
 * Fixed `copyArtifacts` build step for matrix jobs, minimum supported version of [Copy Artifact Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Copy+Artifact+Plugin) raised to 1.26
 * Allow the Job DSL build step to be used as conditional build step ([JENKINS-25961](https://issues.jenkins-ci.org/browse/JENKINS-25961))
 * Allow views to be deleted ([JENKINS-26152](https://issues.jenkins-ci.org/browse/JENKINS-26152))
 * The non-closure variants of the `report` methods in the `publishHtml` context are deprecated, see [[Migration]]
 * Set return type for most DSL methods to void, see [[Migration]]
 * Moved `Context` and `ContextHelper` to package `javaposse.jobdsl.dsl`, see [[Migration]]
* 1.27 (December 05 2014)
 * Added support for the [Rbenv Plugin](https://wiki.jenkins-ci.org/display/JENKINS/rbenv+plugin)
 * Added support for the [NodeJS Plugin](https://wiki.jenkins-ci.org/display/JENKINS/NodeJS+Plugin)
 * Added support for the [Golang Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Go+Plugin)
 * Added more support for the [Gradle Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Gradle+Plugin)
 * Added `forcePush` option for Git Publisher
 * Improved build step console output
 * added `entries` method in `publishScp` closure to add multiple entries
 * The context helper classes have been removed
 * Top-level `permission` methods are deprecated, see [[Migration]]
 * `pattern` method in `archiveArtifacts` closure can be called multiple times to collect patterns
 * Added a no parameter variant of `colorizeOutput` to match documentation.
 * The `name` method with a closure parameter in the `job` closure is deprecated, see [[Migration]]
 * Fixed time condition for [Run Condition Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Run+Condition+Plugin)
* 1.26 (October 04 2014)
 * Support for "Pipeline starts with parameters" for [Build Pipeline Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Pipeline+Plugin)
 * Support for [Build User Vars Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+User+Vars+Plugin)
 * Support for [Test Stability Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Test+stability+plugin)
 * Support for [Mask Passwords Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Mask+Passwords+Plugin)
 * Support for [Analysis Collector Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Analysis+Collector+Plugin)
 * Support for [Delivery Pipeline Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Delivery+Pipeline+Plugin)
 * Support for [Notification Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Notification+Plugin)
 * Allow to merge more than one branch with Git SCM
 * Support for [S3 Plugin](https://wiki.jenkins-ci.org/display/JENKINS/S3+Plugin)
 * Removed unnecessary undocumented methods in Gerrit trigger ([JENKINS-24787](https://issues.jenkins-ci.org/browse/JENKINS-24787))
 * Short names in the Gerrit trigger event closure have been replaced by DSL methods, see [[Migration]]
 * The `archiveJunit` method with boolean arguments has been deprecated, see [[Migration]]
 * The `xvnc` method with boolean arguments has been deprecated, see [[Migration]]
 * Support to specify the xAuthority file option of the [Xvnc Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Xvnc+Plugin)
 * `javaposse.jobdsl.dsl.helpers.step.AbstractStepContext` has been removed, see [[Migration]]
 * Fixed endless recursion when calling `properties` in configure blocks ([JENKINS-22708](https://issues.jenkins-ci.org/browse/JENKINS-22708))
 * Support for [Flexible Publish Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Flexible+Publish+Plugin)
 * Support for [Any Build Step Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Any+Build+Step+Plugin)
 * Support to specify the Stash browser URL for the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
 * Partial support for [NodeLabel Parameter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/NodeLabel+Parameter+Plugin)
 * Increased minimum supported Jenkins core version to 1.509.3
* 1.25 (September 01 2014)
 * Dropped support for Java 5, Java 6 or later is required at runtime
 * Support for [Rake Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Rake+plugin)
 * Support for [Lockable Resources Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Lockable+Resources+Plugin)
 * Support for [vSphere Cloud Plugin](https://wiki.jenkins-ci.org/display/JENKINS/vSphere+Cloud+Plugin)
 * Support for [Sectioned View Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Sectioned+View+Plugin)
 * Support for [M2 Release Plugin](https://wiki.jenkins-ci.org/display/JENKINS/M2+Release+Plugin)
 * Support for [Nested View Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Nested+View+Plugin)
 * Support for [Config File Provider Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Config+File+Provider+Plugin)
 * Support more options of the [Multijob Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Multijob+Plugin)
 * Added option to add classpath entries for Job DSL runs
 * Added localBranch option for Git SCM
 * Added method to read a file from any job's workspace
 * Fixed workspace cleanup external delete command ([JENKINS-24231](https://issues.jenkins-ci.org/browse/JENKINS-24231))
 * Fixed Build Timeout Plugin no activity timeout ([JENKINS-24258](https://issues.jenkins-ci.org/browse/JENKINS-24258))
 * Fixed alwaysRun and neverRun conditions ([JENKINS-24510](https://issues.jenkins-ci.org/browse/JENKINS-24510))
* 1.24 (July 05 2014)
 * Support for [Build Name Setter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Name+Setter+Plugin)
 * Support for [RunDeck Plugin](https://wiki.jenkins-ci.org/display/JENKINS/RunDeck+Plugin)
 * Support for [ClearCase Plugin](https://wiki.jenkins-ci.org/display/JENKINS/ClearCase+Plugin)
 * Support for [Keychains and Provisioning Profiles Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Keychains+and+Provisioning+Profiles+Plugin)
 * Support for [xUnit Plugin](https://wiki.jenkins-ci.org/display/JENKINS/xUnit+Plugin)
 * Support for [Batch Task Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Batch+Task+Plugin)
 * Support for [Matrix Projects](https://wiki.jenkins-ci.org/display/JENKINS/Building+a+matrix+project)
 * Extend support for [Build Timeout](https://wiki.jenkins-ci.org/display/JENKINS/Build-timeout+Plugin)
 * Added option for treating job names relative to the seed job
 * Added pruneBranches option for Git SCM
 * Fixed ClassCastException when removing folder ([JENKINS-23289](https://issues.jenkins-ci.org/browse/JENKINS-23289))
 * Fixed GerritContext not honoring default settings ([JENKINS-23318](https://issues.jenkins-ci.org/browse/JENKINS-23318))
 * Moved PerforcePasswordEncryptor to javaposse.jobdsl.dsl.helpers.scm package
 * Support for [Exclusion Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Exclusion-Plugin)
* 1.23 (May 23 2014)
 * Added support for injecting globally defined passwords ([EnvInject Plugin](https://wiki.jenkins-ci.org/display/JENKINS/EnvInject+Plugin))
 * Added file name to exception message when reading missing workspace files ([JENKINS-23006](https://issues.jenkins-ci.org/browse/JENKINS-23006))
 * Fixed behavior when creating Jabber targets that are not group chats ([JENKINS-23090](https://issues.jenkins-ci.org/browse/JENKINS-23090))
 * Create and update folders ([Cloudbees Folders Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Folders+Plugin))
 * Allow to create or update views within folders
 * Removed multiscm limit
 * Fixed support for the Description Setter Plugin
 * new parameter for CopyArtifact's StatusBuildSelector to consider only stable builds
 * Limited [Extra Columns Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Extra+Columns+Plugin) support
 * [Flowdock Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Flowdock+Plugin)
 * More conditions for the Conditional BuildStep Plugin supported
 * Initial support for the [Stash Notifier Plugin](https://wiki.jenkins-ci.org/display/JENKINS/StashNotifier+Plugin)
 * Support for [LogFileSizeChecker Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Logfilesizechecker+Plugin)
 * Support for [Maven Deployment Linker Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Maven+Deployment+Linker)
 * Added support for parameterized manual jobs for the [Build Pipeline Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Pipeline+Plugin)
 * Support for [Workspace Cleanup Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Workspace+Cleanup+Plugin)
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
