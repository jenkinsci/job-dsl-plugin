This is the in-depth documentation of the methods available on inside the _job_ part of the DSL.

## Name
```groovy
name(String jobName)
```

The Name of the job, **required**. This could be a static name but given the power of Groovy you could get very fancy with the these.

If using the [folders plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Free+Enterprise+Plugins#CloudBeesFreeEnterprisePlugins-FoldersPlugin), the full path to the job can be used. e.g.
```groovy
name('path/to/myjob')
```
Note that the folders must already exist. (Available since 1.17).
## Display Name
```groovy
displayName(String displayName)
```

The name to display instead of the actual job name. (Available since 1.16)

## Using
```groovy
using(String templateName)
```

Refers to a template Job to be used as the basis for this job. These are loaded before any configure blocks or DSL commands.  Template Jobs are just standard Jenkins Jobs which are used for their underlying config.xml. When they are changed, the seed job will attempt to re-run, which has the side-effect of cascading changes of the template the jobs generated from it.

## Description
```groovy
description(String desc)
```

Sets description of the job. This is a not a good way of creating a dynamic description of a job.

## Label
```groovy
label(String labelStr)
```

Label which specifies which nodes this job can run on, e.g. 'X86&&Ubuntu'

## Disable

```groovy
disabled(Boolean shouldDisable)
```

Provides ability to disable a job.

## Quiet period
```groovy
quietPeriod()
quietPeriod(int seconds)
```

Defines a timespan to wait for additional events (pushes, check-ins) before triggering a build. This prevents Jenkins from starting multiple jobs for check-ins/pushes that occur almost at the same time.

If the number of seconds to wait is omitted from the call the job will be configured to wait for five seconds. If you need to wait for a different amount of time just specify the number of seconds to wait. (Available since 1.16)

## Block build
```groovy
blockOn(String projectName)
blockOn(Iterable<String> projectNames)
```

Block build if certain jobs are running, supported by the <a href="https://wiki.jenkins-ci.org/display/JENKINS/Build+Blocker+Plugin">Build Blocker Plugin</a>. If more than one name is provided to projectName, it is newline separated. Per the plugin, regular expressions can be used for the projectNames, e.g. ".*-maintenance" will match all maintenance jobs.

## Block on upstream/downstream projects
```groovy
blockOnUpstreamProjects()
blockOnDownstreamProjects()
```

Blocks the build of a project when one ore more upstream (blockOnUpstreamProjects()) or a downstream projects (blockOnDownstreamProjects()) are running. (Available since 1.16)

## Build History
```groovy
logRotator(int daysToKeepInt = -1, int numToKeepInt = -1, int artifactDaysToKeepInt = -1, int artifactNumToKeepInt = -1)
```

Sets up the number of builds to keep.

## Execute concurrent builds
```groovy
concurrentBuild(boolean allowConcurrentBuild = true)
```

If enabled, Jenkins will schedule and execute multiple builds concurrently (provided that you have sufficient executors and incoming build requests).

```groovy
job {
   ...
   concurrentBuild()
   ...
}
``` 


## Custom workspace
```groovy
customWorkspace(String workspacePath)
```

Defines that a project should use the given directory as a workspace instead of the default workspace location. (Available since 1.16) 

## JDK
```groovy
jdk(String jdkStr)
```

Selects the JDK to be used for this project. The jdkStr must match the name of a JDK installation defined in the Jenkins system configuration. The default JDK will be used when the jdk method is omitted.

## Security
```groovy
permission(String)
permission(String permEnumName, String user)
permission(Permissions perm, String user)
permissionAll(String user)
```

Creates permission records. The first form adds a specific permission, e.g. 'hudson.model.Item.Workspace:authenticated', as seen in the config.xml. The second form simply breaks apart the permission from the user name, to make scripting easier. The third uses a helper Enum called [Permissions] (https://github.com/jenkinsci/job-dsl-plugin/blob/master/job-dsl-core/src/main/groovy/javaposse/jobdsl/dsl/helpers/Permissions.groovy) to hide some of the names of permissions. It is available by importing javaposse.jobdsl.dsl.helpers.Permissions. By using the enum you get some basic type checking. A flaw with this system is that Jenkins plugins can create their own permissions, and the job-dsl plugin doesn't necessarily know about them. The last form will take everything in the Permissions enum and gives them to the user, this method also suffers from the problem that not all permissions from every plugin are included.

The permissions as of the latest version can be found in [the Permissions enum](https://github.com/jenkinsci/job-dsl-plugin/blob/master/job-dsl-core/src/main/groovy/javaposse/jobdsl/dsl/helpers/Permissions.groovy). For illustration, [Permissions](https://github.com/jenkinsci/job-dsl-plugin/blob/master/job-dsl-core/src/main/groovy/javaposse/jobdsl/dsl/helpers/Permissions.groovy) here are a couple of examples:

```groovy
// Gives permission for the special authenticated group to see the workspace of the job
authorization {
    permission('hudson.model.Item.Workspace:authenticated')
}
```

```groovy
// Gives discover permission for the special anonymous user
authorization {
    permission(Permissions.ItemDiscover, 'anonymous')
}
```

```groovy
// Gives all permissions found in the Permissions enum to the special authenticated group
authorization {
    permissionAll('authenticated')
}
```

## [Throttle Concurrent Builds](https://wiki.jenkins-ci.org/display/JENKINS/Throttle+Concurrent+Builds+Plugin)

```groovy
job {
    // Throttle one job on its own
    throttleConcurrentBuilds {
        maxPerNode 1
        maxTotal 2
    }
}
```

```groovy
job {
    // Throttle as part of a category
    throttleConcurrentBuilds {
        categories(['cat-1'])
    }
}
```

## Build Flow 

```groovy
buildFlow(String flowDsl)
```

Insert text into the Build Flow text block. This can only be used in [Build Flow](https://wiki.jenkins-ci.org/display/JENKINS/Build+Flow+Plugin) job types.

Examples:

Triple-quote can be used for retaining Groovy style in the embedded DSL.

```groovy
job(type: BuildFlow) {
    buildFlow("""  
        build("job1")
    """)
}
```

Using job variables in build flow text block. The new job will have a build flow text like this: `build("hello-there")`.

```groovy
CUSTOM_VARIABLE = "hello-there"
job(type: BuildFlow) {
    buildFlow('build("${CUSTOM_VARIABLE}")')
}
```

The build flow text can also be stored in a file and set in the new job when it's created.

```groovy
job(type: BuildFlow) {
    buildFlow(readFileFromWorkspace("my-build-flow-text.groovy"))
}
```

Since 1.21.

# Maven

The 'rootPOM', 'goals', 'mavenOpts', 'mavenInstallation', 'perModuleEmail', 'archivingDisabled', 'runHeadless', 'preBuildSteps' and 'postBuildSteps' methods can only be used in jobs with type 'Maven'.

## Root POM
```groovy
rootPOM(String rootPOM)
```

To use a different 'pom.xml' in some other directory than the workspace root.

## Goals
```groovy
goals(String goals) 
```

The Maven goals to execute including other command line options. 

When specified multiple times, the goals and options will be concatenated, e.g.

```groovy
goals("clean") 
goals("install") 
goals("-DskipTests") 
```

is equivalent to

```groovy
goals("clean install -DskipTests") 
```

## MAVEN_OPTS
```groovy
mavenOpts(String mavenOpts) 
```

The JVM options to be used when starting Maven. When specified multiple times, the options will be concatenated.

## Maven Installation
```groovy
mavenInstallation(String name)
```

Refers to the pull down box in the UI to select which installation of Maven to use, specify the exact string seen in the UI. The last call will be the one used.

(since 1.20)

## Isolated Local Maven Repository

```groovy
localRepository(LocalRepositoryLocation location)
```

LocalRepositoryLocation is available as two enums, injected into the script. Their names are LocalToExecutor and LocalToWorkspace, they can be used like this:

```groovy 
localRepository(LocalToWorkspace)
```

(Since 1.17)

## Email Per Module
```groovy
perModuleEmail(boolean shouldSendEmailPerModule)
```

Enable or disable email notifications for each Maven module.

## Disable Artifact Archiving
```groovy
archivingDisabled(boolean shouldDisableArchiving)
```

Disables automatic Maven artifact archiving. Artifact archiving is enabled by default.

## Run Headless
```groovy
runHeadless(boolean shouldRunHeadless)
```

Specifiy this to run the build in headless mode if desktop access is not required. Headless mode is not enabled by default.

## Maven Pre and Post Build Steps
```groovy
preBuildSteps(Closure mavenPreBuildClosure)
postBuildSteps(Closure mavenPostBuildClosure)
```

For Maven jobs, you can also run arbitrary build steps before and after the Maven execution. Note that this can only be used with Maven jobs.

Examples:
```groovy
job(type: 'Maven') {
  preBuildSteps {
    shell("echo 'run before Maven'")
  }
  postBuildSteps {
    shell("echo 'run after Maven'")
  }
}
```

(since 1.20)

## Environment Variables
```groovy
environmentVariables(Map<Object,Object> vars, Closure envClosure = null)
environmentVariables {
    scriptFile(String filePath)
    script(String content)
    env(Object key, Object value)
    envs(Map<Object, Object> map) 
    groovy(String groovyScript)
    propertiesFile(String filePath)
    loadFilesFromMaster(boolean loadFromMaster)
    keepSystemVariables(boolean keepSystemVariables)
    keepBuildVariables(boolean keepBuildVariables)
}
```

Injects environment variables into the build. They can be provided as a Map or applied as part of a context. The optional Groovy script must return a map Java object. Requires the [EnvInject plugin](https://wiki.jenkins-ci.org/display/JENKINS/EnvInject+Plugin).

## Job Priority
```groovy
priority(int value)
```

Allows jobs waiting in the build queue to be sorted by a static priority rather than the standard FIFO. The default priority is 100. A jobs with a higher priority will be executed before jobs with a lower priority. Requires the [Priority Sorter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Priority+Sorter+Plugin).

# Source Control

## SCM retry count

```groovy
checkoutRetryCount()
checkoutRetryCount(int times)
```

Defines the number of times the build should retry to check out from the SCM if the SCM checkout fails. 

The parameterless invocation sets a default retry count of three (3) times. To specify more (or less) retry counts pass the number of times to retry the checkout. (Available since 1.16)

## Mercurial

```groovy
hg(String url, String branch = null, Closure configure = null)
```

Add Mercurial SCM source. Will not clean by default, to change this use the configure block, e.g.

```groovy
hg('http://scm') { node ->
    node / clean('true')
}
```

## Git
```groovy
git {
    // since 1.20
    remote { // can be repeated to add multiple remotes
        name(String name) // optional
        url(String url) // use either url or github
        github(String ownerAndProject, String protocol = "https", String host = "github.com") // will also set the browser
                                                                                              // and GitHub property
        refspec(String refspec) // optional
        credentials(String credentials) // optional
    }
    branch(String name) // the branches to build, multiple calls are accumulated, defaults to **
    branches(String... names)
    mergeOptions(String remote = null, String branch) // merge a branch before building, optional
    createTag(boolean createTag = true) // create a tag for every build, optional, defaults to false
    clean(boolean clean = true) // clean after checkout, optional, defaults to false
    wipeOutWorkspace(boolean wipeOutWorkspace = true) // wipe out workspace and force clone, optional, defaults to false
    remotePoll(boolean remotePoll = true) // force polling using workspace, optional, defaults to false
    shallowClone(boolean shallowClone = true) // perform shallow clone, optional, defaults to false
    relativeTargetDir(String relativeTargetDir) // checkout to a sub-directory, optional
    reference(String reference) // path to a reference repository, optional
    configure(Closure configure) // optional configure block, the GitSCM node is passed in 
}

git(String url, String branch = null, Closure configure = null)

github(String ownerAndProject, String branch = null, String protocol = 'https',
       String host = 'github.com', Closure configure = null)
```

Adds a Git SCM source. The first variant can be used for advanced configuration (since 1.20), the other two variants are shortcuts for simpler Git SCM configurations.

The GitHub variants will derive the Git URL from the ownerAndProject, protocol and host parameters. Valid protocols are `https`, `ssh` and `git`. They also configure the source browser to point to GitHub.

The Git plugin has a lot of configurable options, which are currently not all supported by the DSL. A  configure block can be used to add more options.

Version 2.0 or later of the Git Plugin is required to use Jenkins managed credentials for Git authentication. The arguments for the credentials method is the description field or the UUID generated from Jenkins | Manage Jenkins | Manage Credentials. The easiest way to find this value, is to navigate Jenkins | Credentials | Global credentials | (Key Name). Then look at the description in parenthesis or using the UUID in the URL.

Examples:

```groovy
// checkout repo1 to a sub directory and clean the workspace after checkout
git {
    remote {
        name('remoteB')
        url('git@server:account/repo1.git')      
    }
    clean()
    relativeTargetDir('repo1')
}
```

```groovy
// add the upstream repo as second remote and merge branch featureA with master
git {
    remote {
        name('origin')
        url('git@serverA:account/repo1.git')      
    }
    remote {
        name('upstream')
        url('git@serverB:account/repo1.git')      
    }
    branch('featureA')
    mergeOptions('upstream', 'master')
}
```

```groovy
// add user name and email options
git('git@git') { node -> // is hudson.plugins.git.GitSCM
    node / gitConfigName('DSL User')
    node / gitConfigEmail('me@me.com')
}
```

```groovy
// add a Git SCM for the GitHub repository job-dsl-plugin of GitHub user jenkinsci
github('jenkinsci/job-dsl-plugin')
```

```groovy
// add a Git SCM for a GitHub repository and use the given credentials for authentication
git {
    remote {
        github('account/repo', 'ssh')
        credentials('GitHub CI Key')
    }
}
```

## Subversion

**BEGIN Unreleased Feature - Documentation is a work in progress**

### Job DSL Plugin Version X.XX or greater

As of version X.XX of the Job DSL Plugin, the Subversion plugin can be configured using an improved svn closure.  The following are the methods availble in the svn closure (note: these methods are **not** available in the older svn(...) closures):

```groovy
svn {
    /*
     * At least one location MUST be specified.
     * Additional locations can be specified by calling location() multiple times.
     *   svnUrl   - What to checkout from SVN.
     *   localDir - Destination directory relative to workspace.
     *              If not specified, defaults to '.'.
     */
    location(String svnUrl, String localDir = '.')

    /*
     * The checkout strategy that should be used.  This is a global setting for all
     * locations.
     *   strategy - Strategy to use. Possible values:
     *                CheckoutStrategy.Update
     *                CheckoutStrategy.Checkout
     *                CheckoutStrategy.UpdateWithClean
     *                CheckoutStrategy.UpdateWithRevert
     *
     * If no checkout strategy is configured, the default is CheckoutStrategy.Update.
     */
    checkoutStrategy(CheckoutStrategy strategy)

    /*
     * Add an excluded region.  Each call to excludedRegion() adds to the list of
     * excluded regions.
     * If excluded regions are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any files and/or folders that match the specified
     * patterns when determining if a build needs to be triggered.
     *   pattern - RegEx
     */
    excludedRegion(String pattern)

    /*
     * Add a list of excluded regions.  Each call to excludedRegions() adds to the
     * list of excluded regions.
     * If excluded regions are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any files and/or folders that match the specified
     * patterns when determining if a build needs to be triggered.
     *   patterns - RegEx
     */
    excludedRegions(Iterable<String> patterns)

    /*
     * Add an included region.  Each call to includedRegion() adds to the list of
     * included regions.
     * If included regions are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any files and/or folders that do _not_ match the specified
     * patterns when determining if a build needs to be triggered.
     *   pattern - RegEx
     */
    includedRegion(String pattern)

    /*
     * Add a list of included regions.  Each call to includedRegions() adds to the
     * list of included regions.
     * If included regions are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any files and/or folders that do _not_ match the specified
     * patterns when determining if a build needs to be triggered.
     *   patterns - RegEx
     */
    includedRegions(Iterable<String> patterns)

    /*
     * Add an excluded user.  Each call to excludedUser() adds to the list of
     * excluded users.
     * If excluded users are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any revisions committed by the specified users when
     * determining if a build needs to be triggered.
     *   user - User to ignore when triggering builds
     */
    excludedUser(String user)

    /*
     * Add a list of excluded users.  Each call to excludedUsers() adds to the
     * list of excluded users.
     * If excluded users are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any revisions committed by the specified users when
     * determining if a build needs to be triggered.
     *   users - Users to ignore when triggering builds
     */
    excludedUsers(Iterable<String> users)

    /*
     * Add an exluded commit message.  Each call to excludedCommitMsg() adds to the list of
     * excluded commit messages.
     * If excluded messages are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any revisions with commit messages that match the specified
     * patterns when determining if a build needs to be triggered.
     *   pattern - RegEx
     */
    excludedCommitMsg(String pattern)

    /*
     * Add a list of excluded commit messages.  Each call to excludedCommitMsgs() adds to the
     * list of excluded commit messages.
     * If excluded messages are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any revisions with commit messages that match the specified
     * patterns when determining if a build needs to be triggered.
     *   patterns - RegEx
     */
    excludedCommitMsgs(Iterable<String> patterns)

    /*
     * Set an excluded revision property.
     * If an excluded revision property is set, and Jenkins is set to poll for changes,
     * Jenkins will ignore any revisions that are marked with the specified
     * revision property when determining if a build needs to be triggered.
     * This only works in Subversion 1.5 servers or greater.
     *   pattern - RegEx
     */
    excludedRevProp(String revisionProperty)
}
```
Note that no support for a configure block is available in the new svn closure.  Use the job closure's configure method instead.

### Job DSL Plugin Version less than X.XX

If using a version of the Job DSL Plugin older than X.XX, the following configuration methods are available.
Note; For backwards compatibility, these are still supported in version X.XX and above.

**END Unreleased Feature**

```groovy
svn(String svnUrl, String localDir='.', Closure configure = null)
```

Add Subversion source. 'svnUrl' is self explanatory. 'localDir' sets the <local> tag (which is set to '.' if you leave this arg out). The Configure block is handed a hudson.scm.SubversionSCM node.

You can use the Configure block to configure additional svn nodes such as a <browser> node. First a FishEyeSVN example:

```groovy
svn('http://svn.apache.org/repos/asf/xml/crimson/trunk/') { svnNode ->
    svnNode / browser(class:'hudson.scm.browsers.FishEyeSVN') {
        url 'http://mycompany.com/fisheye/repo_name'
        rootModule 'my_root_module'
    }
}
```

and now a ViewSVN example:
```groovy
svn('http://svn.apache.org/repos/asf/xml/crimson/trunk/') { svnNode ->
    svnNode / browser(class:'hudson.scm.browsers.ViewSVN') / url << 'http://mycompany.com/viewsvn/repo_name'
}
```

For more details on using the configure block syntax, see our [dedicated page](configure-block).

## Perforce

```groovy
p4(String viewspec, String user = 'rolem', String password = '', Closure configure = null)
```

Add Perforce SCM source. The user probably has to be specified. The password will be properly encrypted. Sets p4Client to builds-${JOB_NAME}. The configure block is handed a hudson.plugins.perforce.PerforceSCM. Perforce requires a few fields to be setup, so it's very likely that the configure block will be needed, especially for things like p4Port.

```groovy
p4('//depot/Tools/build') { node ->
    node / p4Port('perforce:1666')
    node / p4Tool('/usr/bin/p4')
    node / exposeP4Passwd('false')
    node / pollOnlyOnMaster('true')
}
```

## Clone Workspace

```
cloneWorkspace(String parentProject, String criteriaArg = 'Any') 
```

Support the Clone Workspace plugin, by copy the workspace of another build. This complements another job which published their workspace.

# Triggers


Triggers block contains the available triggers.

## Cron
```groovy
cron(String cronString)
```

Triggers job based on regular intervals.

## Source Control Trigger
```groovy
scm(String cronString)
```

Polls source control for changes at regular intervals.

## Github Push Notification Trigger
```groovy
githubPush()
```

Enables the job to be started whenever a change is pushed to a github repository. Requires that Jenkins has the github plugin installed and that it is registered as service hook for the repository (also works with Github Enterprise). (Since 1.16)

## Gerrit
```groovy
gerrit {
    events(Closure eventClosure) // Free form listing of event names
    project(String projectName, List<String> branches) // Can be called multiple times
    project(String projectName, String branches) // Can be called multiple times
    buildStarted(int verified, int codeReview) //Updates the gerrit report values for the build started event
     buildSuccessful(int verified, int codeReview) //Updates the gerrit report values for the build successful event
    buildFailed(int verified, int codeReview) //Updates the gerrit report values for the build failed event 
    buildUnstable(int verified, int codeReview) //Updates the gerrit report values for the build unstable event 
    buildNotBuilt(int verified, int codeReview) //Updates the gerrit report values for the build not built event 
    configure(Closure configureClosure) // Handed com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTrigger
}
```

Polls gerrit for changes. This DSL method works slightly differently by exposing most of its functionality in its own block. This is accommodating how the plugin can be pointed to multiple projects and trigger on many events. The most complex part is the events block, which takes the "short name" of an event. When looking at the raw config.xml for a Job which has Gerrit trigger, you'll see multiple class names in the triggerOnEvents element. The DSL method will take the names in the events block and prepend it with "com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.events.Plugin" and append "Event", meaning that shorter names like ChangeMerged and DraftPublished can be used. Straight from the unit test:

```groovy
gerrit {
    events {
        ChangeMerged
        DraftPublished
    }
    project('reg_exp:myProject', ['ant:feature-branch', 'plain:origin/refs/mybranch'])
    project('test-project', '**')
    configure { node ->
        node / gerritBuildSuccessfulVerifiedValue << '10'
    }
}
```

## Github Pull Request Trigger

```groovy
pullRequest {
    admins(String admin) // add admin
    admins(Iterable<String> admins) // add admins
    userWhitelist(String user) // add user to whitelist
    userWhitelist(Iterable<String> users) // add users to whitelist
    orgWhitelist(String organization) // add organization to whitelist
    orgWhitelist(Iterable<String> organizations) // add organizations to whitelist
    cron(String cron) // set cron schedule, defaults to 'H/5 * * * *'
    triggerPhrase(String triggerPhrase) // set phrase to trigger by commenting within the pull request
    onlyTriggerPhrase(boolean onlyTriggerPhrase = true) // defaults to false if not specified
    useGitHubHooks(boolean useGithubHooks = true) // defaults to false if not specified
    permitAll(boolean permitAll = true) // defaults to false if not specified
    autoCloseFailedPullRequests(boolean autoCloseFailedPullRequests = true) // defaults to false if not specified
}
```

Builds pull requests from GitHub and will report the results directly to the pull request. Requires the [GitHub pull request builder plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin). (Available since 1.22)

The pull request builder plugin requires a special Git SCM configuration, see the plugin documentation for details.

```groovy
job {
    ...
    scm {
        git {
            remote {
                github('test-owner/test-project')
                refspec('+refs/pull/*:refs/remotes/origin/pr/*')
            }
            branch('${sha1}')
        }
    }
    triggers {
        pullRequest {
            admins('USER_ID')
            userWhitelist('you@you.com')
            orgWhitelist('your_github_org', 'another_org')
            cron('H/5 * * * *')
            triggerPhrase('Ok to test')
            onlyTriggerPhrase()
            useGitHubHooks()
            permitAll()
            autoCloseFailedPullRequests()
        }
    }
    ...
}
```

## URL Trigger

The URL trigger plugin checks one or more specified URLs and starts a build when a change is detected. (Since 1.16)

Currently (v1.16) on Jenkins <= 1.509.2, the alternative syntax with the cron line as a parameter rather than inside the closure is required, because job creation throws an exception if a trigger is initialized with the default `'H/5 * * * *'` schedule. These versions of Jenkins do not understand "H" in a trigger schedule.

```groovy
urlTrigger {
  cron '* 0 * 0 *'    // set cron schedule (defaults to : 'H/5 * * * *')
  restrictLabel 'foo' // restrict execution to the specified label expression

  /* Simple configuration statements */
  url('http://www.example.com/foo/') {
    proxy true           // use Jenkins Proxy for requests
    status 404           // set the expected HTTP Response status code (default: 200)
    timeout 4000         // set the request timeout in seconds (default: 300 seconds)
    check 'status'       // check the returned status code (not checked by default) 
    check 'etag'         // check ETag header (not checked by default)
    check 'lastModified' // check last modified date of resource (not checked by default)
  }

  /* Content inspection (MD5 hash) */
  url('http://www.example.com/bar/') {
    inspection 'change' //calculate MD5 sum of URL content and on hash changes
  }

  /* Content inspection for JSON or XML content with detailed checking 
     using XPath/JSONPath */
  url('http://www.example.com/baz/') {
    inspection('json'|'xml') {              // inspect XML or JSON content type
      path '//div[@class="foo"]'            // XPath for checking XML content
      path '$.store.book[0].title'          // JSONPath expression (dot syntax) 
      path "$['store']['book'][0]['title']" // JSONPath expression (bracket syntax)
    }
  }
 
  /* Content inspection for text content with detailed checking using regular expressions */
  url('http://www.example.com/fubar/') {
    inspection('text') {    // inspect content type text
      regexp '_(foo|bar).+' // regular expression for checking content changes
    }
  }
}
```

There is an alternate syntax for specifying the cronSchedule:

```groovy
urlTrigger('* 0 * 0 *'){
  //closure same as above
}
```

The trigger can check multiple URLs and virtually all options are combinable, although not all combinations may be sensible or useful (as checking one URL for both XML and JSON/text content or checking both modification date and content changes).

More on JSON path expressions: [http://goessner.net/articles/JsonPath/]

The URL trigger is particularly useful for monitoring snapshot dependencies for non-Maven/Ivy projects like SBT:
 
```groovy
urlTrigger {
  url('http://snapshots.repository.codehaus.org/org/picocontainer/picocontainer/2.11-SNAPSHOT/maven-metadata.xml' {
    check 'etag'
    check 'lastModified'
  }
}
```

The sample above monitors the metadata file of the picocontainer 2.11-SNAPSHOT that changes whenever the snapshot changes and triggers a build of the dependent project.

## Snapshot Dependencies
```groovy
snapshotDependencies(boolean checkSnapshotDependencies)
```

When enabling the snapshot dependencies trigger, Jenkins will check the snapshot dependencies from the  '\<dependency\>', '\<plugin\>' and '\<extension\>' elements used in Maven POMs and setup a job relationship to the jobs building the snapshots. This can only be used in jobs with type 'maven'.

# Build Environment (Build Wrappers)

Adds wrappers block to contain an list of build wrappers. The block exists since 1.19 and before that the methods were top-level.

## Node Stalker

Allows job to build on the same node as another job (https://wiki.jenkins-ci.org/display/JENKINS/Node+Stalker+Plugin).

```groovy
runOnSameNodeAs(String jobName, boolean useSameWorkspace = false)
```

(Since 1.17)

## RVM
```groovy
rvm('ruby-1.9.3')
rvm('ruby-2.0@gemset')
```

Configures the job to prepare a Ruby environment controlled by RVM for the build. Requires at least the ruby version, can take also a gemset specification to prevent side effects with other builds. (Available since 1.16)

## Timeout

```groovy
timeout(String type) { //type is one of: 'absolute', 'elastic', 'likelyStuck'
    limit 15       // timeout in minutes
    percentage 200 // percentage of runtime to consider a build timed out
}
```

The timeout method enables you to define a timeout for builds. It can either be absolute (build times out after a fixed number of minutes), elastic (times out if build runs x% longer than the average build duration) or likelyStuck. (Available since 1.16)

The simplest invocation looks like this:

```groovy
timeout()
```

It defines an absolute timeout with a maximum build time of 3 minutes.

Here is an absolute timeout:

```groovy
timeout('absolute') {
    limit 60              // 60 minutes before timeout
}
```

The elastic timeout accepts two parameters: a percentage for determining builds that take longer than normal an a limit that is used if there is no average successful build duration (i.e. no jobs run or all runs failed):

```groovy
timeout('elastic') {
    limit 30        // 30 minutes default timeout (no successful builds available as reference)
    percentage 300  // Build will timeout when it take 3 time longer than the reference build duration
}
```

The likelyStuck timeout times out a build when it is likely to be stuck. Does not take extra configuration parameters.

```groovy
timeout('likelyStuck')
```

The following syntax has been available before 1.16 and will be retained for compatibility reasons:

```groovy
timeout(int timeoutInMinutes, Boolean shoudFailBuild = true)
```

Using the build timeout plugin, it can fail a build after a certain amount of time.

## Port allocation
```groovy
allocatePorts 'HTTP', '8080' // allocates two ports: one randomly assigned and accessible by env var $HTTP
                             // the second is fixed and the port allocator controls concurrent usage

allocatePorts {
  port 'HTTP'                          // random port available as $HTTP
  port '8080'                          // concurrent build execution controlled to prevent resource conflicts 
  glassfish '1234', 'user', 'password' // adds a glassfish port
  tomcat '1234', 'password'            // adds a port for tomcat
}
```

The port allocation plugin enables to allocate ports for build executions to prevent conflicts between build jobs competing for a single port number (useful for any build that needs to allocate a port like Rails,Play! web containers, etc). See the [plugin documentation|https://wiki.jenkins-ci.org/display/JENKINS/Port+Allocator+Plugin] for more details. (Available since 1.16)

## [SSH Agent](https://wiki.jenkins-ci.org/display/JENKINS/SSH+Agent+Plugin)

Makes shared SSH credential available to builds.

```groovy
job {
    wrappers {
        sshAgent(String credentials)
    }
}
```

The credentials argument is the description field or the UUID generated from Jenkins | Manage Jenkins | Manage Credentials. The easiest way to find this value, is to navigate Jenkins | Credentials | Global credentials | (Key Name). The look at the description in parenthesis or using the UUID in the URL.

(Since 1.17)

## [Timestamper](https://wiki.jenkins-ci.org/display/JENKINS/Timestamper)

```groovy
job {
    wrappers {
        timestamps()
    }
}
```

Adds timestamps to the console log.

(Since 1.19)

## [AnsiColor](https://wiki.jenkins-ci.org/display/JENKINS/AnsiColor+Plugin)

```groovy
job {
    wrappers {
        colorizeOutput('xterm') // when no parameter is given it will fall back to 'xterm'
    }
}
```

Renders ANSI escape sequences, including color, to Console Output.

(Since 1.19)

## [XVNC](https://wiki.jenkins-ci.org/display/JENKINS/Xvnc+Plugin)

```groovy
job {
    wrappers {
        xvnc(boolean takeScreenshot = false)
    }
}
```

This plugin lets you run an Xvnc session during a build. This is handy if your build includes UI testing that needs a display available.

(Since 1.19)

## [Tool Environment](https://wiki.jenkins-ci.org/display/JENKINS/Tool+Environment+Plugin)

```groovy
job {
  wrappers {
    toolenv("Ant 1.8.2", "Maven 3.1")
  }
}
```

Downloads the specified tools, if needed, and puts the path to each of them in the build's environment.

(since 1.21)

## Environment Variables
```groovy
job {
  wrappers {
    environmentVariables {
      scriptFile(String filePath)
      script(String content)
      env(Object key, Object value)
      envs(Map<Object, Object> map) 
      propertiesFile(String filePath)
    }
  }
}
```

Injects environment variables into the build. Requires the [EnvInject plugin](https://wiki.jenkins-ci.org/display/JENKINS/EnvInject+Plugin).

(Since 1.21)

## Release
```groovy
job {
    wrappers {
        release {
            releaseVersionTemplate(String template)
            doNotKeepLog(boolean keep = true)
            overrideBuildParameters(boolean override = true)
            parameterDefinitions(Closure parameters) 
            preBuildSteps(Closure steps) 
            postSuccessfulBuildSteps(Closure steps) 
            postBuildSteps(Closure steps) 
            postFailedBuildSteps(Closure steps)
        }
    }
}
```

Configure a release inside a Jenkins job. Requires the [Release Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Release+Plugin).

For details of defining parameters (parameterDefinitions) see [Reference of Parameters](https://github.com/jenkinsci/job-dsl-plugin/wiki/Job-reference#wiki-parameters)

For details of defining steps (preBuildSteps, postSuccessfulBuildSteps, postBuildSteps, postFailedBuildSteps) see [Reference of Build Steps](https://github.com/jenkinsci/job-dsl-plugin/wiki/Job-reference#wiki-build-steps)

Example
```groovy
job {
    ...
    wrappers {
        ...
        release {
            doNotKeepLog()
            overrideBuildParameters()
            parameterDefinitions {
                booleanParam('param', false, 'some boolean build parameter')
            }
            preBuildSteps {
                shell("echo 'hello'")
            }
        }
    }
}
```

(Since 1.22)

## Maven Release
```groovy
job {
    wrappers {
        mavenRelease {
            /**
             * If defined, an environment variable with this name will hold the scm username when triggering a
             * release build (this is the username the user enters when triggering a release build, not the username
             * given to Jenkins' SCM configuration of the job).
             *
             * @param scmUserEnvVar (default: <<empty>>)
             */
            scmUserEnvVar(String scmUserEnvVar)

            /**
             * If defined, an environment variable with this name will hold the scm password when triggering a
             * release build (this is the password the user enters when triggering a release build, not the password
             * given to Jenkins' SCM configuration of the job).
             *
             * As the passed passwords would potentially get written to the logs and therefore visible to users,
             * we recommend you to install the
             * <a href="https://wiki.jenkins-ci.org/display/JENKINS/Mask+Passwords+Plugin">Mask Password Plugin</a>.
             *
             * @param scmPasswordEnvVar (default: <<empty>>)
             */
            scmPasswordEnvVar(String scmPasswordEnvVar)

            /**
             * An environment variable with this name indicates whether the current build is a release build or not.
             * This can be used e.g. within a shell or the conditional buildstep to do pre and post release processing.
             * The value will be boolean (true if it is a release build, false if its not a release build).
             *
             * @param releaseEnvVar (default: "IS_M2RELEASEBUILD")
             */
            releaseEnvVar(String releaseEnvVar)

            /**
             * Enter the goals you wish to use as part of the release process. e.g. "release:prepare release:perform"
             *
             * @param releaseGoals (default: "-Dresume=false release:prepare release:perform")
             */
            releaseGoals(String releaseGoals)

            /**
             * Enter the goals you wish to use as part of the 'dryRun' - to simulate the release build.
             * e.g. "release:prepare -DdryRun=true"
             *
             * @param dryRunGoals (default: "-Dresume=false -DdryRun=true release:prepare")
             */
            dryRunGoals(String dryRunGoals)

            /**
             * Enable this to have the "Select custom SCM comment prefix" option selected by default
             * in the "Perform Maven Release" view.
             *
             * @param selectCustomScmCommentPrefix (default: false)
             */
            selectCustomScmCommentPrefix(boolean selectCustomScmCommentPrefix)

            /**
             * Enable this to have the "Append Jenkins Username" option (part of the "Specify custom SCM comment prefix"
             * configuration) selected by default in the "Perform Maven Release" view.
             *
             * @param selectAppendHudsonUsername (default: false)
             */
            selectAppendHudsonUsername(boolean selectAppendHudsonUsername)

            /**
             * Enable this to have the "specify SCM login/password" option selected by default in the
             * "Perform Maven Release" view.
             *
             * @param selectScmCredentials (default: false)
             */
            selectScmCredentials(boolean selectScmCredentials)

            /**
             * Specify the number of successful release builds to keep forever. A value of -1 will lock all successful
             * release builds, 0 will not lock any builds.
             *
             * @param numberOfReleaseBuildsToKeep (default: 1)
             */
            numberOfReleaseBuildsToKeep(int numberOfReleaseBuildsToKeep)
        }
    }
}
```

Example: using the default values
```groovy
job {
    ...
    wrappers {
        ...
        mavenRelease()
    }
}
```

Example: overwriting the default values
```groovy
job {
    ...
    wrappers {
        ...
        mavenRelease() {
            scmUserEnvVar 'MY_USER_ENV'
            scmPasswordEnvVar 'MY_PASSWORD_ENV'
            releaseEnvVar 'RELEASE_ENV'

            releaseGoals '-DautoVersionSubmodules -DcommitByProject release:prepare release:perform'
            dryRunGoals '-DdryRun=true -DautoVersionSubmodules -DcommitByProject release:prepare'
        
            selectCustomScmCommentPrefix()
            selectAppendHudsonUsername()
            selectScmCredentials()
        
            numberOfReleaseBuildsToKeep 10
        }
    }
}
```

Default values
```groovy
job {
    ...
    wrappers {
        ...
        mavenRelease() {
            scmUserEnvVar ''
            scmPasswordEnvVar ''
            releaseEnvVar 'IS_M2RELEASEBUILD'

            releaseGoals '-Dresume=false release:prepare release:perform'
            dryRunGoals '-Dresume=false -DdryRun=true release:prepare'
        
            selectCustomScmCommentPrefix false
            selectAppendHudsonUsername false
            selectScmCredentials false
        
            numberOfReleaseBuildsToKeep 1
        }
    }
}
```

Configure a maven release inside a Jenkins job. Job type need to be "Maven". Requires the [M2 Release Plugin](https://wiki.jenkins-ci.org/display/JENKINS/M2+Release+Plugin).

(Since 1.22)

## Workspace Cleanup Plugin

```groovy
job {
    wrappers {
        preBuildCleanup {
            includePattern(String pattern)
            excludePattern(String pattern)
            deleteDirectories(boolean deleteDirectories = true)
            cleanupParameter(String parameter)
            deleteCommand(String command)
        }
    }
}
```

Deleted files from the workspace before the build starts. Requires the [Workspace Cleanup Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Workspace+Cleanup+Plugin).

Examples:

```groovy
// cleanup all files
job {
    wrappers {
        preBuildCleanup()
    }
}
```

```groovy
// cleanup all files and directories in target directories, but only if the CLEANUP build parameter is set to 'true'
job {
    wrappers {
        preBuildCleanup {
            includePattern('**/target/**')
            deleteDirectories()
            cleanupParameter('CLEANUP')
        }
    }
}
```

(since 1.22)

# Build Steps

Adds step block to contain an ordered list of build steps. Cannot be used for jobs with type 'maven'.

## Shell command
```groovy
shell(String commandStr)
```

Runs a shell command.

## Batch File
```groovy
batchFile(String commandStr)
```

Supports running a Windows batch file as a build step.

## Gradle
```groovy
gradle(String tasksArg = null, String switchesArg = null, Boolean useWrapperArg = true, Closure configure = null)
```

Runs Gradle, defaulting to the Gradle Wrapper. Configure block is handed a hudson.plugins.gradle.Gradle node.

closure gradle command, which support all currently availibe jenkins/gradle plugin options:
```groovy
gradle {
    tasks('task1')
    switches('--profile -Pasd=1')
    useWrapper(true)
    description('gradle task for executing task1 with profile')
    rootBuildScriptDir('some/path/where/you/gradle/project/reside')
    buildFile('other')
    fromRootBuildScriptDir(true)
    makeExecutable(true)
    gradleName('jenkins-gradle-combobox-item-name')
    configure {
      it / 'unknownOption' << 'on'
    }
}
```



## Maven
```groovy
maven(String targetsArg = null, String pomArg = null, Closure configure = null)

maven {                                               // since 1.20; all methods are optional
    goals(String goals)                               // the goals to run, multiple calls will be accumulated
    rootPOM(String fileName)                          // path to the POM
    mavenOpts(String options)                         // JVM options, multiple calls will be accumulated
    localRepository(LocalRepositoryLocation location) // can be either LocalToWorkspace or LocalToExecutor (default)
    mavenInstallation(String name)                    // name of the Maven installation to use
    properties(Map properties)                        // since 1.21; add (system)-properties
    property(String key, String value)                // since 1.21; add a (system)-property
    configure(Closure configure)                      // configure block
}
```

Runs Apache Maven. Configure block is handed hudson.tasks.Maven.

Examples:

```groovy
maven('verify')

maven('clean verify', 'module-a/pom.xml')

maven {
    goals('clean')
    goals('verify')
    mavenOpts('-Xms256m')
    mavenOpts('-Xmx512m')
    localRepository(LocalToWorkspace)
    properties skipTests: true
    mavenInstallation('Maven 3.1.1')
}
```

## Ant
```groovy
ant(String targetsArg = null, String buildFileArg = null, String antInstallation = '(Default)', Closure antClosure = null) {
    target(targetName)
    targets(Iterable<String> targets)
    prop(Object key, Object value)
    props(Map<String, String> map)
    buildFile(String buildFile)
    javaOpt(String opt)
    javaOpts(Iterable<String> opts)
    antInstallation(String antInstallationName)
}
```

Runs Apache Ant. Ant Closure block can be used for all configuration, and it's the only way to add Java Options and System
Properties.
* Target argument - Available as an argument or closure method, each call is cumulative. The target argument can be space or newline delimited.
* Properties - Available via closure method calls. All calls are cumulative. A Map is used to back it, which will enforce unique keys for the properties.
* Ant Installation - Available as an argument or closure method. Refers to the pull down box in the UI to select which installation of Ant to use, specify the exact string seen in the UI. The last call will be the one used.
* Build File - Available as an argument or closure method. Specifies which build.xml file to use, this should be relative to the workspace.
* Java Options - Available via closure method calls. Arguments to be passed directly to the JVM.

From the unit tests, it'll use the targets "build test publish deploy":

```groovy
steps {
    ant('build') {
        target 'test'
        targets(['publish', 'deploy'])
        prop 'logging', 'info'
        props 'test.threads': 10, 'input.status':'release'
        buildFile 'dir1/build.xml'
        javaOpt '-Xmx1g'
        javaOpts(['-Dprop2=value2', '-Dprop3=value3'])
        antInstallation 'Ant 1.8'
    }
}
```

## SBT

Executes the Scala Build Tool (SBT) as a build step. (Since 1.16)

```groovy
steps {
    sbt('SBT 0.12.3',              // name of SBT installation to use (required)
        'test',                    // actions to execute (optional)
        '-Dsbt.log.noformat=true', // additional system properties (optional)
        '-Xmx2G -Xms512M',         // JVM options (optional)
        'subproject')              // subdirectory to work in (optional)
}
```

Currently all options are available via the DSL. If new plugin versions should introduce new parameters there is the possiblilty to configure them via a configure closure:

```groovy
sbt(/*standard parameters here*/) {
    newParameter 'foo'
}
```

## DSL
```groovy
dsl {
    removeAction(String removeAction)      // one of: 'DISABLE', 'IGNORE', 'DELETE'
    external (String... dslFilenames)      // one or more filenames/-paths in the workspace containing DSLs
    text (String dslSpecification)         // direct specification of DSL as String
    ignoreExisting(boolean ignoreExisting) // flag if to ignore existing jobs
}

/* equivalent calls as parameters instead of closure */
def dsl(String scriptText, String removedJobAction = null, boolean ignoreExisting = false)
def dsl(Collection<String> externalScripts, String removedJobAction = null, boolean ignoreExisting = false)
```

The DSL build step creates a new job that in turn is able to generate other jobs. Particularly useful to generate a monitoring Job for things like feature/release branches. (Available since 1.16)

Sample definition using several DSL files:
```groovy
dsl {
    removeAction 'DISABLE'
    external 'some-dsl.groovy','some-other-dsl.groovy'
    external 'still-another-dsl.groovy'
    ignoreExisting true
}

/* same definition using parameters instead of closure */
dsl(['some-dsl.groovy','some-other-dsl.groovy','still-another-dsl.groovy'], 'DISABLE', true)
```

Another sample that specifies the DSL text directly:
```groovy
dsl {
    removeAction('DELETE')
    text '''
job {
    foo()
    bar {
        baz()
    }
} 
}

/* same definition using parameters instead of closure */
dsl('''
job {
    foo()
    bar {
        baz()
    }
}
''', 'DELETE')
```

## Copy Artifacts

```groovy
copyArtifacts(String jobName, String includeGlob, String targetPath = '', boolean flattenFiles = false, boolean optionalAllowed = false, Closure copyArtifactClosure) {
    upstreamBuild(boolean fallback = false) // Upstream build that triggered this job
    latestSuccessful() // Latest successful build
    latestSaved() // Latest saved build (marked "keep forever")
    permalink(String linkName) // Specified by permalink: lastBuild, lastStableBuild
    buildNumber(int buildNumber) // Specific Build
    buildNumber(String buildNumber) // Specific Build
    workspace() // Copy from WORKSPACE of latest completed build
    buildParameter(String parameterName) // Specified by build parameter
}
```

Supports the Copy Artifact plugin. As per the plugin, the input glob is for files in the workspace. The methods in the closure are considered the selectors, of which only one can be used.

## Groovy
```groovy
groovyCommand(String commandStr = null, String groovyInstallation = '(Default)', Closure groovyClosure = null) {   
    groovyParam(String param)
    groovyParams(Iterable<String> params)
    scriptParam(String param)
    scriptParams(Iterable<String> params)
    prop(String key, String value)
    props(Map<String, String> map)
    javaOpt(String opt)
    javaOpts(Iterable<String> opts)
    groovyInstallation(String groovyInstallationName)
    classpath(String classpathEntry)
}
groovyScriptFile(String fileName = null, String groovyInstallation = '(Default)', Closure groovyClosure = null) {   
    groovyParam(String param)
    groovyParams(Iterable<String> params)
    scriptParam(String param)
    scriptParams(Iterable<String> params)
    prop(String key, String value)
    props(Map<String, String> map)
    javaOpt(String opt)
    javaOpts(Iterable<String> opts)
    groovyInstallation(String groovyInstallationName)
    classpath(String classpathEntry)
}
```

Runs a Groovy script which can either be passed inline ('groovyCommand' method) or by specifying a script file ('groovyScriptFile' method). The closure block can be used for all configuration. All calls are cumulative except for 'groovyInstallation' where the last call will be used.
* Groovy Parameters - Specifies arguments for the Groovy executable.
* Script Parameters - Specifies arguments for the script.
* Properties - Shortcut to define '-D' parameters.
* Groovy Installation - Also available as an argument. Refers to the pull down box in the UI to select which installation of Groovy to use, specify the exact string seen in the UI.
* Java Options - Arguments to be passed directly to the JVM.
* Classpath - Defines the classpath for the script.
 
## System Groovy Scripts
```groovy
systemGroovyCommand(String commandStr, Closure systemGroovyClosure = null) {
    binding(String name, String value)
    classpath(String classpathEntry)
}
systemGroovyScriptFile(String fileName, Closure systemGroovyClosure = null) {
    binding(String name, String value)
    classpath(String classpathEntry)
}
```

Runs a system groovy script, which is executed inside the Jenkins master. Thus it will have access to all the internal objects of Jenkins and can be used to alter the state of Jenkins. The `systemGroovyCommand` method will run an inline script and the `systemGroovyScriptFile` will execute a script file from the generated job's workspace. The closure block can be used to add variable bindings and extra classpath entries for a script. The methods in the closure block can be called multiple times to add any number of bindings or classpath entries. The Groovy plugin must be installed to use these build steps.

## Grails
```groovy
grails {
    target(String targetName)              // a single target to run
    targets(Iterable<String> targets)      // multiple targets to tun
    name(String grailsName)                // the name of the Grails installation
    grailsWorkDir(String grailsWorkDir)    // grails.work.dir system property
    projectWorkDir(String projectWorkDir)  // grails.project.work.dir system property
    projectBaseDir(String projectBaseDir)  // path to the root of the Grails project
    serverPort(String serverPort)          // server.port system property
    prop(String key, String value)         // a single system property key and value
    props(Map<String, String> map)         // a map of system property key and values
    forceUpgrade(boolean forceUpgrade)     // run 'grails upgrade --non-interactive' first
    nonInteractive(boolean nonInteractive) // append --non-interactive to all build targets
    useWrapper(boolean useWrapper)         // use Grails wrapper to execute targets
}

// additional methods
grails(String targets, Closure grailsClosure)                                    // space-separated targets
grails(String targets, boolean useWrapper = false, Closure grailsClosure = null) // space-separated targets
```

Supports the Grails plugin. Only targets field is required. To pass arguments to a particular target, surround the target and its arguments with double quotes.

## Environment Variables
```groovy
job {
  steps {
    environmentVariables {
      env(Object key, Object value)
      envs(Map<Object, Object> map) 
      propertiesFile(String filePath)
    }
  }
}
```

Injects environment variables into the build. Requires the [EnvInject plugin](https://wiki.jenkins-ci.org/display/JENKINS/EnvInject+Plugin).

(Since 1.21)

# Multijob Phase

```
phase(String name, String continuationConditionArg = 'SUCCESSFUL', Closure phaseClosure = null) {
    phaseName(String phaseName)
    continuationCondition(String continuationCondition)
    job(String jobName, boolean currentJobParameters = true, boolean exposedScm = true, Closure phaseJobClosure = null)  {
        currentJobParameters(boolean currentJobParameters = true) 
        exposedScm(boolean exposedScm = true)
        boolParam(String paramName, boolean defaultValue = false)
        fileParam(String propertyFile, boolean failTriggerOnMissing = false)
        sameNode(boolean nodeParam = true) 
        matrixParam(String filter)
        subversionRevision(boolean includeUpstreamParameters = false)
        gitRevision(boolean combineQueuedCommits = false) 
        prop(Object key, Object value)
        props(Map<String, String> map)
    }
}
```

Phases allow jobs to be group together to be run in parallel, they only exist in a Multijob typed job. The name and continuationConditionArg can be set directly in the phase method or in the closure. The job method is used to list each job in the phase, and hence can be called multiple times. Each call can be further configured with the parameters which will be sent to it. The parameters are show above and documented in different parts of this page. See below for an example of multiple phases strung together:

```
job(type: Multijob) {
    steps {
        phase() {
            phaseName 'Second'
            job('JobZ') {
                fileParam('my1.properties')
                fileParam('my2.properties')
            }
        }
        phase('Third') {
            job('JobA')
            job('JobB')
            job('JobC')
        }
        phase('Fourth') {
            job('JobD', false, true) {
                boolParam('cParam', true)
                fileParam('my.properties')
                sameNode()
                matrixParam('it.name=="hello"')
                subversionRevision()
                gitRevision()
                prop('prop1', 'value1')
            }
        }
   }
}
```

# [Prerequisite Build Step](https://wiki.jenkins-ci.org/display/JENKINS/Prerequisite+build+step+plugin)

```
prerequisite(String projectList = '', boolean warningOnlyBool = false)
```

Arguments:
* `projectList` A comma delimited list of jobs to check. 
* `warningOnlyBool` If set to true then the build will not be failed even if the checks are failed

When a job is checked the following conditions must be validated before the job is marked passed.
* The job must exist
* The job must have been built at least once
* The job cannot currently be building
* The last completed build must have resulted in a stable (blue) build.

(Since 1.19)

# [Parameterized Trigger as Build Step](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Trigger+Plugin)

```groovy
downstreamParameterized(Closure downstreamClosure) {
     trigger(String projects, String condition = 'SUCCESS', boolean triggerWithNoParameters = false, Map<String, String> blockingThresholds = [:], Closure downstreamTriggerClosure = null) {
        currentBuild() // Current build parameters
        propertiesFile(String propFile) // Parameters from properties file
        gitRevision(boolean combineQueuedCommits = false) // Pass-through Git commit that was built
        predefinedProp(String key, String value) // Predefined properties
        predefinedProps(Map<String, String> predefinedPropsMap)
        predefinedProps(String predefinedProps) // Newline separated
        matrixSubset(String groovyFilter) // Restrict matrix execution to a subset
        subversionRevision() // Subversion Revision
     }
}
```

Supports <a href="https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Trigger+Plugin">the Parameterized Trigger plugin</a>. The plugin is configured by adding triggers
to other projects, multiple triggers can be specified. The projects arg is a comma separated list of downstream projects. The condition arg is one of these
possible values: SUCCESS, UNSTABLE, UNSTABLE_OR_BETTER, UNSTABLE_OR_WORSE, FAILED.  The methods inside the downstreamTriggerClosure are optional, though it
makes the most sense to call at least one.  Each one is relatively self documenting, mapping directly to what is seen in the UI. The predefinedProp and
predefinedProps methods are used to accumulate properties, meaning that they can be called multiple times to build a superset of properties.

In addition to the above (which is common to both the build step and publisher use cases of the Parameterized Trigger plugin), there is the blockingThresholds map argument to the trigger. This is optional, and if given, the build will block until the child build has completed. Then the build status will be updated depending on the blockingThresholds, which are the following:

* buildStepFailure
* failure
* unstable

These can each be set to any of the allowed build statuses ('SUCCESS', 'UNSTABLE', or 'FAILURE'). The parent build's status will be set to failure (or unstable, if that's configured) if the child build's status is equal to or worse than the configured status, while the buildStepFailure threshold allows you to set the parent build's status but continue to further steps as if it hadn't failed.

Examples:
```groovy
steps {
    downstreamParameterized {
        trigger('Project1, Project2', 'UNSTABLE_OR_BETTER', true,
                    ["buildStepFailure": "FAILURE",
                            "failure": "FAILURE",
                            "unstable": "UNSTABLE"]) {
            currentBuild() // Current build parameters
            propertiesFile('dir/my.properties') // Parameters from properties file
            gitRevision(false) // Pass-through Git commit that was built
            predefinedProp('key1', 'value1') // Predefined properties
            predefinedProps([key2: 'value2', key3: 'value3'])
            predefinedProps('key4=value4\nkey5=value5') // Newline separated
            matrixSubset('label=="${TARGET}"') // Restrict matrix execution to a subset
            subversionRevision() // Subversion Revision
        }
        trigger('Project2') {
            currentBuild()
        }
    }
}
```

(since 1.20)

## [Conditional BuildStep Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Conditional+BuildStep+Plugin)

```groovy
conditionalSteps {
    condition {
        // Only one condition is allowed.
        alwaysRun() // Run no matter what
        neverRun() // Never run
        booleanCondition(String token) // Run if the token evaluates to true.
        stringsMatch(String arg1, String arg2, boolean ignoreCase) // Run if the two strings match
        cause(String buildCause, boolean exclusiveCondition) // Run if the build cause matches the given string
        expression(String expression, String label) // Run if the regular expression matches the label
        time(String earliest, String latest, boolean useBuildTime) // Run if the current (or build) time is between the given dates.
        status(String worstResult, String bestResult) // Run if worstResult <= (current build status) <= bestResult
    }
    runner(String runner) // How to evaluate the results of a failure in the conditional step
    (one or more build steps)
}
```

See the [Run Condition Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Run+Condition+Plugin) for details on the run conditions - note that not all run conditions supported by the Run Condition Plugin are supported here yet.

The runner can be any one of "Fail", "Unstable", "RunUnstable", "Run", "DontRun".

Examples:
```groovy
steps {
    conditionalSteps {
        condition {
            stringsMatch('${SOME_PARAMETER}', 'pants', false)
        }
        runner("Fail")
        shell("echo 'just one step')
    }
}
```

```groovy
steps {
    conditionalSteps {
        condition {
            time("9:00", "13:00", false)
        }
        runner("Unstable")
        shell("echo 'a first step')
        ant('build') {
            target 'test'
        }
    }
}
```

(Since 1.20)

## Parameterized Remote Trigger

````groovy
job {
    steps {
        remoteTrigger(String remoteJenkinsName, String jobName) {
            parameter(String name, String value)
            parameters(Map<String, String> parameters)
        }
    }
}
```

Triggers a job on another Jenkins instance. Requires the [Parameterized Remote Trigger Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Remote+Trigger+Plugin).

Examples:

````groovy
// start the job 'test-flow' on the Jenkins instance named 'test-ci' without parameters
job {
    steps {
        remoteTrigger('test-ci', 'test-flow')
    }
}
```

````groovy
// start the job 'test-flow' on the Jenkins instance named 'test-ci' with three parameters
job {
    steps {
        remoteTrigger('test-ci', 'test-flow') {
            parameter('VERSION', '$PIPELINE_VERSION')
            parameters(BRANCH: 'feature-A', STAGING_REPO_ID: '41234232')
        }
    }
}
```

(since 1.22)

# Publishers

Block to contain list of publishers.

## Extended Email Plugin
```groovy
extendedEmail(String recipients = null, String subjectTemplate = null, String contentTemplate = null, Closure emailClosure = null)
```

Supports the Extended Email plugin. DSL methods works like the gerrit plugin, providing its own block to help set it up. The emailClosure is primarily used to specify the triggers, which is optional. Its definition:

```groovy
extendedEmail {
    trigger(String triggerName, String subject = null, String body = null, String recipientList = null,
            Boolean sendToDevelopers = null, Boolean sendToRequester = null, includeCulprits = null, Boolean sendToRecipientList = null)
    trigger(Map args)
    configure(Closure configureClosure) // Handed hudson.plugins.emailext.ExtendedEmailPublisher
}
```

The first trigger method allow complete control of the email going out, and maps directly to what is seen in the config.xml of a job. The triggerName needs to be one of these values: 'PreBuild', 'StillUnstable', 'Fixed', 'Success', 'StillFailing', 'Improvement', 'Failure', 'Regression', 'Aborted', 'NotBuilt', 'FirstFailure', 'Unstable'. Those names come from classes prefix with 'hudson.plugins.emailext.plugins.trigger.' and appended with Trigger. The second form of trigger, uses the names from the first, but can be called with a Map syntax, so that values can be left out more easily. To help explain it, here an example from the unite tests:

```groovy
extendedEmail('me@halfempty.org', 'Oops', 'Something broken') {
    trigger('PreBuild')
    trigger(triggerName: 'StillUnstable', subject: 'Subject', body:'Body', recipientList:'RecipientList',
            sendToDevelopers: true, sendToRequester: true, includeCulprits: true, sendToRecipientList: false)
    configure { node ->
        node / contentType << 'html'
    }
}
```

## Mailer Tasks
```groovy
mailer(String recipients, String dontNotifyEveryUnstableBuildBoolean = false, String sendToIndividualsBoolean = false)
```

This is the default mailer task. Specify the recipients, whether to flame on unstable builds, and whether to send email to individuals who broke the build. Note the double negative in the dontNotifyEveryUnstableBuild condition. If you want notification on every unstable build, keep it false. 
Simple example:

```groovy
publishers {
    mailer('me@example.com', true, true)
}
```

(Since 1.17)

## Archive Artifacts
```groovy
archiveArtifacts(String glob, String excludeGlob = null, Boolean latestOnlyBoolean = false)
```

Supports archiving artifacts with each build. Simple example:

```groovy
publishers {
    archiveArtifacts 'build/test-output/**/*.html'
}
```

Since 1.20, an alternate form is also acceptable:

```groovy
archiveArtifacts {
    pattern(String pattern)
    exclude(String excludePattern = '')
    latestOnly(bool latestOnly = true) // Will be false if function is not called.
    allowEmpty(bool allowEmpty = true) // Will be false if function is not called. Note: not available with jenkins <= 1.480
}
```

## Fingerprint / KeepDependencies
```groovy
fingerprint(String targets, boolean recordBuildArtifacts = false)
```

Activates fingerprinting for the build. 
If recordBuildArtifacts evaluates to "true", then all archived artifacts are also fingerprinted.
Moreover, the option to keep the build logs of dependencies can be set at the top level via:
```groovy
keepDependencies(boolean keep = true)
```

Examples:

```groovy
keepDependencies()
publishers {
    fingerprint('**/*.jar')
}
```

## Build Description Setter

Automatically sets a description for the build after it has completed. Requires the [Description Setter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Description+Setter+Plugin).

```groovy
buildDescription(String regularExpression, String description = '', String regularExpressionForFailed = '', String descriptionForFailed = '', boolean multiConfigurationBuild = false)
```

Arguments:
* `regularExpression` If configured, the regular expression will be applied to each line in the build log. A description will be set based on the first match.
* `description` The description to set on the build. If a regular expression is configured, every instance of \n will be replaced with the n-th group of the regular expression match. If the description is empty, the first group selected by the regular expression will be used as description. If no regular expression is configured, the description is taken verbatim.
* `regularExpressionForFailed` If set, this regular expression will be used instead of the regular regular expression when the build has failed.
* `descriptionForFailed` The description to use for failed builds.
* `multiConfigurationBuild` Also set the description on a multi-configuration build. The first description found for any of the invididual builds will be used as description for the multi-configuration build.

The following example sets build description to the project version in case the output contains the line `Building my.project.name 0.4.0`:

```groovy
publishers {
  buildDescription(/.*Building [^\s]* ([^\s]*)/)
}
```

The next example sets the build description to a values defined by a build parameter or environment variable called `BRANCH`:

```groovy
publishers {
  buildDescription('', '${BRANCH}')
}
```

(Since 1.17)

## Archive JUnit
```groovy
archiveJunit(String glob, boolean retainLongStdout = false, boolean allowClaimingOfFailedTests = false, boolean publishTestAttachments = false)
```

Supports archiving JUNit results for each build.

## HTML Publisher
```groovy
publishHtml {
    report(String reportDir, String reportName = null, String reportFiles = 'index.html', Boolean keepAll = false)
    report(Map args) // same names as the method above
}

```

Provides context to add html reports to be archive. The report method can be called multiple times in the closure. Simple
example with variations on how to call the report method:

```groovy
publishers {
    publishHtml {
        report('build/test-output/*', 'Test Output')
        report 'build/coverage/*', 'Coverage Report', 'coverage.html' // Without parens
        report reportName: 'Gradle Tests', reportDir: 'test/*', keepAll: true // Map synxtax
    }
}
```

## Jabber Publisher
```groovy
publishJabber(String target, String strategyName, String channelNotificationName, Closure jabberClosure) {
    strategyName 'ALL' // ALL, FAILURE_AND_FIXED, ANY_FAILURE, STATECHANGE_ONLY
    notifyOnBuildStart false
    notifySuspects false
    notifyCulprits false
    notifyFixers false
    notifyUpstreamCommitters false
    channelNotificationName 'Default' // Default, SummaryOnly, BuildParameters, PrintFailingTests
}
```

Supports <a href="https://wiki.jenkins-ci.org/display/JENKINS/Jabber+Plugin">Jabber Plugin</a>. A few arguments can be specified in the method call or in the closure.

## SCP Publisher
```groovy
publishScp(String site, Closure scpClosure) {
    entry(String source, String destination = '', boolean keepHierarchy = false)
}
```

Supports <a href="https://wiki.jenkins-ci.org/display/JENKINS/SCP+plugin">SCP Plugin</a>. First arg, site, is specified globally by the plugin. Each entry is
individually specified in the closure block, e.g. entry can be called multiple times.

## CloneWorkspace Publisher
```groovy
publishCloneWorkspace(String workspaceGlob, String workspaceExcludeGlob = '', String criteria = 'Any', String archiveMethod = 'TAR', boolean overrideDefaultExcludes = false, Closure cloneWorkspaceClosure = null) {}
```

Supports the <a href="https://wiki.jenkins-ci.org/display/JENKINS/Clone+Workspace+SCM+Plugin">Clone Workspace SCM Plugin</a>.

Due to the simplicity of this publisher, the closure support is purely provided for creating very specific configs.  Usually the non-closure variants will suffice - the simplest purely requiring the workspaceGlob alone, and the other (equivalent to pressing the "Advanced" button in the Jenkins UI) provided all settings.

## Downstream
```groovy
downstream(String projectName, String thresholdName = 'SUCCESS')
```

Specifies a downstream job. The second arg, thresholdName, can be one of three values: 'SUCCESS', 'UNSTABLE' or 'FAILURE'.

## Extended Downstream
```groovy
downstreamParameterized(Closure downstreamClosure) {
     trigger(String projects, String condition = 'SUCCESS', boolean triggerWithNoParameters = false, Closure downstreamTriggerClosure = null) {
        currentBuild() // Current build parameters
        propertiesFile(String propFile) // Parameters from properties file
        gitRevision(boolean combineQueuedCommits = false) // Pass-through Git commit that was built
        predefinedProp(String key, String value) // Predefined properties
        predefinedProps(Map<String, String> predefinedPropsMap)
        predefinedProps(String predefinedProps) // Newline separated
        matrixSubset(String groovyFilter) // Restrict matrix execution to a subset
        subversionRevision() // Subversion Revision
     }
}
```

Supports <a href="https://wiki.jenkins-ci.org/display/JENKINS/Downstream-Ext+Plugin">Downstream-Ext plugin</a>. The plugin is configured by adding triggers
to other projects, multiple triggers can be specified. The projects arg is a comma separated list of downstream projects. The condition arg is one of these
possible values: SUCCESS, UNSTABLE, UNSTABLE_OR_BETTER, UNSTABLE_OR_WORSE, FAILED.  The methods inside the downstreamTriggerClosure are optional, though it
makes the most sense to call at least one.  Each one is relatively self documenting, mapping directly to what is seen in the UI. The predefinedProp and
predefinedProps methods are used to accumulate properties, meaning that they can be called multiple times to build a superset of properties.

Examples:
```groovy
publishers {
    downstreamParameterized {
        trigger('Project1, Project2', 'UNSTABLE_OR_BETTER', true) {
            currentBuild() // Current build parameters
            propertiesFile('dir/my.properties') // Parameters from properties file
            gitRevision(false) // Pass-through Git commit that was built
            predefinedProp('key1', 'value1') // Predefined properties
            predefinedProps([key2: 'value2', key3: 'value3'])
            predefinedProps('key4=value4\nkey5=value5') // Newline separated
            matrixSubset('label=="${TARGET}"') // Restrict matrix execution to a subset
            subversionRevision() // Subversion Revision
        }
        trigger('Project2') {
            currentBuild()
        }
    }
}
```

## Violations Plugin
```groovy
violations(int perFileDisplayLimit = 100, Closure violationsClosure = null) {
    sourcePathPattern(String sourcePathPattern)
    fauxProjectPath(String fauxProjectPath)
    perFileDisplayLimit(Integer perFileDisplayLimit)
    sourceEncoding(String encoding = 'default')
    *(Integer min = 10, Integer max = 999, Integer unstable = 999, String pattern = null)
}
```
Supports <a href="https://wiki.jenkins-ci.org/display/JENKINS/Violations">Violations Plugin</a>. The violationsClosure is crucial to configure this DSL method.
For each supported type, you specify it and a few option parameters, this is represented above with the asterisk (*). If a type isn't specified, then it uses
defaults. The following example wil

```groovy
violations(50) {
   sourcePathPattern 'source pattern'
   fauxProjectPath 'faux path'
   perFileDisplayLimit 51
   checkstyle(10, 11, 10, 'test-report/*.xml')
   findbugs(12, 13, 12)
   jshint(10, 11, 10, 'test-report/*.xml')
}
```

## Chuck Norris

```groovy
chucknorris()
```

Enables the Cordell Walker plugin.

## IRC

Interface for the [IRC plugin](https://wiki.jenkins-ci.org/display/JENKINS/IRC+Plugin). All the options can be set and multiple channels can be added. The channel calls support named parameters as well. The notification settings can be disabled with passing false as a parameter. A complete example:

```groovy
irc {
  channel('#channel1', 'password1', true)
  channel(name: '#channel2', password: 'password2', notificationOnly: false)
  notifyScmCommitters()
  notifyScmCulprits()
  notifyUpstreamCommitters(false)
  notifyScmFixers()
  strategy('ALL')
  notificationMessage('SummaryOnly')
}
```

## Cobertura coverage report

Supports the [Cobertura Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Cobertura+Plugin). Only the Cobertura xml reportfile pattern is required to locate all the generated reports. Advanced options are all set to defaults.

```groovy
cobertura(xmlReportFilePattern)
```

The previous example is basically equivalent to the following more verbose one, where all the default settings are visible.

```groovy
cobertura(xmlReportFilePattern) {
  onlyStable(false)    // Include only stable builds, i.e. exclude unstable and failed ones.
  failUnhealthy(false) // Unhealthy projects will be failed.
  failUnstable(false)  // Unstable projects will be failed.
  autoUpdateHealth(false)    // Auto update threshold for health on successful build.
  autoUpdateStability(false) // Auto update threshold for stability on successful build.
  zoomCoverageChart(false)   // Zoom the coverage chart and crop area below the minimum and above the maximum coverage of the past reports.
  failNoReports(true) // Fail builds if no coverage reports have been found.
  sourceEncoding('ASCII') // Character encoding of source files
  // The following targets are added by default to check the method, line and conditional level coverage:
  methodTarget(80, 0, 0) 
  lineTarget(80, 0, 0)   
  conditionalTarget(70, 0, 0)
}
```

### More about targets

Targets are used to mark the build as healthy/unhealthy/unstable based on given tresholds. Targets can be set separately for conditional, line, method, class, file and the package level.

Targets can be tuned using the following helper methods:
```groovy
conditionalTarget(healthy, unhealthy, failing)
lineTarget(healthy, unhealthy, failing)
methodTarget(healthy, unhealthy, failing)
classTarget(healthy, unhealthy, failing)
fileTarget(healthy, unhealthy, failing)
packageTarget(healthy, unhealthy, failing)
```

Each of the 3 parameters represent a percentage treshold. They have the following meaning:
* healthy: Report health as 100% when coverage is greater than {healthy}% 
* unhealthy: Report health as 0% when coverage is less than {unhealthy}%
* failing: Mark the build as unstable when coverage is less than {failing}% 

## Allow Broken Build Claiming

```groovy
allowBrokenBuildClaiming()
```

Activates broken build claiming for the [Claim plugin](https://wiki.jenkins-ci.org/display/JENKINS/Claim+plugin).

(Since 1.17)

## Jacoco

This plugin allows you to capture code coverage report from the [JaCoCo Plugin](https://wiki.jenkins-ci.org/display/JENKINS/JaCoCo+Plugin). Jenkins will generate the trend report of coverage.

```
//Shown with defaults
jacocoCodeCoverage {
    execPattern '**/target/**.exec'
    classPattern '**/classes'
    sourcePattern '**/src/main/java'
    inclusionPattern '**/*.class'
    exclusionPattern '**/*Test*'
    minimumInstructionCoverage '0'
    minimumBranchCoverage '0'
    minimumComplexityCoverage '0' 
    minimumLineCoverage '0' 
    minimumMethodCoverage '0' 
    minimumClassCoverage '0' 
    maximumInstructionCoverage '0' 
    maximumBranchCoverage '0' 
    maximumComplexityCoverage '0' 
    maximumLineCoverage '0' 
    maximumMethodCoverage '0' 
    maximumClassCoverage '0'
    changeBuildStatus false // introduced in 1.22
}
```

Simplest usage will output with the defaults above
```
jacocoCodeCoverage()
```

`changeBuildStatus` was introduced in Jenkins Job DSL 1.22 (and in the [Jacoco Code Coverage plugin](https://wiki.jenkins-ci.org/display/JENKINS/JaCoCo+Plugin) as of 10.0.13). If not explicitly set, no value will be written to the XML for older Jacoco plugin users. If called with no arguments, it's value will be set to true.

(Since 1.17)

## [Static Code Analysis Plugins](https://wiki.jenkins-ci.org/display/JENKINS/Static+Code+Analysis+Plug-ins)

The static code analysis plugins all take some form of the staticAnalysisClosure. The closure is used to configure the (common) properties shown in the advanced section of the configuration of the corresponding plugin.

(Since 1.17)

```
The closure can look like this (example for the pmd plugin):
```groovy
publishers {
  pmd('**/*.pmd') {
    healthLimits 3, 20
    thresholdLimit 'high'
    defaultEncoding 'UTF-8'
    canRunOnFailed true
    useStableBuildAsReference true
    useDeltaValues true
    computeNew true
    shouldDetectModules true
    thresholds(
      unstableTotal: [all: 1, high: 2, normal: 3, low: 4],
      failedTotal:   [all: 5, high: 6, normal: 7, low: 8],
      unstableNew:   [all: 9, high: 10, normal: 11, low: 12],
      failedNew:     [all: 13, high: 14, normal: 15, low: 16]
    )
  }
}
```

The argument above is the pattern to the pmd files.

ComputeNew is set automatically if the unstableNew or the failedNew threshold is used.

In the examples of the concrete plugins, only a part of the closure is shown

### [Findbugs](https://wiki.jenkins-ci.org/display/JENKINS/FindBugs+Plugin)
```groovy
publishers {
  findbugs('**/findbugsXml.xml', true) {
    thresholds(
      unstableTotal: [all: 1, high: 2, normal: 3, low: 4]
    )    
  }
}
```

The arguments here are in order:
* (String) the findbugs-files to parse
* (boolean) use the findbugs rank for the priority, default to false

### [Pmd](https://wiki.jenkins-ci.org/display/JENKINS/PMD+Plugin)
```groovy
publishers {
  pmd('**/pmd.xml') {
    shouldDetectModules true
  }
}
```

### [Checkstyle](https://wiki.jenkins-ci.org/display/JENKINS/Checkstyle+Plugin)
```groovy
publishers {
  checkstyle('**/checkstyle-result.xml') {
    shouldDetectModules true
  }
}
```

### [JsHint](https://wiki.jenkins-ci.org/display/JENKINS/JSHint+Checkstyle+Plugin)
```groovy
publishers {
  jshint('**/jshint-result.xml') {
    shouldDetectModules true
  }
}
```

### [DRY](https://wiki.jenkins-ci.org/display/JENKINS/DRY+Plugin) 
```groovy
publishers {
  dry('**/cpd.xml', 80, 20) {
    useStableBuildAsReference true
  }
}
```

The arguments here are in order:
* cpd-files to parse
* threshold of duplicated lines for high priority
* threshold of duplicated lines for normal priority

### [Task Scanner](https://wiki.jenkins-ci.org/display/JENKINS/Task+Scanner+Plugin)
```groovy
publishers {
  tasks('**/cpd.xml', '**/*.xml', 'FIXME', 'TODO', 'LOW', true) {
    thresholdLimit 'high'
    defaultEncoding 'UTF-8'
  }
}
```

The arguments here are in order:
* include pattern
* exclude pattern
* high priority text
* normal priority text
* low priority text
* ignore case

### [CCM](https://wiki.jenkins-ci.org/display/JENKINS/CCM+Plugin)
```groovy
publishers {
  ccm('**/ccm.xml')
}
```

### [Android Lint](https://wiki.jenkins-ci.org/display/JENKINS/Android+Lint+Plugin)
```groovy
publishers {
  androidLint('**/lint-results.xml') {
    shouldDetectModules true
  }
}
```

### [OWASP Dependency Check](https://wiki.jenkins-ci.org/display/JENKINS/OWASP+Dependency-Check+Plugin)
```groovy
publishers {
  dependencyCheck('**/DependencyCheck-Report.xml') {
    shouldDetectModules true
  }
}
```

### [Compiler Warnings](https://wiki.jenkins-ci.org/display/JENKINS/Warnings+Plugin)
```groovy
publishers {
  warnings(['Java Compiler (javac)'], ['Java Compiler (javac)': '**/*.log']) {
    includePattern '.*include.*'
    excludePattern '.*exclude.*'
    resolveRelativePaths true
  }
}
```

The warnings plugin has additional method arguments:
* a list of the console parsers - each entry is the name of the parser
* a map of the log parsers
** the key is the name of the parser
** the value are the files to scan

Moreover, the warningsClosure takes, additional to all the options from the staticAnalysisClosure, three more options:
* includePattern
* excludePattern
* resolveRelativePaths

## Text Finder

Searches for keywords in files or the console log and uses that to downgrade a build to be unstable or a failure. Requires the [Text Finder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Text-finder+Plugin).

```groovy
textFinder(String regularExpression, String fileSet = '', boolean alsoCheckConsoleOutput = false, boolean succeedIfFound = false, unstableIfFound = false)
```

Arguments:
* `regularExpression` The regular expression used to search in files or the console output. Will be applied to each line.
* `fileSet` The path to the files in which to search, relative to the workspace root. This can use wildcards like `logs/**/*/*.txt`. Leave this empty if you don't want to scan any files (usually combined with `alsoCheckConsoleOutput`).
* `alsoCheckConsoleOutput ` If set, the regular expression will be used to search the console log.
* `succeedIfFound` If set and the regular expression matched, the build is forced to succeed.
* `unstableIfFound` If set, the build is marked as unstable instead of failing the build.

The example marks a build as unstable if `[ERROR]` has been in found in any `.log` file:

```groovy
publishers {
  textFinder(/[ERROR]/, '**/*.log', false, false, true)
}
```

(Since 1.19)

## [Post Build Task](https://wiki.jenkins-ci.org/display/JENKINS/Post+build+task)

Searches for a regular expression in the console log and, if matched, executes a script. Requires the [Post Build Task Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Post+build+task).

```groovy
postBuildTask {
    task(String logText, String script, boolean escalate = false, boolean runIfSuccessful = false)
}
```

Arguments:
* `logText` The regular expression used to search the console output. Will be applied to each line.
* `script` The path to the file to execute or raw commands to run in the shell.
* `escalate ` If set, and the execution of the specified script fails, the status of the entire job/build is escalated to failed.
* `runIfSuccessful` If this option is true, the specified script is only executed if all previous build steps were successful.

The example runs `git clean -fdx` if `BUILD SUCCESSFUL` has been in found in the console log:

```groovy
publishers {
  postBuildTask {
    task('BUILD SUCCESSFUL', 'git clean -fdx')
  }
}
```

(Since 1.19)

## Aggregate Downstream Test Results

Aggregates downstream test results.

```groovy
aggregateDownstreamTestResults(String jobs = null, boolean includeFailedBuilds = false)
```

Arguments:
* `jobs` The comma delimited list of jobs to aggregate manually. Can be set to null to "automatically" aggregate downstream jobs using fingerprinting.
* `includeFailedBuilds` If this option is true, include failed builds in results.

The example manually aggregates test results from project-A and project-B, and includes failed builds in the results:

```groovy
publishers {
  aggregateDownstreamTestResults('project-A, project-B', true)
}
```

This example automatically aggregates test results from downstream jobs, and ignores failed builds:

```groovy
publishers {
  aggregateDownstreamTestResults()
}
```

(Since 1.19)

## [Groovy Postbuild](https://wiki.jenkins-ci.org/display/JENKINS/Groovy+Postbuild+Plugin)

Executes Groovy scripts after a build.

```groovy
groovyPostBuild(String script, Behavior behavior = Behavior.DoNothing)
```

Arguments:
* `script` The Groovy script to execute after the build. See [the plugin's page](https://wiki.jenkins-ci.org/display/JENKINS/Groovy+Postbuild+Plugin) for details on what can be done.
* `behavior` optional. If the script fails, allows you to set mark the build as failed, unstable, or do nothing.

The behavior argument uses an enum, which currently has three values: DoNothing, MarkUnstable, and MarkFailed.

Examples:

This example will run a groovy script that prints hello, world and if that fails, it won't affect the build's status:
```groovy
    groovyPostBuild('println "hello, world"')
```

This example will run a groovy script, and if that fails will mark the build as failed:
```groovy
    groovyPostBuild('// some groovy script', Behavior.MarkFailed)
```

This example will run a groovy script, and if that fails will mark the build as unstable:
```groovy
    groovyPostBuild('// some groovy script', Behavior.MarkUnstable)
```

(Since 1.19)

## Archive Javadoc

This plugin allows you to archive generated Javadoc using the [Javadoc Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Javadoc+Plugin).

```
//Shown with defaults
archiveJavadoc {
    javadocDir ''  // Path to the Javadoc directory in the workspace.
    keepAll false  // If true, retain javadoc for all the successful builds.
}
```

Simplest usage will output with the defaults above
```
archiveJavadoc()
```

(Since 1.19)

## Emma Code Coverage

Supports the [Emma Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Emma+Plugin). Only the Emma xml report file pattern is required to locate all the generated reports. Coverage thresholds are all set to defaults.

```groovy
emma(String xmlReportFilePattern)
```

Simple Example:
```groovy
emma('coverage-results/coverage.xml')
```

The previous example is basically equivalent to the following more verbose one, where all the default settings are visible.

```groovy
emma('coverage-results/coverage.xml') {
    classThreshold(0..100)
    methodThreshold(0..70)
    blockThreshold(0..80)
    lineThreshold(0..80)
    conditionThreshold(0..<1)
}
```

### More about targets

Targets are used to mark the build as healthy/unhealthy/unstable based on given thresholds. Targets can be set separately for conditional, line, block, method and class level.

Targets can be tuned using ranges for each category, or setting a specific minimum or maximum. The example above shows the category/range version. You can also use the following helper methods inside the closure (which is equivalent to the shorter example above):

```groovy
emma('coverage-results/coverage.xml') {
    minClass(0)
    maxClass(100)
    minMethod(0)
    maxMethod(70)
    minBlock(0)
    maxBlock(80)
    minLine(0)
    maxLine(80)
    minCondition(0)
    maxCondition(0)
}
```

Each of the 3 parameters represent a percentage threshold. They have the following meaning:
* healthy: Report health as 100% when coverage is greater than {healthy}% 
* unhealthy: Report health as 0% when coverage is less than {unhealthy}%
* failing: Mark the build as unstable when coverage is less than {failing}% 

(since 1.20)

## Associated Files

Supports the [Associated Files Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Associated+Files+Plugin). 

```groovy
associatedFiles(String associatedFilesPattern)
```

(since 1.20)

## Robot Framework Reports

Supports [Robot Framework Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Robot+Framework+Plugin) to publish the reports from Robot Framework execution:  

```groovy
publishRobotFrameworkReports(Closure closure = null)
``` 

If no `closure` is provided, default values will be used that are based on [Jenkins Robot Framework plugin](https://github.com/jenkinsci/robot-plugin/blob/master/src/main/java/hudson/plugins/robot/RobotPublisher.java). The following properties can configured using the closure:

```groovy
publishRobotFrameworkReports {
    passThreshold(double)     // A double value in range (0.0, 100.0). The default is 100.0.
    unstableThreshold(double) // A double value in range (0.0, 100.0). The default is 0.0.
    onlyCritical(boolean)     // A boolean value. The default is false
    outputPath(String)        // The path to the reports. The default is 'target/robotframework-reports'.
    reportFileName(String)    // The name of the report file. The default is 'report.html'.
    logFileName(String)       // The name of the log file. The default is 'log.html'.
    outputFileName(String)    // The name of the output file. The default is 'output.xml'.
}
```

For example, to mark a build status with more relaxed threshold values and only on the critical test cases from Robot Framework:

```groovy
publishRobotFrameworkReports {
    passThreshold(90.0)
    onlyCritical(true)
}
```

Or to use the default configurations with the plugin:

```groovy
publishRobotFrameworkReports()
```

(Since 1.21)

## Build Pipeline Trigger

```groovy
buildPipelineTrigger(String downstreamProjectNames)
```

Add a manual triggers for jobs that require intervention prior to execution, e.g. an approval process outside of Jenkins. The argument takes a comma separated list of job names. Requires the [Build Pipeline Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Pipeline+Plugin).

```groovy
buildPipelineTrigger('deploy-cluster-1, deploy-cluster-2')
```

(Since 1.21)

## Github Commit Notifier

This publisher sets the build status on a Github commit, using the [Github Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Github+Plugin).

```
githubCommitNotifier()
```

(Since 1.21)

## Git Publisher

```groovy
job {
    publishers {
        git {
            pushOnlyIfSuccess(boolean pushOnlyIfSuccess = true)
            pushMerge(boolean pushMerge = true)
            tag(String targetRepoName, String tagName) {
                message(String message)
                create(boolean create = true)
                update(boolean update = true)
            }
            branch(String targetRepoName, String branchName)
        }
    }
}
```

Push tags or branches to a Git repository. Requires the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin).

Examples:

```groovy
// push a to branch if the job succeeded
job {
    publishers {
        git {
            pushOnlyIfSuccess()
            branch('origin', 'staging')
        }
    }
}
```

```groovy
// create and push a tag if the job succeeded, the tag name and message are parametrized.
job {
    publishers {
        git {
            pushOnlyIfSuccess()
            tag('origin', 'foo-$PIPELINE_VERSION') {
                message('Release $PIPELINE_VERSION')
                create()
            }
        }
    }
}
```

(Since 1.22)

# Parameters
**Note: In all cases apart from File Parameter the parameterName argument can't be null or empty**
_Note: The Password Parameter is not yet supported. See https://issues.jenkins-ci.org/browse/JENKINS-18141_

## Boolean Parameter
Simplest usage (taking advantage of all defaults)
```groovy
// In this use case, the value will be "true" and the description will be ''
booleanParam("myParameterName") 
```
Simple usage (omitting the description)
```groovy
// In this use case, the value will be "false" and the description will be ''
booleanParam("myParameterName", false)
```
Full usage
```groovy
// In this use case, the value will be "false" and the description will be 'the description 
// of my parameter'
booleanParam("myParameterName", false, "The description of my parameter") 
```

## ListTags Parameter
Simplest usage (taking advantage of all defaults)
```groovy
// in this case "maxTags will be set to "all", reverseByDate and reverseByName will be set to "false", 
// and description and defaultValue xml tags will not be created at all
listTagsParam("myParameterName", "http://kenai.com/svn/myProject/tags", "^mytagsfilterregex") 
```
Simple usage (omitting the description and defaultValue)
```groovy
// in this case "maxTags will be set to "all", reverseByDate and reverseByName will be set to "true", 
// and description and defaultValue xml tags will not be created at all
listTagsParam("myParameterName", "http://kenai.com/svn/myProject/tags", "^mytagsfilterregex", true, true) 
```
Full usage (omitting the description and defaultValue)
```groovy
// in this case "maxTags will be set to "all", reverseByDate and reverseByName will be set to "true", 
// and description and defaultValue xml tags will be set as shown
listTagsParam("myParameterName", "http://kenai.com/svn/myProject/tags", "^mytagsfilterregex", true, true, "defaultValue", "description") 
```
NOTE: The second-to-last parameter needs to be a String, although you are provinding in most cases a number.  This is because the default value for defaultValue is "all".

## Choice Parameter
Simplest usage (taking advantage of default description)
```groovy
// In this case the description will be set to '' and you will have a 3-option list with 
// "option 1 (default)" as the default (because it's first, not because it says so in the String 
choiceParam("myParameterName", ["option 1 (default)", "option 2", "option 3"])
```
Full usage
```groovy
// In this case the description will be set to 'my description' and you will have a 3-option list with 
// "option 1 (default)" as the default (because it's first, not because it says so in the String 
choiceParam("myParameterName", ["option 1 (default)", "option 2", "option 3"], "my description")
```

## File Parameter
Simplest usage (taking advantage of default description)
```groovy
// In this case the description will be set to ''
fileParam("test/upload.zip")
```
Full usage
```groovy
// In this case the description will be set to 'my description'
fileParam("test/upload.zip", "my description")
```

## Run Parameter
Note: The Job Name needs to be an existing Jenkins Job, though we don't check when we generate the XML.

Simplest usage (taking advantage of default description and default filter)
```groovy
// In this case the description will be set to '' and the filter won't be set
runParam("myParameterName", "myJobName")
```
Full usage
```groovy
// In this case the description will be set to 'my description' and the filter will be set to 'SUCCESSFUL'
runParam("myParameterName", "myJobName", "my description", "SUCCESSFUL")
```

## String Parameter
Simplest usage (taking advantage of default defaultValue and default description)
```groovy
// In this case the defaultValue and description will be set to ''
stringParam("myParameterName")
```
Simple usage (taking advantage of default description)
```groovy
// In this case the description will be set to ''
stringParam("myParameterName", "my default stringParam value")
```
Full usage
```groovy
// In this case the defaultValue will be set to 'my default stringParam value' and the description 
// will be set to 'my description'
stringParam("myParameterName", "my default stringParam value", "my description")
```

## Text Parameter
Simplest usage (taking advantage of default defaultValue and default description)
```groovy
// In this case the defaultValue and description will be set to ''
textParam("myParameterName")
```
Simple usage (taking advantage of default description)
```groovy
// In this case the description will be set to ''
textParam("myParameterName", "my default textParam value")
```
Full usage
```groovy
textParam("myParameterName", "my default textParam value", "my description")
```
 