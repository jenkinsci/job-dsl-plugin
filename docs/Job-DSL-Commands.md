**NOTE: See the [[Job Reference]] and [[View Reference]] pages for details about all options.**

# DSL Methods

The DSL execution engine exposes a method, called `job`. This `job` method implies the creation of a Jenkins job
and the closure to this method can be used to define the job's settings. The only mandatory option is `name`.

```groovy
job {
    name 'my-job'
}
```

There are similar methods to create Jenkins views and folders:

```groovy
view {
    name 'my-view'
}

folder {
    name 'my-folder'
}
```

The name is treated as absolute to the Jenkins root by default, but the seed job can be configured to interpret names
relative to the seed job. (since 1.24)

In the closure provided to `job` there are a few top level methods, like `label` and `chucknorris`. Others are nested
deeper in blocks which represent their role in Jenkins, e.g. the `publishers` block contains all the publisher actions.

DSL methods can be cumulative or overriding, meaning that some methods will add nodes (e.g. `publishers` and `steps`)
and some will replace nodes (e.g. `disabled` will replace any existing disabled nodes). Some methods like `scm` and
`multiscm` are mutually exclusive. Likewise, when using the `scm` block, only one SCM can be specified.

When a DSL method isn't available, look at [[The Configure Block]] for extending the DSL.

**NOTE: when using these methods, remember that you need to use them in context. I.e. to use the `downstream` method,
it needs to be enclosed in a `publishers` context.**

The following high level overview shows what's available in a Groovy-like syntax. If an argument is followed by an
equals sign, this means it's a default value that is used when the argument is left out. Arguments can only be omitted
from right to left. Many methods provide options in deeper nested blocks which are not shown in this overview. See the
[[Job Reference]] and [[View Reference]] pages for details.

