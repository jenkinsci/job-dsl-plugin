This is the in-depth documentation of the methods available on inside the _job_ part of the DSL.

## Free Style Job

```groovy
freeStyleJob(String name) { // since 1.30
    name(String name) // deprecated since 1.30

    // DSL specific methods
    using(String templateName)
    configure(Closure configBlock)
    previousNames(String regex) // since 1.29

    batchTask(String name, String script)
    blockOn(String projectNames)
    blockOn(Iterable<String> projectNames)
    blockOn(String projectNames, Closure closure) // since 1.36
    blockOn(Iterable<String> projectNames, Closure closure) // since 1.36
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
    logRotator(int daysToKeep = -1, int numToKeep = -1,
               int artifactDaysToKeep = -1, int artifactNumToKeep = -1)
    logRotator(Closure logRotatorClosure) // since 1.25
    notifications(Closure notificationClosure) // since 1.26
    priority(int value)
    quietPeriod(int seconds = 5)
    throttleConcurrentBuilds(Closure throttleClosure)
    authorization {
        permission(String permission)
        permission(String permission, String user)
        permission(Permissions perm, String user) // deprecated since 1.31
        permissionAll(String user)
        blocksInheritance(boolean blocksInheritance = true) // since 1.35 
    }
    parameters {
        booleanParam(String parameterName, boolean defaultValue = false,
                     String description = null)
        choiceParam(String parameterName, List<String> options, String description = null)
        fileParam(String fileLocation, String description = null)
        gitParam(String parameterName, Closure closure = null) // since 1.31
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
        hg(String url, Closure hgClosure) // since 1.33
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
        rundeck(Closure rundeckTriggerClosure = null) // since 1.33
        scm(String cron)
        scm(String cron, Closure scmTriggerClosure) // since 1.31
        snapshotDependencies(boolean checkSnapshotDependencies)
        urlTrigger(String cronString = null, Closure urlTriggerClosure)
        upstream(String projects, String threshold = 'SUCCESS') // since 1.33
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
        preScmSteps(Closure closure) // since 1.31
        rbenv(String rubyVersion, Closure rbenvClosure = null) // since 1.27
        release(Closure releaseClosure) // since 1.22
        runOnSameNodeAs(String jobName, boolean useSameWorkspace = false)
        rvm(String rubySpecification)
        sshAgent(String credentials)
        timeout(Closure timeoutClosure = null)
        timestamps()
        toolenv(String... tools)
        xvfb(String installation, Closure xvfbClosure = null) // since 1.31
        xvnc(Closure xvncClosure = null) // since 1.26
    }
    properties { // since 1.33
        customIcon(String iconFileName)
        sidebarLinks(Closure sidebarLinkClosure)
    }
    steps {
        ant(Closure antClosure = null)
        ant(String targets, Closure antClosure = null)
        ant(String targets, String buildFile, Closure antClosure = null)
        ant(String targets, String buildFile, String antInstallation,
            Closure antClosure = null)
        batchFile(String command)
        buildDescription(String regexp, String description = null) // since 1.31
        conditionalSteps(Closure conditionalClosure)
        copyArtifacts(String jobName, String includeGlob,
                      Closure buildSelectorClosure)  // deprecated since 1.33
        copyArtifacts(String jobName, String includeGlob, String targetPath,
                      Closure buildSelectorClosure)  // deprecated since 1.33
        copyArtifacts(String jobName, String includeGlob, String targetPath = '',
                      boolean flattenFiles,
                      Closure buildSelectorClosure)  // deprecated since 1.33
        copyArtifacts(String jobName, String includeGlob, String targetPath = '',
                      boolean flattenFiles, boolean optionalAllowed,
                      Closure buildSelectorClosure) // deprecated since 1.33
        copyArtifacts(String jobName, Closure copyArtifactClosure = null) // since 1.33
        criticalBlock(Closure stepClosure) // since 1.24
        debianPackage(String path, Closure debianClosure = null) // since 1.31
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
        nodejsCommand(String command, String nodeVersion) // since 1.31
        httpRequest(String url, Closure closure = null) // since 1.28
        maven(Closure mavenClosure) // since 1.20
        maven(String target = null, String pom = null, Closure configure = null)
        phase(Closure phaseClosure)
        phase(String name, Closure phaseClosure = null)
        phase(String name, String continuationConditionArg, Closure phaseClosure)
        powerShell(String command) // since 1.32
        prerequisite(String projectList = '', boolean warningOnly = false) // since 1.19
        publishOverSsh(Closure publishOverSshClosure) // since 1.28
        rake(Closure rakeClosure = null) // since 1.25
        rake(String tasksArg, Closure rakeClosure = null) // since 1.25
        remoteTrigger(String remoteJenkinsName, String jobName,
                      Closure remoteTriggerClosure) // since 1.22
        resolveArtifacts(Closure repositoryConnectorClosure) // since 1.29
        sbt(String sbtName = null, String actions = null, String sbtFlags = null,
            String jvmFlags = null, String subdirPath = null, Closure configure = null)
        setBuildResult(String result) // since 1.35
        shell(String command)
        systemGroovyCommand(String command, Closure systemGroovyClosure = null)
        systemGroovyScriptFile(String fileName, Closure systemGroovyClosure = null)
        vSpherePowerOff(String server, String vm)
        vSpherePowerOn(String server, String vm)
        vSphereRevertToSnapshot(String server, String vm, String snapshot)
        xcode(Closure xcodeClosure) // since 1.36
        xcodeDevProfile(String id) // since 1.36
    }
    publishers {
        aggregateBuildFlowTests() // since 1.35
        aggregateDownstreamTestResults(String jobs = null,
                                       boolean includeFailedBuilds = false) // since 1.19
        allowBrokenBuildClaiming()
        analysisCollector(Closure analysisCollectorClosure = null) // since 1.26
        androidLint(String pattern, Closure staticAnalysisClosure = null)
        archiveArtifacts(String glob, String excludeGlob = null,
                         boolean latestOnlyBoolean = false)
        archiveArtifacts(Closure archiveArtifactsClosure) // since 1.20
        archiveJavadoc(Closure javadocClosure) // since 1.19
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
        deployArtifacts(Closure deployArtifactsClosure = null) // since 1.31
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
        hipChat(Closure hipChatClosure = null) // since 1.33
        irc(Closure ircClosure)
        jacocoCodeCoverage(Closure jacocoClosure)
        joinTrigger(Closure joinTriggerClosure) // since 1.35
        jshint(String pattern, Closure staticAnalysisClosure = null)
        mailer(String recipients, Boolean dontNotifyEveryUnstableBuild = false,
               Boolean sendToIndividuals = false)
        mavenDeploymentLinker(String regex) // since 1.23
        mergePullRequest(Closure pullRequestClosure = null) // since 1.33
        plotBuildData(Closure closure) // since 1.31
        pmd(String pattern, Closure staticAnalysisClosure = null)
        postBuildScripts(Closure postBuildScriptsClosure) // since 1.31
        postBuildTask(Closure closure) // since 1.19
        publishBuild(Closure closure = null) // since 1.33
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
        publishOverSsh(Closure publishOverSshClosure) // since 1.34
        publishRobotFrameworkReports(Closure closure = null) // since 1.21
        publishScp(String site, Closure scpClosure)
        retryBuild(Closure naginatorClosure = null) // since 1.33
        rundeck(String jobId, Closure rundeckClosure = null) // since 1.24
        s3(String profile, Closure s3Closure) // since 1.26
        sonar(Closure sonarClosure = null) // since 1.31
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
}

job(type: Freeform, Closure closure) // deprecated since 1.30
```

## Build Flow Job

```groovy
buildFlowJob(String name) { // since 1.30
    // includes all options from freeStyleJob

    buildFlow(String buildFlowText)
}

job(type: BuildFlow, Closure closure) // deprecated since 1.30
```

## Matrix Job

```groovy
matrixJob(String name) { // since 1.30
    // includes all options from freeStyleJob

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

job(type: Matrix, Closure closure) // deprecated since 1.30
```

## Maven Job

```groovy
mavenJob(String name) { // since 1.30
    // includes all options from freeStyleJob

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
    postBuildSteps(String threshold, Closure stepsClosure) // since 1.35
    providedSettings(String mavenSettingsName) // since 1.25
    disableDownstreamTrigger(boolean value = true) // since 1.35
    wrappers {
        mavenRelease(Closure mavenReleaseClosure = null) // since 1.25
    }
}

job(type: Maven, Closure closure) // deprecated since 1.30
```

## Multi Job
```groovy
multiJob(String name) { // since 1.30
    // includes all options from freeStyleJob
}

job(type: Multijob, Closure closure) // deprecated since 1.30
```

## Workflow Job
```groovy
workflowJob(String name) { // since 1.30
    // includes all options from freeStyleJob

    definition {
        cps(Closure cpsClosure)
    }
}

job(type: Workflow, Closure closure) // deprecated since 1.30
```

## Job Options

### Name
```groovy
name(String jobName)
```

The Name of the job, **required**. This could be a static name but given the power of Groovy you could get very fancy with the these.

