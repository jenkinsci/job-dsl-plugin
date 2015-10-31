Welcome to the jenkins-job-dsl wiki!

* The high-level DSL concepts are documented in [[Job DSL Commands]].
* See the [API Viewer](https://jenkinsci.github.io/job-dsl-plugin/) for a full syntax reference
* There are also tips on [[more advanced usage and workflows|User Power Moves]].
* An introduction to the DSL can be found in a collection of [[Talks and Blog Posts]].
* [The forum](https://groups.google.com/forum/#!forum/job-dsl-plugin) has lots of information, some of which is making its way to the [[FAQ|Frequently Asked Questions]].
* If you want to get more involved, here's [how to contribute](https://github.com/jenkinsci/job-dsl-plugin/blob/master/CONTRIBUTING.md)...

## Getting Started
Highly recommended starting point is [[the tutorial|Tutorial - Using the Jenkins Job DSL]].

Once you know how to create a "seed" job from the tutorial, start looking at the [[real world examples|Real World Examples]] for examples to steal from.
After you get familiar with some of the commands, try them out at the [Job DSL Playground](http://job-dsl.herokuapp.com/).

If you want to get fancy you'll want to read up on [[configure block|The Configure Block]], which gives you direct access to the `config.xml`.

Have a look at the [Jenkins Job DSL Gradle example](https://github.com/sheehan/job-dsl-gradle-example) to see how to organize a SCM repository for Job DSL scripts.

## Release Notes
* 1.40 (unreleased)
 * Increased the minimum supported Jenkins version to 1.609
 * Added support for the
   [Matrix Combinations Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Matrix+Combinations+Plugin)
 * Enhanced support for the [GitHub Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+Plugin)
   ([JENKINS-29849](https://issues.jenkins-ci.org/browse/JENKINS-29849))
 * Enhanced support for [Maven Project Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Maven+Project+Plugin)
   ([JENKINS-30544](https://issues.jenkins-ci.org/browse/JENKINS-30544))
 * Enhanced support for the [Multijob Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Multijob+Plugin)
   ([JENKINS-30760](https://issues.jenkins-ci.org/browse/JENKINS-30760))
 * Enhanced support for the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
   ([#645](https://github.com/jenkinsci/job-dsl-plugin/pull/645))
 * Enhanced support for the
   [GitHub Pull Request Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin)
 * Fixed a problem with deprecation warnings
   ([JENKINS-30826](https://issues.jenkins-ci.org/browse/JENKINS-30826))
 * Allow `@DslExtensionMethod` annotated methods to return `null` to not contribute to the job configuration
 * Allow `DownstreamTriggerParameterContext` to be extended
   ([JENKINS-31111](https://issues.jenkins-ci.org/browse/JENKINS-31111))
 * Added support for the [TestNG Plugin](https://wiki.jenkins-ci.org/display/JENKINS/testng-plugin)
   ([JENKINS-30895](https://issues.jenkins-ci.org/browse/JENKINS-30895))
 * Added workaround for [GROOVY-6263](https://issues.apache.org/jira/browse/GROOVY-6263) to `WorkspaceCleanupContext`
 * Added support for the [SSH Plugin](https://wiki.jenkins-ci.org/display/JENKINS/SSH+plugin)
   ([JENKINS-30957](https://issues.jenkins-ci.org/browse/JENKINS-30957))
* 1.39 (October 05 2015)
 * Increased the minimum supported Jenkins version to 1.596
 * Added support for the [ZenTimestamp Plugin](https://wiki.jenkins-ci.org/display/JENKINS/ZenTimestamp+Plugin)
 * Added support for the [CloudBees Docker Build and Publish
   Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Docker+Build+and+Publish+plugin)
   ([JENKINS-29600](https://issues.jenkins-ci.org/browse/JENKINS-29600))
 * Added support for remote trigger authentication token
 * Added support for the [ArtifactDeployer Plugin](https://wiki.jenkins-ci.org/display/JENKINS/ArtifactDeployer+Plugin)
 * Added support for the [CloudBees Docker Custom Build Environment
   Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Docker+Custom+Build+Environment+Plugin)
 * Enhanced support for the [Subversion Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Subversion+Plugin)
 * Enhanced support for the [xUnit Plugin](https://wiki.jenkins-ci.org/display/JENKINS/xUnit+Plugin)
 * Enhanced support for the
   [Parameterized Trigger Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Trigger+Plugin)
 * Enhanced support for the [vSphere Cloud Plugin](https://wiki.jenkins-ci.org/display/JENKINS/vSphere+Cloud+Plugin)
 * Enhanced support for the [SonarQube Plugin](https://wiki.jenkins-ci.org/display/JENKINS/SonarQube+plugin)
 * Enhanced support the
   [Config File Provider Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Config+File+Provider+Plugin)
   ([JENKINS-30493](https://issues.jenkins-ci.org/browse/JENKINS-30493))
 * Changed support for the [Multijob Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Multijob+Plugin), see
   [Migration](Migration#migrating-to-139)
   ([JENKINS-27921](https://issues.jenkins-ci.org/browse/JENKINS-27921))
 * Support for older versions of the [Subversion Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Subversion+Plugin)
   is deprecated, see [Migration](Migration#migrating-to-139)
 * Support for older versions of the
   [Parameterized Trigger Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Trigger+Plugin) is
   deprecated, see [Migration](Migration#migrating-to-139)
 * Support for the [JSHint Checkstyle Plugin](https://wiki.jenkins-ci.org/display/JENKINS/JSHint+Checkstyle+Plugin) is
   deprecated, see [Migration](Migration#migrating-to-139)
 * Fixed `StackOverflowError` when using `downstreamParameterized` publisher
   ([JENKINS-30504](https://issues.jenkins-ci.org/browse/JENKINS-30504))
 * Fixed problem with additional classpath
   ([JENKINS-30348](https://issues.jenkins-ci.org/browse/JENKINS-30348))
 * Allow to abort DSL processing
 * Improved console output
 * Added support for the [Cron Column Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Cron+Column+Plugin)
 * Added support for the [Progress Bar Column Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Progress+Bar+Column+Plugin)
 * Added support for the [Rebuild Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Rebuild+Plugin)
 * Added support for the [Global Variable String Parameter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Global+Variable+String+Parameter+Plugin)
 * Added support for the [Phabricator Differential Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Phabricator+Differential+Plugin)
 * The signature of `DslScriptLoader.runDslEngineForParent` has changed, see [Migration](Migration#migrating-to-139)
 * Removed implicit star import of `javaposse.jobdsl.dsl.ConfigFileType` in scripts, see
   [Migration](Migration#migrating-to-139)
 * Removed anything that has been deprecated in 1.31, see [Migration](Migration#migrating-to-131)
* 1.38 (September 09 2015)
 * Replaced the [[Job Reference]], [[View Reference]] and [[Folder Reference]] pages by the
   [API Viewer](https://jenkinsci.github.io/job-dsl-plugin/)
 * Added a collection of [[Talks and Blog Posts]]
 * Added support for [Crittercism dSYM Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Crittercism+dSYM+Plugin)
   ([JENKINS-29501](https://issues.jenkins-ci.org/browse/JENKINS-29501))
 * Added support for the [ShiningPanda Plugin](https://wiki.jenkins-ci.org/display/JENKINS/ShiningPanda+Plugin)
 * Enhanced support for the [Active Choices Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Active+Choices+Plugin)
 * Enhanced support for the [Credentials Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Credentials+Plugin)
 * Enhanced support for the
   [GitHub Pull Request Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin)
 * Enhanced support for the
   [Delivery Pipeline Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Delivery+Pipeline+Plugin)
   ([JENKINS-30221](https://issues.jenkins-ci.org/browse/JENKINS-30221))
 * Enhanced support for the
   [Parameterized Trigger Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Trigger+Plugin)
   ([JENKINS-29662](https://issues.jenkins-ci.org/browse/JENKINS-29662),
   [JENKINS-29801](https://issues.jenkins-ci.org/browse/JENKINS-29801))
 * Enhanced support for the [Parameterized Remote Trigger
   Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Remote+Trigger+Plugin)
   ([JENKINS-29531](https://issues.jenkins-ci.org/browse/JENKINS-29531))
 * Enhanced support for the [S3 Plugin](https://wiki.jenkins-ci.org/display/JENKINS/S3+Plugin)
 * Added documentation about [[handling credentials]]
 * Fixed a problem with `ScmContext` and the extension point
   ([JENKINS-29972](https://issues.jenkins-ci.org/browse/JENKINS-29972))
 * Deprecated some overloaded DSL methods for the
   [Parameterized Trigger Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Trigger+Plugin), see
   [Migration](Migration#migrating-to-138)
 * Passing parameters to jobs has been changed, see [Migration](Migration#migrating-to-138)
 * Support for older versions of the
   [Parameterized Trigger Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Trigger+Plugin) is
   deprecated, see [Migration](Migration#migrating-to-138)
 * Support for the older versions of the
   [GitHub Pull Request Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin)
   is deprecated, see [Migration](Migration#migrating-to-138)
 * Increased the minimum supported Jenkins version to 1.580
 * Increased the minimum required version of the [Config File Provider
   Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Config+File+Provider+Plugin)
   ([JENKINS-30013](https://issues.jenkins-ci.org/browse/JENKINS-30013))
 * Added support for the [Ivy Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Ivy+Plugin)
   ([JENKINS-29910](https://issues.jenkins-ci.org/browse/JENKINS-29910))
 * Removed anything that has been deprecated in 1.30, see [Migration](Migration#migrating-to-130)
* 1.37 (August 08 2015)
 * Added support for the [Clang Scan Build Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Clang+Scan-Build+Plugin)
   ([JENKINS-29505](https://issues.jenkins-ci.org/browse/JENKINS-29505))
 * Enhanced support for the [Groovy Postbuild Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Groovy+Postbuild+Plugin)
   ([JENKINS-29500](https://issues.jenkins-ci.org/browse/JENKINS-29500))
 * Enhanced support for the [Multijob Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Multijob+Plugin)
 * Enhanced support for the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
   ([JENKINS-29347](https://issues.jenkins-ci.org/browse/JENKINS-29347))
 * Fixed problem with methods in build scripts
   ([JENKINS-29862](https://issues.jenkins-ci.org/browse/JENKINS-29862))
 * Support for older versions of the [Multijob Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Multijob+Plugin)
   is deprecated, see [Migration](Migration#migrating-to-137)
 * Support for older versions of the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
   is deprecated, see [Migration](Migration#migrating-to-137)
 * Support for older versions of the [Groovy Postbuild Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Groovy+Postbuild+Plugin)
   is deprecated, see [Migration](Migration#migrating-to-137)
* 1.36 (August 06 2015)
 * Added support for the [Compress Build Log Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Compress+Build+Log+Plugin)
 * Added support for the [Active Choices Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Active+Choices+Plugin)
 * Enhanced support for the [xUnit Plugin](https://wiki.jenkins-ci.org/display/JENKINS/xUnit+Plugin)
   ([JENKINS-29753](https://issues.jenkins-ci.org/browse/JENKINS-29753))
 * Enhanced support for the [Run Condition Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Run+Condition+Plugin)
   ([JENKINS-29503](https://issues.jenkins-ci.org/browse/JENKINS-29503))
 * Added support for the [Slack Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Slack+Plugin)
 * Added support for the [Heavy Job Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Heavy+Job+Plugin)
   ([JENKINS-29499](https://issues.jenkins-ci.org/browse/JENKINS-29499))
 * Enhanced support for the [Folders Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Folders+Plugin)
   ([JENKINS-29498](https://issues.jenkins-ci.org/browse/JENKINS-29498))
 * Enhanced support for the [Debian Package Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Debian+Package+Builder+Plugin)
   ([JENKINS-29502](https://issues.jenkins-ci.org/browse/JENKINS-29502))
 * Enhanced support for the [Build Blocker Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Blocker+Plugin)
   ([JENKINS-29629](https://issues.jenkins-ci.org/browse/JENKINS-29629))
 * Enhanced support for the [Matrix Project Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Matrix+Project+Plugin)
   ([JENKINS-29375](https://issues.jenkins-ci.org/browse/JENKINS-29375))
 * Removed automatic IDE support for IDEA, must be provided separately, see [[IDE Support]]
   ([JENKINS-29668](https://issues.jenkins-ci.org/browse/JENKINS-29668),
   [JENKINS-29669](https://issues.jenkins-ci.org/browse/JENKINS-29669))
 * Improved error logging
   ([JENKINS-16354](https://issues.jenkins-ci.org/browse/JENKINS-16354))
 * Fixed support for the [Parameterized Trigger Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Trigger+Plugin)
   ([JENKINS-24851](https://issues.jenkins-ci.org/browse/JENKINS-24851))
 * Support for the older versions of the [Build Blocker Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Blocker+Plugin)
   is deprecated, see [Migration](Migration#migrating-to-136)
 * Support for arbitrary script names has been deprecated, see [Migration](Migration#migrating-to-136)
 * Some methods in `AbstractJobManagement` have been deprecated and the exception handling has changed, see
   [Migration](Migration#migrating-to-136)
 * The `getCredentialsId` method in `JobManagement` has been deprecated, see [Migration](Migration#migrating-to-136)
 * Removed support for the `@Grab` and `@Grapes` annotations, see [[Migration|Migration#migrating-to-129]]
 * Removed anything that has been deprecated in 1.29, see [Migration](Migration#migrating-to-129)
* 1.35 (July 01 2015)
 * Added support for the [Build Flow Test Aggregator Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Flow+Test+Aggregator+Plugin)
   ([JENKINS-28851](https://issues.jenkins-ci.org/browse/JENKINS-28851))
 * Added support for the [Join Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Join+Plugin)
   ([JENKINS-28985](https://issues.jenkins-ci.org/browse/JENKINS-28985))
 * Added support for the [Fail The Build Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Fail+The+Build+Plugin)
 * Added closure method for logRotator
 * Allow to extend the trigger context
   ([JENKINS-28562](https://issues.jenkins-ci.org/browse/JENKINS-28562))
 * Enhanced DSL support for the Job DSL plugin
 * Fixed problem when deleting views from already deleted folder
   ([JENKINS-28458](https://issues.jenkins-ci.org/browse/JENKINS-28458))
 * Fixed [Parameterized Trigger Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Trigger+Plugin) build step
   ([JENKINS-28353](https://issues.jenkins-ci.org/browse/JENKINS-28353))
 * Fixed problem with implementing the extension point 
   ([JENKINS-28408](https://issues.jenkins-ci.org/browse/JENKINS-28408))
 * Provide better error message when trying to move a job into a non-existing folder 
   ([JENKINS-29100](https://issues.jenkins-ci.org/browse/JENKINS-29100))
 * Fixed problem that caused a changing order of elements not to trigger a job update
   ([JENKINS-29107](https://issues.jenkins-ci.org/browse/JENKINS-29107))
 * Fixed support for multi-job phases in conditional build steps
 * Introduced nested steps context for conditional build steps and deprecated direct use of build steps in conditional
   build steps, see [[Migration]]
 * Enhanced support for the [HTML Publisher Plugin](https://wiki.jenkins-ci.org/display/JENKINS/HTML+Publisher+Plugin)
   ([JENKINS-28564](https://issues.jenkins-ci.org/browse/JENKINS-28564))
 * Enhanced support for the [Config File Provider Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Config+File+Provider+Plugin)
 * Enhanced support for the [GitHub Pull Request Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin)
 * Enhanced support for the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
   ([JENKINS-28405](https://issues.jenkins-ci.org/browse/JENKINS-28405))
 * Enhanced support for [Matrix Authorization Strategy Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Matrix+Authorization+Strategy+Plugin)
   ([JENKINS-27320](https://issues.jenkins-ci.org/browse/JENKINS-27320))
 * Removed unnecessary update of Jenkins project dependency graph
 * Enhanced support for [Maven Project Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Maven+Project+Plugin)
   ([JENKINS-29070](https://issues.jenkins-ci.org/browse/JENKINS-29070),
   [JENKINS-29110](https://issues.jenkins-ci.org/browse/JENKINS-29110))
 * Added support for the latest version of the [S3 Plugin](https://wiki.jenkins-ci.org/display/JENKINS/S3+Plugin)
   ([JENKINS-26561](https://issues.jenkins-ci.org/browse/JENKINS-26561))
 * Added documentation for [[IDE Support]]
 * Added documentation about [logging](Job-DSL-Commands#logging)
 * Write output files for `FileJobManagment` to the same directory as the input files
 * Fixed `FileJobManagement` to create missing folders
   ([JENKINS-27124](https://issues.jenkins-ci.org/browse/JENKINS-27124))
 * Support for the older versions of the [Matrix Authorization Strategy Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Matrix+Authorization+Strategy+Plugin) is deprecated, see [[Migration]]
 * Support for the older versions of the [S3 Plugin](https://wiki.jenkins-ci.org/display/JENKINS/S3+Plugin) is deprecated, see [[Migration]]
 * Support for the older versions of the [GitHub Pull Request Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin) is deprecated, see [[Migration]]
 * Support for the older versions of the [Maven Project Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Maven+Project+Plugin) is deprecated, see [[Migration]]
 * Removed anything that has been deprecated in 1.27, see [Migration](Migration#migrating-to-127)
   ([JENKINS-27492](https://issues.jenkins-ci.org/browse/JENKINS-27492))
 * Removed anything that has been deprecated in 1.28, see [Migration](Migration#migrating-to-128)
* 1.34 (May 08 2015)
 * Enhanced support for the [Publish Over SSH Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Publish+Over+SSH+Plugin)
   ([JENKINS-26636](https://issues.jenkins-ci.org/browse/JENKINS-26636))
 * Fixed XML encoding issue when using a single conditional build step
   ([JENKINS-28308](https://issues.jenkins-ci.org/browse/JENKINS-28308))
 * Fixed issue with multiple (script) parameters for Groovy build steps
   ([JENKINS-28310](https://issues.jenkins-ci.org/browse/JENKINS-28310))
 * Deprecated an undocumented variant of the `runner` method in `conditionalSteps` context, see [[Migration]]
* 1.33 (May 07 2015)
 * Enhanced support for the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
   ([JENKINS-27891](https://issues.jenkins-ci.org/browse/JENKINS-27891), [JENKINS-28264](https://issues.jenkins-ci.org/browse/JENKINS-28264))
 * Added support for the [Build Publisher Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Publisher+Plugin)
 * Added support for the [Naginator Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Naginator+Plugin)
 * Added support for the [Sidebar-Link Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Sidebar-Link+Plugin)
 * Added support for the [Custom Job Icon Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Custom+Job+Icon+Plugin)
 * Added support for "Build after other projects are built" trigger
 * Added more options for `archiveArtifacts`
 * The `latestOnly` option of `archiveArtifacts` is deprecated, see [[Migration]]
 * Enhanced support for the [Robot Framework Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Robot+Framework+Plugin)
 * Enhanced support for [RunDeck Plugin](https://wiki.jenkins-ci.org/display/JENKINS/RunDeck+Plugin)
 * Enhanced support for the [Mercurial Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Mercurial+Plugin)
 * Support for the older versions of the [Mercurial Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Mercurial+Plugin) is deprecated, see [[Migration]]
 * Enhanced support for the [Flexible Publish Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Flexible+Publish+Plugin)
 * Support for the older versions of the [Flexible Publish Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Flexible+Publish+Plugin) is deprecated, see [[Migration]]
 * Enhanced support for the [Copy Artifact Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Copy+Artifact+Plugin)
   ([JENKINS-27838](https://issues.jenkins-ci.org/browse/JENKINS-27838), [JENKINS-27894](https://issues.jenkins-ci.org/browse/JENKINS-27894))
 * Enhanced support for the [GitHub Pull Request Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin)
   ([JENKINS-27932](https://issues.jenkins-ci.org/browse/JENKINS-27932))
 * Support for the older versions of the [Copy Artifact Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Copy+Artifact+Plugin) is deprecated, see [[Migration]]
 * Enhanced support for the [Robot Framework Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Robot+Framework+Plugin)
 * Support for the older versions of the [Robot Framework Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Robot+Framework+Plugin) is deprecated, see [[Migration]]
 * Variants of `copyArtifacts` with more than two parameters have been replaced and are deprecated, see [[Migration]]
 * Added a Jenkins extension point for adding DSL methods
 * Added support for [HipChat Plugin](https://wiki.jenkins-ci.org/display/JENKINS/HipChat+Plugin)
 * Added support for uploading [user conent](https://wiki.jenkins-ci.org/display/JENKINS/User+Content)
 * Increased the minimum supported Jenkins version to 1.565
   ([JENKINS-28167](https://issues.jenkins-ci.org/browse/JENKINS-28167))
* 1.32 (April 07 2015)
 * Added support for [PowerShell Plugin](https://wiki.jenkins-ci.org/display/JENKINS/PowerShell+Plugin)
   ([JENKINS-27820](https://issues.jenkins-ci.org/browse/JENKINS-27820))
 * Fixed problem with publishers in Maven jobs
   ([JENKINS-27767](https://issues.jenkins-ci.org/browse/JENKINS-27767))
* 1.31 (April 04 2015)
 * Added support for [Categorized Jobs View](https://wiki.jenkins-ci.org/display/JENKINS/Categorized+Jobs+View)
 * Added support for [Build Node Column Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Node+Column+Plugin)
 * Added support for [Pre-SCM Build Step Plugin](https://wiki.jenkins-ci.org/display/JENKINS/pre-scm-buildstep)
 * Added support for [Sonar Plugin](http://docs.sonarqube.org/display/SONAR/Jenkins+Plugin)
 * Added support for [Debian Package Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Debian+Package+Builder+Plugin)
 * Added support for [Plot Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Plot+Plugin)
 * Added support for [Git Parameter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Parameter+Plugin)
 * Added `recurse` option for list views
 * Added `ignorePostCommitHooks` option for SCM trigger
 * Added `commentFilePath` option for [GitHub Pull Request Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin)
 * Added "Configure Project" column for [Extra Columns Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Extra+Columns+Plugin)
 * Added support for [PostBuildScript Plugin](https://wiki.jenkins-ci.org/display/JENKINS/PostBuildScript+Plugin)
 * Added support for [Xvfb Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Xvfb+Plugin)
 * Enhanced support for the [Credentials Binding Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Credentials+Binding+Plugin)
 * Enhanced support for the [Multijob Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Multijob+Plugin)
 * Enhanced support for the [NodeJS Plugin](https://wiki.jenkins-ci.org/display/JENKINS/NodeJS+Plugin)
 * Enhanced support for the [Maven Project Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Maven+Project+Plugin)
 * Enhanced support for the [Description Setter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Description+Setter+Plugin)
 * Enhanced support for ([Cloudbees Folders Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Folders+Plugin))
 * Support all available permissions ([JENKINS-16365](https://issues.jenkins-ci.org/browse/JENKINS-16365))
 * Deprecated the Permissions enum, see [[Migration]]
 * The `tagFilterRegex` argument of `listTagsParam` can be null or empty
 * The enum argument of `localRepository` for the Maven job and context has changed, see [[Migration]]
 * Support for the older versions of the [Multijob Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Multijob+Plugin) is deprecated, see [[Migration]]
 * The views closure of the nested view type has been changed, see [[Migration]]
 * Removed anything that has been deprecated in 1.26, see [Migration](Migration#migrating-to-126)
* 1.30 (March 08 2015)
 * Added support for [Custom Tools Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Custom+Tools+Plugin)
 * Added support for [Flaky Test Handler Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Flaky+Test+Handler+Plugin)
 * Added configure block to the Multijob job context
 * Added strategy option for the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
 * Added strategy build chooser for the [Gerrit Trigger](https://wiki.jenkins-ci.org/display/JENKINS/Gerrit+Trigger)
 * Enhanced support for the [Subversion Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Subversion+Plugin)
 * Added `abortBuild` action for the [Build Timeout](https://wiki.jenkins-ci.org/display/JENKINS/Build-timeout+Plugin)
 * Deprecated the `failBuild` action with a boolean parameter for the [Build Timeout](https://wiki.jenkins-ci.org/display/JENKINS/Build-timeout+Plugin), see [[Migration]]
 * Deprecated the `javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext.Timeout` enum, see [[Migration]]
 * Added support for label parameters from the [NodeLabel Parameter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/NodeLabel+Parameter+Plugin)
 * Added populateToolInstallations and overrideBuildVariables options for the [EnvInject Plugin](https://wiki.jenkins-ci.org/display/JENKINS/EnvInject+Plugin)
 * Added groovy option in the wrappers context for the [EnvInject Plugin](https://wiki.jenkins-ci.org/display/JENKINS/EnvInject+Plugin)
 * Fixed the `notifySuspects` option for the [Jabber Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Jabber+Plugin)
 * Fixed `extendedEmail` configure block resolve strategy ([JENKINS-27063](https://issues.jenkins-ci.org/browse/JENKINS-27063))
 * Deprecated some overloaded DSL methods for the [Jabber Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Jabber+Plugin), see [[Migration]]
 * Introduced new factory methods and deprecated the generic factory and `name` methods, see [[Migration]]
 * Finding credentials by description is deprecated, see [[Migration]]
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
