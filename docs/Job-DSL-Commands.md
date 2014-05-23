### Please see the [[Job Reference]] page for the full details of the _job_ component and the [[View Reference]] page for details about the _view_ methods.

# Job Factory
The DSL execution engine exposes only one method, called 'job'. This 'job' method implies the creation of a Jenkins job and the closure to this method then further exposed some methods where things get interesting. See the later sections to learn the specific available methods available, for when a DSL helper method isn't available, look to the [configure block](The Configure Block). Below is the simplest job possible:

```groovy
job {
    name 'Simplest Job Possible'
}
```

Because the engine is just Groovy, you can call other Groovy classes available in the workspace. When in those methods the 'job' method is no longer available, so it is recommended to pass in the current context to make this method available to other context. For example, when making utility methods, you would call them like this:

```groovy
BuildFramework.ant(this, arg1, arg2)
```

Then the BuildFramework class has enough to make 'job' calls, it would look like this:

```groovy
public class BuildFramework {
    public void static ant(jobFactory, arg1, arg2) {
        jobFactory.job {
            name arg1
            steps {
                ant(arg2)
            }
        }
    }
}
```

TBD, Current this method is hard-coded to free-form and maven projects. It wouldn't be too hard to make it implement multi-job and ivy projects. File a bug if you'd find this useful.

# DSL Methods

This is the formal documentation of the available DSL methods. In the Closure provided to job there are a few top level methods, like label and chucknorris. Others are nested deeper in blocks which represent their role in Jenkins, e.g. the publishers block contains all the publisher steps. The one caveat is that scm and multiscm are mutually exclusive. Likewise, when using the scm block, only one scm can be specified.

Further sections will define in detail how they work, in a Java-like syntax. If an argument is followed with an equals, this means it's a default value. DSL Methods can be cumulative or overriding, meaning that some methods will add nodes (e.g. publishers and steps) and some will replace nodes (e.g. disabled() will replace any existing disabled tags).

**NOTE: when using these commands, remember that you need to use them in context.  I.e. to use the "downstream" command, it needs to be enclosed in a "publisher" command block.**

Here's a high level overview of what's available:

