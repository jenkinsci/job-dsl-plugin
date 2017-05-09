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

Job DSL provides a secure environment for executing DSL scripts. See [[Script Security]] for details.

Not all of the 1000+ Jenkins plugins are supported by the built-in DSL. If the
[API Viewer](https://jenkinsci.github.io/job-dsl-plugin/) does not list support for a certain plugin, the
[[Automatically Generated DSL]] can be used to fill the gap.

If you want to get fancy you'll want to read up on [[configure block|The Configure Block]], which gives you direct access to the `config.xml`.

Have a look at the [Jenkins Job DSL Gradle example](https://github.com/sheehan/job-dsl-gradle-example) to see how to
organize a SCM repository for Job DSL scripts, including [[tests for DSL scripts|Testing DSL Scripts]] and
[[IDE Support]].

Browse the Jenkins issue tracker to see any [open issues](https://issues.jenkins-ci.org/issues/?filter=15140).

## Release Notes
* 1.64 (unreleased)
  * Enhanced support for the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
    ([#1029](https://github.com/jenkinsci/job-dsl-plugin/pull/1029))
* 1.63 (May 09 2017)
  * Fixed problem with special characters in job, folder and view names
    ([JENKINS-44140](https://issues.jenkins-ci.org/browse/JENKINS-44140))
* 1.62 (May 09 2017)
  * Show enum values for generated DSL in embedded API viewer
    [#1020](https://github.com/jenkinsci/job-dsl-plugin/pull/1020)
  * Added option to remove unreferenced config files
    ([JENKINS-40720](https://issues.jenkins-ci.org/browse/JENKINS-40720))
  * Enhanced support for the
    [Pipeline Multibranch Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Pipeline+Multibranch+Plugin)
    ([JENKINS-43693](https://issues.jenkins-ci.org/browse/JENKINS-43693))
  * Enhanced support for the [Parameterized Remote Trigger
    Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Remote+Trigger+Plugin)
    ([JENKINS-43025](https://issues.jenkins-ci.org/browse/JENKINS-43025))
  * Fixed problem with absolute paths when using "Seed Job" lookup strategy
    ([JENKINS-43991](https://issues.jenkins-ci.org/browse/JENKINS-43991))
  * Improved error handling and fail if an item or view could not be created or updated
    ([JENKINS-43991](https://issues.jenkins-ci.org/browse/JENKINS-43991))
  * Allow `..` path segments in job, folder and view names.
    ([JENKINS-40732](https://issues.jenkins-ci.org/browse/JENKINS-40732))
  * Added a switch to the command line runner to put the script's directory on the classpath, see
    [User-Power-Moves](User-Power-Moves#run-a-dsl-script-locally)
    ([JENKINS-42299](https://issues.jenkins-ci.org/browse/JENKINS-42299))
  * Support for the older versions of the [Parameterized Remote Trigger
    Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Remote+Trigger+Plugin) is deprecated, see
    [Migration](Migration#migrating-to-162)
  * Support for the [Build Flow Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Flow+Plugin) is deprecated,
    see [Migration](Migration#migrating-to-162)
  * Support for the [Active Choices Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Active+Choices+Plugin) is
    deprecated, see [Migration](Migration#migrating-to-162)
  * Support for the [PostBuildScript Plugin](https://wiki.jenkins-ci.org/display/JENKINS/PostBuildScript+Plugin) is
    deprecated, see [Migration](Migration#migrating-to-162)
  * Support for the [ArtifactDeployer Plugin](https://wiki.jenkins-ci.org/display/JENKINS/ArtifactDeployer+Plugin) is
    deprecated, see [Migration](Migration#migrating-to-162)
  * Support for the [Subversion Tagging Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Subversion+Tagging+Plugin)
    is deprecated, see [Migration](Migration#migrating-to-162)
  * Support for the [Grails Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Grails+Plugin) is deprecated, see
    [Migration](Migration#migrating-to-162)
  * Removed anything that has been deprecated in 1.53, see [Migration](Migration#migrating-to-153)
* 1.61 (April 17 2017)
  * Enhanced support for the [Stash Notifier Plugin](https://wiki.jenkins-ci.org/display/JENKINS/StashNotifier+Plugin)
    ([JENKINS-42900](https://issues.jenkins-ci.org/browse/JENKINS-42900),
    [JENKINS-29183](https://issues.jenkins-ci.org/browse/JENKINS-29183))
  * Enhanced support for the [Join Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Join+Plugin)
    ([JENKINS-43219](https://issues.jenkins-ci.org/browse/JENKINS-43219))
  * Fixed a problem with `readFileFromWorkspace`
    ([JENKINS-43537](https://issues.jenkins-ci.org/browse/JENKINS-43537))
  * Do not print a warning when config file name equals config file identifier
    ([JENKINS-43345](https://issues.jenkins-ci.org/browse/JENKINS-43345))
  * Support for the older versions of the [Join Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Join+Plugin) is
    deprecated, see [Migration](Migration#migrating-to-161)
  * Support for the older versions of the
    [Stash Notifier Plugin](https://wiki.jenkins-ci.org/display/JENKINS/StashNotifier+Plugin) is deprecated, see
    [Migration](Migration#migrating-to-161)
  * Removed anything that has been deprecated in 1.52, see [Migration](Migration#migrating-to-152)
* 1.60 (April 10 2017)
  * Enabled script approval with the
    [Script Security Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Script+Security+Plugin), see
    [Migration](Migration#migrating-to-160)
    ([SECURITY-369](https://issues.jenkins-ci.org/browse/SECURITY-369))
  * Added permission checks and enforced running the build as a particular user, see
    [Migration](Migration#migrating-to-160)
    ([SECURITY-363](https://issues.jenkins-ci.org/browse/SECURITY-363))
* 1.59 (March 31 2017)
  * Enable [[Automatically Generated DSL]] for
    [Parameterized Trigger Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Trigger+Plugin)
    ([JENKINS-41741](https://issues.jenkins-ci.org/browse/JENKINS-41741))
  * Log deprecation warnings for [[Automatically Generated DSL]] and DSL extensions
  * Updated the troubleshooting section for [[The Configure Block]]
    ([JENKINS-41958](https://issues.jenkins-ci.org/browse/JENKINS-41958))
  * Updated [Structs Plugin](https://github.com/jenkinsci/structs-plugin) dependency to version 1.6
  * Support for the [Mattermost Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Mattermost+Plugin) is
    deprecated, see [Migration](Migration#migrating-to-159)
    ([JENKINS-42887](https://issues.jenkins-ci.org/browse/JENKINS-42887))
  * Removed anything that has been deprecated in 1.51, see [Migration](Migration#migrating-to-151)
  * Removed anything that has been deprecated in 1.47, see [Migration](Migration#migrating-to-147)
* 1.58 (February 16 2017)
  * Increased the minimum supported Jenkins version to 1.651
  * Added support for the
    [GitHub Branch Source Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+Branch+Source+Plugin)
    ([JENKINS-39977](https://issues.jenkins-ci.org/browse/JENKINS-39977))
  * Enhanced support for
    [Config File Provider Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Config+File+Provider+Plugin)
    ([JENKINS-33630](https://issues.jenkins-ci.org/browse/JENKINS-33630),
    [JENKINS-39754](https://issues.jenkins-ci.org/browse/JENKINS-39754),
    [JENKINS-40719](https://issues.jenkins-ci.org/browse/JENKINS-40719))
  * Enhanced support for the [GitLab Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitLab+Plugin)
    ([JENKINS-41485](https://issues.jenkins-ci.org/browse/JENKINS-41485),
    [JENKINS-41789](https://issues.jenkins-ci.org/browse/JENKINS-41789))
  * Fixed a problem with the plugin's dependencies
   ([JENKINS-41001](https://issues.jenkins-ci.org/browse/JENKINS-41001))
  * Fixed a problem with Windows paths on Unix
   ([JENKINS-41612](https://issues.jenkins-ci.org/browse/JENKINS-41612))
  * Improved error message for invalid enum values
   ([JENKINS-41270](https://issues.jenkins-ci.org/browse/JENKINS-41270))
  * Show only one link to embedded API viewer
   ([JENKINS-41083](https://issues.jenkins-ci.org/browse/JENKINS-41083))
  * The syntax for creating config files is changing, see [Migration](Migration#migrating-to-158)
  * Most classes and related methods for creating config files are deprecated, see
    [Migration](Migration#migrating-to-158)
  * Support for the older versions of the [GitLab Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitLab+Plugin) is
    deprecated, see [Migration](Migration#migrating-to-158)
  * Removed anything that has been deprecated in 1.49, see [Migration](Migration#migrating-to-149)
* 1.57 (January 15 2017)
  * Updated optional
    [Config File Provider Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Config+File+Provider+Plugin) dependency to
    version 2.15.4
    ([JENKINS-40943](https://issues.jenkins-ci.org/browse/JENKINS-40943))
  * Allow `DashboardPortletContext` to be extended
    ([#981](https://github.com/jenkinsci/job-dsl-plugin/pull/981))
  * Show more available method signatures in embedded API viewer
    ([#982](https://github.com/jenkinsci/job-dsl-plugin/pull/982))
  * Fixed compatibility issue with [Ruby Runtime Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Ruby+Runtime+Plugin)
    ([JENKINS-39193](https://issues.jenkins-ci.org/browse/JENKINS-39193),
    [JENKINS-39807](https://issues.jenkins-ci.org/browse/JENKINS-39807))
  * Version 0.12 of the [Ruby Runtime Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Ruby+Runtime+Plugin) is no
    longer deprecated since 0.13 has several issues
    ([JENKINS-37353](https://issues.jenkins-ci.org/browse/JENKINS-37353),
    [JENKINS-37771](https://issues.jenkins-ci.org/browse/JENKINS-37771),
    [JENKINS-38145](https://issues.jenkins-ci.org/browse/JENKINS-38145))
  * Support for the older versions of the [Rbenv Plugin](https://wiki.jenkins-ci.org/display/JENKINS/rbenv+plugin) is
    deprecated, see [Migration](Migration#migrating-to-157)
* 1.56 (January 06 2017)
  * Fixed support for
    [Config File Provider Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Config+File+Provider+Plugin)
    ([JENKINS-40797](https://issues.jenkins-ci.org/browse/JENKINS-40797))
  * Enhanced support for the [Extra Columns Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Extra+Columns+Plugin)
    ([#978](https://github.com/jenkinsci/job-dsl-plugin/pull/978))
  * Enhanced support for the [SSH Agent Plugin](https://wiki.jenkins-ci.org/display/JENKINS/SSH+Agent+Plugin)
    ([#980](https://github.com/jenkinsci/job-dsl-plugin/pull/980))
  * Support for the older versions of the
    [SSH Agent Plugin](https://wiki.jenkins-ci.org/display/JENKINS/SSH+Agent+Plugin) is deprecated,
    see [Migration](Migration#migrating-to-156)
* 1.55 (January 03 2017)
  * Updated optional
    [Config File Provider Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Config+File+Provider+Plugin) dependency to
    version 2.15
    ([JENKINS-39769](https://issues.jenkins-ci.org/browse/JENKINS-39769))
  * Updated optional [Managed Scripts Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Managed+Script+Plugin)
    dependency to version 1.3
  * Use Groovy Shell instead of Groovy Script Engine to run DSL scripts, see [Migration](Migration#migrating-to-155)
    ([#976](https://github.com/jenkinsci/job-dsl-plugin/pull/976))
* 1.54 (December 24 2016)
  * Enhanced support for the [S3 Plugin](https://wiki.jenkins-ci.org/display/JENKINS/S3+Plugin)
    ([#953](https://github.com/jenkinsci/job-dsl-plugin/pull/953))
  * Enhanced support for the
    [Delivery Pipeline Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Delivery+Pipeline+Plugin)
    ([#956](https://github.com/jenkinsci/job-dsl-plugin/pull/956))
  * Enhanced support for the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
    ([#958](https://github.com/jenkinsci/job-dsl-plugin/pull/958))
  * Enhanced support for the
    [GitHub Branch Source Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+Branch+Source+Plugin)
    ([#969](https://github.com/jenkinsci/job-dsl-plugin/pull/969))
  * Enhanced support for the
    [PostBuildScript Plugin](https://wiki.jenkins-ci.org/display/JENKINS/PostBuildScript+Plugin)
    ([#973](https://github.com/jenkinsci/job-dsl-plugin/pull/973))
  * Enhanced support for the Maven build step
    ([JENKINS-40636](https://issues.jenkins-ci.org/browse/JENKINS-40636))
  * Changed embedded API Viewer to work without Internet connectivity
    ([JENKINS-40205](https://issues.jenkins-ci.org/browse/JENKINS-40205))
  * Improved error message when a plugin is missing
    ([JENKINS-40601](https://issues.jenkins-ci.org/browse/JENKINS-40601))
  * The short URL for the embedded API Viewer (http://localhost:8080/plugin/job-dsl/api-viewer) is deprecated, see
    [Migration](Migration#migrating-to-154)
  * Fixed problem with
    [GitHub Branch Source Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+Branch+Source+Plugin)
    ([JENKINS-40191](https://issues.jenkins-ci.org/browse/JENKINS-40191))
  * Added a workaround for a problem with XStream conversion
    ([JENKINS-40130](https://issues.jenkins-ci.org/browse/JENKINS-40130))
  * Support for the [S3 Plugin](https://wiki.jenkins-ci.org/display/JENKINS/S3+Plugin) is deprecated, see
    [Migration](Migration#migrating-to-154)
  * Support for the older versions of the
    [Delivery Pipeline Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Delivery+Pipeline+Plugin) is deprecated, see
    [Migration](Migration#migrating-to-154)
  * Support for the older versions of the
    [GitHub Branch Source Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+Branch+Source+Plugin) is deprecated,
    see [Migration](Migration#migrating-to-154)
  * The class `javaposse.jobdsl.plugin.JobDslPlugin` is deprecated, see [Migration](Migration#migrating-to-154)
  * Removed most things that have been deprecated in 1.47, see [Migration](Migration#migrating-to-147)
  * Removed anything that has been deprecated in 1.48, see [Migration](Migration#migrating-to-148)
* 1.53 (November 08 2016)
  * Enhanced support the
    [Config File Provider Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Config+File+Provider+Plugin)
    ([JENKINS-38637](https://issues.jenkins-ci.org/browse/JENKINS-38637))
  * Enhanced support for the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
    ([JENKINS-39558](https://issues.jenkins-ci.org/browse/JENKINS-39558))
  * Allow `GString` as argument type for Automatically Generated DSL
    ([JENKINS-39153](https://issues.jenkins-ci.org/browse/JENKINS-39153))
  * The XML configuration for jobs and folders will only be generated once
    ([JENKINS-39417](https://issues.jenkins-ci.org/browse/JENKINS-39417))
  * Fixed a problem with relative job names
    ([JENKINS-39137](https://issues.jenkins-ci.org/browse/JENKINS-39137))
  * Added "Next Launch" and "Next Possible Launch" columns for
    [Next Executions Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Next+Executions)
    ([#940](https://github.com/jenkinsci/job-dsl-plugin/pull/940))
  * Overriding job, folder or view names is deprecated, see [Migration](Migration#migrating-to-153)
  * Deprecated unnecessary option in GitHub branch source context
    ([JENKINS-39146](https://issues.jenkins-ci.org/browse/JENKINS-39146))
* 1.52 (October 17 2016)
  * Increased the minimum supported Jenkins version to 1.642
  * Enhanced support for the [Exclusion Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Exclusion-Plugin)
    ([JENKINS-36683](https://issues.jenkins-ci.org/browse/JENKINS-36683))
  * Enhanced support for the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
    ([#930](https://github.com/jenkinsci/job-dsl-plugin/pull/930))
  * Enhanced support for the [Multijob Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Multijob+Plugin)
    ([#929](https://github.com/jenkinsci/job-dsl-plugin/pull/929))
  * Enhanced support for the [JUnit Plugin](https://wiki.jenkins-ci.org/display/JENKINS/JUnit+Plugin)
    ([#928](https://github.com/jenkinsci/job-dsl-plugin/pull/928))
  * Added an option to mark a seed job build as failed if a plugin must be installed or updated to support all feature
    used in DSL scripts
    ([JENKINS-37417](https://issues.jenkins-ci.org/browse/JENKINS-37417))
  * Added an option to mark a seed job build as unstable when using deprecated features
    ([JENKINS-37418](https://issues.jenkins-ci.org/browse/JENKINS-37418))
  * Allow `CredentialsBindingContext` to be extended
    ([#920](https://github.com/jenkinsci/job-dsl-plugin/pull/920))
  * Fixed problem with embedded API viewer
    ([JENKINS-38456](https://issues.jenkins-ci.org/browse/JENKINS-38456),
    [JENKINS-38964](https://issues.jenkins-ci.org/browse/JENKINS-38964))
  * Sort generated items by name on the seed job's summary and build pages
    ([JENKINS-38648](https://issues.jenkins-ci.org/browse/JENKINS-38648))
  * Support for the older versions of the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin) is
    deprecated, see [Migration](Migration#migrating-to-152)
  * Support for the older versions of the [Multijob Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Multijob+Plugin)
    is deprecated, see [Migration](Migration#migrating-to-152)
  * Support for the older versions of the
    [Exclusion Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Exclusion-Plugin) is deprecated, see
    [Migration](Migration#migrating-to-152)
  * Support for the [RunDeck Plugin](https://wiki.jenkins-ci.org/display/JENKINS/RunDeck+Plugin) is deprecated, see
    [Migration](Migration#migrating-to-152)
  * Removed anything that has been deprecated in 1.45, see [Migration](Migration#migrating-to-145)
* 1.51 (September 13 2016)
  * Enhanced support for the [RunDeck Plugin](https://wiki.jenkins-ci.org/display/JENKINS/RunDeck+Plugin)
    ([#885](https://github.com/jenkinsci/job-dsl-plugin/pull/885))
  * Fixed compatibility issue with [Ruby Runtime Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Ruby+Runtime+Plugin)
    ([JENKINS-37422](https://issues.jenkins-ci.org/browse/JENKINS-37422))
  * Fixed problem with embedded API viewer when no default Update Center is used
    ([JENKINS-37103](https://issues.jenkins-ci.org/browse/JENKINS-37103))
  * Fixed problem with whitespace in additional classpath
    ([#911](https://github.com/jenkinsci/job-dsl-plugin/pull/911))
  * Improved performance for embedded API viewer with caching, compression and conditional GET requests
  * Support for the older versions of the [RVM Plugin](https://wiki.jenkins-ci.org/display/JENKINS/RVM+Plugin) is
    deprecated, see [Migration](Migration#migrating-to-151)
  * Support for the older versions of the
    [Ruby Runtime Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Ruby+Runtime+Plugin) is deprecated, see
    [Migration](Migration#migrating-to-151)
  * Support for the older versions of the [RunDeck Plugin](https://wiki.jenkins-ci.org/display/JENKINS/RunDeck+Plugin) is
    deprecated, see [Migration](Migration#migrating-to-151)
  * Removed anything that has been deprecated in 1.44, see [Migration](Migration#migrating-to-144)
* 1.50 (August 17 2016)
  * Fixed regression when updating views
    ([JENKINS-37450](https://issues.jenkins-ci.org/browse/JENKINS-37450))
  * Removed unnecessary transitive dependencies to `xmlpull:xmlpull` and `xpp3:xpp3_min`
* 1.49 (August 16 2016)
  * Enhanced support for the [Sauce OnDemand Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Sauce+OnDemand+Plugin)
    ([JENKINS-36370](https://issues.jenkins-ci.org/browse/JENKINS-36370))
  * Enhanced support for the [HTTP Request Plugin](https://wiki.jenkins-ci.org/display/JENKINS/HTTP+Request+Plugin)
    ([#879](https://github.com/jenkinsci/job-dsl-plugin/pull/879))
  * Enhanced support for the [Gradle Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Gradle+Plugin)
    ([JENKINS-33093](https://issues.jenkins-ci.org/browse/JENKINS-33093))
  * Enhanced support for the [Extra Columns Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Extra+Columns+Plugin)
    ([#882](https://github.com/jenkinsci/job-dsl-plugin/pull/882))
  * Enhanced support for the [Groovy Postbuild Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Groovy+Postbuild+Plugin)
    ([#884](https://github.com/jenkinsci/job-dsl-plugin/pull/884))
  * Allow extensions for job view filters
    ([#896](https://github.com/jenkinsci/job-dsl-plugin/pull/896))
  * Updated [Structs Plugin](https://github.com/jenkinsci/structs-plugin) dependency to version 1.2
  * Improved support for [[Automatically Generated DSL]]: print deprecation warnings and show deprecated methods in API
    viewer
  * Added symbol to allow nicer pipeline syntax, see
    [Use Job DSL in Pipeline scripts](User-Power-Moves#use-job-dsl-in-pipeline-scripts)
    ([JENKINS-36768](https://issues.jenkins-ci.org/browse/JENKINS-36768))
  * Improved script processing performance
    ([JENKINS-37138](https://issues.jenkins-ci.org/browse/JENKINS-37138))
  * Added a `SEED_JOB` script variable which provides access to the seed job,
    see [Job DSL Commands](Job-DSL-Commands#seed-job)
  * Fixed issues with embedded API Viewer
    ([#886](https://github.com/jenkinsci/job-dsl-plugin/pull/886))
  * Fixed compatibility with Pipeline Snippet Generator
    ([JENKINS-36502](https://issues.jenkins-ci.org/browse/JENKINS-36502))
  * Deprecated the `scriptLocation` property of the `ExecuteDslScripts` build step, see
    [Migration](Migration#migrating-to-149)
  * Improved error message when trying to change the type of a view
    ([#888](https://github.com/jenkinsci/job-dsl-plugin/pull/888))
  * Removed anything that has been deprecated in 1.42, see [Migration](Migration#migrating-to-142)
  * Removed anything that has been deprecated in 1.43, see [Migration](Migration#migrating-to-143)
* 1.48 (June 24 2016)
  * Added option to ignore missing DSL script files or empty wildcards
    ([JENKINS-34060](https://issues.jenkins-ci.org/browse/JENKINS-34060))
  * Improved support for the [Build-timeout Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build-timeout+Plugin)
    ([JENKINS-35228](https://issues.jenkins-ci.org/browse/JENKINS-35228))
  * Enhanced support for the
    [Flexible Publish Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Flexible+Publish+Plugin)
    ([JENKINS-34282](https://issues.jenkins-ci.org/browse/JENKINS-34282))
  * Enhanced support for the
    [Throttle Concurrent Builds Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Throttle+Concurrent+Builds+Plugin)
    ([JENKINS-32631](https://issues.jenkins-ci.org/browse/JENKINS-32631))
  * Enhanced support for the [Release Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Release+Plugin)
    ([JENKINS-33341](https://issues.jenkins-ci.org/browse/JENKINS-33341))
  * Enhanced support for the [Subversion Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Subversion+Plugin)
    ([JENKINS-34091](https://issues.jenkins-ci.org/browse/JENKINS-34091))
  * Enhanced support for the
    [Delivery Pipeline Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Delivery+Pipeline+Plugin)
    ([#863](https://github.com/jenkinsci/job-dsl-plugin/pull/863),
    [#872](https://github.com/jenkinsci/job-dsl-plugin/pull/872))
  * Made `ExecuteDslScripts` build step compatible with [Pipeline Plugin](https://github.com/jenkinsci/pipeline-plugin)
    ([JENKINS-35282](https://issues.jenkins-ci.org/browse/JENKINS-35282))
  * Several classes and some constructors have been deprecated, see [Migration](Migration#migrating-to-148)
  * Removed optional dependency to
    [CloudBees Folders Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Folders+Plugin)
  * Removed anything that has been deprecated in 1.41, see [Migration](Migration#migrating-to-141)
* 1.47 (May 24 2016)
  * Improved support for [[Testing DSL Scripts]]
    ([JENKINS-29091](https://issues.jenkins-ci.org/browse/JENKINS-29091))
  * Allow extensions for views
    ([JENKINS-29510](https://issues.jenkins-ci.org/browse/JENKINS-29510))
  * Enhanced support for the
    [CloudBees Folders Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Folders+Plugin)
    ([JENKINS-31488](https://issues.jenkins-ci.org/browse/JENKINS-31488))
  * Enhanced support for the
    [Parameterized Trigger Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Trigger+Plugin)
   ([JENKINS-34552](https://issues.jenkins-ci.org/browse/JENKINS-34552))
  * Enhanced support for the [Branch API Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Branch+API+Plugin)
    ([#846](https://github.com/jenkinsci/job-dsl-plugin/pull/846))
  * Enhanced support for the [Copy Artifact Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Copy+Artifact+Plugin)
    ([JENKINS-34720](https://issues.jenkins-ci.org/browse/JENKINS-34720))
  * Enhanced support for the [Run Condition Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Run+Condition+Plugin)
    ([JENKINS-34941](https://issues.jenkins-ci.org/browse/JENKINS-34941))
  * Enhanced support for the [Gitlab Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitLab+Plugin)
    ([JENKINS-34534](https://issues.jenkins-ci.org/browse/JENKINS-34534))
  * Fixed support for the [Violations Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Violations)
    ([JENKINS-26086](https://issues.jenkins-ci.org/browse/JENKINS-26086))
  * Support for the [Slack Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Slack+Plugin) is deprecated, see
    [Migration](Migration#migrating-to-147)
    ([JENKINS-34124](https://issues.jenkins-ci.org/browse/JENKINS-34124))
  * Support for [HipChat Plugin](https://wiki.jenkins-ci.org/display/JENKINS/HipChat+Plugin) is deprecated, see
    [Migration](Migration#migrating-to-147)
    ([JENKINS-32502](https://issues.jenkins-ci.org/browse/JENKINS-32502))
  * Support for the older versions of the [Gitlab Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitLab+Plugin) is
    deprecated, see [Migration](Migration#migrating-to-147)
  * Added `pipelineJob` and `multibranchPipelineJob` as replacements for `workflowJob` and `multibranchWorkflowJob`, see
    [Migration](Migration#migrating-to-147)
    ([JENKINS-33325](https://issues.jenkins-ci.org/browse/JENKINS-33325))
  * The enum `javaposse.jobdsl.dsl.helpers.step.condition.FileExistsCondition.BaseDir` is deprecated, see
    [Migration](Migration#migrating-to-147)
* 1.46 (May 08 2016)
  * Increased the minimum supported Jenkins version to 1.625
  * Added support for [[Automatically Generated DSL]]
    ([#816](https://github.com/jenkinsci/job-dsl-plugin/pull/816))
  * Integrated the API Viewer into the plugin
    ([#822](https://github.com/jenkinsci/job-dsl-plugin/pull/822))
  * Added a plugin dependency to the [Structs Plugin](https://github.com/jenkinsci/structs-plugin)
  * Added support for the [Maven Info Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Maven+Info+Plugin)
    ([JENKINS-32196](https://issues.jenkins-ci.org/browse/JENKINS-32196))
  * Added support for the [Log Parser Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Log+Parser+Plugin)
    ([JENKINS-33795](https://issues.jenkins-ci.org/browse/JENKINS-33795))
  * Added support for the [Jython Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Jython+Plugin)
    ([JENKINS-33881](https://issues.jenkins-ci.org/browse/JENKINS-33881))
  * Added support for the [Job Exporter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Job+Exporter+Plugin)
    ([JENKINS-33882](https://issues.jenkins-ci.org/browse/JENKINS-33882))
  * Added support for the [Phing Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Phing+Plugin)
    ([JENKINS-33887](https://issues.jenkins-ci.org/browse/JENKINS-33887))
  * Added support for the [MSBuild Plugin](https://wiki.jenkins-ci.org/display/JENKINS/MSBuild+Plugin)
    ([JENKINS-33825](https://issues.jenkins-ci.org/browse/JENKINS-33825))
  * Added support for the
    [CloudBees GitHub Branch Source Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+GitHub+Branch+Source+Plugin)
  * Added support for the [Wall Display Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Wall+Display+Plugin)
    ([#830](https://github.com/jenkinsci/job-dsl-plugin/pull/830))
  * Enhanced support for the [Dashboard View Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Dashboard+View)
    ([#824](https://github.com/jenkinsci/job-dsl-plugin/pull/824))
  * Enhanced support for the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
    ([JENKINS-33967](https://issues.jenkins-ci.org/browse/JENKINS-33967),
    [JENKINS-33968](https://issues.jenkins-ci.org/browse/JENKINS-33968))
  * Enhanced support for the [Copy Artifact Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Copy+Artifact+Plugin)
    ([JENKINS-34360](https://issues.jenkins-ci.org/browse/JENKINS-34360))
  * Enhanced documentation for the [Gerrit Trigger Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Gerrit+Trigger)
    ([JENKINS-30323](https://issues.jenkins-ci.org/browse/JENKINS-30323))
  * Enhanced support for the [JIRA Plugin](https://wiki.jenkins-ci.org/display/JENKINS/JIRA+Plugin)
    ([#834](https://github.com/jenkinsci/job-dsl-plugin/pull/834))
  * Removed anything that has been deprecated in 1.40, see [Migration](Migration#migrating-to-140)
  * Changed the behavior of the `currentJobParameters` method in the `phaseJob` context, see
    [Migration](Migration#migrating-to-146)
    ([#836](https://github.com/jenkinsci/job-dsl-plugin/pull/836))
* 1.45 (April 05 2016)
  * Added support for the [CMake Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CMake+Plugin)
    ([JENKINS-33829](https://issues.jenkins-ci.org/browse/JENKINS-33829))
  * Added support for the [JIRA Plugin](https://wiki.jenkins-ci.org/display/JENKINS/JIRA+Plugin)
    ([JENKINS-31545](https://issues.jenkins-ci.org/browse/JENKINS-31545))
  * Added support for the
    [BuildResultTrigger Plugin](https://wiki.jenkins-ci.org/display/JENKINS/BuildResultTrigger+Plugin)
    ([JENKINS-33463](https://issues.jenkins-ci.org/browse/JENKINS-33463))
  * Enhanced documentation for the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
    ([JENKINS-33482](https://issues.jenkins-ci.org/browse/JENKINS-33482))
  * Enhanced support for the [SonarQube Plugin](https://wiki.jenkins-ci.org/display/JENKINS/SonarQube+plugin)
    ([JENKINS-33792](https://issues.jenkins-ci.org/browse/JENKINS-33792))
  * Enhanced support for [xUnit Plugin](https://wiki.jenkins-ci.org/display/JENKINS/xUnit+Plugin)
    ([#725](https://github.com/jenkinsci/job-dsl-plugin/pull/725))
  * Enhanced support for the [View Job Filters Plugin](https://wiki.jenkins-ci.org/display/JENKINS/View+Job+Filters) and
    the [Release Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Release+Plugin)
    ([JENKINS-33416](https://issues.jenkins-ci.org/browse/JENKINS-33416),
    [JENKINS-33675](https://issues.jenkins-ci.org/browse/JENKINS-33675))
  * Enhanced support for [Maven Project Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Maven+Project+Plugin)
    ([JENKINS-33823](https://issues.jenkins-ci.org/browse/JENKINS-33823))
  * Enhanced support for the [Extra Columns Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Extra+Columns+Plugin)
    ([JENKINS-32785](https://issues.jenkins-ci.org/browse/JENKINS-32785),
    [JENKINS-33676](https://issues.jenkins-ci.org/browse/JENKINS-33676),
    [JENKINS-33677](https://issues.jenkins-ci.org/browse/JENKINS-33677))
  * Improved script execution performance by re-using script engines
    ([#782](https://github.com/jenkinsci/job-dsl-plugin/pull/782))
  * Enhanced support for the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
    ([#790](https://github.com/jenkinsci/job-dsl-plugin/pull/790))
  * Enhanced support for the [Priority Sorter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Priority+Sorter+Plugin)
    ([JENKINS-29678](https://issues.jenkins-ci.org/browse/JENKINS-29678))
  * Enhanced support for the [CloudBees Docker Build and Publish
    Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Docker+Build+and+Publish+plugin)
    ([JENKINS-33439](https://issues.jenkins-ci.org/browse/JENKINS-33439))
  * Allow `EnvironmentVariableContributorsContext` to be extended
    ([JENKINS-32742](https://issues.jenkins-ci.org/browse/JENKINS-32742))
  * Added a `__FILE__` script variable containing the script location
    ([JENKINS-25935](https://issues.jenkins-ci.org/browse/JENKINS-25935))
  * Support for the older versions of the [CloudBees Docker Build and Publish
    Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Docker+Build+and+Publish+plugin) is deprecated, see
    [Migration](Migration#migrating-to-145)
  * Support for [Build Node Column Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Node+Column+Plugin) is
    deprecated, see [Migration](Migration#migrating-to-145)
  * Support for the older versions of the
    [Priority Sorter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Priority+Sorter+Plugin) is deprecated, see
    [Migration](Migration#migrating-to-145)
  * Enhanced support for the [EnvInject Plugin](https://wiki.jenkins-ci.org/display/JENKINS/EnvInject+Plugin)
    ([#747](https://github.com/jenkinsci/job-dsl-plugin/pull/747))
  * Deprecated a method for the [EnvInject Plugin](https://wiki.jenkins-ci.org/display/JENKINS/EnvInject+Plugin), see
    [Migration](Migration#migrating-to-145)
  * Add support for the
    [Git Chooser Alternative Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Chooser+Alternative+Plugin)
    ([JENKINS-33782](https://issues.jenkins-ci.org/browse/JENKINS-33782))
  * Removed anything that has been deprecated in 1.39, see [Migration](Migration#migrating-to-139)
* 1.44 (March 11 2016)
  * Added support for the [Mattermost Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Mattermost+Plugin)
    ([JENKINS-32764](https://issues.jenkins-ci.org/browse/JENKINS-32764))
  * Added support for the [P4 Plugin](https://wiki.jenkins-ci.org/display/JENKINS/P4+Plugin)
    ([JENKINS-32391](https://issues.jenkins-ci.org/browse/JENKINS-32391))
  * Enhanced support for the
    [Lockable Resources Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Lockable+Resources+Plugin)
    ([JENKINS-32906](https://issues.jenkins-ci.org/browse/JENKINS-32906))
  * Enhanced support for the [IRC Plugin](https://wiki.jenkins-ci.org/display/JENKINS/IRC+Plugin) and the
    [Jabber Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Jabber+Plugin)
    ([#770](https://github.com/jenkinsci/job-dsl-plugin/pull/770))
  * Enhanced support for the [Pipeline Plugin](https://github.com/jenkinsci/pipeline-plugin)
    ([JENKINS-32678](https://issues.jenkins-ci.org/browse/JENKINS-32678))
  * Enhanced support for the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
    ([#753](https://github.com/jenkinsci/job-dsl-plugin/pull/753),
    [#767](https://github.com/jenkinsci/job-dsl-plugin/pull/767),
    [#769](https://github.com/jenkinsci/job-dsl-plugin/pull/769))
  * Enhanced support for the [Mask Passwords Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Mask+Passwords+Plugin)
    ([#755](https://github.com/jenkinsci/job-dsl-plugin/pull/755))
  * Enhanced support for [Maven Project Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Maven+Project+Plugin)
    ([JENKINS-33359](https://issues.jenkins-ci.org/browse/JENKINS-33359))
  * Fixed a problem with relative job names
    ([JENKINS-32995](https://issues.jenkins-ci.org/browse/JENKINS-32995))
  * Fixed a problem with multiple nodes generated by configure blocks
    ([JENKINS-32941](https://issues.jenkins-ci.org/browse/JENKINS-32941))
  * Fixed documentation for [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
    ([JENKINS-33176](https://issues.jenkins-ci.org/browse/JENKINS-33176))
  * Added a method to allow instantiating contexts from an extension
    ([JENKINS-32912](https://issues.jenkins-ci.org/browse/JENKINS-32912))
  * Log warning when script name collides with package name
    ([JENKINS-32628](https://issues.jenkins-ci.org/browse/JENKINS-32628))
  * Support for the older versions of the
    [Lockable Resources Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Lockable+Resources+Plugin) is deprecated, see
    [Migration](Migration#migrating-to-144)
  * Support for the older versions of the [IRC Plugin](https://wiki.jenkins-ci.org/display/JENKINS/IRC+Plugin) is
    deprecated, see [Migration](Migration#migrating-to-144)
  * Deprecated a method for the [Perforce Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Perforce+Plugin), see
    [Migration](Migration#migrating-to-144)
  * Deprecated several methods for the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin), see
    [Migration](Migration#migrating-to-144)
  * Deprecated the `WithXmlAction` class, see [Migration](Migration#migrating-to-144)
  * Removed a method in `JobManagement` interface, see [Migration](Migration#migrating-to-144)
  * Moved two classes, see [Migration](Migration#migrating-to-144)
  * Removed anything that has been deprecated in 1.38, see [Migration](Migration#migrating-to-138)
* 1.43 (February 13 2016)
  * Add support for the [Emotional Jenkins Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Emotional+Jenkins+Plugin)
    ([JENKINS-32907](https://issues.jenkins-ci.org/browse/JENKINS-32907))
  * Added support for the [Clover PHP Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Clover+PHP+Plugin)
    ([JENKINS-31557](https://issues.jenkins-ci.org/browse/JENKINS-31557))
  * Allow `BuildParametersContext` to be extended
    ([JENKINS-32285](https://issues.jenkins-ci.org/browse/JENKINS-32285))
  * Enhanced support for the [CloudBees Docker Custom Build Environment
    Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Docker+Custom+Build+Environment+Plugin)
    ([JENKINS-32363](https://issues.jenkins-ci.org/browse/JENKINS-32363))
  * Enhanced support for the [Email-ext Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Email-ext+plugin)
    ([JENKINS-28986](https://issues.jenkins-ci.org/browse/JENKINS-28986),
    [JENKINS-30542](https://issues.jenkins-ci.org/browse/JENKINS-30542),
    [JENKINS-32131](https://issues.jenkins-ci.org/browse/JENKINS-32131))
  * Enhanced support for the [SonarQube Plugin](https://wiki.jenkins-ci.org/display/JENKINS/SonarQube+plugin)
    ([JENKINS-32419](https://issues.jenkins-ci.org/browse/JENKINS-32419))
  * Enhanced support for the [JaCoCo Plugins](https://wiki.jenkins-ci.org/display/JENKINS/JaCoCo+Plugin)
    ([#729](https://github.com/jenkinsci/job-dsl-plugin/pull/729))
  * Enhanced support for the [RunDeck Plugin](https://wiki.jenkins-ci.org/display/JENKINS/RunDeck+Plugin)
    ([#493](https://github.com/jenkinsci/job-dsl-plugin/pull/493))
  * Enhanced support for the [JUnit Plugin](https://wiki.jenkins-ci.org/display/JENKINS/JUnit+Plugin)
    ([#734](https://github.com/jenkinsci/job-dsl-plugin/pull/734))
  * Fixed support for the
    [Static Code Analysis Plugins](https://wiki.jenkins-ci.org/display/JENKINS/Static+Code+Analysis+Plug-ins)
    ([#724](https://github.com/jenkinsci/job-dsl-plugin/pull/724))
  * Fixed support for scripts in directories when using the command line runner
    ([#740](https://github.com/jenkinsci/job-dsl-plugin/pull/740))
  * Fixed NPE when checking available permissions
    ([JENKINS-32598](https://issues.jenkins-ci.org/browse/JENKINS-32598))
  * Enhanced support for the [Notification Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Notification+Plugin)
    ([#741](https://github.com/jenkinsci/job-dsl-plugin/pull/741))
  * Built-in support for the
    [GitHub Pull Request Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin)
    is deprecated, see [Migration](Migration#migrating-to-143)
    ([JENKINS-31214](https://issues.jenkins-ci.org/browse/JENKINS-31214))
  * Support for the older versions of the [CloudBees Docker Custom Build Environment
    Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Docker+Custom+Build+Environment+Plugin) is deprecated,
    see [Migration](Migration#migrating-to-143)
  * Support for the older versions of the [RunDeck Plugin](https://wiki.jenkins-ci.org/display/JENKINS/RunDeck+Plugin) is
    deprecated, see [Migration](Migration#migrating-to-143)
  * Support for the older versions of the [JUnit Plugin](https://wiki.jenkins-ci.org/display/JENKINS/JUnit+Plugin) is
    deprecated, see [Migration](Migration#migrating-to-143)
  * Support for the older versions of the
    [Notification Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Notification+Plugin) is deprecated, see
    [Migration](Migration#migrating-to-143)
  * Deprecated some methods in `JobManagement` interface, see [Migration](Migration#migrating-to-143)
  * Removed anything that has been deprecated in 1.36, see [Migration](Migration#migrating-to-136)
  * Removed anything that has been deprecated in 1.37, see [Migration](Migration#migrating-to-137)
* 1.42 (January 05 2016)
  * Added support for the [Dashboard View Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Dashboard+View)
    ([JENKINS-29146](https://issues.jenkins-ci.org/browse/JENKINS-29146))
  * Added support for the [Workflow Multibranch Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Workflow+Plugin)
    ([JENKINS-31671](https://issues.jenkins-ci.org/browse/JENKINS-31671),
    [JENKINS-31719](https://issues.jenkins-ci.org/browse/JENKINS-31719))
  * Added support for the [JSLint Plugin](https://wiki.jenkins-ci.org/display/JENKINS/JSLint+plugin)
    ([JENKINS-32195](https://issues.jenkins-ci.org/browse/JENKINS-32195))
  * Enhanced support for the
    [PostBuildScript Plugin](https://wiki.jenkins-ci.org/display/JENKINS/PostBuildScript+Plugin)
    ([JENKINS-31853](https://issues.jenkins-ci.org/browse/JENKINS-31853))
  * Support for the older versions of the
    [PostBuildScript Plugin](https://wiki.jenkins-ci.org/display/JENKINS/PostBuildScript+Plugin) is deprecated, see
    [Migration](Migration#migrating-to-142)
  * Fixed documentation for the [M2 Release Plugin](https://wiki.jenkins-ci.org/display/JENKINS/M2+Release+Plugin)
    ([JENKINS-32135](https://issues.jenkins-ci.org/browse/JENKINS-32135))
  * Added support for the [SeleniumHQ Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Seleniumhq+Plugin)
    ([JENKINS-31887](https://issues.jenkins-ci.org/browse/JENKINS-31887))
  * Added support for the
    [Selenium HTML Report Plugin](https://wiki.jenkins-ci.org/display/JENKINS/seleniumhtmlreport+Plugin)
    ([JENKINS-31886](https://issues.jenkins-ci.org/browse/JENKINS-31886))
  * Added support for the [NAnt Plugin](https://wiki.jenkins-ci.org/display/JENKINS/NAnt+Plugin)
    ([JENKINS-31883](https://issues.jenkins-ci.org/browse/JENKINS-31883))
  * Added support for the [GitLab Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitLab+Plugin)
    ([JENKINS-31789](https://issues.jenkins-ci.org/browse/JENKINS-31789))
  * Added support for the [Ruby Metrics Plugin](https://wiki.jenkins-ci.org/display/JENKINS/RubyMetrics+plugin)
    ([JENKINS-31830](https://issues.jenkins-ci.org/browse/JENKINS-31830))
  * Added support for the [DOS Trigger Plugin](https://wiki.jenkins-ci.org/display/JENKINS/DOS+Trigger)
    ([JENKINS-31879](https://issues.jenkins-ci.org/browse/JENKINS-31879))
  * Enhanced support for the
    [Flexible Publish Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Flexible+Publish+Plugin)
    ([JENKINS-30010](https://issues.jenkins-ci.org/browse/JENKINS-30010))
  * Enhanced support for the [Task Scanner Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Task+Scanner+Plugin)
    ([JENKINS-30543](https://issues.jenkins-ci.org/browse/JENKINS-30543))
  * Support for the older versions of the
    [Task Scanner Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Task+Scanner+Plugin) is deprecated, see
    [Migration](Migration#migrating-to-142)
  * Changed the DSL syntax for `flexiblePublish`, see [Migration](Migration#migrating-to-142)
  * Check DSL scripts for existence
    ([JENKINS-30541](https://issues.jenkins-ci.org/browse/JENKINS-30541))
  * Enhanced support for the [Build Flow Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Flow+Plugin)
    ([JENKINS-30201](https://issues.jenkins-ci.org/browse/JENKINS-30201))
  * Removed anything that has been deprecated in 1.35, see [Migration](Migration#migrating-to-135)
* 1.41 (December 15 2015)
  * Added support for the [WebLogic Deployer Plugin](https://wiki.jenkins-ci.org/display/JENKINS/WebLogic+Deployer+Plugin)
    ([JENKINS-21880](https://issues.jenkins-ci.org/browse/JENKINS-21880))
  * Added support for the [Mantis Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Mantis+Plugin)
    ([JENKINS-31911](https://issues.jenkins-ci.org/browse/JENKINS-31911))
  * Added support for the [Gatling Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Gatling+Plugin)
  * Added support for the
    [Cucumber Test Result Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Cucumber+Test+Result+Plugin)
    ([JENKINS-31815](https://issues.jenkins-ci.org/browse/JENKINS-31815))
  * Added support for the [Ruby Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Ruby+Plugin)
    ([JENKINS-31783](https://issues.jenkins-ci.org/browse/JENKINS-31783))
  * Added support for the [BitBucket Plugin](https://wiki.jenkins-ci.org/display/JENKINS/BitBucket+Plugin)
    ([JENKINS-31788](https://issues.jenkins-ci.org/browse/JENKINS-31788))
  * Added support for the
    [Subversion Tagging Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Subversion+Tagging+Plugin)
    ([JENKINS-31784](https://issues.jenkins-ci.org/browse/JENKINS-31784))
  * Added support for the [XShell Plugin](https://wiki.jenkins-ci.org/display/JENKINS/XShell+Plugin)
    ([JENKINS-31512](https://issues.jenkins-ci.org/browse/JENKINS-31512))
  * Added support for the [Ownership Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Ownership+Plugin)
    ([JENKINS-31531](https://issues.jenkins-ci.org/browse/JENKINS-31531))
  * Added support for the
    [Build Failure Analyzer Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Failure+Analyzer)
    ([JENKINS-31544](https://issues.jenkins-ci.org/browse/JENKINS-31544))
  * Added support for the [SLOCCount Plugin](https://wiki.jenkins-ci.org/display/JENKINS/SLOCCount+Plugin)
    ([JENKINS-29740](https://issues.jenkins-ci.org/browse/JENKINS-29740))
  * Added support for the [Cucumber Reports Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Cucumber+Reports+Plugin)
    ([JENKINS-31762](https://issues.jenkins-ci.org/browse/JENKINS-31762))
  * Support for the older versions of the
    [CloudBees Folders Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Folders+Plugin) is deprecated, see
    [Migration](Migration#migrating-to-141)
  * Support for the older versions of the [Jabber Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Jabber+Plugin) is
    deprecated, see [Migration](Migration#migrating-to-141)
  * Enhanced support for the [Run Condition Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Run+Condition+Plugin)
    ([JENKINS-31581](https://issues.jenkins-ci.org/browse/JENKINS-31581))
  * Enhanced support for the [vSphere Cloud Plugin](https://wiki.jenkins-ci.org/display/JENKINS/vSphere+Cloud+Plugin)
    ([JENKINS-31818](https://issues.jenkins-ci.org/browse/JENKINS-31818))
  * Enhanced support for the [Jabber Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Jabber+Plugin)
    ([#697](https://github.com/jenkinsci/job-dsl-plugin/pull/697))
  * Fixed support for the [Run Condition Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Run+Condition+Plugin)
    ([JENKINS-31604](https://issues.jenkins-ci.org/browse/JENKINS-31604))
* 1.40 (November 08 2015)
  * Increased the minimum supported Jenkins version to 1.609
  * Added support for the [Sauce OnDemand Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Sauce+OnDemand+Plugin)
  * Added support for the
    [Matrix Combinations Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Matrix+Combinations+Plugin)
  * Added support for the [Managed Scripts Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Managed+Script+Plugin)
  * Enhanced support for the [GitHub Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+Plugin)
    ([JENKINS-29849](https://issues.jenkins-ci.org/browse/JENKINS-29849))
  * Enhanced support for [Maven Project Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Maven+Project+Plugin)
    ([JENKINS-30544](https://issues.jenkins-ci.org/browse/JENKINS-30544))
  * Enhanced support for the [Multijob Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Multijob+Plugin)
    ([JENKINS-30760](https://issues.jenkins-ci.org/browse/JENKINS-30760))
  * Enhanced support for the [Copy Artifact Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Copy+Artifact+Plugin)
    ([JENKINS-31387](https://issues.jenkins-ci.org/browse/JENKINS-31387))
  * Enhanced support for the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin)
    ([#645](https://github.com/jenkinsci/job-dsl-plugin/pull/645))
  * Enhanced support for the
    [GitHub Pull Request Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin)
  * Fixed a problem with deprecation warnings
    ([JENKINS-30826](https://issues.jenkins-ci.org/browse/JENKINS-30826))
  * Fixed a problem with the [Build Pipeline Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Pipeline+Plugin)
    ([JENKINS-31427](https://issues.jenkins-ci.org/browse/JENKINS-31427))
  * Allow `@DslExtensionMethod` annotated methods to return `null` to not contribute to the job configuration
  * Allow `DownstreamTriggerParameterContext` to be extended
    ([JENKINS-31111](https://issues.jenkins-ci.org/browse/JENKINS-31111))
  * Added support for the [TestNG Plugin](https://wiki.jenkins-ci.org/display/JENKINS/testng-plugin)
    ([JENKINS-30895](https://issues.jenkins-ci.org/browse/JENKINS-30895))
  * Added workaround for [GROOVY-6263](https://issues.apache.org/jira/browse/GROOVY-6263) to `WorkspaceCleanupContext`
  * Added workaround for [JENKINS-31366](https://issues.jenkins-ci.org/browse/JENKINS-31366)
  * Added support for the [SSH Plugin](https://wiki.jenkins-ci.org/display/JENKINS/SSH+plugin)
    ([JENKINS-30957](https://issues.jenkins-ci.org/browse/JENKINS-30957))
  * Set default runner for `conditionalSteps`
    ([JENKINS-31373](https://issues.jenkins-ci.org/browse/JENKINS-31373))
  * Enhanced support for the [Gradle Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Gradle+Plugin)
    ([JENKINS-31264](https://issues.jenkins-ci.org/browse/JENKINS-31264))
  * Support for older versions of the [Gradle Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Gradle+Plugin) is
    deprecated, see [Migration](Migration#migrating-to-140)
  * Support for older versions of the
    [HTML Publisher Plugin](https://wiki.jenkins-ci.org/display/JENKINS/HTML+Publisher+Plugin) is deprecated, see
    [Migration](Migration#migrating-to-140)
  * Removed anything that has been deprecated in 1.33, see [Migration](Migration#migrating-to-133)
  * Removed anything that has been deprecated in 1.34, see [Migration](Migration#migrating-to-134)
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
