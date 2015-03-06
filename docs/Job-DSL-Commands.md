**NOTE: See the [[Job Reference]] and [[View Reference]] pages for details about all options.**

# DSL Methods

The DSL execution engine exposes a method, called `job`. This `job` method implies the creation of a Jenkins job
and the closure to this method can be used to define the job's settings. The only mandatory option is `name`.

```groovy
job('my-job') {
}
```

There are similar methods to create Jenkins views, folders and config files:

```groovy
view('my-view') {
}

folder('my-folder') {
}

configFile('my-config') {
}
```

When defining jobs, views or folders the name is treated as absolute to the Jenkins root by default, but the seed job
can be configured to interpret names relative to the seed job. (since 1.24)

In the closure provided to `job` there are a few top level methods, like `label` and `description`. Others are nested
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
job(Map<String, ?> arguments = [:], String name) { // since 1.30
    name(String name) // deprecated since 1.30

    // DSL specific methods
    using(String templateName)
    configure(Closure configBlock)
    previousNames(String regex) // since 1.29

    // common options
    batchTask(String name, String script)
    blockOn(String projectNames)
    blockOn(Iterable<String> projectNames)
    blockOnDownstreamProjects()
    blockOnUpstreamProjects()
    checkoutRetryCount(int times = 3)
    concurrentBuild(boolean allowConcurrentBuild = true) // since 1.21
    customWorkspace(String workspacePath)
    deliveryPipelineConfiguration(String stageName, String taskName = null) // since 1.26
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
    notifications(Closure notificationClosure) // since 1.26
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
        labelParam(String parameterName, Closure closure = null) // since 1.30
        listTagsParam(String parameterName, String scmUrl, String tagFilterRegex,
                      boolean sortNewestFirst = false, boolean sortZtoA = false,
                      String maxTagsToDisplay = 'all', String defaultValue = null,
                      String description = null)
        nodeParam(String parameterName, Closure closure = null) // since 1.26
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
        rtc(Closure closure) // since 1.28
        svn(Closure svnClosure) // since 1.30
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
        buildUserVars() // since 1.26
        credentialsBinding(Closure closure) // since 1.28
        colorizeOutput(String colorMap = 'xterm')
        configFiles(Closure closure) // since 1.28
        customTools(Iterable<String> toolNames, Closure closure = null) // since 1.30
        deliveryPipelineVersion(String template, boolean setDisplayName = false) // since 1.26
        environmentVariables(Closure envClosure)
        exclusionResources(String... resourceNames) // since 1.24
        exclusionResources(Iterable<String> resourceNames) // since 1.24
        golang(String version) // since 1.27
        injectPasswords() // since 1.23
        keychains(Closure closure) // since 1.24
        logSizeChecker(Closure closure = null) // since 1.23
        maskPasswords() // since 1.26
        nodejs(String installation) // since 1.27
        preBuildCleanup(Closure closure = null) // since 1.22
        rbenv(String rubyVersion, Closure rbenvClosure = null) // since 1.27
        release(Closure releaseClosure) // since 1.22
        runOnSameNodeAs(String jobName, boolean useSameWorkspace = false)
        rvm(String rubySpecification)
        sshAgent(String credentials)
        timeout(Closure timeoutClosure = null)
        timestamps()
        toolenv(String... tools)
        xvnc(boolean takeScreenshot) // deprecated
        xvnc(Closure xvncClosure = null) // since 1.26
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
        dsl(Closure dslClosure)
        dsl(String scriptText, String removedJobAction = null,
            boolean ignoreExisting = false)
        dsl(Collection<String> externalScripts, String removedJobAction = null,
            boolean ignoreExisting = false)
        environmentVariables(Closure envClosure)
        gradle(String tasks = null, String switches = null, Boolean useWrapperArg = true,
               Closure configure = null)
        gradle(Closure gradleClosure) // since 1.27
        grails(Closure grailsClosure)
        grails(String targets, Closure grailsClosure)
        grails(String targets, boolean useWrapperArg = false, Closure grailsClosure)
        groovyCommand(String command, Closure groovyClosure = null)
        groovyCommand(String command, String groovyName, Closure groovyClosure = null)
        groovyScriptFile(String fileName, Closure groovyClosure = null)
        groovyScriptFile(String fileName, String groovyName, Closure groovyClosure = null)
        httpRequest(String url, Closure closure = null) // since 1.28
        maven(Closure mavenClosure) // since 1.20
        maven(String target = null, String pom = null, Closure configure = null)
        phase(Closure phaseClosure)
        phase(String name, Closure phaseClosure = null)
        phase(String name, String continuationConditionArg, Closure phaseClosure)
        prerequisite(String projectList = '', boolean warningOnly = false) // since 1.19
        publishOverSsh(Closure publishOverSshClosure) // since 1.28
        rake(Closure rakeClosure = null) // since 1.25
        rake(String tasksArg, Closure rakeClosure = null) // since 1.25
        remoteTrigger(String remoteJenkinsName, String jobName,
                      Closure remoteTriggerClosure) // since 1.22
        resolveArtifacts(Closure repositoryConnectorClosure) // since 1.29
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
        analysisCollector(Closure analysisCollectorClosure = null) // since 1.26
        androidLint(String pattern, Closure staticAnalysisClosure = null)
        archiveArtifacts(String glob, String excludeGlob = null,
                         boolean latestOnlyBoolean = false)
        archiveArtifacts(Closure archiveArtifactsClosure) // since 1.20
        archiveJavadoc(Closure javadocClosure) // since 1.19
        archiveJunit(String glob, boolean retainLongStdout,
                     boolean allowClaimingOfFailedTests = false,
                     boolean publishTestAttachments = false) // deprecated
        archiveJunit(String glob, Closure junitClosure = null) // since 1.26
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
        flexiblePublish(Closure flexiblePublishClosure) // since 1.26
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
        publishJabber(String target, String strategyName,
                      Closure jabberClosure = null) // deprecated since 1.30
        publishJabber(String target, String strategyName, String channelNotificationName,
                      Closure jabberClosure = null) // deprecated since 1.30
        publishRobotFrameworkReports(Closure closure = null) // since 1.21
        publishScp(String site, Closure scpClosure)
        rundeck(String jobId, Closure rundeckClosure = null) // since 1.24
        s3(String profile, Closure s3Closure) // since 1.26
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
    perModuleEmail(boolean shouldSendEmailPerModule) // deprecated since 1.29
    archivingDisabled(boolean shouldDisableArchiving)
    runHeadless(boolean shouldRunHeadless)
    preBuildSteps(Closure stepsClosure)
    postBuildSteps(Closure stepsClosure)
    providedSettings(String mavenSettingsName) // since 1.25
    wrappers {
        mavenRelease(Closure mavenReleaseClosure = null) // since 1.25
    }

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

    // Workflow options, since 1.29
    definition {
      cps(Closure cpsClosure)
    }
}
job(Map<String, ?> arguments = [:], Closure jobClosure) // deprecated since 1.30