```groovy
job(Map<String, ?> arguments = [:]) {
    name(String name)
    
    // DSL specific methods
    using(String templateName)
    configure(Closure configBlock)

    // common options
    batchTask(String name, String script)
    blockOn(String projectNames)
    blockOn(Iterable<String> projectNames)
    blockOnDownstreamProjects()
    blockOnUpstreamProjects()
    checkoutRetryCount(int times = 3)
    concurrentBuild(boolean allowConcurrentBuild = true) // since 1.21
    customWorkspace(String workspacePath)
    description(String description)
    disabled(boolean shouldDisable = true)
    displayName(String displayName)
    environmentVariables(Map<Object, Object> vars, Closure envClosure = null)
    environmentVariables(Closure envClosure)
    jdk(String jdk)
    keepDependencies(boolean keep = true)
    label(String label)
    lockableResources(String resources, Closure lockableResourcesClosure) // since 1.25
    logRotator(int daysToKeep = -1, int numToKeep = -1, int artifactDaysToKeep = -1,
               int artifactNumToKeep = -1)
    priority(int value)
    quietPeriod(int seconds = 5)
    throttleConcurrentBuilds(Closure throttleClosure)
    authorization {
        permission(String permission)
        permission(String permEnumName, String user)
        permission(Permissions perm, String user)
        permissionAll(String user)
    }
    parameters {
        booleanParam(String parameterName, boolean defaultValue = false,
                     String description = null)
        choiceParam(String parameterName, List<String> options, String description = null)
        fileParam(String fileLocation, String description = null)
        listTagsParam(String parameterName, String scmUrl, String tagFilterRegex,
                      boolean sortNewestFirst = false, boolean sortZtoA = false,
                      String maxTagsToDisplay = 'all', String defaultValue = null,
                      String description = null)
        runParam(String parameterName, String jobToRun, String description = null,
                 String filter = null)
        stringParam(String parameterName, String defaultValue = null,
                    String description = null)
        textParam(String parameterName, String defaultValue = null,
                  String description = null)
    }
    scm {
        baseClearCase(Closure closure) // since 1.24
        cloneWorkspace(String parentProject, String criteriaArg)
        git(Closure gitClosure)
        git(String url, Closure configure = null)
        git(String url, String branch, Closure configure = null)
        github(String ownerAndProject, String branch = null, String protocol = 'https',
               Closure configure)
        github(String ownerAndProject, String branch = null, String protocol = 'https',
               String host = 'github.com', Closure configure = null)
        hg(String url, String branch = null, Closure configure = null)
        p4(String viewspec, Closure configure = null)
        p4(String viewspec, String user, Closure configure = null)
        p4(String viewspec, String user, String password, Closure configure = null)
        svn(String svnUrl, Closure configure = null)
        svn(String svnUrl, String localDir, Closure configure = null)
    }
    multiscm(Closure scmClosure)
    triggers {
        cron(String cron)
        gerrit(Closure gerritClosure = null)
        githubPush()
        pullRequest(Closure pullRequestClosure) // since 1.22
        scm(String cron)
        snapshotDependencies(boolean checkSnapshotDependencies)
        urlTrigger(String cronString = null, Closure urlTriggerClosure)
    }
    wrappers { // since 1.19, before that the methods were on top level
        allocatePorts(Closure closure = null)
        allocatePorts(String[] ports, Closure closure = null)
        buildName(String nameTemplate) // since 1.24
        colorizeOutput(String colorMap)
        environmentVariables(Closure envClosure)
        exclusionResources(String... resourceNames) // since 1.24
        exclusionResources(Iterable<String> resourceNames) // since 1.24
        injectPasswords() // since 1.23
        keychains(Closure closure) // since 1.24
        logSizeChecker(Closure closure = null) // since 1.23
        preBuildCleanup(Closure closure = null) // since 1.22
        release(Closure releaseClosure) // since 1.22
        runOnSameNodeAs(String jobName, boolean useSameWorkspace = false)
        rvm(String rubySpecification)
        sshAgent(String credentials)
        timeout(Closure timeoutClosure)
        timeout(String type = 'absolute', Closure timeoutClosure = null) // deprecated
        timeout(Integer timeoutInMinutes, Boolean shouldFailBuild = true) // deprecated
        timestamps()
        toolenv(String... tools)
        xvnc(boolean takeScreenshot = false)
    }
    steps {
        ant(Closure antClosure = null)
        ant(String targets, Closure antClosure = null)
        ant(String targets, String buildFile, Closure antClosure = null)
        ant(String targets, String buildFile, String antInstallation,
            Closure antClosure = null)
        batchFile(String command)
        conditionalSteps(Closure conditionalClosure)
        copyArtifacts(String jobName, String includeGlob, Closure copyArtifactClosure)
        copyArtifacts(String jobName, String includeGlob, String targetPath,
                      Closure copyArtifactClosure)
        copyArtifacts(String jobName, String includeGlob, String targetPath = '',
                      boolean flattenFiles, Closure copyArtifactClosure)
        copyArtifacts(String jobName, String includeGlob, String targetPath = '',
                      boolean flattenFiles, boolean optionalAllowed,
                      Closure copyArtifactClosure)
        criticalBlock(Closure stepClosure) // since 1.24
        downstreamParameterized(Closure downstreamClosure)
        dsl(Closure dslClosure = null)
        dsl(String scriptText, String removedJobAction = null, 
            boolean ignoreExisting = false)
        dsl(Collection<String> externalScripts, String removedJobAction = null,
            boolean ignoreExisting = false)
        environmentVariables(Closure envClosure)
        gradle(String tasks = null, String switches = null, Boolean useWrapperArg = true,
               Closure configure = null)
        grails(Closure grailsClosure)
        grails(String targets, Closure grailsClosure)
        grails(String targets, boolean useWrapperArg = false, Closure grailsClosure)
        groovyCommand(String command, Closure groovyClosure = null)
        groovyCommand(String command, String groovyName, Closure groovyClosure = null)
        groovyScriptFile(String fileName, Closure groovyClosure = null)
        groovyScriptFile(String fileName, String groovyName, Closure groovyClosure = null)
        maven(Closure mavenClosure) // since 1.20
        maven(String target = null, String pom = null, Closure configure = null)
        phase(Closure phaseClosure)
        phase(String name, Closure phaseClosure = null)
        phase(String name, String continuationConditionArg, Closure phaseClosure)
        prerequisite(String projectList = '', boolean warningOnly = false) // since 1.19 
        rake(Closure rakeClosure = null) // since 1.25
        rake(String tasksArg, Closure rakeClosure = null) // since 1.25
        remoteTrigger(String remoteJenkinsName, String jobName,
                      Closure remoteTriggerClosure) // since 1.22
        sbt(String sbtName = null, String actions = null, String sbtFlags = null,
            String jvmFlags = null, String subdirPath = null, Closure configure = null)
        shell(String command)
        systemGroovyCommand(String command, Closure systemGroovyClosure = null)
        systemGroovyScriptFile(String fileName, Closure systemGroovyClosure = null)
        vSpherePowerOff(String server, String vm)
        vSpherePowerOn(String server, String vm)
        vSphereRevertToSnapshot(String server, String vm, String snapshot)
    }
    publishers {
        aggregateDownstreamTestResults(String jobs = null, 
                                       boolean includeFailedBuilds = false) // since 1.19
        allowBrokenBuildClaiming()
        androidLint(String pattern, Closure staticAnalysisClosure = null)
        archiveArtifacts(String glob, String excludeGlob = null,
                         boolean latestOnlyBoolean = false)
        archiveArtifacts(Closure archiveArtifactsClosure) // since 1.20
        archiveJavadoc(Closure javadocClosure) // since 1.19
        archiveJunit(String glob, boolean retainLongStdout = false,
                     boolean allowClaimingOfFailedTests = false,
                     boolean publishTestAttachments = false)
        archiveXunit(Closure xunitClosure) // since 1.24
        associatedFiles(String files = null) // since 1.20
        buildDescription(String regularExpression, String description = '',
                         String regularExpressionForFailed = '',
                         String descriptionForFailed = '',
                         boolean multiConfigurationBuild = false)
        ccm(String pattern, Closure staticAnalysisClosure = null)
        checkstyle(String pattern, Closure staticAnalysisClosure = null)
        chucknorris()
        cobertura(String coberturaReportFilePattern, Closure coberturaClosure = null)
        dependencyCheck(String pattern, Closure staticAnalysisClosure = null)
        downstream(String projectName, String thresholdName = 'SUCCESS')
        downstreamParameterized(Closure downstreamClosure)
        dry(String pattern, highThreshold = 50, normalThreshold = 25,
            Closure staticAnalysisClosure = null)
        emma(String coverageFile = '', Closure closure) // since 1.20
        extendedEmail(String recipients = null, Closure emailClosure = null)
        extendedEmail(String recipients, String subjectTemplate,
                      Closure emailClosure = null)
        extendedEmail(String recipients, String subjectTemplate, String contentTemplate,
                      Closure emailClosure = null)
        findbugs(String pattern, boolean isRankActivated = false,
                 Closure staticAnalysisClosure = null)
        fingerprint(String targets, boolean recordBuildArtifacts = false)
        flowdock(String token, Closure flowdockClosure = null) // since 1.23
        flowdock(String[] tokens, flowdockClosure = null) // since 1.23
        git(Closure gitPublisherClosure) // since 1.22
        githubCommitNotifier() // since 1.21
        groovyPostBuild(String script, Behavior behavior = Behavior.DoNothing) // since 1.19
        irc(Closure ircClosure)
        jacocoCodeCoverage(Closure jacocoClosure)
        jshint(String pattern, Closure staticAnalysisClosure = null)
        mailer(String recipients, Boolean dontNotifyEveryUnstableBuild = false,
               Boolean sendToIndividuals = false)
        mavenDeploymentLinker(String regex) // since 1.23
        pmd(String pattern, Closure staticAnalysisClosure = null)
        postBuildTask(Closure closure) // since 1.19
        publishCloneWorkspace(String workspaceGlob, Closure cloneWorkspaceClosure)
        publishCloneWorkspace(String workspaceGlob, String workspaceExcludeGlob,
                              Closure cloneWorkspaceClosure)
        publishCloneWorkspace(String workspaceGlob, String workspaceExcludeGlob,
                              String criteria, String archiveMethod,
                              Closure cloneWorkspaceClosure)
        publishCloneWorkspace(String workspaceGlob, String workspaceExcludeGlob = '', 
                              String criteria = 'Any', String archiveMethod = 'TAR', 
                              boolean overrideDefaultExcludes = false,
                              Closure cloneWorkspaceClosure = null)
        publishHtml(Closure htmlReportClosure)
        publishJabber(String target, Closure jabberClosure = null)
        publishJabber(String target, String strategyName, Closure jabberClosure = null)
        publishJabber(String target, String strategyName, String channelNotificationName,
                      Closure jabberClosure = null)
        publishRobotFrameworkReports(Closure closure = null) // since 1.21
        publishScp(String site, Closure scpClosure)
        rundeck(String jobId, Closure rundeckClosure = null) // since 1.24
        stashNotifier(Closure stashNotifierClosure = null) // since 1.23
        tasks(String pattern, excludePattern = '', high = '', normal = '', low = '',
              ignoreCase = false, Closure staticAnalysisClosure = null)
        textFinder(String regularExpression, String fileSet = '',
                   boolean alsoCheckConsoleOutput = false,
                   boolean succeedIfFound = false,
                   boolean unstableIfFound = false) // since 1.19
        violations(Closure violationsClosure = null)
        violations(int perFileDisplayLimit, Closure violationsClosure = null)
        warnings(List consoleParsers, Map parserConfigurations = [:],
                 Closure warningsClosure = null)
        wsCleanup(Closure wsCleanupClosure = null) // since 1.23
    }

    // Maven options
    rootPOM(String rootPOM)
    goals(String goals)
    mavenOpts(String mavenOpts)
    mavenInstallation(String name) // since 1.20
    localRepository(LocalRepositoryLocation location)
    perModuleEmail(boolean shouldSendEmailPerModule)
    archivingDisabled(boolean shouldDisableArchiving)
    runHeadless(boolean shouldRunHeadless)
    preBuildSteps(Closure stepsClosure)
    postBuildSteps(Closure stepsClosure)

    // BuildFlow options
    buildFlow(String buildFlowText) // since 1.21

    // Matrix options, since 1.24
    axes {
        text(String name, String... values)
        text(String name, Iterable<String> values)
        label(String name, String... labels)
        label(String name, Iterable<String> labels)
        labelExpression(String name, String... expressions)
        labelExpression(String name, Iterable<String> expressions)
        jdk(String... jdks)
        jdk(Iterable<String> jdks)
        configure(Closure configBlock)
    }
    runSequentially(boolean runSequentially = true)
    touchStoneFilter(String expression, boolean continueOnFailure = false)
    combinationFilter(String expression)
}

view(Map<String, Object> arguments = [:]) { // since 1.21
    name(String name)
    
    // DSL specific methods
    configure(Closure configBlock)
    
    // common options
    description(String description)
    filterBuildQueue(boolean filterBuildQueue)
    filterExecutors(boolean filterExecutors)

    // ListView options
    columns {
        buildButton()
        lastBuildConsole() // since 1.23
        lastDuration()
        lastFailure()
        lastSuccess()
        name()
        status()
        weather()
    }
    jobs {
        name(String jobName)
        names(String... jobNames)
        regex(String regex)
    }
    statusFilter(StatusFilter filter)

    // BuildPipelineView options
    alwaysAllowManualTrigger(boolean alwaysAllowManualTrigger = true)
    consoleOutputLinkStyle(OutputStyle consoleOutputLinkStyle)
    customCssUrl(String cssUrl)
    displayedBuilds(int noOfDisplayedBuilds)
    refreshFrequency(int seconds)
    selectedJob(String rootJob)
    title(String pipelineTitle)
    triggerOnlyLatestJob(boolean triggerOnlyLatestJob = true)
    showPipelineDefinitionHeader(boolean showPipelineDefinitionHeader = true)
    showPipelineParameters(boolean showPipelineParameters = true)
    showPipelineParametersInHeaders(boolean showPipelineParametersInHeaders = true)
}

folder { // since 1.23
    name(String name)
    
    // DSL specific methods
    configure(Closure configBlock)

    // common options
    displayName(String displayName)
}
```