```groovy
job(attributes) {
    name(nameStr)
    displayName(displayName)
    using(templateNameStr)
    description(descStr)
    label(labelStr)
    quietPeriod(seconds)
    customWorkspace(workspacePath)
    disabled(shouldDisable)
    blockOn(projectNames)
    blockOnUpstreamProjects()
    blockOnDownstreamProjects()
    logRotator(daysToKeepInt, numToKeepInt, artifactDaysToKeepInt, artifactNumToKeepInt)
    concurrentBuild(allowConcurrentBuild) // since 1.21
    jdk(jdkStr)
    keepDependencies(keep)
    rootPOM(rootPOMStr)
    goals(goalsStr)
    mavenOpts(mavenOptsStr)
    mavenInstallation(name) // since 1.20
    localRepository(location)
    perModuleEmail(shouldSendEmailPerModule)
    archivingDisabled(shouldDisableArchiving)
    runHeadless(shouldRunHeadless)
    preBuildSteps(mavenPreBuildStepsClosure)
    postBuildSteps(mavenPostBuildStepsClosure)
    environmentVariables(vars)
    environmentVariables(closure) // See [[Job Reference]] for details of EnvironmentVariablesContext
    priority(value)
    throttleConcurrentBuilds(throttleClosure)
    buildFlow(buildFlowText) // Since 1.21, can only be used on 'BuildFlow' job types. See [[Job Reference]].
    authorization {
        permission(permissionStr) // e.g. hudson.model.Item.Workspace:authenticated
        permission(String permEnumName, String user)
        permission(Permission perm, String user)
        permissionAll(securityGroup)
    }
    scm {
        hg(url, branch) {}
        git(url, branch) {}
        github(ownerAndProject, branch, protocol, host) {}
        svn(svnUrl, localDir) {}
        p4(viewspec, user, password) {}
        cloneWorkspace(parentProject, criteriaArg) 
    }
    multiscm {
        hg(url, branch) {}
        git(url, branch) {}
        github(ownerAndProject, branch, protocol, host) {}
        svn(svnUrl) {}
        p4(viewspec, user, password) {}
    }
    checkoutRetryCount(times)
    triggers {
        cron(cronString)
        scm(cronString)
        githubPush()
        gerrit(gerritClosure) // See [[Job Reference]] for gerritClosure syntax
        pullRequest(pullRequestClosure) // since 1.22 See [[Job Reference]] for pullRequestClosure syntax
        urlTrigger([cronString,] urlTriggerClosure) // See [[Job Reference]] for closure syntax 
        snapshotDependencies(checkSnapshotDependencies)
    }
    wrappers { // This block exists since 1.19. Before that the methods were on top level
        runOnSameNodeAs(jobName, useSameWorkspace)
        rvm(rubySpecification)
        timeout(timeoutInMinutes, shouldFailBuild)
        timeout(type) {} //see Job Reference for closure details
        allocatePorts(ports){}
        sshagent(credentials)
        timestamps()
        colorizeOutput()
        xvnc(takeScreenshot = false)
        toolenv(String.. tools)
        environmentVariables(closure) // See [[Job Reference]] for details of EnvironmentVariablesContext
        release(closure) // since 1.22, see [[Job Reference]] for details
        preBuildCleanup(closure) // since 1.22
        logSizeChecker(closure) // since 1.23, see [Job Reference]] for details
        injectPasswords() // since 1.23
    }
    steps {
        shell(String commandStr)
        batchFile(String commandStr)
        gradle(tasksArg, switchesArg, useWrapperArg) {}
        maven(targetsArg, pomArg) {}
        maven {} // since 1.20, see [[JobReference]]
        sbt(sbtNameArg, actionsArg, sbtFlagsArg, jvmFlagsArg, subdirPathArg) {} // See [[Job Reference]] for details
        ant(targetsArg, buildFileArg, antInstallation, antClosure) // See [[Job Reference]] for antClosure syntax
        copyArtifacts(jobName, includeGlob, targetPath, flattenFiles, optionalAllowed, copyArtifactClosure) // See [[Job Reference]] for copyArtifactClosure syntax
        groovyCommand(commandStr, groovyClosure) // See [[Job Reference]] for groovyClosure syntax
        groovyScriptFile(fileName, groovyClosure)  // See [[Job Reference]] for groovyClosure syntax
        systemGroovyCommand(commandStr, systemGroovyClosure) // See [[Job Reference]] for systemGroovyClosure syntax
        systemGroovyScriptFile(fileName, systemGroovyClosure) // See [[Job Reference]] for systemGroovyClosure syntax
        phase(String name, String continuationConditionArg = 'SUCCESSFUL', Closure phaseClosure = null) // See [[Job Reference]] for phaseClosure syntax
        dsl(dslClosure) // See [[Job Reference]] for dslClosure syntax
        dsl(scriptText, removedJobAction, ignoreExisting)
        dsl(externalScripts, removedJobAction, ignoreExisting)
        grails(grailsClosure) // See [[Job Reference]] for grailsClosure syntax
        grails(targetsArg, grailsClosure)
        grails(targetsArg, useWrapperArg, grailsClosure)
        prerequisite(projectList, warningOnlyBool) // Since 1.19
        downstreamParameterized(downstreamClosure) // See [[Job Reference]] for downstreamClosure syntax
        conditionalSteps(conditionalClosure) // See [[Job Reference]] for conditionalClosure syntax
        environmentVariables(closure) // See [[Job Reference]] for details of EnvironmentVariablesContext
        remoteTrigger(remoteJenkinsName, jobName, remoteTriggerClosure) // Since 1.22
    }
    publishers {
        extendedEmail(recipients, subjectTemplate, contentTemplate ) {}
        mailer(recipients, dontNotifyEveryUnstableBuild, sendToIndividuals)
        archiveArtifacts(glob, excludeGlob, latestOnlyBoolean)
        archiveArtifacts(archiveArtifactsClosure) // Since 1.20, // See [[Job Reference]] for archiveArtifactsClosure syntax
        archiveJunit(glob, retainLongStdout, allowClaimingOfFailedTests, publishTestAttachments)
        publishHtml {
            report(reportDir, reportName, reportFiles, keepAll)
        }
        publishJabber(target, strategyName, channelNotificationName, jabberClosure) // See [[Job Reference]] for jabberClosure syntax
        publishScp(site, scpClosure) // See [[Job Reference]] for scpClosure syntax
        publishCloneWorkspace(workspaceGlob, workspaceExcludeGlob, criteria, archiveMethod, overrideDefaultExcludes, cloneWorkspaceClosure) // See [[Job Reference]] for cloneWorkspaceClosure
        downstream(projectName, thresholdName)
        downstreamParameterized(downstreamClosure) // See [[Job Reference]] for downstreamClosure syntax
        violations(perFileDisplayLimit, violationsClosure) // See [[Job Reference]] for violationsClosure syntax
        chucknorris() // Really important
        irc(ircClosure) // See [[Job Reference]] for ircClosure syntax
        cobertura(coberturaReportFilePattern, coberturaClosure) // See [[Job Reference]] for coberturaClosure syntax
        allowBrokenBuildClaiming()
        jacocoCodeCoverage(jacocoClosure) // See [[Job Reference]] for jacococClosure syntax
        fingerprint(targets, recordBuildArtifacts) // See [[Job Reference]], too
        buildDescription(regularExpression, description, regularExpressionForFailed, descriptionForFailed, multiConfigurationBuild)
        findbugs(pattern, isRankActivated = false, staticAnalysisClosure = null) // See [[Job Reference]] for staticAnalysisClosure syntax
        pmd(pattern, staticAnalysisClosure = null) // See [[Job Reference]] for staticAnalysisClosure syntax
        checkstyle(pattern, staticAnalysisClosure = null) // See [[Job Reference]] for staticAnalysisClosure syntax
        dry(pattern, highThreshold = 50, normalThreshold = 25, staticAnalysisClosure = null) // See [[Job Reference]] for staticAnalysisClosure syntax
        tasks(pattern, excludePattern = '', high = '', normal = '', low = '', ignoreCase = false, staticAnalysisClosure = null) // See [[Job Reference]] for staticAnalysisClosure syntax
        ccm(pattern, staticAnalysisClosure = null) // See [[Job Reference]] for staticAnalysisClosure syntax
        androidLint(pattern, staticAnalysisClosure = null) // See [[Job Reference]] for staticAnalysisClosure syntax
        dependencyCheck(pattern, staticAnalysisClosure = null) // See [[Job Reference]] for staticAnalysisClosure syntax
        warnings(consoleParsers, parserConfigurations = [:], warningsClosure = null) // See [[Job Reference]] for how to fill the parameters
        textFinder(regularExpression, fileSet = '', alsoCheckConsoleOutput = false, succeedIfFound = false, unstableIfFound = false) // since 1.19
        postBuildTask(closure)  // See [[Job Reference]] for closure syntax, since 1.19
        aggregateDownstreamTestResults(jobs = null, includeFailedBuilds = false) // since 1.19
        groovyPostBuild(script, behavior = Behavior.DoNothing) // since 1.19, See [[Job Reference]] for details of Behavior enum
        archiveJavadoc(javadocClosure) // See [[Job Reference]] for closure syntax, since 1.19
        emma(coverageFile, closure) // See [[Job Reference]] for closure syntax, since 1.20
        jshint(pattern, staticAnalysisClosure = null) // See [[Job Reference]] for staticAnalysisClosure syntax, since 1.20
        associatedFiles(String files = null) // since 1.20
        publishRobotFrameworkReports(Closure closure = null) // Since 1.21. See [[Job Reference]] for the closure syntax
        buildPipelineTrigger(downstreamProjectNames, Closure closure) // since 1.21. Closure support since 1.23
        githubCommitNotifier() // since 1.21
        git(gitPublisherClosure) // since 1.22
        flowdock(String token, flowdockClosure = null) // since 1.23. See [[Job Reference]] for the closure syntax
        flowdock(String[] tokens, flowdockClosure = null) // since 1.23. See [[Job Reference]] for the closure syntax
        stashNotifier(stashNotifierClosure = null) // since 1.23. See [[Job Reference]] for the closure syntax
        mavenDeploymentLinker(String regex) // since 1.23
        wsCleanup(wsCleanupClosure = null) // since 1.23. See [[Job Reference]] for the closure syntax
    }
    parameters {
        booleanParam(parameterName, defaultValue, description)
        listTagsParam(parameterName, scmUrl, tagFilterRegex, sortNewestFirst, sortZtoA, maxTagsToDisplay, defaultValue, description)
        choiceParam(parameterName, options, description)
        fileParam(fileLocation, description)
        runParam(parameterName, jobToRun, description, filter)
        stringParam(parameterName, defaultValue, description)
        textParam(parameterName, defaultValue, description)
    }
}

view(attributes) {  // since 1.21, see [[View Reference]]
    name(nameStr)
    description(descriptionStr)
    filterBuildQueue(filterBuildQueueBool)
    filterExecutors(filterExecutorsBool)
    configure(configBlock)

    // ListView options
    statusFilter(filter)
    jobs {
        name(jobName)
        names(jobNames)
        regex(regex)
    }
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
        lastBuildConsole() // since 1.23
    }

    // BuildPipelineView options
    displayedBuilds(noOfDisplayedBuilds)
    title(pipelineTitle)
    selectedJob(rootJob)
    // consoleOutputLinkStyle and cssUrl require 1.4.2 of the Build Pipeline Plugin
    consoleOutputLinkStyle(consoleOutputLinkStyle)
    customCssUrl(cssUrl)
    triggerOnlyLatestJob(triggerOnlyLatestJobBool)
    alwaysAllowManualTrigger(alwaysAllowManualTriggerBool)
    showPipelineParameters(showPipelineParametersBool)
    showPipelineParametersInHeaders(showPipelineParametersInHeadersBool)
    refreshFrequency(seconds)
    showPipelineDefinitionHeader(showPipelineDefinitionHeaderBool)
}

folder { // since 1.23
    name(nameStr)
    displayName(displayNameStr)
    configure(configBlock)
}
```