If using the [folders plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Free+Enterprise+Plugins#CloudBeesFreeEnterprisePlugins-FoldersPlugin), the full path to the job can be used. e.g.
```groovy
name('path/to/myjob')
```
Note that the folders must already exist. (Available since 1.17).

The name is treated as absolute to the Jenkins root by default, but the seed job can be configured to interpret names
relative to the seed job. (since 1.24)

### Display Name
```groovy
displayName(String displayName)
```

The name to display instead of the actual job name. (Available since 1.16)

### Using
```groovy
using(String templateName)
```

Refers to a template Job to be used as the basis for this job. These are loaded before any configure blocks or DSL
commands.  Template Jobs are just standard Jenkins Jobs which are used for their underlying config.xml. When they are
changed, the seed job will attempt to re-run, which has the side-effect of cascading changes of the template the jobs
generated from it.

The template name is treated as absolute to the Jenkins root by default, but the seed job can be configured to interpret
names relative to the seed job. (since 1.24)

### Description
```groovy
description(String desc)
```

Sets description of the job. This is a not a good way of creating a dynamic description of a job.

### Label
```groovy
label(String labelStr)
```

Label which specifies which nodes this job can run on, e.g. 'X86&&Ubuntu'

### Previous Names
```groovy
previousNames(String regex)
```

Is used by the Plugin when the jobs configuration is updated. If exactly one jobs exists matching this regular expression
then it is renamed to the name of the current job before the configuration is updated.
The regular expression needs to match the full name of the job, i.e. with folders included.

### Disable

```groovy
disabled(Boolean shouldDisable)
```

Provides ability to disable a job.

### Quiet period
```groovy
quietPeriod()
quietPeriod(int seconds)
```

Defines a timespan to wait for additional events (pushes, check-ins) before triggering a build. This prevents Jenkins from starting multiple jobs for check-ins/pushes that occur almost at the same time.

If the number of seconds to wait is omitted from the call the job will be configured to wait for five seconds. If you need to wait for a different amount of time just specify the number of seconds to wait. (Available since 1.16)

### Block Build

```groovy
job {
    blockOn(String projectName)
    blockOn(Iterable<String> projectNames)
    blockOn(String projectName) {              // since 1.36
        blockLevel(String blockLevel)
        scanQueueFor(String scanQueueFor)
    }
    blockOn(Iterable<String> projectNames) {   // since 1.36
        blockLevel(String blockLevel)
        scanQueueFor(String scanQueueFor)
    }
}
```

Block build if certain jobs are running. Requires the
[Build Blocker Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Blocker+Plugin).
 
Regular expressions can be used for the project names, e.g. `/.*-maintenance/` will match all maintenance jobs.

Possible values for `blockLevel` are `'GLOBAL'` and `'NODE'` (default). Possible values for `scanQueueFor` are `'ALL'`,
`'BUILDABLE'` and `'DISABLED'` (default).

```groovy
job('example-1') {
    blockOn('project-a')
}

job('example-2') {
    blockOn(['project-a', 'project-b']) {
        blockLevel('GLOBAL')
        scanQueueFor('ALL')
    }
}
```

### Block on upstream/downstream projects
```groovy
blockOnUpstreamProjects()
blockOnDownstreamProjects()
```

Blocks the build of a project when one ore more upstream (blockOnUpstreamProjects()) or a downstream projects (blockOnDownstreamProjects()) are running. (Available since 1.16)

### Build History

```groovy
job {
    logRotator(int daysToKeep = -1, int numToKeep = -1,
               int artifactDaysToKeep = -1, int artifactNumToKeep = -1)
    logRotator { // since 1.35
        daysToKeep(int daysToKeep)
        numToKeep(int numToKeep)
        artifactDaysToKeep(int artifactDaysToKeep)
        artifactNumToKeep(int artifactNumToKeep)
    }
}
```

Sets up the number of builds to keep.

```groovy
job('example-1') {
    logRotator(30, -1, 1, -1)
}

job('example-2') {
    logRotator {
        numToKeep(5)
        artifactNumToKeep(1)
    }
}
```

### Execute concurrent builds
```groovy
concurrentBuild(boolean allowConcurrentBuild = true)
```

If enabled, Jenkins will schedule and execute multiple builds concurrently (provided that you have sufficient executors and incoming build requests).

```groovy
job('example') {
   concurrentBuild()
}
```


### Custom workspace
```groovy
customWorkspace(String workspacePath)
```

Defines that a project should use the given directory as a workspace instead of the default workspace location. (Available since 1.16)

### JDK
```groovy
jdk(String jdkStr)
```

Selects the JDK to be used for this project. The jdkStr must match the name of a JDK installation defined in the Jenkins system configuration. The default JDK will be used when the jdk method is omitted.

### Batch Tasks

```groovy
job {
    batchTask(String name, String script)
}
```

Adds batch tasks that are not regularly executed to projects, such as releases, integration, archiving. Can be called
multiple times to add more batch tasks. Requires the
[Batch Task Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Batch+Task+Plugin).

Example:

```groovy
job('example') {
    batchTask('upload', 'curl --upload-file build/dist.zip http://www.example.com/upload')
    batchTask('release', readFileFromWorkspace('scripts/release.sh'))
}
```

(since 1.24)

### Lockable Resources

```groovy
job {
    lockableResources(String resources) {
        resourcesVariable(String name) // reserved resources variable name
        resourceNumber(int number)     // number of the listed resources to request
    }
}
```

Lock resources while a job is running. Requires the
[Lockable Resources Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Lockable+Resources+Plugin).

Examples:

```groovy
// lock single resource
job('example-1') {
    lockableResources('lock-resource')
}

// notation that locks three resources at once
job('example-2') {
    lockableResources('resource1 resource2 resource3')
}

// lock two available resources from given three and capture locked resources in the variable name
job('example-3') {
    lockableResources('resource1 resource2 resource3') {
        resourcesVariable('LOCKED_RESOURCES')
        resourceNumber(2)
    }
    steps {
        shell('echo Following resources are locked: $LOCKED_RESOURCES')
    }
}
```

(Since 1.25)

### Security

```groovy
job {
    authorization {
        permission(String)
        permission(String permission, String user)
        permissionAll(String user)
        permission(Permissions perm, String user) // deprecated since 1.31
        blocksInheritance(boolean blocksInheritance = true) // since 1.35 
    }
}
```

Creates permission records. Requires the
[Matrix Authorization Strategy Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Matrix+Authorization+Strategy+Plugin).

The first form adds a specific permission, e.g. `'hudson.model.Item.Workspace:authenticated'`, as seen in config.xml.
The second form breaks apart the permission from the user name, to make scripting easier. The third form will add all
available permission for the user.

```groovy
// add a permission for the special authenticated group to see the workspace of the job
job('example-1') {
    authorization {
        permission('hudson.model.Item.Workspace:authenticated')
    }
}

// adds the build permission for the special anonymous user
job('example-2') {
    authorization {
        permission('hudson.model.Item.Build', 'anonymous')
    }
}

// add all permissions for user joe, blocking inheritance of the global
// authorization matrix
job('example-3') {
    authorization {
        permissionAll('joe')
        blocksInheritance()
    }
}
```

### [Throttle Concurrent Builds](https://wiki.jenkins-ci.org/display/JENKINS/Throttle+Concurrent+Builds+Plugin)

```groovy
job('example-1') {
    // Throttle one job on its own
    throttleConcurrentBuilds {
        maxPerNode 1
        maxTotal 2
    }
}
```

```groovy
job('example-2') {
    // Throttle as part of a category
    throttleConcurrentBuilds {
        categories(['cat-1'])
    }
}
```

### Delivery Pipeline Configuration

```groovy
job {
    deliveryPipelineConfiguration(String stageName, String taskName = null)
}
```

Sets the stage name and task name for the delivery pipeline view. Each of the parameters can be set to `null` to use the
job name as stage or task name. Requires the
[Delivery Pipeline Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Delivery+Pipeline+Plugin).

```groovy
// use job name as task name
job('example-1') {
    deliveryPipelineConfiguration('qa')
}

// use custom task name
job('example-2') {
    deliveryPipelineConfiguration('qa', 'integration-tests')
}
```

(since 1.26)

### Notification Plugin

```groovy
job {
    notifications {
        endpoint(String url, String protocol = 'HTTP', String format = 'JSON') {
            event(String event)       // defaults to 'all', introduced in Notification Plugin 1.6
            timeout(int milliseconds) // defaults to 30000, introduced in Notification Plugin 1.6
        }
    }
}
```

Configures notifications for the build. Requires the
[Notification Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Notification+Plugin).

Possible values for protocol are `'HTTP'`, `'TCP'`, or `'UDP'`. Possible values for format are `'JSON'` or `'XML'`.
Possible values for event are `'all'`, `'started'`, `'completed'`, or `'finalized'`.

```groovy
job('example') {
    notifications {
        endpoint('http://example.com:8080/monitor')
        endpoint('10.100.2.3:3434', 'TCP', 'XML') {
            event('started')
            timeout(60000)
        }
    }
}
```

(since 1.26)

### Build Flow

```groovy
buildFlow(String flowDsl)
```

Insert text into the Build Flow text block. This can only be used in [Build Flow](https://wiki.jenkins-ci.org/display/JENKINS/Build+Flow+Plugin) job types.

Examples:

Triple-quote can be used for retaining Groovy style in the embedded DSL.

```groovy
buildFlowJob('example-1') {
    buildFlow("""
        build("job1")
    """)
}
```

Using job variables in build flow text block. The new job will have a build flow text like this: `build("hello-there")`.

```groovy
CUSTOM_VARIABLE = "hello-there"
buildFlowJob('example-2') {
    buildFlow('build("${CUSTOM_VARIABLE}")')
}
```

The build flow text can also be stored in a file and set in the new job when it's created.

```groovy
buildFlowJob('example-3') {
    buildFlow(readFileFromWorkspace("my-build-flow-text.groovy"))
}
```

Since 1.21.

# Maven

The `rootPOM`, `goals`, `mavenOpts`, `mavenInstallation`, `perModuleEmail`, `archivingDisabled`, `runHeadless`,
 `preBuildSteps`, `postBuildSteps` and `providedSettings` methods can only be used in jobs with type `Maven`.

### Root POM

```groovy
mavenJob {
    rootPOM(String rootPOM)
}
```

To use a different `pom.xml` in some other directory than the workspace root.

### Goals

```groovy
mavenJob {
    goals(String goals)
}
```

The Maven goals to execute including other command line options.

When specified multiple times, the goals and options will be concatenated, e.g.

```groovy
mavenJob('example-1') {
    goals('clean')
    goals('install')
    goals('-DskipTests')
}
```

is equivalent to

```groovy
mavenJob('example-1') {
    goals('clean install -DskipTests')
}
```

### MAVEN_OPTS

```groovy
mavenJob {
    mavenOpts(String mavenOpts)
}
```

The JVM options to be used when starting Maven. When specified multiple times, the options will be concatenated.

### Maven Installation

```groovy
mavenJob {
    mavenInstallation(String name)
}
```

Refers to the pull down box in the UI to select which installation of Maven to use, specify the exact string seen in the UI. The last call will be the one used.

(since 1.20)

### Isolated Local Maven Repository

```groovy
mavenJob {
    localRepository(LocalRepositoryLocation location)
}
```

Possible values for `localRepository` are `LocalRepositoryLocation.LOCAL_TO_WORKSPACE` and
`LocalRepositoryLocation.LOCAL_TO_EXECUTOR`. The `LocalToWorkspace` and `LocalToExecutor` values are deprecated since
1.31.

```groovy
mavenJob {
    localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
}
```

(Since 1.17)

### Email Per Module

```groovy
mavenJob {
    perModuleEmail(boolean sendEmailPerModule) // deprecated since 1.29
}
```

Enable or disable email notifications for each Maven module.

### Disable Artifact Archiving

```groovy
mavenJob {
    archivingDisabled(boolean shouldDisableArchiving)
}
```

Disables automatic Maven artifact archiving. Artifact archiving is enabled by default.

### Run Headless

```groovy
mavenJob {
    runHeadless(boolean shouldRunHeadless)
}
```

Specify this to run the build in headless mode if desktop access is not required. Headless mode is not enabled by default.

### Disable Downstream Triggering

```groovy
mavenJob {
    disableDownstreamTrigger(boolean disableDownstreamTrigger = true)
}
```

Disables automatic downstream build triggering. Downstream build triggering is enabled by default.

```groovy
mavenJob('example') {
    disableDownstreamTrigger()
}
```

(since 1.35)

### Maven Pre and Post Build Steps

```groovy
mavenJob {
    preBuildSteps(Closure stepsClosure)
    postBuildSteps(Closure stepsClosure)
    postBuildSteps(String thresholdName, Closure stepsClosure) // since 1.35
}
```

For Maven jobs, you can also run arbitrary build steps before and after the Maven execution. Note that this can only be
used with Maven jobs. You can also also specify a threshold for the build result when to run the postBuildSteps.
The thresholdName can be one of three values: `'SUCCESS'`, `'UNSTABLE'` or `'FAILURE'`. The default value is
`'FAILURE'`, i.e. always run the post build steps.

```groovy
mavenJob('example-1') {
  preBuildSteps {
    shell("echo 'run before Maven'")
  }
  postBuildSteps {
    shell("echo 'run after Maven'")
  }
}

mavenJob('example-2') {
  // run post build steps only when the build succeeds
  postBuildSteps('SUCCESS') {
    shell("echo 'run after Maven'")
  }
}
```

(since 1.20)

### Maven Settings

```groovy
mavenJob {
    providedSettings(String mavenSettingsName)
}
```

Use managed Maven settings. Requires the
[Config File Provider Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Config+File+Provider+Plugin).

```groovy
mavenJob('example') {
    providedSettings('central-mirror')
}
```

(since 1.25)

### Environment Variables
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
    overrideBuildParameters(boolean overrideBuildParameters = true) // since 1.30
    contributors {
        populateToolInstallations() // requires the SharedObjects Plugin, since 1.30
    }
}
```

Injects environment variables into the build. They can be provided as a Map or applied as part of a context. The optional Groovy script must return a map Java object. Requires the [EnvInject plugin](https://wiki.jenkins-ci.org/display/JENKINS/EnvInject+Plugin).

### Inject global passwords

```groovy
job {
    wrappers {
        injectPasswords()
    }
}
```

Injects globally defined passwords as environment variables into the job. Requires the [EnvInject plugin](https://wiki.jenkins-ci.org/display/JENKINS/EnvInject+Plugin).

(since 1.23)

### Job Priority
```groovy
priority(int value)
```

Allows jobs waiting in the build queue to be sorted by a static priority rather than the standard FIFO. The default priority is 100. A jobs with a higher priority will be executed before jobs with a lower priority. Requires the [Priority Sorter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Priority+Sorter+Plugin).

# Source Control

### SCM retry count

```groovy
checkoutRetryCount()
checkoutRetryCount(int times)
```

Defines the number of times the build should retry to check out from the SCM if the SCM checkout fails.

The parameterless invocation sets a default retry count of three (3) times. To specify more (or less) retry counts pass the number of times to retry the checkout. (Available since 1.16)

### Mercurial

```groovy
job {
    scm {
        hg(String url) { // since 1.33
            installation(String installation)  // use a specific installation
            credentials(String credentialsId)  // use pre-defined credentials
            branch(String branch)              // checkout selected branch
            tag(String tag)                    // checkout selected tag
            modules(String... modules)         // checkout selected modules
            clean(boolean clean = true)        // defaults to false
            subdirectory(String subdirectory)  // checkout into subdirectory
            disableChangeLog(boolean disable = true) // defaults to false
            configure(Closure configure)       // optional configure block
        }

        hg(String url, String branch = null, Closure configure = null)
    }
}
```

Adds a Mercurial SCM source. Requires the
[Mercurial Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Mercurial+Plugin). The first variant can be used for
advanced configuration (since 1.33) and requires version 1.50.1 or later of the Mercurial plugin, the other variant is a
shortcut for simpler Mercurial SCM configuration. Support for versions older than 1.50.1 is deprecated and will be
removed.

A [[configure block|The-Configure-Block]] can be used to add more options. The `scm` node is passed into the configure
block.

```groovy
// checkout feature_branch1
job('example-1') {
    scm {
        hg('http://scm', 'feature_branch1')
    }
}

// clean checkout module1 from feature_branch1
job('example-2') {
    scm {
        hg('http://scm') {
            branch('feature_branch1')
            modules('module1')
            clean(true)
        }
    }
}
```

### Git

```groovy
job {
    scm {
        git {
            // since 1.20
            remote { // can be repeated to add multiple remotes
                name(String name) // optional
                url(String url) // use either url or github
                github(String ownerAndProject, String protocol = 'https',
                       String host = 'github.com')
                refspec(String refspec) // optional
                credentials(String credentialsId) // optional
            }
            branch(String name) // calls are accumulated, defaults to '**'
            branches(String... names)
            mergeOptions(String remote = null, String branch)
            createTag(boolean createTag = true) // defaults to false
            clean(boolean clean = true) // defaults to false
            wipeOutWorkspace(boolean wipeOut = true) // defaults to false
            remotePoll(boolean remotePoll = true) // defaults to false
            shallowClone(boolean shallowClone = true) // defaults to false
            recursiveSubmodules(boolean recursive = true) // since 1.33
            pruneBranches(boolean pruneBranches = true) // defaults to false
            localBranch(String branch) // check out to specific local branch
            relativeTargetDir(String directory)
            reference(String reference) // path to a reference repository
            cloneTimeout(int timeout) // since 1.28, timeout in minutes
            ignoreNotifyCommit(boolean value = true) // since 1.33
            browser { // since 1.26
                stash(String url) // URL to the Stash repository, optional
                gitblit(String url, String projectName) // since 1.35
                gitLab(String url, String version)      // since 1.35
            }
            strategy { // since 1.30
                inverse()
                ancestry(int maxAgeInDays, String commit)
                gerritTrigger()
            }

            configure(Closure configure) // optional configure block
        }

        git(String url, String branch = null, Closure configure = null)

        github(String ownerAndProject, String branch = null,
               String protocol = 'https', String host = 'github.com',
               Closure configure = null)
    }
}
```

Adds a Git SCM source. The first variant can be used for advanced configuration (since 1.20), the other two variants are
shortcuts for simpler Git SCM configurations. Requires the
[Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin).

The closure parameter of the shortcut variants expects a [[configure block|The-Configure-Block]] closure.

The `github` methods will derive the Git URL from the `ownerAndProject`, `protocol` and `host` parameters. Valid
protocols are `'https'`, `'ssh'` and `'git'`. They also configure the source browser to point to GitHub and set the
GitHub project URL.

The Git plugin has a lot of configurable options, which are currently not all supported by the DSL. A
[[configure block|The-Configure-Block]] can be used to add more options. The `GitSCM` node is passed into the configure
block.

Version 2.0 or later of the Git Plugin is required to use `cloneTimeout` or Jenkins managed credentials for Git
authentication. The argument for the `credentials` method can either be the ID of the credentials or its description.
Note that finding credentials by description has been [[deprecated|Deprecation-Policy]], see [[Migration]].

When Git Plugin version 2.0 or later is used, `mergeOptions` can be called multiple times to merge more than one branch.

```groovy
// checkout repo1 to a sub directory and clean the workspace after checkout
job('example-1') {
    scm {
        git {
            remote {
                name('remoteB')
                url('git@server:account/repo1.git')
            }
            clean()
            relativeTargetDir('repo1')
        }
    }
}

// add the upstream repository as second remote and
// merge branch featureA with master
job('example-2') {
    scm {
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
    }
}

// add user name and email options
job('example-3') {
    scm {
        git('git@git') { node -> // is hudson.plugins.git.GitSCM
            node / gitConfigName('DSL User')
            node / gitConfigEmail('me@me.com')
        }
    }
}

// add Git SCM for GitHub repository job-dsl-plugin of GitHub user jenkinsci
job('example-4') {
    scm {
        github('jenkinsci/job-dsl-plugin')
    }
}

// add Git SCM for a GitHub repository with authentication
job('example-5') {
    scm {
        git {
            remote {
                github('account/repo', 'ssh')
                credentials('github-ci-key')
            }
        }
    }
}
```

### Subversion

```groovy
job {
    scm {
        svn(String svnUrl, Closure configure = null)
        svn(String svnUrl, String localDir, Closure configure = null)
        svn { // since 1.30
            location(String svnUrl) {           // at least on required
                directory(String directory)     // defaults to '.'
                credentials(String credentialsId)
                depth(SvnDepth depth)           // defaults to INFINITY
            }
            checkoutStrategy(SvnCheckoutStrategy strategy)
            excludedRegions(String... patterns)
            excludedRegions(Iterator<String> patterns)
            includedRegions(String... patterns)
            includedRegions(Iterable<String> patterns)
            excludedUsers(String... users)
            excludedUsers(Iterable<String> users)
            excludedCommitMessages(String... patterns)
            excludedCommitMessages(Iterable<String> patterns)
            excludedRevisionProperty(String revisionProperty)
            configure(Closure configure) // the scm node is passed into the configure block
        }
    }
}
```

Adds a Subversion SCM source. The first two variants are shortcuts for simpler SVN configurations, the last variant
should be used for advanced configurations.

When using the advanced variant, at least one location must be configured in order for the SVN plugin to operate
correctly. By default, files are checked out into the workspace directory. To change this behaviour specify an
alternate directory using the `directory` option. Directories specified using `directory` are relative to the workspace
directory.

Valid values for `checkoutStrategy` are `SvnCheckoutStrategy.UPDATE` (the default), `SvnCheckoutStrategy.CHECKOUT`,
`SvnCheckoutStrategy.UPDATE_WITH_CLEAN` or `SvnCheckoutStrategy.UPDATE_WITH_REVERT`.

Valid values for `depth` are `SvnDepth.INFINITY` (the default), `SvnDepth.EMPTY`, `SvnDepth.IMMEDIATES`,
`SvnDepth.FILES` and `SvnDepth.AS_IT_IS`.

`excludedRegions`, `includedRegions`, `excludedUsers` and `excludedCommitMessages` can be called multiple times to
exclude or include more patterns or users.

Version 2.0 or later of the Subversion Plugin is required to use the `credentials` method. The argument for the
`credentialsId` method can either be the ID of the credentials or its description. Note that finding credentials by
description has been [[deprecated|Deprecation-Policy]], see [[Migration]].

```groovy
// checkout a project into the workspace directory
job('example-1') {
    scm {
        svn('https://svn.mydomain.com/repo/project1/trunk')
    }
}

// checkout multiple projects
job('example-2') {
    scm {
        svn {
            location('https://svn.mydomain.com/repo/project1/trunk')
            location('https://svn.mydomain.com/repo/project2/trunk') {
                directory('proj2')
            }
        }
    }
}

// do a sparse checkout
job('example-3') {
    scm {
        svn {
            location('https://svn.mydomain.com/repo/project/trunk') {
                directory('proj2')
                depth(SvnDepth.EMPTY)
            }
        }
    }
}

// using a different checkout strategy
job('example-4') {
    scm {
        svn {
            location('https://svn.mydomain.com/repo/project1/trunk')
            checkoutStrategy(SvnCheckoutStrategy.CHECKOUT)
        }
    }
}

// configure excluded and included regions
job('example-5') {
    scm {
        svn {
            location('https://svn.mydomain.com/repo/project1/trunk')
            excludedRegions('/project1/trunk/.*\\.html')
            includedRegions('/project1/trunk/src/.*\\.java', '/project1/trunk/src/.*\\.groovy')
        }
    }
}

// configure excluded users, commit messages, and an excluded revision property
job('example-6') {
    scm {
        svn {
            location('https://svn.mydomain.com/repo/project1/trunk')
            excludedUsers('jsmith')
            excludedUsers('jdoe', 'sally')
            excludedCommitMessages('[Bb][Aa][Dd]')
            excludedRevisionProperty('mycompany:dontbuild')
        }
    }
}

// configure repository browser
job('example-7') {
    scm {
        svn {
            location('https://svn.mydomain.com/repo/project1/trunk')
            configure { scmNode ->
                scmNode / browser(class: 'hudson.scm.browsers.FishEyeSVN') {
                    url('http://mycompany.com/fisheye/repo_name')
                    rootModule('my_root_module')
                }
            }
        }
    }
}
```

### Perforce

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

### Clone Workspace

```
cloneWorkspace(String parentProject, String criteriaArg = 'Any')
```

Support the Clone Workspace plugin, by copy the workspace of another build. This complements another job which published their workspace.

### Base ClearCase

```groovy
baseClearCase {
    configSpec(String... configSpec)
    loadRules(String... loadRules)
    mkviewOptionalParameter(String... parameter)
    viewName(String viewName) // Default: 'Jenkins_${USER_NAME}_${NODE_NAME}_${JOB_NAME}${DASH_WORKSPACE_NUMBER}'
    viewPath(String viewPath) // Default: //view
```

Support for the [ClearCase plugin](http://wiki.jenkins-ci.org/display/JENKINS/ClearCase+Plugin).

`configSpec`, `loadRules`, `mkviewOptionalParameter` can also be called multiple times as these configurations can be
quite long.

Example defining config spec and load rules:

```groovy
baseClearCase {
    configSpec('''element * CHECKEDOUT
element * /main/LATEST''')
    loadRules('/vob/some_vob')
```

Example defining config spec and load rules with multiple methods calls:

```groovy
baseClearCase {
    configSpec('element * CHECKEDOUT')
    configSpec('element * /main/LATEST')
    loadRules('/vob/some_vob')
    loadRules('/vob/another_vob')
```

Example defining config spec and load rules using varargs parameters:

```groovy
baseClearCase {
    configSpec('element * CHECKEDOUT', 'element * /main/LATEST')
    loadRules('/vob/some_vob', '/vob/another_vob'')
```

This is another example which reads the config spec from a file in the seed job's workspace using
`readFileFromWorkspace`:

```groovy
baseClearCase {
    configSpec(readFileFromWorkspace('configSpec.txt'))
    loadRules('/vob/some_vob')
```

(since 1.24)

### Rational Team Concert (RTC)

```groovy
job {
    scm {
        rtc {
            buildDefinition(String buildDefinition)
            buildWorkspace(String buildWorkspace)
            connection(String buildTool, String credentialsId,
                       String serverURI, int timeout)
        }
    }
}
```

Support for the [Team Concert Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Team+Concert+Plugin).

The `credentialsId` argument can either be the ID of the credentials or its description. Note that finding credentials
by description has been [[deprecated|Deprecation-Policy]], see [[Migration]].

Examples:

```groovy
job('example-1') {
    scm {
        rtc {
            buildDefinition('buildDefinitionInRTC')
        }
    }
}

job('example-2') {
    scm {
        rtc {
            buildWorkspace('some-workspace')
            connection('my-build-tool', 'build-user', 'https://localhost:9444/ccm', 60)
        }
    }
}
```

(since 1.28)

# Triggers


Triggers block contains the available triggers.

### Cron
```groovy
cron(String cronString)
```

Triggers job based on regular intervals.

### Source Control Trigger

```groovy
job {
    triggers {
        scm(String cronString) {
            ignorePostCommitHooks(boolean ignorePostCommitHooks = true) // since 1.31
        }
    }
}
```

Polls source control for changes at regular intervals.

```groovy
job {
    triggers {
        scm('@daily')
    }
}

job {
    triggers {
        scm('@midnight') {
            ignorePostCommitHooks()
        }
    }
}
```

### Github Push Notification Trigger
```groovy
githubPush()
```

Enables the job to be started whenever a change is pushed to a github repository. Requires that Jenkins has the github plugin installed and that it is registered as service hook for the repository (also works with Github Enterprise). (Since 1.16)

### Gerrit

```groovy
gerrit {
    events {
        changeAbandoned() // since 1.26
        changeMerged()    // since 1.26
        changeRestored()  // since 1.26
        commentAdded()    // since 1.26
        draftPublished()  // since 1.26
        patchsetCreated() // since 1.26
        refUpdated()      // since 1.26
    }
    project(String projectName, List<String> branches)    // can be called multiple times
    project(String projectName, String branches)          // can be called multiple times
    buildStarted(Integer verified, Integer codeReview)    // updates the Gerrit report values for the build started event, use null to keep the default value
    buildSuccessful(Integer verified, Integer codeReview) // updates the Gerrit report values for the build successful event, use null to keep the default value
    buildFailed(Integer verified, Integer codeReview)     // updates the Gerrit report values for the build failed event, use null to keep the default value
    buildUnstable(Integer verified, Integer codeReview)   // updates the Gerrit report values for the build unstable event, use null to keep the default value
    buildNotBuilt(Integer verified, Integer codeReview)   // updates the Gerrit report values for the build not built event, use null to keep the default value
    configure(Closure configureClosure)                   // the com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTrigger node is handed in
}
```

Polls Gerrit for changes. This DSL method works slightly differently by exposing most of its functionality in its own
block. This is accommodating how the plugin can be pointed to multiple projects and trigger on many events.

Requires the [Gerrit Trigger Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Gerrit+Trigger).

Example:

```groovy
gerrit {
    events {
        changeMerged()
        draftPublished()
    }
    project('reg_exp:myProject', ['ant:feature-branch', 'plain:origin/refs/mybranch'])
    project('test-project', '**')
    buildSuccessful(10, null)
}
```

### Github Pull Request Trigger

```groovy
job {
    triggers {
        pullRequest {
            admin(String admin)
            admins(Iterable<String> admins)
            userWhitelist(String user)
            userWhitelist(Iterable<String> users)
            orgWhitelist(String organization)
            orgWhitelist(Iterable<String> organizations)
            cron(String cron)                           // defaults to 'H/5 * * * *'
            triggerPhrase(String triggerPhrase)
            onlyTriggerPhrase(boolean value = true)           // defaults to false
            useGitHubHooks(boolean useGithubHooks = true)     // defaults to false
            permitAll(boolean permitAll = true)               // defaults to false
            autoCloseFailedPullRequests(boolean value = true) // defaults to false
            commentFilePath(String commentFilePath)           // since 1.31
            allowMembersOfWhitelistedOrgsAsAdmin(boolean value = true) // since 1.35
        }
    }
}
```

Builds pull requests from GitHub and will report the results directly to the pull request. Requires the
[GitHub pull request builder plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin).

The pull request builder plugin requires a special Git SCM configuration, see the plugin documentation for details.

```groovy
job('example') {
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
            admin('USER_ID')
            userWhitelist('you@you.com')
            orgWhitelist(['your_github_org', 'another_org'])
            cron('H/5 * * * *')
            triggerPhrase('Ok to test')
            onlyTriggerPhrase()
            useGitHubHooks()
            permitAll()
            autoCloseFailedPullRequests()
            allowMembersOfWhitelistedOrgsAsAdmin()
        }
    }
}
```

(since 1.22)

### URL Trigger

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

### Snapshot Dependencies
```groovy
snapshotDependencies(boolean checkSnapshotDependencies)
```

When enabling the snapshot dependencies trigger, Jenkins will check the snapshot dependencies from the  '\<dependency\>', '\<plugin\>' and '\<extension\>' elements used in Maven POMs and setup a job relationship to the jobs building the snapshots. This can only be used in jobs with type 'maven'.

### Upstream

```groovy
job {
    triggers {
        upstream(String project, String threshold = 'SUCCESS')
    }
}
```

Starts a build on completion of an upstream job, i.e. adds the "Build after other projects are built" trigger. Requires
Jenkins 1.560 or later.

Possible values for `threshold` are `'SUCCESS'`, `'UNSTABLE'` or `'FAILURE'`.

```groovy
job('example') {
    triggers {
        upstream('other', 'UNSTABLE')
    }
}
```

(since 1.33)

### Rundeck

```groovy
job {
    triggers {
        rundeck {
            jobIdentifiers(String... jobIdentifiers)
            executionStatuses(String... executionStatuses)
        }
    }
}
```

Allows to schedule a build on Jenkins after a job execution on RunDeck. Requires the
[RunDeck Plugin](https://wiki.jenkins-ci.org/display/JENKINS/RunDeck+Plugin).

Possible values for `executionStatuses` are `'SUCCEEDED'`, `'FAILED'` and `'ABORTED'`. Both `jobIdentifiers` and
`executionStatuses` are empty by default.

```groovy
job('example') {
    triggers {
        rundeck {
            jobIdentifiers('2027ce89-7924-4ecf-a963-30090ada834f',
                           'my-project-name:main-group/sub-group/my-job')
            executionStatuses('FAILED', 'ABORTED')
        }
    }
}
```

(since 1.33)

# Build Environment (Build Wrappers)

Adds wrappers block to contain an list of build wrappers. The block exists since 1.19 and before that the methods were top-level.

### Node Stalker

Allows job to build on the same node as another job (https://wiki.jenkins-ci.org/display/JENKINS/Node+Stalker+Plugin).

```groovy
runOnSameNodeAs(String jobName, boolean useSameWorkspace = false)
```

(Since 1.17)

### RVM
```groovy
rvm('ruby-1.9.3')
rvm('ruby-2.0@gemset')
```

Configures the job to prepare a Ruby environment controlled by RVM for the build. Requires at least the ruby version, can take also a gemset specification to prevent side effects with other builds. (Available since 1.16)

### Build Timeout

```groovy
job {
    wrappers {
        timeout {
            elastic(int percentage = 150, int numberOfBuilds = 3, int minutesDefault = 60)
            noActivity(int seconds = 180)
            absolute(int minutes = 3)                  // default
            likelyStuck()
            failBuild()
            failBuild(boolean fail)                    // deprecated since 1.30
            abortBuild()                               // since 1.30
            writeDescription(String description)
        }
    }
}
```

The timeout method enables you to define a timeout for builds. It can either be absolute (build times out after a fixed
number of minutes), elastic (times out if build runs x% longer than the average build duration) or likelyStuck.

Requires version 1.12 or later of the
[Build Timeout Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build-timeout+Plugin).

The simplest invocation looks like this:

```groovy
job('example-1') {
    wrappers {
        timeout()
    }
}
```

It defines an absolute timeout with a maximum build time of 3 minutes.

Here is an absolute timeout:

```groovy
job('example-2') {
    wrappers {
        timeout {
            absolute(60)   // 60 minutes before timeout
        }
    }
}
```

The elastic timeout accepts three parameters: a percentage for determining builds that take longer than normal,
a limit that is used if there is no average successful build duration (i.e. no jobs run or all runs failed) and
the number of successful/unstable builds to consider to calculate the average duration:

```groovy
job('example-3') {
    wrappers {
        timeout {
            elastic(
                300, // Build will timeout when it take 3 time longer than the reference build duration, default = 150
                3,   // Number of builds to consider for average calculation
                30   // 30 minutes default timeout (no successful builds available as reference)
            )
        }
    }
}
```

The likelyStuck timeout times out a build when it is likely to be stuck. Does not take extra configuration parameters.

```groovy
job('example-4') {
    wrappers {
        timeout {
            likelyStuck()
        }
    }
}
```

The noActivity timeout times out a build when there has been no console activity for a certain duration.

```groovy
job('example-5') {
    wrappers {
        timeout {
            noActivity(180) // Timeout if there has been no activity for 180 seconds
        }
    }
}
```

When the timeout happens, the default action is to abort if no other actions are configured. There are three
configurable actions:

- Fail the build
- Abort the build
- Add a build description

They can be configured like this:

```groovy
job('example-6') {
    wrappers {
        timeout {
            absolute(30)
            failBuild()
            writeDescription('Build failed due to timeout after {0} minutes')
        }
    }
}
```

(since 1.24)

### Port allocation
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

### [SSH Agent](https://wiki.jenkins-ci.org/display/JENKINS/SSH+Agent+Plugin)

Makes shared SSH credential available to builds.

```groovy
job {
    wrappers {
        sshAgent(String credentialsId)
    }
}
```

The `credentialsId` argument can either be the ID of the credentials or its description. Note that finding credentials
by description has been [[deprecated|Deprecation-Policy]], see [[Migration]].

(Since 1.17)

### [Timestamper](https://wiki.jenkins-ci.org/display/JENKINS/Timestamper)

```groovy
job {
    wrappers {
        timestamps()
    }
}
```

Adds timestamps to the console log.

(Since 1.19)

### AnsiColor

```groovy
job {
    wrappers {
        colorizeOutput(String colorMap = 'xterm')
    }
}
```

Renders ANSI escape sequences, including color, to console output. Requires the
[AnsiColor Plugin](https://wiki.jenkins-ci.org/display/JENKINS/AnsiColor+Plugin).

```groovy
job('example') {
    wrappers {
        colorizeOutput()
    }
}
```

(Since 1.19)

### Xvfb

```groovy
job {
    wrappers {
        xvfb(String xvfbInstallation) {
            screen(String screen)                               // defaults to 1024x768x24
            debug(boolean debug = true)                         // defaults to false
            timeout(int timeout)                                // defaults to 0
            displayNameOffset(int displayNameOffset)            // defaults to 1
            shutdownWithBuild(boolean shutdownWithBuild = true) // defaults to false
            autoDisplayName(boolean autoDisplayName = true)     // defaults to false
            assignedLabels(String labels)
            parallelBuild(boolean parallelBuild = true)         // defaults to false
        }
    }
}
```

Controls the Xvfb virtual frame buffer X11 server. Requires the
[Xvfb Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Xvfb+Plugin).

```groovy
job {
    wrappers {
        xvfb('default') {
            screen('1920x1080x24')
        }
    }
}
```

### Xvnc

```groovy
job {
    wrappers {
        xvnc { // since 1.26
            takeScreenshot(boolean taskScreenshot = true) // defaults to false
            useXauthority(boolean useXauthority = true)   // defaults to true
        }
    }
}
```

This plugin lets you run an Xvnc session during a build. This is handy if your build includes UI testing that needs a
display available. Requires the [Xvnc Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Xvnc+Plugin).

The `useXauthority` option requires version 1.16 or later of the Xvnc Plugin.

```groovy
job('example') {
    wrappers {
        xvnc {
            takeScreenshot()
            useXauthority(false)
        }
    }
}
```

(since 1.19)

### [Tool Environment](https://wiki.jenkins-ci.org/display/JENKINS/Tool+Environment+Plugin)

```groovy
job('example') {
  wrappers {
    toolenv("Ant 1.8.2", "Maven 3.1")
  }
}
```

Downloads the specified tools, if needed, and puts the path to each of them in the build's environment.

(since 1.21)

### Config Files

```groovy
job {
    wrappers {
        configFiles {
            file(String fileName) {
                targetLocation(String targetLocation)       // optional
                variable(String variable)                   // optional
            }
            custom(String fileName,
                   Closure configFileClosure = null)        // since 1.35
            mavenSettings(String fileName,
                          Closure configFileClosure = null) // since 1.35
        }
    }
}
```

Makes an existing custom config file available to builds. Requires
the [Config File Provider Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Config+File+Provider+Plugin).

`file` is an alias for `custom`.

```groovy
job('example') {
    wrappers {
        configFiles {
            file('myCustomConfigFile') {
                variable('CONFIG_FILE')
            }
            mavenSettings('myJenkinsSettingsFile') {
                targetLocation('settings.xml')
            }
        }
    }
}
```

(since 1.28)

### Environment Variables

```groovy
job {
    wrappers {
        environmentVariables {
            scriptFile(String filePath)
            script(String content)
            env(Object key, Object value)
            envs(Map<Object, Object> map)
            propertiesFile(String filePath)
            groovy(String groovyScript)     // since 1.30
        }
    }
}
```

Injects environment variables into the build. Requires the
[EnvInject plugin](https://wiki.jenkins-ci.org/display/JENKINS/EnvInject+Plugin).

(Since 1.21)

### Release
```groovy
job {
    wrappers {
        release {
            releaseVersionTemplate(String template)
            doNotKeepLog(boolean keep = true)
            overrideBuildParameters(boolean override = true)
            parameters(Closure parameters)
            preBuildSteps(Closure steps)
            postSuccessfulBuildSteps(Closure steps)
            postBuildSteps(Closure steps)
            postFailedBuildSteps(Closure steps)
        }
    }
}
```

Configure a release inside a Jenkins job. Requires the [Release Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Release+Plugin).

For details of defining parameters (parameter) see [Reference of Parameters](https://github.com/jenkinsci/job-dsl-plugin/wiki/Job-reference#wiki-parameters)

For details of defining steps (preBuildSteps, postSuccessfulBuildSteps, postBuildSteps, postFailedBuildSteps) see [Reference of Build Steps](https://github.com/jenkinsci/job-dsl-plugin/wiki/Job-reference#wiki-build-steps)

Example
```groovy
job('example') {
    wrappers {
        release {
            doNotKeepLog()
            overrideBuildParameters()
            parameters {
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

### Workspace Cleanup Plugin

```groovy
job {
    wrappers {
        preBuildCleanup {
            includePattern(String pattern)  // all files are deleted if omitted
            excludePattern(String pattern)
            deleteDirectories(boolean deleteDirectories = true) // defaults to false if omitted
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
job('example') {
    wrappers {
        preBuildCleanup()
    }
}
```

```groovy
// cleanup all files and directories in target directories, but only if the CLEANUP build parameter is set to 'true'
job('example') {
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

### Pre SCM Build Steps

```groovy
job {
    wrappers {
        preScmSteps {
            steps(Closure stepClosure)
            failOnError(boolean failOnError = true) // defaults to false
        }
    }
}
```

Allows build steps to run before SCM checkout. Requires the
[Pre-SCM Build Step Plugin](https://wiki.jenkins-ci.org/display/JENKINS/pre-scm-buildstep).

See [Build Steps](#build-steps) for available steps in the `stepClosure`.

```groovy
job {
    wrappers {
        preScmSteps {
            steps {
                shell('echo Hello World')
            }
            failOnError()
        }
    }
}
```

(since 1.31)

### Log File Size Checker Plugin

```groovy
job {
    wrappers {
        logSizeChecker {
            maxSize(int size)
            failBuild(boolean failBuild = true) // optional, defaults to false if omitted
        }
    }
}
```

Configures the log file size checker plugin. Requires the [LogFileSizeChecker Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Logfilesizechecker+Plugin).

Examples:
```groovy
// default configuration using the system wide definition
job('example') {
    wrappers {
        logSizeChecker()
    }
}
```

```groovy
// using job specific configuration, setting the max log size to 10 MB and fail the build of the log file is larger.
job('example') {
    wrappers {
        logSizeChecker {
            maxSize(10)
            failBuild()
        }
    }
}
```

(since 1.23)

### Build Name Setter Plugin

```groovy
job {
    wrappers {
        buildName(String nameTemplate)
    }
}
```

Configures the [Build Name Setter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Name+Setter+Plugin). Token
expansion mechanism is provided by the
[Token Macro Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Token+Macro+Plugin).

Example:
```groovy
// define the build name based on the build number and an environment variable
job('example') {
    wrappers {
        buildName('#${BUILD_NUMBER} on ${ENV,var="BRANCH"}')
    }
}
```

(since 1.24)

### Keychains

```groovy
job {
    wrappers {
        keychains {
            keychain(String keychain, String identity, String prefix = '')
            delete(boolean delete = true)
            overwrite(boolean overwrite = true)
        }
    }
}
```

Configures keychains for the build. Requires the [Keychains and Provisioning Profiles
Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Keychains+and+Provisioning+Profiles+Plugin).

`keychain` can be used multiple times to add more keychains. With a single keychain, the prefix is optional. When using
multiple keychains, a prefix to differentiate between them.

Example:

```groovy
job('example') {
    wrappers {
        keychains {
            keychain('test1', 'test2')
            delete()
            overwrite()
        }
    }
}
```

(since 1.24)

### Exclusion Resources

```groovy
job {
    wrappers {
        exclusionResources(String... resourceNames)
        exclusionResources(Iterable<String> resourceNames)
    }
}
```

Configures exclusion plugin resources that are required for the `criticalBlock` step. The critical block contains
the build steps of the critical zone.
Requires the [Exclusion Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Exclusion-Plugin).

Example:

```groovy
job('example') {
    wrappers {
        exclusionResources('first', 'second')
    }
    steps {
        criticalBlock {
            shell('echo Hello World!')
        }
    }
}
```

(since 1.24)

### Maven Release

```groovy
mavenJob {
    wrappers {
        mavenRelease {
            scmUserEnvVar(String scmUserEnvVar) // empty by default
            scmPasswordEnvVar(String scmPasswordEnvVar) // empty by default
            releaseEnvVar(String releaseEnvVar) // default to 'IS_M2RELEASEBUILD'
            releaseGoals(String releaseGoals) // defaults to '-Dresume=false release:prepare release:perform'
            dryRunGoals(String dryRunGoals) // defaults to '-Dresume=false -DdryRun=true release:prepare'
            selectCustomScmCommentPrefix(boolean selected = true) // defaults to false
            selectAppendJenkinsUsername(boolean selected = true) // defaults to false
            selectScmCredentials(boolean selected = true) // defaults to false
            numberOfReleaseBuildsToKeep(int number) // defaults to 1
        }
    }
}
```

Allows to perform a release build using the maven-release-plugin. Only available for jobs with type `Maven`. Requires
the [M2 Release Plugin](https://wiki.jenkins-ci.org/display/JENKINS/M2+Release+Plugin).

Example:

```groovy
mavenJob('example') {
    wrappers {
        mavenRelease {
            scmUserEnvVar('MY_USER_ENV')
            scmPasswordEnvVar('MY_PASSWORD_ENV')
            releaseEnvVar('RELEASE_ENV')
            releaseGoals('release:prepare release:perform')
            dryRunGoals('-DdryRun=true release:prepare')
            selectCustomScmCommentPrefix()
            selectAppendJenkinsUsername()
            selectScmCredentials()
            numberOfReleaseBuildsToKeep(10)
        }
    }
}
```

(since 1.25)

### Delivery Pipeline Version

```groovy
job {
    wrappers {
        deliveryPipelineVersion(String template, boolean setDisplayName = false)
    }
}
```

Create a version based on the template and optionally sets that version as display name for the build. Requires the
[Delivery Pipeline Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Delivery+Pipeline+Plugin).

```groovy
job('example') {
    wrappers {
        deliveryPipelineVersion('1.0.${BUILD_NUMBER}', true)
    }
}
```

(since 1.26)

### Mask Passwords

```groovy
job {
    wrappers {
        maskPasswords()
    }
}
```

Masks the passwords that occur in the console output. Requires the
[Mask Passwords Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Mask+Passwords+Plugin)

(since 1.26)

### Build User Vars

```groovy
job {
    wrappers {
        buildUserVars()
    }
}
```

Adds a number of environment variables with information of the current user to the environment. Requires the
[Build User Vars Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+User+Vars+Plugin).

(since 1.26)

### NodeJS

```groovy
job {
    wrappers {
        nodejs(String installation)
    }
}
```

Sets up a NodeJS environment. Requires the [NodeJS Plugin](https://wiki.jenkins-ci.org/display/JENKINS/NodeJS+Plugin).

```groovy
job('example') {
    wrappers {
        nodejs('NodeJS 0.10.26')
    }
}
```

(since 1.27)

### NodeJS Command

```groovy
job {
    steps {
        nodejsCommand(String command, String nodeVersion)
    }
}
```

Executes a NodeJS script. Requires the [NodeJS Plugin](https://wiki.jenkins-ci.org/display/JENKINS/NodeJS+Plugin).

```groovy
job {
    steps {
        nodejsCommand('console.log("Hello World!")', 'Node 0.12.0')
    }
}
```

(since 1.31)

### Golang

```groovy
job {
    wrappers {
        golang(String version)
    }
}
```

Adds a wrapper for a golang environment. Requires the
[Golang Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Go+Plugin).

```groovy
job('example') {
    wrappers {
        golang('Go 1.3.3')
    }
}
```

(since 1.27)

### rbenv

```groovy
job {
    wrappers {
        rbenv(String rubyVersion) {
            ignoreLocalVersion(boolean ignore = true) // defaults to false
            gems(String... gems)
            root(String root)                         // defaults to '$HOME/.rbenv'
            rbenvRepository(String repository)        // defaults to 'https://github.com/sstephenson/rbenv.git'
            rbenvRevision(String revision)            // defaults to 'master'
            rubyBuildRepository(String repository)    // defaults to 'https://github.com/sstephenson/ruby-build.git'
            rubyBuildRevision(String revision)        // defaults to 'master'
        }
    }
}
```

Adds the ability to specify the rbenv wrapper to be used during job execution. You can specify the ruby version to used
(or installed if it is not already) and which gems you would like available during the job execution. Requires the
[rbenv Plugin](https://wiki.jenkins-ci.org/display/JENKINS/rbenv+plugin).

```groovy
job('example') {
    wrappers {
        rbenv('2.1.2') {
            ignoreLocalVersion()
            gems('bundler', 'rake')
        }
    }
}
```

(since 1.27)

### Credentials Binding

```groovy
job {
    wrappers {
        credentialsBinding {
            file(String variable, String credentialsId)
            string(String variable, String credentialsId)
            usernamePassword(String variable, String credentialsId)
            usernamePassword(String usernameVariable,
                             String passwordVariable, String credentialsId) // since 1.31
            zipFile(String variable, String credentialsId)
        }
    }
}
```

Bindings environment variables to credentials. Requires the
[Credentials Binding Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Credentials+Binding+Plugin).

The `credentialsId` argument can either be the ID of the credentials or its description. Note that finding credentials
by description has been [[deprecated|Deprecation-Policy]], see [[Migration]].

```groovy
job('example') {
    wrappers {
        credentialsBinding {
            file('KEYSTORE', 'keystore.jks')
            usernamePassword('PASSWORD', 'keystore password')
        }
    }
}
```

(since 1.28)

### Custom Tools Plugin

```groovy
job {
    wrappers {
        customTools(Iterable<String> toolNames) {
            skipMasterInstallation(boolean value = true)  // defaults to false
            convertHomesToUppercase(boolean value = true) // defaults to false
        }
    }
}
```

Specifies custom tools to add to the build environment. Requires the
[Custom Tools Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Custom+Tools+Plugin).

```groovy
job('example') {
    wrappers {
        customTools(['NodeJS', 'figlet']) {
            skipMasterInstallation()
        }
    }
}
```

(since 1.30)

# Build Steps

Adds step block to contain an ordered list of build steps. Cannot be used for jobs with type 'maven'.

### Shell command
```groovy
shell(String commandStr)
```

Runs a shell command.

### Batch File
```groovy
batchFile(String commandStr)
```

Supports running a Windows batch file as a build step.

### Build Description

```groovy
job {
    steps {
        buildDescription(String regexp, String description = null)
    }
}
```

Set build description based upon a regular expression test of the log file.
Requires the [Description Setter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Description+Setter+Plugin)

```groovy
job('example') {
    steps {
        buildDescription(/.*\[INFO\] Uploading project information for [^\s]* ([^\s]*)/)
    }
}
```

### Gradle

```groovy
job {
    steps {
        gradle(String tasksArg = null, String switchesArg = null,
               Boolean useWrapperArg = true, Closure configure = null)
        gradle { // since 1.27
            tasks(String tasks)                                           // can be called multiple times
            switches(String switches)                                     // can be called multiple times
            useWrapper(boolean useWrapper = true)                         // defaults to true
            description(String description)
            rootBuildScriptDir(String rootBuildScriptDir)
            buildFile(String buildFile)
            fromRootBuildScriptDir(boolean fromRootBuildScriptDir = true) // defaults to true
            gradleName(String gradleName)                                 // defaults to '(Default)'
            makeExecutable(boolean makeExecutable = true)                 // defaults to false
            configure(Closure configureBlock)
        }
    }
}
```

Runs Gradle, defaulting to the Gradle Wrapper. A `hudson.plugins.gradle.Gradle` node is passed into the configure block.
Requires the [Gradle Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Gradle+Plugin).

```groovy
job('example') {
    steps {
        gradle('check')
        gradle {
            tasks('clean')
            tasks('check')
            switches('--info')
        }
    }
}
```

### Maven
```groovy
maven(String targetsArg = null, String pomArg = null, Closure configure = null)

maven {                                               // since 1.20; all methods are optional
    goals(String goals)                               // the goals to run, multiple calls will be accumulated
    rootPOM(String fileName)                          // path to the POM
    mavenOpts(String options)                         // JVM options, multiple calls will be accumulated
    localRepository(LocalRepositoryLocation location) // defaults to LocalRepositoryLocation.LOCAL_TO_EXECUTOR
    mavenInstallation(String name)                    // name of the Maven installation to use
    properties(Map properties)                        // since 1.21; add (system)-properties
    property(String key, String value)                // since 1.21; add a (system)-property
    providedSettings(String mavenSettingsName)        // since 1.25
    configure(Closure configure)                      // configure block
}
```

Runs Apache Maven. Configure block is handed `hudson.tasks.Maven`. The
[Config File Provider Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Config+File+Provider+Plugin) is required to
use `providedSettings`.

Possible values for `localRepository` are `LocalRepositoryLocation.LOCAL_TO_WORKSPACE` and
`LocalRepositoryLocation.LOCAL_TO_EXECUTOR`. The `LocalToWorkspace` and `LocalToExecutor` values are deprecated since
1.31.

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
    providedSettings('central-mirror')
}
```

### Ant
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

### SBT

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

### PowerShell

```groovy
job {
    steps {
        powerShell(String command)
    }
}
```

Supports running a Windows PowerShell command as a build step. Requires the
[PowerShell Plugin](https://wiki.jenkins-ci.org/display/JENKINS/PowerShell+Plugin).

```groovy
job('example') {
    steps {
        powerShell('New-Item C:\\test')
    }
}
```

(since 1.32)

### Xcode

Supports
* running Xcode as a build step and
* importing developer profile.
Requires the
[Xcode Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Xcode+Plugin).

```groovy
job('example') {
    steps {
        xcode {
            // General build settings
            target(String target = '')
            interpretTargetAsRegEx(boolean = false)
            // Settings
            cleanBeforeBuild(boolean cleanBeforeBuild = false)
            allowFailingBuildResults(Boolean allowFailingBuildResults = false)
            generateArchive(boolean generateArchive = false)
            configuration(String configuration = 'Release')
            // (buildIpa enables the next 3 options)
            buildIpa(boolean buildIpa = false)
              ipaName(String ipaName = '')
              ipaOutputDirectory(String ipaOutputDirectory = '')
              ipaManifestPlistUrl (String ipaManifestPlistUrl = '')

            // Code signing & OS X keychain options
            // (changeBundleID enables the next 2 options)
            changeBundleID(Boolean changeBundleID = false)
              bundleID(String bundleID = '')
              bundleIDInfoPlistPath(String bundleIDInfoPlistPath = '')
            codeSigningIdentity(String codeSigningIdentity = '')
            embeddedProfileFile(String embeddedProfileFile = '')
            // (unlockKeychain enables the next 3 options)
            unlockKeychain(boolean unlockKeychain = false)
              keychainName(String keychainName = 'none (specify one below)')
              keychainPath(String keychainPath = '')
              keychainPwd(String keychainPwd = '')

            // Advanced Xcode build options
            cleanTestReports(boolean cleanTestReports = false)
            xcodeSchema(String xcodeSchema = '')
            sdk(String sdk = '')
            symRoot(String symRoot = '')
            xcodebuildArguments(String xcodebuildArguments = '')
            xcodeWorkspaceFile(String xcodeWorkspaceFile = '')
            xcodeProjectPath(String xcodeProjectPath = '')
            xcodeProjectFile(String xcodeProjectFile = '')
            configurationBuildDir(String configurationBuildDir = '')

            // Versioning
            // (provideApplicationVersion enables the next 2 options)
            provideApplicationVersion(Boolean provideApplicationVersion = false)
              cfBundleShortVersionStringValue(String cfBundleShortVersionStringValue = '')
              cfBundleVersionValue(String cfBundleVersionValue = '')
        }
    }
}
```

```groovy
job('example') {
    steps {
        xcodeDevProfile(String id)
    }
}
```

(since 1.36)

### Publish Over SSH

```groovy
job {
    steps {
        publishOverSsh {
            server(String name) {
                verbose(boolean verbose = true)
                credentials(String username) {
                    pathToKey(String pathToKey)
                    key(String key)
                }
                retry(int retries = 0, int delay = 10000)
                label(String label)
                transferSet {
                    sourceFiles(String sourceFiles)
                    execCommand(String execCommand)
                    removePrefix(String prefix)
                    remoteDirectory(String remoteDirectory)
                    excludeFiles(String excludeFiles)
                    patternSeparator(String patternSeparator)
                    noDefaultExcludes(boolean noDefaultExcludes = true)
                    makeEmptyDirs(boolean makeEmptyDirs = true)
                    flattenFiles(boolean flattenFiles = true)
                    remoteDirIsDateFormat(boolean value = true)
                    execTimeout(long execTimeout)
                    execInPty(boolean execInPty = true)
                }
            }
            continueOnError(boolean continueOnError = true)
            failOnError(boolean failOnError = true)
            alwaysPublishFromMaster(boolean alwaysPublishFromMaster = true)
            parameterizedPublishing(String parameterName)
        }
    }
    publishers {
        publishOverSsh(Closure publishOverSshClosure) // since 1.34
    }
}
```

Send artifacts to an SSH server (using SFTP) and/or execute commands over SSH. Requires the
[Publish Over SSH Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Publish+Over+SSH+Plugin).

Encrypted keys are currently not supported on job level, use the global configuration instead.

Examples:

```groovy
// basic step
job('example-1') {
    steps {
        publishOverSsh {
            server('server-name') {
                transferSet {
                    sourceFiles('file')
                }
            }
        }
    }
}

// post-build step, using parameter to match server label
job('example-2') {
    publishers {
        publishOverSsh {
            server('my-server-01') {
                credentials('user01') {
                    pathToKey('path01')
                }
                label('server-01')
                transferSet {
                    sourceFiles('files')
                    execCommand('command')
                }
            }
            server('my-server-02') {
                credentials('user2') {
                    key('key')
                }
                label('server-02')
                transferSet {
                    sourceFiles('files2')
                    execCommand('command2')
                }
            }
            parameterizedPublishing('PARAMETER')
        }
    }
}
```

(since 1.28)

### Rake

```groovy
job {
    steps {
        rake {
            task(String task)                     // a single task to execute
            tasks(Iterable<String> tasks)         // a list of tasks to execute
            file(String file)                     // path to a Rakefile
            installation(String installation)     // Ruby installation to use
            libDir(String libDir)                 // path to Rake library directory
            workingDir(String workingDir)         // path the working directory in which Rake should be executed
            bundleExec(boolean bundleExec = true) // execute Rake with Bundler 'bundle exec rake'
            silent(boolean silent = true)         // do not print to STDOUT
        }
        rake(String tasksArg, Closure rakeClosure = null) // see above for rakeClosure syntax
    }
}
```

Executes Rake as a build step. Requires the [Rake Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Rake+plugin).

Examples:

```groovy
job('example') {
    steps {
        rake('task')
        rake('first') {
            task('second')
            tasks(['third', 'fourth'])
            file('/opt/app/Rakefile')
            installation('ruby-2.0.0-p481')
            libDir('./rakelib')
            workingDir('/opt/app')
            bundleExec()
            silent()
        }
    }
}
```

(Since 1.25)

### Set the Build Result

```groovy
job {
    steps {
        setBuildResult(String result)
    }
}
```

Set the build status. Possible values are `'SUCCESS'`, `'UNSTABLE'`, `'FAILURE'`, `'ABORTED'`, `'CYCLE'`.

You can only worsen the current build status, not improve it.

Requires the [Fail The Build Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Fail+The+Build+Plugin).

```groovy
job('example') {
    steps {
        setBuildResult('UNSTABLE')
    }
}
```

(since 1.35)

### Job DSL

```groovy
job {
    steps {
        dsl(String scriptText, String removedJobAction = null,
            boolean ignoreExisting = false)
        dsl(Iterable<String> externalScripts, String removedJobAction = null,
            boolean ignoreExisting = false)
        dsl {
            removeAction(String removeAction)
            removeViewAction(String removeViewAction)     // since 1.35
            external(String... dslFileNames)
            external(Iterable<String> dslFileNames)
            text(String dslSpecification)
            ignoreExisting(boolean ignoreExisting = true) // false by default
            additionalClasspath(String classpath)         // since 1.29
            lookupStrategy(String lookupStrategy)         // since 1.33
        }
    }
}

Allows the programmatic creation of jobs, folders and views using the Job DSL.

Valid values for `removeAction` are `'IGNORE'` (default), `'DISABLE'` and `'DELETE'`. Valid values for
`removeViewAction` are `'IGNORE'` (default) and `'DELETE'`. Valid values for `lookupStrategy` are `'JENKINS_ROOT'`
(default) and `'SEED_JOB'`.

```groovy
job('example-1') {
    steps {
        dsl {
            external('projectA.groovy', 'projectB.groovy')
            external('projectC.groovy')
            removeAction('DISABLE')
            ignoreExisting()
            additionalClasspath('lib')
        }
    }
}

job('example-2') {
    steps {
        dsl(['projectA.groovy', 'projectB.groovy'], 'DELETE')
    }
}

job('example-3') {
    steps {
        dsl {
            text(readFileFromWorkspace('more-jobs.groovy'))
            removeAction('DELETE')
        }
    }
}
```

(since 1.16)

### Copy Artifacts

```groovy
job {
    steps {
        copyArtifacts(String jobName) { // since 1.33
            includePatterns(String... includes)
            excludePatterns(String... excludes)
            targetDirectory(String targetDirectory)
            flatten(boolean flatten = true)
            optional(boolean optional = true)
            fingerprintArtifacts(boolean fingerprint = true)
            buildSelector {
                upstreamBuild(boolean fallback = false)
                latestSuccessful(boolean stable = false) // default
                latestSaved()
                permalink(String linkName)
                buildNumber(int buildNumber)
                buildNumber(String buildNumber)
                workspace()
                buildParameter(String parameterName)
            }
        }
        copyArtifacts(String jobName, String includeGlob,
                      Closure buildSelectorClosure)      // deprecated since 1.33
        copyArtifacts(String jobName, String includeGlob, String targetPath,
                      Closure buildSelectorClosure)      // deprecated since 1.33
        copyArtifacts(String jobName, String includeGlob, String targetPath = '',
                      boolean flattenFiles,
                      Closure buildSelectorClosure)      // deprecated since 1.33
        copyArtifacts(String jobName, String includeGlob, String targetPath = '',
                      boolean flattenFiles, boolean optionalAllowed,
                      Closure buildSelectorClosure)      // deprecated since 1.33
    }
}
```

A build step to copy artifacts from another project. Requires version 1.26 or later of the
[Copy Artifact Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Copy+Artifact+Plugin).

```groovy
job('example') {
    steps {
        copyArtifacts('upstream') {
            includePatterns('*.xml', '*.properties')
            excludePatterns('test.xml', 'test.properties')
            targetDirectory('files')
            flatten()
            optional()
            buildSelector {
                latestSuccessful(true)
            }
        }
    }
}
```

### Groovy
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

### System Groovy Scripts
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

### vSphere Cloud

```groovy
job {
    steps {
        vSpherePowerOff(String server, String vm)
        vSpherePowerOn(String server, String vm)
        vSphereRevertToSnapshot(String server, String vm, String snapshot)
    }
}
```

These build steps manage virtual machines running in VMWare vSphere. Requires the
[vSphere Cloud Plugin](https://wiki.jenkins-ci.org/display/JENKINS/vSphere+Cloud+Plugin).

Example:

```groovy
// power off the VM 'foo' on server 'vsphere.acme.org', then revert to snapshot 'clean' and power on again
job('example') {
    steps {
        vSpherePowerOff('vsphere.acme.org', 'foo')
        vSphereRevertToSnapshot('vsphere.acme.org', 'foo', 'clean')
        vSpherePowerOn('vsphere.acme.org', 'foo')
    }
}
```

(Since 1.25)

### Grails
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

### Environment Variables
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

### HTTP Request

```groovy
job {
    steps {
        httpRequest(String url) {
            httpMode(String mode)
            authentication(String authentication)
            returnCodeBuildRelevant(boolean returnCodeBuildRelevant = true)
            logResponseBody(boolean logResponseBody = true)
        }
    }
}
```

Adds a step which performs a HTTP request. `httpMode` must be either `GET`, `POST`, `PUT` or `DELETE`. `authentication`
is configured in the global Jenkins settings. Requires the
[HTTP Request Plugin](https://wiki.jenkins-ci.org/display/JENKINS/HTTP+Request+Plugin).

```groovy
job('example') {
    steps {
        httpRequest('http://www.example.com') {
            httpMode('POST')
            authentication('Credentials')
            returnCodeBuildRelevant()
            logResponseBody()
        }
    }
}
```

(since 1.28)

# Multijob Phase

```
multiJob {
    steps {
        phase(String name, String continuationConditionArg = 'SUCCESSFUL', Closure phaseClosure = null) {
            phaseName(String phaseName)
            continuationCondition(String continuationCondition)
            job(String jobName, boolean currentJobParameters = true, boolean exposedScm = true, Closure phaseJobClosure = null) {
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
                disableJob(boolean exposedScm = true) // since 1.25
                killPhaseCondition(String killPhaseCondition) // since 1.25
                nodeLabel(String paramName, String nodeLabel) // since 1.26
                configure(Closure configClosure) // since 1.30
            }
        }
    }
}
```

Phases allow jobs to be group together to be run in parallel, they only exist in a Multijob typed job. The name and
continuationConditionArg can be set directly in the phase method or in the closure. The job method is used to list each
job in the phase, and hence can be called multiple times. Each call can be further configured with the parameters which
will be sent to it. The parameters are show above and documented in different parts of this page. See below for an
example of multiple phases strung together. Requires the
[Multijob Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Multijob+Plugin).

The `nodeLabel` parameter type requires the
[NodeLabel Parameter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/NodeLabel+Parameter+Plugin).

```
multiJob('example') {
    steps {
        phase() {
            phaseName 'Second'
            job('JobZ') {
                fileParam('my1.properties')
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
                nodeLabel('lParam', 'my_nodes')
                configure { phaseJobConfig ->
                  phaseJobConfig / enableCondition << 'true'
                  phaseJobConfig / condition << '${RUN_JOB} == "true"'
                }
            }
        }
   }
}
```

(since 1.16)

# [MatrixJob](https://wiki.jenkins-ci.org/display/JENKINS/Building+a+matrix+project)

The `axes`, `sequential`, `touchStoneFiler` and `combinationFilter` methods can only be used in jobs with type `Matrix`.
Any elements which can be added to a freestyle project can also be added to a MatrixJob and these will be run for each
of the matrix combinations.

See also [Building a matrix project](https://wiki.jenkins-ci.org/display/JENKINS/Building+a+matrix+project).

### Axes

```groovy
matrixJob {
    axes {
        text(String name, String... values)
        text(String name, Iterable<String> values)
        label(String name, String... labels)
        label(String name, Iterable<String> labels)
        labelExpression(String name, String... expressions)
        labelExpression(String name, Iterable<String> expressions)
        jdk(String... jdks)
        jdk(Iterable<String> jdks)
        configure(Closure configClosure)
    }
}
```

This block builds the separate axes and the individual methods (except for `jdk`) can be called multiple times with
separate label names.

The configure block can be used to add axes that are currently not supported by the Job DSL Plugin. The `axes` node is
passed into the configure block.

Example:

```groovy
matrixJob('example') {
    axes {
        label('label', 'linux', 'windows')
        jdk('jdk6', 'jdk7')
        configure { axes ->
            axes << 'org.acme.FooAxis'()
        }
    }
}
```

### Run Sequentially

```groovy
matrixJob {
    runSequentially(boolean runSequentially = true)
}
```

Run each matrix combination in sequence. If omitted, Jenkins will try to build the combinations in parallel if possible.

Example:

```groovy
matrixJob('example') {
    sequential()
}
```

### Touchstone Builds

```groovy
matrixJob(type) {
    touchStoneFilter(String expression, boolean continueOnFailure = false)
}
```

An expression of which combination to run first, the second parameter controls if a failure stops the other builds.

Example:

```groovy
matrixJob('example') {
    touchStoneFilter('label=="linux"')
}
```

### Combination Filter

```groovy
matrixJob(type) {
    combinationFilter(String expression)
}
```

An expression to limit which combinations can be run.

Example:

```groovy
matrixJob('example') {
    combinationFilter('jdk=="jdk-6" || label=="linux"')
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

# Debian Package Builder

```groovy
job {
    step {
        debianPackage(String path) {
            signPackage(boolean sign = true) // defaults to true
            generateChangelog(String nextVersion = null, boolean alwaysBuild = false)
        }
    }
}
```

Requires the [Debian Package Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Debian+Package+Builder+Plugin).

The `path` parameter refers to a path in the workspace where the 'debian' catalog is stored. The plugin will
automatically install packages required to build Debian packages.

```groovy
job {
    step {
        debianPackage('module') {
            generateChangelog()
        }
    }
}
```

(since 1.31)

# Parameterized Trigger

```groovy
job {
    steps {
        downstreamParameterized { // since 1.20
            trigger(String projects, String condition,
                    boolean triggerWithNoParameters,
                    Map<String, String> blockingThresholds) {
                currentBuild()
                propertiesFile(String file, boolean failOnMissing = false)
                gitRevision(boolean combineQueuedCommits = false)
                predefinedProp(String key, String value)
                predefinedProps(Map<String, String> predefinedPropsMap)
                predefinedProps(String predefinedProps) // newline separated
                matrixSubset(String groovyFilter)
                subversionRevision(boolean includeUpstreamParameters = false)
                sameNode()
                nodeLabel(String paramName, String nodeLabel) // since 1.26
            }
            trigger(String projects, Closure downstreamTriggerClosure = null)
            trigger(String projects, String condition,
                    Closure downstreamTriggerClosure = null)
            trigger(String projects, String condition,
                    boolean triggerWithNoParameters,
                    Closure downstreamTriggerClosure = null)
        }
    }
    publishers {
        downstreamParameterized(Closure downstreamClosure)
    }
}
```

Allows to trigger new parameterized builds. Requires the
[Parameterized Trigger Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Trigger+Plugin).

Multiple triggers can be specified by calling `trigger` multiple times.

The `projects` argument is a comma separated list of downstream projects.

The `condition` argument must be one of these values: `'SUCCESS'` (default), `'UNSTABLE'`, `'UNSTABLE_OR_BETTER'`,
`'UNSTABLE_OR_WORSE'`, `'FAILED'` or `'ALWAYS'`. The argument is ignored when configuring a build step, but should be
set to `'ALWAYS'`.

The `predefinedProp` and `predefinedProps` methods are used to accumulate properties, meaning that they can be called
multiple times to build a superset of properties.

The `blockingThresholds` argument can only be used when configuring a build step. Valid keys for the map are
`buildStepFailure`, `failure` and `unstable`. The values can be set to either `'SUCCESS'`, `'UNSTABLE'`  or `'FAILURE'`.

The `nodeLabel` parameter type requires the
[NodeLabel Parameter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/NodeLabel+Parameter+Plugin).

```groovy
job('example-1') {
    steps {
        downstreamParameterized {
            trigger('Project1, Project2', 'ALWAYS', true,
                    [buildStepFailure: 'FAILURE',
                     failure         : 'FAILURE',
                     unstable        : 'UNSTABLE']) {
                predefinedProp('key1', 'value1')
                predefinedProps([key2: 'value2', key3: 'value3'])
                predefinedProps('key4=value4\nkey5=value5')
            }
            trigger('Project2') {
                currentBuild()
            }
        }
    }
}

job('example-2') {
    publishers {
        downstreamParameterized {
            trigger('Project1, Project2', 'UNSTABLE_OR_BETTER') {
                currentBuild()
            }
        }
    }
}
```

### Conditional Build Steps

```groovy
job {
    steps {
        conditionalSteps {
            condition {                      // only one condition is allowed
                alwaysRun()                  // run no matter what
                neverRun()                   // never run
                booleanCondition(String token)
                stringsMatch(String arg1, String arg2, boolean ignoreCase)
                cause(String buildCause, boolean exclusiveCondition)
                expression(String expression, String label)
                time(int earliestHour, int earliestMinute, int latestHour,
                     int latestMinute, boolean useBuildTime)
                status(String worstResult, String bestResult)
                shell(String command)                           // since 1.23
                batch(String command)                           // since 1.23
                fileExists(String file, BaseDir baseDir)        // since 1.23
                not(Closure condition)                          // since 1.23
                and(Closure... conditions)                      // since 1.23
                or(Closure... conditions)                       // since 1.23
            }
            runner(String runner)
            steps(Closure stepClosure) // one or more build steps, since 1.35
            // using build steps directly is deprecated since 1.35
        }
    }
}
```

Wraps any number of other build steps, controlling their execution based on a defined condition. Requires the
[Conditional BuildStep Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Conditional+BuildStep+Plugin). See the
[Run Condition Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Run+Condition+Plugin) for details on the run
conditions - note that not all run conditions supported by the Run Condition Plugin are supported here yet.

The values for `worstResult` and `bestResult` can be any of the following strings: `'SUCCESS'`, `'UNSTABLE'`,
`'FAILURE'`, `'NOT_BUILT'`, or `'ABORTED'`. The runner can be any one of `'Fail'`, `'Unstable'`, `'RunUnstable'`,
`'Run'`, `'DontRun'`. Valid values for `baseDir` are `BaseDir.JENKINS_HOME`, `BaseDir.ARTIFACTS_DIR` and
`BaseDir.WORKSPACE`.

```groovy
job('example-1') {
    steps {
        conditionalSteps {
            condition {
                stringsMatch('${SOME_PARAMETER}', 'pants', false)
            }
            runner('Fail')
            steps {
                shell("echo 'just one step'")
            }
        }
    }
}

job('example-2') {
    steps {
        conditionalSteps {
            condition {
                time(9, 0, 13, 0, false)
            }
            runner('Unstable')
            steps {
                shell("echo 'a first step'")
                ant('build') {
                    target('test')
                }
            }
        }
    }
}

job('example-3') {
    steps {
        conditionalSteps {
            condition {
                and {
                    status('ABORTED', 'FAILURE')
                } {
                    not {
                       fileExists('script.sh', BaseDir.WORKSPACE)
                    }
                }
            }
            runner('Unstable')
            steps {
                shell("echo 'a first step'")
                ant('build') {
                    target('test')
                }
            }
        }
    }
}
```

(since 1.20)

### Repository Connector

```groovy
job {
    steps {
        resolveArtifacts {
            targetDirectory(String targetDirectory)
            failOnError(boolean failOnError = true)
            enableRepoLogging(boolean enableRepoLogging = true)
            snapshotUpdatePolicy(String updatePolicy)
            releaseUpdatePolicy(String updatePolicy)
            artifact {
                groupId(String groupId)
                artifactId(String artifactId)
                classifier(String classifier)
                version(String version)
                extension(String extension)
                targetFileName(String targetFileName)
            }
        }
    }
}
```

Resolves artifacts from a Maven repository. Requires the
[Repository Connector Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Repository+Connector+Plugin).

Valid arguments for `snapshotUpdatePolicy` and `releaseUpdatePolicy` are either `daily`, `never` or `always`.

```groovy
job('example') {
    steps {
        resolveArtifacts {
            failOnError()
            snapshotUpdatePolicy 'always'
            targetDirectory 'lib'
            artifact {
                groupId 'org.slf4j'
                artifactId 'slf4j-api'
                version '[1.7.5,1.7.6]'
            }
            artifact {
                groupId 'ch.qos.logback'
                artifactId 'logback-classic'
                version '1.1.1'
                classifier 'sources'
            }
        }
    }
}
```

(since 1.29)

### Parameterized Remote Trigger

````groovy
job {
    steps {
        remoteTrigger(String remoteJenkinsName, String jobName) {
            parameter(String name, String value)
            parameters(Map<String, String> parameters)
            shouldNotFailBuild(boolean shouldNotFailBuild = true)           // since 1.29
            pollInterval(int pollInterval)                                  // since 1.29
            preventRemoteBuildQueue(boolean preventRemoteBuildQueue = true) // since 1.29
            blockBuildUntilComplete(boolean blockBuildUntilComplete = true) // since 1.29
        }
    }
}
```

Triggers a job on another Jenkins instance. Requires the
[Parameterized Remote Trigger Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Remote+Trigger+Plugin).

Examples:

```groovy
// start the job 'test-flow' on the Jenkins instance named 'test-ci' without parameters
job('example-1') {
    steps {
        remoteTrigger('test-ci', 'test-flow')
    }
}

// start the job 'test-flow' on the Jenkins instance named 'test-ci' with three parameters,
// blocking until the build completes.
job('example-2') {
    steps {
        remoteTrigger('test-ci', 'test-flow') {
            parameter('VERSION', '$PIPELINE_VERSION')
            parameters(BRANCH: 'feature-A', STAGING_REPO_ID: '41234232')
            blockBuildUntilComplete()
        }
    }
}
```

(since 1.22)

# Critical Block Start/End

```groovy
job {
    steps {
        criticalBlock(Closure stepClosure)
    }
}
```

See [Exclusion Resources](#exclusion-resources).

(since 1.24)

# Publishers

Block to contain list of publishers.

### Extended Email Plugin

```groovy
job {
    publishers {
        extendedEmail(String recipients = null, String subjectTemplate = null,
                      String contentTemplate = null) {
            trigger(String triggerName, String subject = null, String body = null,
                    String recipientList = null, Boolean sendToDevelopers = null,
                    Boolean sendToRequester = null, includeCulprits = null,
                    Boolean sendToRecipientList = null)
            trigger(Map args)
            configure(Closure configureClosure)
        }
    }
}
```

Supports the [Email-ext Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Email-ext+plugin). The closure is primarily
used to specify the triggers, which is optional.

The first trigger method allow complete control of the email going out, and maps directly to what is seen in the
config.xml of a job. The second form of trigger, uses the names from the first, but can be called with a Map syntax, so
that values can be left out more easily.

The `triggerName` needs to be one of these values: `PreBuild`, `StillUnstable`, `Fixed`, `Success`, `StillFailing`,
`Improvement`, `Failure`, `Regression`, `Aborted`, `NotBuilt`, `FirstFailure`, `Unstable`, `Always`, `SecondFailure`,
`FirstUnstable`, `FixedUnhealthy` or `StatusChanged`. Older versions of the Email-ext plugin do not support all
triggers. The DSL supports the `Always`, `SecondFailure`, `FirstUnstable`, `FixedUnhealthy` and `StatusChanged` triggers
since version 1.28.

A `hudson.plugins.emailext.ExtendedEmailPublisher` node is handed into the configure block.

```groovy
job('example') {
    publishers {
        extendedEmail('me@halfempty.org', 'Oops', 'Something broken') {
            trigger('PreBuild')
            trigger(triggerName: 'StillUnstable', subject: 'Subject', body:'Body', recipientList: 'RecipientList',
                    sendToDevelopers: true, sendToRequester: true, includeCulprits: true, sendToRecipientList: false)
            configure { node ->
                node / contentType << 'html'
            }
        }
    }
}
```

### Mailer Tasks
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

### Archive Artifacts

```groovy
job {
    publishers {
        archiveArtifacts(String glob, String excludeGlob = null)
        archiveArtifacts { // since 1.20
            pattern(String pattern) // can be called multiple since 1.27
            exclude(String excludePattern)
            allowEmpty(boolean allowEmpty = true) // defaults to false

            // since 1.33, defaults to true, requires Jenkins 1.575
            defaultExcludes(boolean defaultExcludes = true)

            // since 1.33, defaults to false, requires Jenkins 1.571
            fingerprint(boolean fingerprint = true)

            // since 1.33, defaults to false, requires Jenkins 1.567
            onlyIfSuccessful(boolean onlyIfSuccessful = true)

            // deprecated since 1.33, defaults to false
            latestOnly(boolean latestOnly = true)
        }

        // deprecated since 1.33
        archiveArtifacts(String glob, String excludeGlob, boolean latestOnly)
    }
}
```

Supports archiving artifacts with each build.

Examples:

```groovy
job('example-1') {
    publishers {
        archiveArtifacts('build/test-output/**/*.html')
    }
}

job('example-2') {
    publishers {
        archiveArtifacts {
            pattern('build/test-output/**/*.html')
            pattern('build/test-output/**/*.xml')
            onlyIfSuccessful()
        }
    }
}
```

### Fingerprint / KeepDependencies
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

### Build Description Setter

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

### Archive JUnit

```groovy
job {
    publishers {
        archiveJunit(String glob) { // since 1.26
            retainLongStdout(boolean retain = true) // options, defaults to false
            testDataPublishers {
                allowClaimingOfFailedTests()
                publishTestAttachments()
                publishTestStabilityData()
                publishFlakyTestsReport() // since 1.30
            }
        }
    }
}
```

Supports archiving JUnit results for each build. The
[Claim Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Claim+plugin) is required for `allowClaimingOfFailedTests`.
The [JUnit Attachments Plugin](https://wiki.jenkins-ci.org/display/JENKINS/JUnit+Attachments+Plugin) is required for
`publishTestAttachments`. The [Test Stability Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Test+stability+plugin)
is required for `publishTestStabilityData`. The
[Flaky Test Handler Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Flaky+Test+Handler+Plugin) is required for
`publishFlakyTestsReport`.

```groovy
job('example') {
    publishers {
        archiveJunit('**/target/surefire-reports/*.xml')
        archiveJunit('**/minitest-reports/*.xml') {
            retainLongStdout()
            testDataPublishers {
                publishTestStabilityData()
            }
        }
    }
}
```

### HipChat Publisher

```groovy
job {
    publishers {
        hipChat {
            rooms(String... rooms)      // uses global settings if omitted
            token(String token)         // uses global settings if omitted
            notifyBuildStart(boolean notify = true)   // defaults to false
            notifySuccess(boolean notify = true)      // defaults to false
            notifyAborted(boolean notify = true)      // defaults to false
            notifyNotBuilt(boolean notify = true)     // defaults to false
            notifyUnstable(boolean notify = true)     // defaults to false
            notifyFailure(boolean notify = true)      // defaults to false
            notifyBackToNormal(boolean notify = true) // defaults to false
            startJobMessage(String message)     // uses default if omitted
            completeJobMessage(String message)  // uses default if omitted
        }
    }
}
```

Allows notifications to be set to HipChat. Requires the
[HipChat Plugin](https://wiki.jenkins-ci.org/display/JENKINS/HipChat+Plugin).

```groovy
job('example') {
    publishers {
        hipChat {
            rooms('Dev Team A', 'QA')
            notifyAborted()
            notifyNotBuilt()
            notifyUnstable()
            notifyFailure()
            notifyBackToNormal()
        }
    }
}
```

(since 1.33)

### HTML Publisher

```groovy
job {
    publishers {
        publishHtml {
            report(String reportDir) {                          // since 1.28
                reportName(String reportName)
                reportFiles(String reportFiles)   // defaults to 'index.html'
                allowMissing(boolean allow = true)       // defaults to false
                keepAll(boolean keepAll = true)          // defaults to false
                alwaysLinkToLastBuild(boolean value = true)     // since 1.35
            }
        }
    }
}
```

Allows HTML reports to be archived. The report method can be called multiple times to add more reports. Requires the
[HTML Publisher Plugin](https://wiki.jenkins-ci.org/display/JENKINS/HTML+Publisher+Plugin).

```groovy
job('example') {
    publishers {
        publishHtml {
            report('build/test-output') {
                reportName('Test Output')
            }
            report('test') {
                reportName('Gradle Tests')
                keepAll()
                allowMissing()
                alwaysLinkToLastBuild()
            }
        }
    }
}
```

### Jabber Publisher

```groovy
job {
    publishers {
        publishJabber(String target, String strategyName, String channelNotificationName,
                      Closure jabberClosure = null)        // deprecated since 1.30
        publishJabber(String target, String strategyName,
                      Closure jabberClosure = null)        // deprecated since 1.30
        publishJabber(String target) {
            strategyName(String strategy)                  // defaults to 'ALL'
            notifyOnBuildStart(boolean value = true)       // defaults to false
            notifySuspects(boolean value = true)           // defaults to false
            notifyCulprits(boolean value = true)           // defaults to false
            notifyFixers(boolean value = true)             // defaults to false
            notifyUpstreamCommitters(boolean value = true) // defaults to false
            channelNotificationName(String name)           // defaults to 'Default'
        }
    }
}
```

Enables Jenkins to send build notifications via Jabber. Requires the
[Jabber Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Jabber+Plugin).

Valid values for `strategyName` are `ALL`, `FAILURE_AND_FIXED`, `ANY_FAILURE` and `STATECHANGE_ONLY`. Valid values for
`channelNotificationName` are `Default`, `SummaryOnly`, `BuildParameters` and `PrintFailingTests`.

```groovy
job('example') {
    publishers {
        publishJabber('*room@example.org') {
            strategyName('STATECHANGE_ONLY')
            notifySuspects()
            channelNotificationName('BuildParameters')
        }
    }
}
```

### SCP Publisher

```groovy
job {
    publishers {
        publishScp(String site) {
            entry(String source, String destination = '', boolean keepHierarchy = false)
            entries(Iterable<String> sources, String destination = '', boolean keepHierarchy = false) // since 1.27
        }
    }
}
```

The `site` is specified in the global Jenkins configuration. Each entry is individually specified in the closure block,
e.g. entry can be called multiple times. Requires the
[SCP Plugin](https://wiki.jenkins-ci.org/display/JENKINS/SCP+plugin).

```groovy
job('example') {
    publishers {
        publishScp('docs.acme.org') {
            entry('build/docs/**', 'project-a', true)
        }
    }
}
```

### Build Publisher

```groovy
job {
    publishers {
        publishBuild {
            publishUnstable(boolean publishUnstable = true)
            publishFailed(boolean publishFailed = true)
            discardOldBuilds(int daysToKeep = -1, int numToKeep = -1,
                             int artifactDaysToKeep = -1,
                             int artifactNumToKeep = -1)
        }
    }
}
```

Allows to publish records from one Jenkins to another. Requires the
[Build Publisher Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Publisher+Plugin).

The `publishUnstable` and `publishFailed` options are enabled by default.

```groovy
job('example') {
    publishers {
        publishBuild {
            discardOldBuilds(7, 10)
        }
    }
}
```

(since 1.33)

### CloneWorkspace Publisher
```groovy
publishCloneWorkspace(String workspaceGlob, String workspaceExcludeGlob = '', String criteria = 'Any', String archiveMethod = 'TAR', boolean overrideDefaultExcludes = false, Closure cloneWorkspaceClosure = null) {}
```

Supports the <a href="https://wiki.jenkins-ci.org/display/JENKINS/Clone+Workspace+SCM+Plugin">Clone Workspace SCM Plugin</a>.

Due to the simplicity of this publisher, the closure support is purely provided for creating very specific configs.  Usually the non-closure variants will suffice - the simplest purely requiring the workspaceGlob alone, and the other (equivalent to pressing the "Advanced" button in the Jenkins UI) provided all settings.

### Downstream
```groovy
downstream(String projectName, String thresholdName = 'SUCCESS')
```

Specifies a downstream job. The second arg, thresholdName, can be one of three values: 'SUCCESS', 'UNSTABLE' or 'FAILURE'.

### Violations Plugin
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

### Chuck Norris

```groovy
chucknorris()
```

Enables the Cordell Walker plugin.

### IRC

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

### Join Trigger

```groovy
job {
    publishers {
        joinTrigger {
            projects(String... projects)
            publishers(Closure publisherClosure)
            evenIfDownstreamUnstable(boolean evenIfDownstreamUnstable = true)
        }
    }
}
```

Allows a job to be run after all the immediate downstream jobs have completed. Requires the
[Join Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Join+Plugin).

Currently only the `downstreamParameterized` publisher is supported by the Join Plugin.

```groovy
job('example-1') {
    publishers {
        joinTrigger {
            projects('upload-to-staging')
        }
    }
}

job('example-2') {
    publishers {
        joinTrigger {
            publishers {
                downstreamParameterized {
                    trigger('upload-to-staging') {
                        currentBuild()
                    }
                }
            }
        }
    }
}
```

(since 1.35)

### Cobertura coverage report

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

#### More about targets

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

### Allow Broken Build Claiming

```groovy
allowBrokenBuildClaiming()
```

Activates broken build claiming for the [Claim plugin](https://wiki.jenkins-ci.org/display/JENKINS/Claim+plugin).

(Since 1.17)

### Jacoco

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

### [Static Code Analysis Plugins](https://wiki.jenkins-ci.org/display/JENKINS/Static+Code+Analysis+Plug-ins)

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

#### [Findbugs](https://wiki.jenkins-ci.org/display/JENKINS/FindBugs+Plugin)
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

#### Plot Build Data

```groovy
job {
    publishers {
        plotBuildData {
            plot(String group, String dataStore) {
                title(String title)
                numberOfBuilds(int numberOfBuilds)
                yAxis(String yAxis)
                style(String style)                             // defaults to 'line'
                useDescriptions(boolean useDescriptions = true) // defaults to false
                excludeZero(boolean excludeZero = true)         // defaults to false
                keepRecords(boolean keepRecords = true)         // defaults to false
                logarithmic(boolean logarithmic = true)         // defaults to false
                propertiesFile(String fileName) {
                    label(String label)
                }
                csvFile(String fileName) {
                    includeColumns(String... columnNames)
                    excludeColumns(String... columnNames)
                    includeColumns(int... columnIndexes)
                    excludeColumns(int... columnIndexes)
                    url(String url)
                    showTable(boolean showTable = true)         // defaults to false
                }
                xmlFile(String fileName) {
                    nodeType(String nodeType)                   // defaults to 'NODESET'
                    url(String url)
                    xpath(String xpath)
                }
            }
        }
    }
}
```

Show a number of plots, each containing a single data series. Requires the
[Plot Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Plot+Plugin).

Plot plugin relies on a data store to hold the plot data, this is normally stored in a randomly named CSV file within
the workspace root. To avoid conflicts this location needs to be set manually relative to the workspace using the
`dataStore` parameter.

The `style` option can be one of `'area'`, `'bar'`, `'bar3d'`, `'line'` (default), `'line3d'`, `'stackedArea'`,
`'stackedbar'`, `'stackedbar3d'` or `'waterfall'`.

The `nodeType` option can be one of `'NODESET'`, `'NODE'`, `'STRING'`, `'BOOLEAN'`, `'NUMBER'`.

When using `csvFile`, it is not possible to mix `includeColumn` and `excludeColumn` or use both `String` and `int`
arguments.

```groovy
job('example-1') {
    publishers {
        plotBuildData {
            plot('Important Plot', 'my_data_store.csv') {
                propertiesFile('my_data.properties')
            }
        }
    }
}

job('example-2') {
    publishers {
        plotBuildData {
            plot('Bar Charts', 'bar_chart_data_store.csv') {
                style('bar')
                propertiesFile('my_data.properties') {
                    label('My Label')
                }
            }
        }
    }
}

job('example-3') {
    publishers {
        plotBuildData {
            plot('Exciting plots', 'excitment.csv') {
                title('X vs Y')
                yAxis('Y')
                numberOfBuilds(42)
                useDescriptions()
                keepRecords()
                excludeZero()
                logarithmic()
                propertiesFile('my_data.properties') {
                    label('Builds')
                }
            }
        }
    }
}

job('example-4') {
    publishers {
        plotBuildData {
            plot('Other charts', '123012992213.csv') {
                style('line3d')
                csvFile('my_data.properties') {
                    includeColumns(1, 8, 14)
                    showTable()
                }
            }
        }
    }
}
```

(since 1.31)

#### [Pmd](https://wiki.jenkins-ci.org/display/JENKINS/PMD+Plugin)
```groovy
publishers {
  pmd('**/pmd.xml') {
    shouldDetectModules true
  }
}
```

#### [Checkstyle](https://wiki.jenkins-ci.org/display/JENKINS/Checkstyle+Plugin)
```groovy
publishers {
  checkstyle('**/checkstyle-result.xml') {
    shouldDetectModules true
  }
}
```

#### [JsHint](https://wiki.jenkins-ci.org/display/JENKINS/JSHint+Checkstyle+Plugin)
```groovy
publishers {
  jshint('**/jshint-result.xml') {
    shouldDetectModules true
  }
}
```

#### [DRY](https://wiki.jenkins-ci.org/display/JENKINS/DRY+Plugin)
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

#### [Task Scanner](https://wiki.jenkins-ci.org/display/JENKINS/Task+Scanner+Plugin)
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

#### [CCM](https://wiki.jenkins-ci.org/display/JENKINS/CCM+Plugin)
```groovy
publishers {
  ccm('**/ccm.xml')
}
```

#### [Android Lint](https://wiki.jenkins-ci.org/display/JENKINS/Android+Lint+Plugin)
```groovy
publishers {
  androidLint('**/lint-results.xml') {
    shouldDetectModules true
  }
}
```

#### [OWASP Dependency Check](https://wiki.jenkins-ci.org/display/JENKINS/OWASP+Dependency-Check+Plugin)
```groovy
publishers {
  dependencyCheck('**/DependencyCheck-Report.xml') {
    shouldDetectModules true
  }
}
```

#### [Compiler Warnings](https://wiki.jenkins-ci.org/display/JENKINS/Warnings+Plugin)
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

Requires version 4.0 or later of the [Warnings Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Warnings+Plugin).

#### [Analysis Collector](https://wiki.jenkins-ci.org/display/JENKINS/Analysis+Collector+Plugin)

The analysisCollectorClosure takes, additional to all the options from the staticAnalysisClosure, the following options:
* checkstyle,  defaults to false
* dry, defaults to false
* findbugs, defaults to false
* pmd, defaults to false
* tasks, defaults to false
* warnings, defaults to false

```groovy
job('example') {
    publishers {
        analysisCollector {
            checkstyle()
            dry()
            findbugs()
            pmd()
            tasks()
            warnings()
            thresholds(
                unstableTotal: [all: 1, high: 2, normal: 3, low: 4]
            )
        }
    }
}
```

(since 1.26)

#### Deploy Maven Artifacts

```groovy
job {
    publishers {
        deployArtifacts {
            uniqueVersion(boolean uniqueVersion = true)   // defaults to true
            evenIfUnstable(boolean evenIfUnstable = true) // defaults to false
        }
    }
}
```

Deploy artifacts to a Maven repository. Only available for Maven jobs. Requires the
[Maven Project Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Maven+Project+Plugin).

```groovy
job {
    publishers {
        deployArtifacts()
    }
}

job {
    publishers {
        deployArtifacts {
            evenIfUnstable()
        }
    }
}
```

(since 1.31)

### Text Finder

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

### Post Build Scripts

```groovy
job {
    publishers {
        postBuildScripts {
            steps(Closure stepClosure)
            onlyIfBuildSucceeds(boolean onlyIfBuildSucceeds = true) // defaults to true
        }
    }
}
```

Execute a set of scripts at the end of the build. Requires the
[PostBuildScript Plugin](https://wiki.jenkins-ci.org/display/JENKINS/PostBuildScript+Plugin).

```groovy
job {
    publishers {
        postBuildScripts {
            steps {
                shell('echo Hello World')
            }
            onlyIfBuildSucceeds(false)
        }
    }
}
```

(since 1.31)

### [Post Build Task](https://wiki.jenkins-ci.org/display/JENKINS/Post+build+task)

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

(since 1.19)

### Aggregate Build Flow Test Results

```groovy
buildFlowJob {
    publishers {
        aggregateBuildFlowTests()
    }
}
```

Aggregates test results from builds started dynamically by build flow jobs. Requires the
[Build Flow Test Aggregator Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Flow+Test+Aggregator+Plugin).

```groovy
buildFlowJob('example') {
    publishers {
        aggregateBuildFlowTests()
    }
}
```

(since 1.35)

### Aggregate Downstream Test Results

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

### [Groovy Postbuild](https://wiki.jenkins-ci.org/display/JENKINS/Groovy+Postbuild+Plugin)

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

### Archive Javadoc

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

### Emma Code Coverage

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

#### More about targets

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

### Associated Files

Supports the [Associated Files Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Associated+Files+Plugin).

```groovy
associatedFiles(String associatedFilesPattern)
```

(since 1.20)

### Robot Framework Reports

```groovy
job {
    publishers {
        publishRobotFrameworkReports {
            passThreshold(double threshold)
            unstableThreshold(double threshold)
            onlyCritical(boolean value = true)
            outputPath(String path)
            reportFileName(String fileName)
            logFileName(String fileName)
            outputFileName(String fileName)
            disableArchiveOutput(boolean value = true) // since 1.33
            otherFiles(String... files)                // since 1.33
        }
    }
}
```

Collects and publishes Robot Framework test results. Requires the
[Robot Framework Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Robot+Framework+Plugin).

The default values for all options are shown in the example below.

```groovy
job('example') {
    publishers {
        publishRobotFrameworkReports {
            passThreshold(100.0)
            unstableThreshold(0.0)
            onlyCritical(false)
            outputPath('target/robotframework-reports')
            reportFileName('report.html')
            logFileName('log.html')
            outputFileName('output.xml')
            disableArchiveOutput(false)
            otherFiles()
        }
    }
}
```

(since 1.21)

### Build Pipeline Trigger

```groovy
job {
    publishers {
        buildPipelineTrigger(String downstreamProjectNames) {
            parameters { // since 1.23
                currentBuild()
                propertiesFile(String propFile)
                gitRevision(boolean combineQueuedCommits = false)
                predefinedProp(String key, String value)
                predefinedProps(Map<String, String> predefinedPropsMap)
                predefinedProps(String predefinedProps)
                matrixSubset(String groovyFilter)
                subversionRevision()
                nodeLabel(String paramName, String nodeLabel) // since 1.26
            }
        }
    }
}
```

Add a manual triggers for jobs that require intervention prior to execution, e.g. an approval process outside of
Jenkins. The argument takes a comma separated list of job names. Requires the
[Build Pipeline Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Pipeline+Plugin).

The `parameters` closure and the methods inside it are optional, though it makes the most sense to call at least one.
Each one is relatively self documenting, mapping directly to what is seen in the UI. The `predefinedProp` and
`predefinedProps` methods are used to accumulate properties, meaning that they can be called multiple times to build a
superset of properties. They are basically equivalent to the ones defined for `downstreamParameterized`.

The `nodeLabel` parameter type requires the
[NodeLabel Parameter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/NodeLabel+Parameter+Plugin).

Examples:

```groovy
job('example-1') {
    publishers {
        buildPipelineTrigger('deploy-cluster-1, deploy-cluster-2')
    }
}
```

```groovy
job('example-2') {
    publishers {
        buildPipelineTrigger('deploy-cluster-1, deploy-cluster-2') {
            parameters {
                predefinedProp('GIT_COMMIT', '$GIT_COMMIT')
                predefinedProp('ARTIFACT_BUILD_NUMBER', '$BUILD_NUMBER')
            }
        }
    }
}
```

(since 1.21)

### Github Commit Notifier

This publisher sets the build status on a Github commit, using the [Github Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Github+Plugin).

```
githubCommitNotifier()
```

(Since 1.21)

### Git Publisher

```groovy
job {
    publishers {
        git {
            pushOnlyIfSuccess(boolean pushOnlyIfSuccess = true)
            pushMerge(boolean pushMerge = true)
            forcePush(boolean forcePush = true) // since 1.27
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
The `forcePush` option requires version 2.2.6 or later of the Git Plugin.

Examples:

```groovy
// push a to branch if the job succeeded
job('example-1') {
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
job('example-2') {
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

### Flowdock Publisher

```groovy
job {
    publishers {
        flowdock('a-long-token') {
            unstable(boolean unstable = true)
            success(boolean success = true)
            aborted(boolean aborted = true)
            failure(boolean failure = true)
            fixed(boolean fixed = true)
            notBuilt(boolean notBuilt = true)
            chat(boolean chat = true)
            tag(String tagName)
            tags(String[] tags)
        }
    }
}
```

Sends build notification from Jenkins to your flow. Requires the [Flowdock Plugin](https://github.com/jenkinsci/flowdock-plugin).
Omitting an argument to any of the methods taking a boolean will behave as if you passed in true. Not calling the method will default to the plugin's default values (which are true for success, failure and fixed; false for all others).
Tags are appended to form a single list, so that multiple calls to tag will behave as if youc alled tags variant with the concatenated list of Strings.

Examples:

```groovy
// Minimal example. Notify using all the plugin defaults (inbox, not chat; notify on success, failure, fixed; no tags)
job('example-1') {
    publishers {
        flowdock('a-flow-token')
    }
}
```

```groovy
// Notify on all build statuses
job('example-2') {
    publishers {
        flowdock('flow-token') {
            unstable()
            success()
            aborted()
            failure()
            fixed()
            notBuilt()
        }
    }
}
```

```groovy
// Notify on multiple flows in their chat for the default build statuses (success, failure and fixed) using the tags 'jenkins' and 'build'
job('example-3') {
    publishers {
        flowdock('first-flow-token', 'second-flow-token') {
            chat()
            tags('jenkins', 'build')
        }
    }
}
```

(Since 1.23)

### Sonar

```groovy
job {
    publishers {
        sonar {
            branch(String branch)
            overrideTriggers {
                skipIfEnvironmentVariable(String environmentVariable)
            }
        }
    }
}
```

Allows to trigger SonarQube analysis. Requires the
[Sonar Plugin](http://docs.sonarqube.org/display/SONAR/Jenkins+Plugin).

```groovy
// run Sonar analysis for feature-xy branch,
// but skip if SKIP_SONAR environment variable is set to true
job {
    publishers {
        sonar {
            branch('feature-xy')
            overrideTriggers {
                skipIfEnvironmentVariable('SKIP_SONAR')
            }
        }
    }
}
```

(since 1.31)

### StashNotifier Publisher

```groovy
job {
    publishers {
        stashNotifier {
            commitSha1(String commitSha1) // optional
            keepRepeatedBuilds(boolean keepRepeatedBuilds = true) // optional, defaults to false if omitted
        }
    }
}
```

Supports the [Stash Notifier Plugin](https://wiki.jenkins-ci.org/display/JENKINS/StashNotifier+Plugin).
Uses global Jenkins settings for Stash URL, username, password and unverified SSL certificate handling.
All parameters are optional. If a method is not called then the plugin default parameter will be used.

Examples:

```groovy
//The following example will notify Stash using the global Jenkins settings
job('example-1') {
    publishers {
        stashNotifier()
    }
}
```

```groovy
// The following example will notify Stash using the global Jenkins settings and sets keepRepeatedBuilds to true
job('example-2') {
    publishers {
        stashNotifier {
            keepRepeatedBuilds()
        }
    }
}
```

(Since 1.23)

### Maven Deployment Linker Publisher

```groovy
job {
    publishers {
        mavenDeploymentLinker(String regex)
    }
}
```

Supports the [Maven Deployment Linker Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Maven+Deployment+Linker).

The following example will create links to all tar.gz build artifacts.

```groovy
job('example') {
    publishers {
        mavenDeploymentLinker('.*.tar.gz')
    }
}
```

(Since 1.23)

### Workspace Cleanup Publisher

Supports the [Workspace Cleanup Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Workspace+Cleanup+Plugin).
All parameters are optional.

```groovy
job {
    publishers {
        wsCleanup {
            includePattern(String pattern) // all files are deleted if omitted
            excludePattern(String pattern)
            deleteDirectories(boolean value = true) // defaults to false if omitted
            cleanWhenSuccess(boolean value = true)  // defaults to true if omitted
            cleanWhenUnstable(boolean value = true) // defaults to true if omitted
            cleanWhenFailure(boolean value = true) // defaults to true if omitted
            cleanWhenNotBuilt(boolean value = true) // defaults to true if omitted
            cleanWhenAborted(boolean value = true) // defaults to true if omitted
            failBuildWhenCleanupFails(boolean value = true) // defaults to true if omitted
            deleteCommand(String command)
        }
    }
}
```

The following example will delete all files after a build.

```groovy
job('example-1') {
    publishers {
        wsCleanup()
    }
}
```

The following example will delete all 'src' directories in the directory tree

```groovy
job('example-2') {
    publishers {
        wsCleanup {
            includePattern('**/src/**')
            deleteDirectories(true)
        }
    }
}
```

### Rundeck Notifier

```groovy
job {
    publishers {
        rundeck(String jobId) {
            options(Map<String, String> option)           // defaults to empty map if omitted
            option(String key, String value)
            tag(String tag)                               // defaults to empty string if omitted
            nodeFilters(Map<String, String> filters)      // defaults to empty map if omitted
            nodeFilter(String key, String value)
            shouldWaitForRundeckJob(boolean value = true) // defaults to false if omitted
            shouldFailTheBuild(boolean value = true)      // defaults to false if omitted
        }
    }
}
```

Configure a Jenkins job to trigger a Rundeck job as a post-build action. Requires the
[RunDeck Plugin](https://wiki.jenkins-ci.org/display/JENKINS/RunDeck+Plugin).

Examples:

```groovy
job('example-1') {
    publishers {
        rundeck('13eba461-179d-40a1-8a08-bafee33fdc12') {
    }
}
```

```groovy
job('example-2') {
    publishers {
        rundeck('13eba461-179d-40a1-8a08-bafee33fdc12') {
            options(artifact: 'app', env: 'dev')
            option('version', '1.1')
            tag('deploy app to dev')
            nodeFilters(hostname: 'dev(\\d+).company.net')
            nodeFilter('tags', 'www+dev')
            shouldWaitForRundeckJob()
            shouldFailTheBuild()
        }
    }
}
```

(since 1.24)

### xUnit

```groovy
job {
    publishers {
        archiveXUnit {
            aUnit {
                pattern(String pattern)                     // empty by default
                skipNoTestFiles(boolean value = true)       // defaults to false
                failIfNotNew(boolean value = true)          // defaults to true
                deleteOutputFiles(boolean value = true)     // defaults to true
                stopProcessingIfError(boolean value = true) // defaults to true
            }
            boostTest { ... }                               // see aUnit closure above
            cTest { ... }                                   // see aUnit closure above
            check { ... }                                   // see aUnit closure above
            cppTest { ... }                                 // see aUnit closure above
            cppUnit { ... }                                 // see aUnit closure above
            embUnit { ... }                                 // see aUnit closure above
            fpcUnit { ... }                                 // see aUnit closure above
            googleTest { ... }                              // see aUnit closure above
            jUnit { ... }                                   // see aUnit closure above
            msTest { ... }                                  // see aUnit closure above
            mbUnit { ... }                                  // see aUnit closure above
            nUnit { ... }                                   // see aUnit closure above
            phpUnit { ... }                                 // see aUnit closure above
            qTestLib { ... }                                // see aUnit closure above
            valgrind { ... }                                // see aUnit closure above
            customTool {
                // all options from the aUnit closure above, plus
                stylesheet(String url)                      // empty by default
            }
            failedThresholds {
                unstable(int threshold)                     // defaults to 0
                unstableNew(int threshold)                  // defaults to 0
                failure(int threshold)                      // defaults to 0
                failureNew(int threshold)                   // defaults to 0
            }
            skippedThresholds {
                unstable(int threshold)                     // defaults to 0
                unstableNew(int threshold)                  // defaults to 0
                failure(int threshold)                      // defaults to 0
                failureNew(int threshold)                   // defaults to 0
            }
            thresholdMode(ThresholdMode mode)               // defaults to ThresholdMode.NUMBER
            timeMargin(int margin)                          // defaults to 3000
        }
    }
}
```

Configures a job to collect test results. Requires the
[xUnit Plugin](https://wiki.jenkins-ci.org/display/JENKINS/xUnit+Plugin).
For more details about individual options, please see the plugin page.

The threshold mode can either be `ThresholdMode.NUMBER` or `ThresholdMode.PERCENT`.

Examples:

```groovy
job('example-1') {
    publishers {
        archiveXUnit {
            jUnit {
                pattern 'my_file.xml'
            }
        }
    }
}
```

```groovy
job('example-2') {
    publishers {
        archiveXUnit {
            aUnit {
                pattern 'my_file.xml'
            }
            jUnit {
                pattern 'my_other_file.xml'
            }
            failedThresholds {
                unstable 10
                unstableNew 10
                failure 10
                failureNew 10
            }
            skippedThresholds {
                unstable 5
                unstableNew 5
                failure 5
                failureNew 5
            }
            thresholdMode ThresholdMode.PERCENT
            timeMargin 4000
        }
    }
}
```

(since 1.24)

### S3

```groovy
job {
    publishers {
        s3(String profile) {
            entry(String source, String bucket, String region) {
                storageClass(String storageClass)
                noUploadOnFailure(boolean noUploadOnFailure = true)
                uploadFromSlave(boolean uploadFromSlave = true)
                managedArtifacts(boolean managedArtifacts = true)
            }
            metadata(String key, String value)
        }
    }
}
```

Adds a S3 bucket publisher. Requires the [S3 Plugin](https://wiki.jenkins-ci.org/display/JENKINS/S3+Plugin).

Valid values for region are `'us-gov-west-1'`, `'us-east-1'`, `'us-west-1'`, `'us-west-2'`, `'eu-west-1'`,
`'eu-central-1'`, `'ap-southeast-1'`, `'ap-southeast-2'`, `'ap-northeast-1'`, `'sa-east-1'` or `'cn-north-1'`. The
storage class can be either `'STANDARD'` or `'REDUCED_REDUNDANCY'`.

```groovy
job('example') {
    publishers {
        s3('myProfile') {
            entry('foo', 'bar', 'eu-west-1') {
                storageClass('REDUCED_REDUNDANCY')
                noUploadOnFailure()
                uploadFromSlave()
            }
        }
    }
}
```

(since 1.26)

### Flexible publish

```groovy
job {
    publishers {
        flexiblePublish {
            condition(Closure runConditionClosure) // see conditionalSteps
            publisher(Closure publishersClosure)
            step(Closure stepsClosure)
        }
    }
}
```

Configures a conditional publisher action. Requires the
[Flexible Publish Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Flexible+Publish+Plugin).

If the [Any Build Step Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Any+Build+Step+Plugin) is installed, then
build steps can be used together with publishers. When using versions older then 0.13 of the Flexible Publish Plugin,
only one build step or one publisher can be used.

Examples:

```groovy
job('example-1') {
    publishers {
        flexiblePublish {
            condition {
                status('ABORTED', 'FAILURE')
            }
            publisher {
                wsCleanup()
            }
        }
    }
}

job('example-2') {
    publishers {
        flexiblePublish {
            condition {
                and {
                    stringsMatch('foo', 'bar', false)
                } {
                    status('SUCCESS', 'SUCCESS')
                }
            }
            step {
                shell('echo hello!')
            }
        }
    }
}
```

(since 1.26)

### Retry Build After Failure

```groovy
job {
    publishers {
        retryBuild {
            rerunIfUnstable(boolean rerun = true)    // defaults to false
            retryLimit(int limit)                    // defaults to 0
            progressiveDelay(int increment, int max)
            fixedDelay(int delay)
        }
    }
}
```

Allows to automatically reschedule a build after a failure. Requires version 1.15 or later of the
[Naginator Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Naginator+Plugin).

The parameters for `progressiveDelay` and `fixedDelay` must be specified in seconds. By default a progressive delay with
an increment of 5 minutes and a maximum of 3 hours is used.

```groovy
job('example') {
    publishers {
        retryBuild {
            rerunIfUnstable()
            retryLimit(3)
            progressiveDelay(60, 600)
        }
    }
}
```

(since 1.33)

### GitHub Pull Request Merger

```groovy
job {
    publishers {
        mergePullRequest {
            mergeComment(String comment)             // empty by default
            onlyTriggerPhrase(boolean enable = true) // defaults to false
            onlyAdminsMerge(boolean enable = true)   // defaults to false
            disallowOwnCode(boolean enable = true)   // defaults to false
        }
    }
}
```

Allows to merge the pull request if the build was successful. Requires version 1.17 or later of the
[GitHub Pull Request Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin).

```groovy
job('example') {
    publishers {
        mergePullRequest {
            mergeComment('merged by Jenkins')
            disallowOwnCode()
        }
    }
}
```

(since 1.33)

# Parameters
**Note: In all cases apart from File Parameter the parameterName argument can't be null or empty**
_Note: The Password Parameter is not yet supported. See https://issues.jenkins-ci.org/browse/JENKINS-18141_

### Boolean Parameter
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

### ListTags Parameter
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

### Choice Parameter
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

### File Parameter
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

### Run Parameter
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

### String Parameter
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

### Text Parameter
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

### Node parameter

```groovy
job {
    parameters {
        nodeParam(String name) {
            description(String description)
            defaultNodes(List<String> defaultNodes) // empty by default
            allowedNodes(List<String> allowedNodes) // defaults to all nodes if omitted
            trigger(String trigger)                 // see below, defaults to 'multiSelectionDisallowed'
            eligibility(String eligibility)         // see below, defaults to 'AllNodeEligibility'
        }
    }
}
```

Define a list of nodes on which the job should be allowed to be executed on. Requires the
[NodeLabel Parameter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/NodeLabel+Parameter+Plugin).

`trigger` defines in which case a build on the next node should be triggered, must be one of `'allCases'`, `'success'`,
`'unstable'`, `'allowMultiSelectionForConcurrentBuilds'` or `'multiSelectionDisallowed'`.

`eligibility` defines how selected offline nodes should be handled, must be one of `'AllNodeEligibility'`,
`'IgnoreOfflineNodeEligibility'` or `'IgnoreTempOfflineNodeEligibility'`.

```groovy
// allows to select a single node from all nodes available
job('example-1') {
    parameters {
        nodeParam('TEST_HOST')
    }
}

// runs on node1 by default and can be run on node1, node2 or node3 when triggered manually
job('example-2') {
    parameters {
        nodeParam('TEST_HOST') {
            description('select test host')
            defaultNodes(['node1'])
            allowedNodes(['node1', 'node2', 'node3'])
            trigger('multiSelectionDisallowed')
            eligibility('IgnoreOfflineNodeEligibility')
        }
    }
}
```

(since 1.26)

### Label Parameter

```groovy
job {
    parameters {
        labelParam(String name) {
            defaultValue(String default)
            description(String description)
            allNodes(String trigger = 'allCases', String eligibility = 'AllNodeEligibility') // optional
        }
    }
}
```

Defines a label used to identify/restrict the node where this job should run on. Requires the
[NodeLabel Parameter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/NodeLabel+Parameter+Plugin).

`allNodes` defines if a job should be run on all nodes matching the label. If specified it will set the trigger and node
eligibility criteria.

`trigger` defines in which case a build on the next node should be triggered, must be one of `'allCases'`,
`'success'` or `'unstable'`.

`eligibility` defines how selected offline nodes should be handled, must be one of `'AllNodeEligibility'`,
`'IgnoreOfflineNodeEligibility'` or `'IgnoreTempOfflineNodeEligibility'`.

```groovy
job('example-1') {
    parameters {
        labelParam('MY_LABEL')
    }
}

// runs on all nodes which are labeled with "linux" and are online
job('example-2') {
    parameters {
        labelParam('MY_LABEL') {
            defaultValue('linux')
            description('Select nodes')
            allNodes('allCases', 'IgnoreOfflineNodeEligibility')
        }
    }
}
```

(since 1.30)

### Git Parameter

```groovy
job {
    parameters {
        gitParam(String name) {
            description(String description)   // empty by default
            type(String type)                 // defaults to 'TAG'
            branch(String branch)             // empty by default
            tagFilter(String tagFilter)       // empty by default
            sortMode(SortMode sortMode)       // defaults to 'NONE'
            defaultValue(String defaultValue) // empty by default
        }
    }
}
```

Allows you to assign a Git tag or revision as parameter in parametrized builds. Requires the
[Git Parameter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Parameter+Plugin).

The `type` parameter can be one of `'TAG'`, `'BRANCH'`, `'BRANCH_TAG'` or `'REVISION'`. The `sortMode` can be either
`'NONE'`, `'ASCENDING_SMART'`, `'DESCENDING_SMART'`, `'ASCENDING'` or `'DESCENDING'`.

```groovy
job('example') {
    parameters {
        gitParam('sha') {
            description('Revision commit SHA')
            type('REVISION')
            branch('master')
        }
    }
}
```

(since 1.31)

# Job Properties

### Sidebar Links

```groovy
job {
    properties {
        sidebarLinks {
            link(String url, String text, String icon = null)
        }
    }
}
```

Add links in the sidebar of the project page. Requires the
[Sidebar-Link Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Sidebar-Link+Plugin).

The icon may be a plain filename of an image in Jenkins' `images/24x24` directory (such as `help.gif`, `document.gif` or
`refresh.gif`), or `/userContent/filename.ext` for a custom icon placed in the `JENKINS_HOME/userContent` directory.
User content can also be uploaded by using the `userContent` DSL method, see
[[Job-DSL-Commands#uploading-user-content]].

```groovy
userContent('wiki.png', streamFileFromWorkspace('images/wiki.png'))

job('example') {
    properties {
        sidebarLinks {
            // use built-in image
            link('https://jira.acme.org/', 'JIRA', 'notepad.png')
            // use uploaded image
            link('https://wiki.acme.org/', 'Wiki', '/userContent/wiki.png')
        }
    }
}
```

(since 1.33)

### Custom Icon

```groovy
job {
    properties {
        customIcon(String iconFileName)
    }
}
```

Allows to configure a custom icon for each job. Requires the
[Custom Job Icon Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Custom+Job+Icon+Plugin).

The `iconFileName` must point to a custom icon placed in the `JENKINS_HOME/userContent/customIcon` directory. User
content can be uploaded by using the `userContent` DSL method, see [[Job-DSL-Commands#uploading-user-content]].

```groovy
userContent('customIcon/job.png', streamFileFromWorkspace('icons/job.png'))

job('example') {
    properties {
        customIcon('job.png')
    }
}
```

(since 1.33)

# Workflow Definitions

### Groovy CPS DSL

```
workflowJob {
    definition {
        cps {
            script(String script)
            sandbox(boolean sandbox = true)
        }
    }
}
```

Defines a Groovy CPS DSL definition.

```
def flow = '''node {
  git url: 'https://github.com/jglick/simple-maven-project-with-tests.git'
  def mvnHome = tool 'M3'
  sh "${mvnHome}/bin/mvn -B verify"
}'''
workflowJob('example-1') {
    definition {
        cps {
            script(flow)
        }
    }
}

workflowJob('example-2') {
    definition {
        cps {
            script(readFileFromWorkspace('project-a-workflow.groovy'))
            sandbox()
        }
    }
}
```

(since 1.29)