The plugin tries to provide DSL methods to cover "common use case" scenarios as simple method calls. When these methods
fail you, you can always generate the XML yourself via [[The Configure Block]]. Sometimes, a DSL
method will provide a configure block of its own, which will set the a good context to help modify a few fields. 
This gives native access to the job config XML, which is typically very straight forward to understand.

(Note: The full XML can be found for any job, view or folder by taking the Jenkins URL and appending `/config.xml` to
it. We find that creating a job the way you like it, then viewing the XML is the best way to learn what fields you
need.)

# Job

```groovy
job(Map<String, Object> attributes = [:], Closure closure)
```

The above method will return a _Job_ object that can be re-used and passed around. E.g.

```groovy
def myJob = job {
    name 'SimpleJob'
}
myJob.with {
    description 'A Simple Job'
}
```

A job can have optional attributes. Currently only a `type` attribute with value of `Freeform`, `Maven`, `Multijob`,
`BuildFlow` or `MatrixJob` is supported. When no type is specified, a free-style job will be generated. Some methods
will only be available in some job types, e.g. `phase` can only be used in Multijob. Each DSL method documents where
they are relevant.

```groovy
job(type: Maven) {
  name 'maven-job'
}
```

Please see the [[Job Reference]] page for details.

# View

```groovy
view(Map<String, Object> attributes = [:], Closure closure)
```