The plugin tries to provide DSL methods to cover "common use case" scenarios as simple method calls. When these methods fail you, you can always generate the XML yourself via the [configure block](The Configure Block). Sometimes, a DSL method will provide a configure block of its own, which will set the a good context to help modify a few fields.  This gives native access to the Job config XML, which is typically very straight forward to understand.

(Note: The full XML can be found for any job by taking the Jenkins URL and appending "/config.xml" to it. We find that creating a job the way you like it, then viewing the XML is the best way to learn what fields you need.)

# Job
```groovy
job(Map<String, Object> attributes=[:], Closure closure)
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

A job can have optional attributes. Currently only a 'type' attribute with value of 'Freeform', 'Maven', 'Multijob', or 'BuildFlow' is supported. When no type attribute is specified, a free-style job will be generated. Some methods will only be available in some job types, e.g. phase is meant only in Multijob. Each dsl method should document where they are relevant.

```groovy
job(type: Maven) {
  name 'maven-job'
}
```

Please see the [[Job Reference]] page for details to the _job_ component.

# View
```groovy
view(Map<String, Object> attributes=[:], Closure closure)
```

The view method behaves like the job method explained above and will return a _View_ object.

Currently only a 'type' attribute with value of 'ListView' is supported. When no type attribute is specified, a list view will be generated.

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

The folder method behaves like the job method explained above and will return a _Folder_ object.

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

This provide the ability to schedule a Job to be executable after the DSL runs. The given name doesn't have to be a job which was generated by the DSL, but it could be.

# Reading Files from Workspace
Requires version >1.15.
```groovy
InputStream streamFileFromWorkspace(String filePath) throws IOException;
String readFileFromWorkspace(String filePath) throws IOException;
```

Anywhere in the script you can read in a file from the current workspace using the above calls. This assumes that you checked out some source control as part of the job processing the DSL. This can be useful when populated fields on a generated job, e.g.

```groovy
job {
    steps {
        shell(readFileFromWorkspace('build.sh')
    }
}
```

# Grab

Groovy provides the ability to "grab" dependencies and use them right away, this is called Grape (http://groovy.codehaus.org/Grape). The Job-DSL supports this feature, as long as it's provided at the top of the script. Not only can the @Grab annotation be used, it can be used with @GrabResolver to pull dependencies from custom defined repositories. This means that you can write a set of conventions  specific to your Enterprise, jar them up, distribute them via a repository, then grab them via @Grab. Here is an example of pulling in a utility library.

```groovy
@Grab(group='commons-lang', module='commons-lang', version='2.4')
import org.apache.commons.lang.WordUtils
println "Hello ${WordUtils.capitalize('world')}"
```

#  Configure
_This is primarily defined in the [[configure block]] page. This is a short overview._

A lot of property directory on a project are best accessed directly with the configure block and via a DSL command.
Here are some simple examples:
```
configure {
    description 'My Description'
    jdk = 'JDK 6'
    disabled false
    labels 'MASTER' // Need way to disable too
}
```

## To Be Implemented

These are the ones in pipeline, and will be implemented sooner than later. If you're looking on working on one, claim it.

* Publish - xUnit
* Publish - Cobertura, Analysis

@kmarquardsen:
* wrappers - ArtifactoryGenericConfigurator
* Publish - PerformancePublisher

@wolfs:
* Publish - Checkstyle, FindBugs, PMD, CCM, OWASP Dependency Analysis, Compiler Warnings, Android Lint, DRY, Task Scanner

@daspilker:
* Config File Provider Plugin

@andrewharmellaw:
* Build parameters - password (https://issues.jenkins-ci.org/browse/JENKINS-18141)

bealeaj12414
* update the defaults and provide convenience methods for setting status values of the gerrit trigger

@sgtcoolguy
* Publish - Emma

## To Be Designed
* Publish - DeployPublisher
* Build - Python
* Report - MavenMailer
* Publish - BuildTrigger
* Publish - SVNTagPublisher
* Publish - RedeployPublisher
* Extend SCM (SVN and others?) to handle multiple Module Locations
* Publish - JoinTrigger