view(Map<String, Object> arguments = [:], String name) { // since 1.30
    name(String name) // deprecated since 1.30

    // DSL specific methods
    configure(Closure configBlock)

    // common options
    description(String description)
    filterBuildQueue(boolean filterBuildQueue)
    filterExecutors(boolean filterExecutors)

    // ListView options
    columns {
        buildButton()
        claim() // since 1.29
        lastBuildConsole() // since 1.23
        lastDuration()
        lastFailure()
        lastSuccess()
        name()
        status()
        weather()
    }
    jobFilters { // since 1.29
        regex(Closure regexFilterClosure)
        status(Closure statusFilterClosure)
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
    startsWithParameters(boolean startsWithParameters = true) // since 1.26

    // SectionedView options, since 1.25
    sections {
        listView(Closure listNiewSectionClosure)
    }

    // NestedView options, since 1.25
    views {
        view(Map<String, Object> arguments = [:], Closure viewClosure)
    }
    columns {
        status()
        weather()
    }

    // DeliveryPipelineView options, since 1.26
    pipelineInstances(int number)
    showAggregatedPipeline(boolean showAggregatedPipeline = true)
    columns(int number)
    sorting(Sorting sorting)
    updateInterval(int seconds)
    enableManualTriggers(boolean enable = true)
    showAvatars(boolean showAvatars = true)
    showChangeLog(boolean showChangeLog = true)
    pipelines {
        component(String name, String initialJob)
        regex(String regex)
    }

    // BuildMonitorView options, since 1.28
    jobs {
        name(String jobName)
        names(String... jobNames)
        regex(String regex)
    }
    statusFilter(StatusFilter filter)
}
view(Map<String, Object> arguments = [:],
     Closure viewClosure) // since 1.21, deprecated since 1.30

folder(String name) { // since 1.30
    name(String name) // deprecated since 1.30

    // DSL specific methods
    configure(Closure configBlock)

    // common options
    displayName(String displayName)
}
folder(Closure folderClosure) // since 1.23, deprecated since 1.30

configFile(Map<String, Object> arguments = [:], String name) { // since 1.30
    name(String name) // deprecated since 1.30
    comment(String comment)
    content(String content)
}
configFile(Map<String, Object> arguments = [:],
           Closure configFileClosure) // since 1.25, deprecated since 1.30
```

The plugin tries to provide DSL methods to cover "common use case" scenarios as simple method calls. When these methods
fail you, you can always generate the underlying XML yourself via [[The Configure Block]]. Sometimes, a DSL
method will provide a configure block of its own, which will set the a good context to help modify a few fields.
This gives native access to the job config XML, which is typically very straight forward to understand.

(Note: The full XML can be found for any job, view or folder by taking the Jenkins URL and appending `/config.xml` to
it. We find that creating a job the way you like it, then viewing the XML is the best way to learn what fields you
need.)

# Job

```groovy
job(Map<String, Object> attributes = [:], String name, Closure closure)
```

The above method will return a _Job_ object that can be re-used and passed around. E.g.

```groovy
def myJob = job('SimpleJob') {
}
myJob.with {
    description 'A Simple Job'
}
```

A job can have optional attributes. Currently only a `type` attribute with value of `Freeform`, `Maven`, `Multijob`,
`BuildFlow`, `Matrix` or `Workflow`is supported. When no type is specified, a free-style job will be generated. Some
methods will only be available in some job types, e.g. `phase` can only be used in Multijob. Each DSL method documents
where they are relevant.

```groovy
job('maven-job', type: Maven) {
}
```

Please see the [[Job Reference]] page for details.

# View

```groovy
view(Map<String, Object> attributes = [:], String name, Closure closure)
```

The `view` method behaves like the `job` method explained above and will return a _View_ object.

Currently only a `type` attribute with value of `ListView`, `BuildPipelineView`, `SectionedView`, `NestedView`,
`DeliveryPipelineView` or `BuildMonitorView` is supported. When no type is specified, a list view will be generated.

```groovy
view('project-view', type: ListView) {
}
```

Please see the [[View Reference]] page for details.

# Folder

```groovy
folder(String name, Closure closure)
```

The `folder` method behaves like the `job` method explained above and will return a _Folder_ object.

Folders will be created before jobs and views to ensure that a folder exists before entries are created.

```groovy
folder('project-a') {
  displayName 'Project A'
}
```

Items can be created within folders by using the full path as job name.

```groovy
folder('project-a') {
}

job('project-a/compile') {
}

view('project-a/pipeline') {
}

folder('project-a/testing') {
}
```

# Config File

```groovy
configFile(Map<String, Object> attributes = [:], String name, Closure closure)
```

The `configFile` method behaves like the `job` method explained above and will return a _ConfigFile_ object.

A config file can have optional attributes. Currently only a `type` attribute with value of `Custom` or `MavenSettings`
is supported. When no type is specified, a custom config file will be generated.

Config files will be created before jobs to ensure that the file exists before it is referenced.

```groovy
configFile('my-config') {
  comment 'My important configuration'
  content '<some-xml/>'
}

configFile('central-mirror', type: MavenSettings) {
  content readFileFromWorkspace('maven-settings/central-mirror.xml')
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
InputStream streamFileFromWorkspace(String filePath)
String readFileFromWorkspace(String filePath)
String readFileFromWorkspace(String jobName, String filePath) // since 1.25
```

With the first two variants, you can read in a file from the current workspace anywhere in the script. This assumes that
you checked out some source control as part of the job processing the DSL. This can be useful when populating fields on
a generated job, e.g.

```groovy
job('example') {
    steps {
        shell(readFileFromWorkspace('build.sh')
    }
}
```

And with the third variant, you can read a file from the workspace of any job. This can be used to set the description
of a job from a file in the job's workspace. The method will return `null` when the job or the file does not exist or
the job has no workspace, e.g. when it has not been built yet.

```groovy
job('acme-tests') {
    description(readFileFromWorkspace('acme-tests', 'README.txt'))
}
```

(since 1.15)

# Grab

**WARNING:** Grab support is deprecated, see [[Migration]]

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
job('example') {
    ...
    configure { project ->
        project / buildWrappers / EnvInjectPasswordWrapper {
            injectGlobalPasswords(true)
        }
    }
}
```

See [[The Configure Block]] page for details.

# DSL Factory

Because the engine is just Groovy, you can call other Groovy classes available in the workspace. When in those methods
the `job` method is no longer available, so it is recommended to pass in the current context to make this method
available to another context. For example, when making utility methods, you would call them like this:

```groovy
BuildFramework.ant(this, arg1, arg2)
```

Then the `BuildFramework` class has everything it needs to make `job` calls:

```groovy
class BuildFramework {
    static ant(dslFactory, arg1, arg2) {
        dslFactory.job {
            name arg1
            steps {
                ant(arg2)
            }
        }
    }
}
```