The `view` method behaves like the `job` method explained above and will return a _View_ object.

Currently only a `type` attribute with value of `ListView` or `BuildPipelineView` is supported. When no type is
specified, a list view will be generated.

```groovy
view(type: ListView) {
  name 'project-view'
}
```

Please see the [[View Reference]] page for details.

# Folder

```groovy
folder(Closure closure)
```

The `folder` method behaves like the `job` method explained above and will return a _Folder_ object.

Folders will be created before jobs and views to ensure that a folder exists before entries are created.

```groovy
folder {
  name 'project-a'
  displayName 'Project A'
}
```

Items can be created within folders by using the full path as job name.

```groovy
folder {
  name 'project-a'
}

job {
  name 'project-a/compile'
}

view {
  name 'project-a/pipeline'
}

folder {
  name 'project-a/testing'
}
```

# Queue

```groovy
queue(String jobName)
queue(Job job)
```

This provide the ability to schedule a job to be executable after the DSL runs. The given name doesn't have to be a job
which was generated by the DSL, but it could be.

# Reading Files from Workspace

```groovy
InputStream streamFileFromWorkspace(String filePath) throws IOException
String readFileFromWorkspace(String filePath) throws IOException
```

Anywhere in the script you can read in a file from the current workspace using the above calls. This assumes that you
checked out some source control as part of the job processing the DSL. This can be useful when populating fields on a
generated job, e.g.

```groovy
job {
    steps {
        shell(readFileFromWorkspace('build.sh')
    }
}
```

(since 1.15)

# Grab

Groovy provides the ability to "grab" dependencies and use them right away, this is called
[Grape](http://groovy.codehaus.org/Grape). The Job DSL supports this feature, as long as it's provided at the top of the
script. Not only can the `@Grab` annotation be used, it can be used with `@GrabResolver` to pull dependencies from
custom defined repositories. This means that you can write a set of conventions specific to your organization, jar them
up, distribute them via a repository, then grab them via `@Grab`. Here is an example of pulling in a utility library.

```groovy
@Grab(group='commons-lang', module='commons-lang', version='2.4')

import org.apache.commons.lang.WordUtils

println "Hello ${WordUtils.capitalize('world')}"
```

# Configure

When an option is not supported by the Job DSL, then [[The Configure Block]] can be used for extending the DSL.

Here is a simple example which adds a EnvInjectPasswordWrapper node:

```groovy
job {
    ...
    configure { project ->
        project / buildWrappers / EnvInjectPasswordWrapper {
            injectGlobalPasswords(true)
        }
    }
}
```

See [[The Configure Block]] page for details.

# Job Factory

Because the engine is just Groovy, you can call other Groovy classes available in the workspace. When in those methods
the `job` method is no longer available, so it is recommended to pass in the current context to make this method
available to another context. For example, when making utility methods, you would call them like this:

```groovy
BuildFramework.ant(this, arg1, arg2)
```

Then the `BuildFramework` class has everything it needs to make `job` calls:

```groovy
class BuildFramework {
    static ant(jobFactory, arg1, arg2) {
        jobFactory.job {
            name arg1
            steps {
                ant(arg2)
            }
        }
    }
}
```
