## Migrating to 1.39

### MultiJob

A `phaseJob` method has been added to `phase` context to fix
([JENKINS-27921](https://issues.jenkins-ci.org/browse/JENKINS-27921)). The `job` method within that context has been
[[deprecated|Deprecation-Policy]] and will be removed. The `jobName` method within the `phaseJob` context has deprecated
and will also be removed.

DSL prior to 1.39
```groovy
multiJob('example') {
    steps {
        phase('first') {
            job {
                jobName('job-a')
            }
            job('job-b', false, false)
        }
    }
}
```

DSL since 1.39
```groovy
multiJob('example') {
    steps {
        phase('first') {
            phaseJob('job-a')
            phaseJob('job-b') {
                currentJobParameters(false)
                exposedScm(false)
            }
        }
    }
}
```

### Subversion

Support for versions older than 2.1 of the
[Subversion Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Subversion+Plugin) is [[deprecated|Deprecation-Policy]]
and will be removed.

### Parameterized Trigger

Support for versions older than 2.26 of the
[Parameterized Trigger Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Trigger+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

### JSHint Checkstyle

Support for the [JSHint Checkstyle Plugin](https://wiki.jenkins-ci.org/display/JENKINS/JSHint+Checkstyle+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed. The plugin is no longer available in the Jenkins Update Center.

## Migrating to 1.38

### Parameterized Trigger

Support for versions older than 2.25 of the
[Parameterized Trigger Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Trigger+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

Some overloaded DSL methods for the
[Parameterized Trigger Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Parameterized+Trigger+Plugin) have been
replaced by new methods in the nested context. The overloaded methods have been [[deprecated|Deprecation-Policy]] and
will be removed.

DSL prior to 1.38
```groovy
job('example-1') {
    steps {
        downstreamParameterized {
            trigger('Project1, Project2', 'ALWAYS', false,
                    [buildStepFailure: 'FAILURE',
                     failure         : 'FAILURE',
                     unstable        : 'UNSTABLE'])
        }
    }
}

job('example-2') {
    publishers {
        downstreamParameterized {
            trigger('Project1, Project2', 'UNSTABLE_OR_BETTER', true)
        }
    }
}
```

DSL since to 1.38
```groovy
job('example-1') {
    steps {
        downstreamParameterized {
            trigger('Project1, Project2') {
                block {
                    buildStepFailure('FAILURE')
                    failure('FAILURE')
                    unstable('UNSTABLE']
                }
            }
        }
    }
}

job('example-2') {
    publishers {
        downstreamParameterized {
            trigger('Project1, Project2') {
                condition('UNSTABLE_OR_BETTER')
                triggerWithNoParameters()
            }
        }
    }
}
```

### Parameter Passing

The way how parameters are passed to downstream jobs or multi-job phases has changed. The existing methods have been
[[deprecated|Deprecation-Policy]] and will be removed.

DSL prior to 1.38
```groovy
job('example-1') {
    steps {
        downstreamParameterized {
            trigger('Project1, Project2') {
                predefinedProp('key1', 'value1')
                predefinedProps('key2=value2\nkey3=value3')
            }
        }
    }
}

job('example-2') {
    publishers {
        downstreamParameterized {
            trigger('Project1, Project2') {
                currentBuild()
                sameNode(true)
            }
        }
    }
}

multiJob('example-3') {
    steps {
        phase('test') {
             job('other', false, true) {
                boolParam('cParam', true)
                fileParam('my.properties')
                sameNode()
                matrixParam('it.name=="hello"')
                subversionRevision()
                gitRevision()
                prop('prop1', 'value1')
                nodeLabel('lParam', 'my_nodes')
            }
        }
   }
}
```

DSL since to 1.38
```groovy
job('example-1') {
    steps {
        downstreamParameterized {
            trigger('Project1, Project2') {
                parameters {
                    predefinedProp('key1', 'value1')
                    predefinedProps([key2: 'value2', key3: 'value3'])
                }
            }
        }
    }
}

job('example-2') {
    publishers {
        downstreamParameterized {
            trigger('Project1, Project2') {
                parameters {
                    currentBuild()
                    sameNode()
                }
            }
        }
    }
}

multiJob('example-3') {
    steps {
        phase('test') {
            job('other', false, true) {
                parameters {
                    booleanParam('cParam', true)
                    propertiesFile('my.properties')
                    sameNode()
                    matrixSubset('it.name=="hello"')
                    subversionRevision()
                    gitRevision()
                    predefinedProp('prop1', 'value1')
                    nodeLabel('lParam', 'my_nodes')
                }
            }
        }
   }
}
```

### GitHub Pull Request Builder

Support for versions older than 1.26 of the
[GitHub Pull Request Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

## Migrating to 1.37

### Multijob

Support for versions older than 1.16 of the
[Multijob Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Multijob+Plugin) is [[deprecated|Deprecation-Policy]] and
will be removed.

### Git

Support for versions older than 2.2.6 of the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

### Groovy Postbuild

Support for versions older than 2.2 of the
[Groovy Postbuild Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Groovy+Postbuild+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

## Migrating to 1.36

### Script Names

Groovy currently does not allow to use arbitrary names for scripts, see
[GROOVY-4020](https://issues.apache.org/jira/browse/GROOVY-4020). In the Job DSL plugin, it's currently only working by
accident, so usage of arbitrary names is [[deprecated|Deprecation-Policy]] and will be removed.

In the future only names which contain letters, digits, underscores or dollar signs are allowed, but the name must not
start with a digit. Basically these are the rules for Java identifiers, see
[this](http://docs.oracle.com/javase/6/docs/api/java/lang/Character.html#isJavaIdentifierStart%28char%29) and
[this](http://docs.oracle.com/javase/6/docs/api/java/lang/Character.html#isJavaIdentifierPart%28char%29)) for details.
The file name extension can be anything and is ignored.  

### Build Blocker

Support for versions older than 1.7.1 of the
[Build Blocker Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Blocker+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

### JobManagement

The following method in the `JobManagement` interface has been [[deprecated|Deprecation-Policy]] and will be removed
along with all implementations:

```groovy
String getCredentialsId(String credentialsDescription)
```

Finding credentials by description has been deprecated some time ago, so this method is no longer needed.

### AbstractJobManagement

The following methods in AbstractJobManagement have been [[deprecated|Deprecation-Policy]] and will be removed:

```groovy
protected static List<StackTraceElement> getStackTrace()

protected static String getSourceDetails(List<StackTraceElement> stackTrace)

protected static String getSourceDetails(String scriptName, int lineNumber)

protected void logWarning(String message, Object... args)
```

### Exception Handling

DSL methods will now throw a `javaposse.jobdsl.dsl.DslException` instead of `java.lang.IllegalArgumentException`,
`java.lang.IllegalStateException` or `java.lang.NullPointerException` when validating arguments.

The `javaposse.jobdsl.dsl.DslScriptLoader` will also throw a `javaposse.jobdsl.dsl.DslException` on script errors like
compilation failures and missing methods or properties.

## Migrating to 1.35

### Maven

Support for versions older than 2.3 of the
[Maven Project Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Maven+Project+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

### S3

Support for versions 0.6 and earlier of the S3 Plugin is [[deprecated|Deprecation-Policy]] and will be removed. The
region identifiers have changed with version 0.7 of the S3 Plugin.

DSL prior to 1.35
```groovy
job('example') {
    publishers {
        s3('example') {
            entry('foo', 'bar', 'EU_WEST_1')
        }
    }
}
```

DSL since to 1.35
```groovy
job('example') {
    publishers {
        s3('example') {
            entry('foo', 'bar', 'eu-west-1')
        }
    }
}
```

### GitHub Pull Request Builder

Support for versions older than 1.15-0 of the
[GitHub Pull Request Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

### Conditional Build Steps

Usage build steps directly in the `conditionalSteps` context is [[deprecated|Deprecation-Policy]] and will be removed.

DSL prior to 1.35
```groovy
job('example') {
    steps {
        conditionalSteps {
            condition {
                stringsMatch('${SOME_PARAMETER}', 'pants', false)
            }
            runner('Fail')
            shell("echo 'just one step'")
        }
    }
}
```

DSL since to 1.35
```groovy
job('example') {
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
```

### Matrix Authorization

Support for versions older than 1.2 of the
[Matrix Authorization Strategy Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Matrix+Authorization+Strategy+Plugin)
is [[deprecated|Deprecation-Policy]] and will be removed.

## Migrating to 1.34

### Conditional Build Steps

An undocumented variant of the `runner` method in `conditionalSteps` and the `EvaluationRunners` enum have been
[[deprecated|Deprecation-Policy]] and will be removed.

DSL prior to 1.34
```groovy
import javaposse.jobdsl.dsl.helpers.step.ConditionalStepsContext

steps {
    conditionalSteps {
        condition {
            alwaysRun()
        }
        runner(ConditionalStepsContext.EvaluationRunners.Fail)
        shell('echo "Hello World!"')
    }
}
```

DSL since to 1.34
```groovy
steps {
    conditionalSteps {
        condition {
            alwaysRun()
        }
        runner('Fail')
        shell('echo "Hello World!"')
    }
}
```

## Migrating to 1.33

### Archive Artifacts

The `latestOnly` option is deprecated in newer versions of Jenkins and therefore it's also
[[deprecated|Deprecation-Policy]] in the DSL and will be removed. Use `logRotator` to configure which artifacts to keep.

DSL prior to 1.33
```groovy
job('example-1') {
    publishers {
        archiveArtifacts('*.xml', null, true)
    }
}

job('example-2') {
    publishers {
        archiveArtifacts {
            pattern('*.xml')
            latestOnly()
        }
    }
}
```

DSL since 1.33
```groovy
job('example-1') {
    logRotator(-1, -1, -1, 1)
    publishers {
        archiveArtifacts('*.xml')
    }
}

job('example-2') {
    logRotator(-1, -1, -1, 1)
    publishers {
        archiveArtifacts {
            pattern('*.xml')
        }
    }
}
```

### Copy Artifacts

Support for versions 1.30 and earlier of the Copy Artifact Plugin is [[deprecated|Deprecation-Policy]] and will be
removed.

All variants of `copyArtifacts` with more than two parameters have been replaced by a nested context and are deprecated.

DSL prior to 1.33
```groovy
job('example') {
    steps {
        copyArtifacts('other-1', '*.xml') {
            latestSaved()
        }
        copyArtifacts('other-2', '*.txt', 'files') {
            buildNumber(5)
        }
        copyArtifacts('other-3', '*.csv', 'target', true) {
            latestSuccessful(true)
        }
        copyArtifacts('other-4', 'build/*.jar', 'libs', true, true) {
            upstreamBuild()
        }
    }
}
```

DSL since 1.33
```groovy
job('example') {
    steps {
        copyArtifacts('other-1') {
            includePatterns('*.xml')
            buildSelector {
                latestSaved()
            }
        }
        copyArtifacts('other-2') {
            includePatterns('*.txt')
            targetDirectory('files')
            buildSelector {
                buildNumber(5)
            }
        }
        copyArtifacts('other-3') {
            includePatterns('*.csv')
            targetDirectory('target')
            flatten()
            buildSelector {
                latestSuccessful(true)
            }
        }
        copyArtifacts('other-4') {
            includePatterns('build/*.jar')
            targetDirectory('libs')
            flatten()
            optional()
            buildSelector {
                upstreamBuild()
            }
        }
    }
}
```

### Robot Framework

Support for versions older than 1.4.3 of the
[Robot Framework Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Robot+Framework+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

### Mercurial

Support for versions older than 1.50.1 of the
[Mercurial Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Mercurial+Plugin) is [[deprecated|Deprecation-Policy]]
and will be removed.

### Flexible Publish

Support for versions older than 0.13 of the
[Flexible Publish Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Flexible+Publish+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

## Migrating to 1.31

### Nested Views

The views closure of the nested view type has been changed to use the same method signatures than the top-level factory
methods.

DSL prior to 1.31
```groovy
nestedView('project-a') {
    views {
        view('overview') {
        }
        view('pipeline', type: BuildPipelineView) {
        }
    }
}
```

DSL since 1.31
```groovy
nestedView('project-a') {
    views {
        listView('overview') {
        }
        buildPipelineView('pipeline') {
        }
    }
}
```

### MultiJob Plugin

Support for version 1.12 and earlier of the MultiJob Plugin is [[deprecated|Deprecation-Policy]] and will be removed.

### Local Maven Repository Location

The `localRepository` method with a `javaposse.jobdsl.dsl.helpers.common.MavenContext.LocalRepositoryLocation` argument
has been [[deprecated|Deprecation-Policy]] and replaced by a method with a
`javaposse.jobdsl.dsl.helpers.LocalRepositoryLocation` argument. The values of the enum have been renamed from camel
case to upper case to follow the naming convention for enum values. The new enum is implicitly imported, but not with
star import as the new deprecated variant.

DSL prior to 1.31
```groovy
mavenJob {
    localRepository(LocalToWorkspace)
}
job {
    steps {
        maven {
            localRepository(LocalToWorkspace)
        }
    }
}
```

DSL since 1.31
```groovy
mavenJob {
    localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
}
job {
    steps {
        maven {
            localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
        }
    }
}
```

### Permissions

The Permissions enum has been deprecated because it can not reflect all permissions that are available at runtime.

DSL prior to 1.31
```groovy
job {
    permission(Permissions.ItemRead, 'jill')
    permission('RunUpdate', 'joe')
}
```

DSL since 1.31
```groovy
job {
    authorization {
        permission('hudson.model.Item.Read', 'jill')
        permission('hudson.model.Run.Update', 'joe')
    }
}
```

## Migrating to 1.30

### Factory and Name Methods

The generic factory methods `job`, `view` and `configFile` have been [[deprecated|Deprecation-Policy]] and replaced by
concrete ones. The `name` methods have also been deprecated. The name must be specified as argument to the factory
methods.

DSL prior to 1.30
```groovy
job {
    name('one')
}
job(type: Maven) {
    name('two')
}
folder {
    name('three')
}
view {
    name('four')
}
view(type: NestedView) {
    name('five')
}
configFile {
    name('six')
}
configFile(type: MavenSettings) {
    name('seven')
}
```

DSL since 1.30
```groovy
freeStyleJob('one') {
}
mavenJob('two') {
}
folder('three') {
}
listView('four') {
}
nestedView('five') {
}
customConfigFile('six')
}
mavenSettingsConfigFile('seven') {
}
```

### Jabber Publisher

The `publishJabber` DSL methods with `strategyName` and `channelNotificationName` have been
[[deprecated|Deprecation-Policy]]. Use the methods of the context instead.

DSL prior to 1.30
```groovy
job {
    publishers {
        publishJabber('one@example.org', 'ANY_FAILURE')
        publishJabber('two@example.org', 'STATECHANGE_ONLY', 'BuildParameters')
    }
}
```

DSL since 1.30
```groovy
job {
    publishers {
        publishJabber('one@example.org' {
            strategyName('ANY_FAILURE')
        }
        publishJabber('two@example.org') {
            strategyName('STATECHANGE_ONLY')
            channelNotificationName('BuildParameters')
        }
    }
}
```

### Finding Credentials by Description

Finding credentials by description has been [[deprecated|Deprecation-Policy]]. The argument passed to the `credentials`
methods (e.g. for Git or Subversion SCM) has been used to find credentials by comparing the value to the credential's
description and ID. Using the description can cause problems because it's not enforced that descriptions are unique. But
it was useful because the ID was a generated UUID that could not be changed and thus was neither portable between
Jenkins instances nor readable in scripts as a symbolic name would be. Since version 1.21, the credentials plugin
supports to set the ID to any unique value when creating new credentials. So it's no longer necessary to use the
description for matching.

DSL prior to 1.30
```groovy
job {
    scm {
        git {
            remote {
                github('account/repo')
                credentials('GitHub CI Key')
            }
        }
    }
}
```

DSL since 1.30
```groovy
job {
    scm {
        git {
            remote {
                github('account/repo')
                credentials('github-ci-key')
            }
        }
    }
}
```

### Build Timeout

The `javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext.Timeout` enum has been [[deprecated|Deprecation-Policy]]
because it's not used by the DSL anymore.

The `failBuild` option with a boolean argument has been [[deprecated|Deprecation-Policy]].

DSL prior to 1.30
```groovy
job {
    wrappers {
        buildTimeout() {
            failBuild(true)
        }
    }
}
```

DSL since 1.30
```groovy
job {
    wrappers {
        buildTimeout() {
            failBuild()
        }
    }
}
```

## Migrating to 1.29

### Grab Support

Support for the `@Grab` and `@Grapes` annotation has been [[deprecated|Deprecation-Policy]] and replaced by the
_Additional classpath_ option of the _Process Job DSLs_ build step.

DSL prior to 1.29
```groovy
@Grab(group='commons-lang', module='commons-lang', version='2.4')

import org.apache.commons.lang.WordUtils

println "Hello ${WordUtils.capitalize('world')}"
```

DSL since 1.29
```groovy
import org.apache.commons.lang.WordUtils

println "Hello ${WordUtils.capitalize('world')}"
```

But to be able to use a library, it has to be added to the _Additional classpath_ option of the _Process Job DSLs_ build
step (e.g. `lib/commons-lang-2.4.jar` or `lib/*.jar`) and the JAR files have to be in workspace of the seed job (e.g. in
a `lib` directory). See [[Using Libraries|User-Power-Moves#using-libraries]] for details.

### Per Module Email

The `perModuleEmail` option has been deprecated because the e-mail notification settings have changed in newer versions
of the [Maven Project Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Maven+Project+Plugin), see
[JENKINS-26284](https://issues.jenkins-ci.org/browse/JENKINS-26284).

DSL prior to 1.29
```groovy
job(type: Maven) {
    perModuleEmail(true)
}
```

DSL since 1.29
```groovy
job(type: Maven) {
    configure {
        it / reporters << 'hudson.maven.reporters.MavenMailer' {
            recipients()
            dontNotifyEveryUnstableBuild(false)
            sendToIndividuals(false)
            perModuleEmail(true)
        }
    }
}
```

## Migrating to 1.28

### HTML Publisher

The non-closure variants of the `report` methods in the `publishHtml` context have been
[[deprecated|Deprecation-Policy]] in favor of a new closure variant.

DSL prior to 1.28
```groovy
job {
    publishers {
        publishHtml {
            report('build', 'Report Name', 'content.html', true)
            report(reportName: 'Report Name', reportDir: 'build', reportFiles: 'content.html', keepAll: true)
        }
    }
}
```

DSL since 1.28
```groovy
job {
    publishers {
        publishHtml {
            report('build') {
                reportName('Report Name')
                reportFiles('content.html')
                keepAll()
            }
        }
    }
}
```

### DSL Method Return Values

Prior to version 1.28 most DSL methods had an undocumented return value. Since 1.28 DSL methods do not return a value
except for the methods defined in `javaposse.jobdsl.dsl.DslFactory`.

### Context and ContextHelper

The `Context` interface and the `ContextHelper` class have been moved from package `javaposse.jobdsl.dsl.helpers` to
package `javaposse.jobdsl.dsl`.

## Migrating to 1.27

### Job Name

The `name` method variant with a closure parameter in the `job` closure is [[deprecated|Deprecation-Policy]], use the
string argument variant instead.

DSL prior to 1.27
```groovy
job {
    name {
        'foo'
    }
}
```

DSL since 1.27
```groovy
job {
    name('foo')
}
```

### Permissions

In version 1.27 undocumented `permission` methods in the `job` context have been [[deprecated|Deprecation-Policy]]. Use
the `authorization` context instead.

DSL prior to 1.27
```groovy
job {
    permission('hudson.model.Item.Configure:jill')
    permission(Permissions.ItemRead, 'jack')
    permission('RunUpdate', 'joe')
}
```

DSL since 1.27
```groovy
job {
    authorization {
        permission('hudson.model.Item.Configure:jill')
        permission(Permissions.ItemRead, 'jack')
        permission('RunUpdate', 'joe')
    }
}
```

## Migrating to 1.26

### Archive JUnit Report

In version 1.26 the archiveJunit method with boolean arguments has been [[deprecated|Deprecation-Policy]] and has been
replaced by a closure variant.

DSL prior to 1.26
```groovy
job {
    publishers {
        archiveJunit('**/target/surefire-reports/*.xml', true, true, true)
    }
}
```

DSL since 1.26
```groovy
job {
    publishers {
        archiveJunit('**/target/surefire-reports/*.xml') {
            retainLongStdout()
            testDataPublishers {
                allowClaimingOfFailedTests()
                publishTestAttachments()
            }
        }
    }
}
```

See the [API Viewer](https://jenkinsci.github.io/job-dsl-plugin/#path/job-publishers-archiveJunit) for further details.

### Xvnc

In version 1.26 the xvnc method with one boolean argument has been [[deprecated|Deprecation-Policy]] and has been
replaced by a closure variant.

DSL prior to 1.26
```groovy
job {
    wrappers {
        xvnc(true)
    }
}
```

DSL since 1.26
```groovy
job {
    wrappers {
        xvnc {
            takeScreenshot()
        }
    }
}
```

See the [API Viewer](https://jenkinsci.github.io/job-dsl-plugin/#path/job-wrappers-xvnc) for further details.

### Gerrit Trigger

The usage "short names" in the event closure is [[deprecated|Deprecation-Policy]] and has been replaced by explicit DSL
methods for each event.

DSL prior to 1.26
```groovy
job {
    triggers {
        gerrit {
            events {
                ChangeAbandoned
                ChangeMerged
                ChangeRestored
                CommentAdded
                DraftPublished
                PatchsetCreated
                RefUpdated
            }
        }
    }
}
```

DSL since 1.26
```groovy
job {
    triggers {
        gerrit {
            events {
                changeAbandoned()
                changeMerged()
                changeRestored()
                commentAdded()
                draftPublished()
                patchsetCreated()
                refUpdated()
            }
        }
    }
}
```

See the [API Viewer](https://jenkinsci.github.io/job-dsl-plugin/#path/job-triggers-gerrit-events) for further details.

### AbstractStepContext

`javaposse.jobdsl.dsl.helpers.step.AbstractStepContext` has been removed, use
`javaposse.jobdsl.dsl.helpers.step.StepContext` instead.

DSL prior to 1.26
```groovy
AbstractStepContext.metaClass.myStep = { ... }
}
```

DSL since 1.26
```groovy
StepContext.metaClass.myStep = { ... }
```

## Migrating to 1.24

### Build Timeout

In version 1.24 the dsl for the build timeout plugin has been modified and the
generated xml requires a newer version of the build timeout plugin.
The old dsl still works but has been [[deprecated|Deprecation-Policy]].

DSL prior to 1.24
```groovy
timeout(String type) { //type is one of: 'absolute', 'elastic', 'likelyStuck'
    limit 15       // timeout in minutes
    percentage 200 // percentage of runtime to consider a build timed out
}

timeout(35, false)
```

DSL since 1.24
```groovy
timeout {
   absolute(15)
   failBuild()
   writeDescription('Build failed due to timeout after {0} minutes')
}

timeout {
    absolute(35)
    failBuild(false)
}
```

See the [API Viewer](https://jenkinsci.github.io/job-dsl-plugin/#path/job-wrappers-timeout) for further details.

### Gerrit Trigger

Before 1.24, the Gerrit trigger configuration used hardwired configuration for unset label configurations
(successfulVerified +1, failedVerified -1, everything else 0, these are the default values of the central Gerrit trigger
plugin configuration). Now the Gerrit trigger configuration correctly honors central configuration of labels. If you use
non-default labels in your central configuration, you might need to change the trigger label configuration of your jobs.

See the [API Viewer](https://jenkinsci.github.io/job-dsl-plugin/#path/job-triggers-gerrit) for further details.

## Migrating to 1.20

In version 1.20, some implementation classes have been moved to work around a
[bug](http://jira.codehaus.org/browse/GROOVY-5875) in Groovy. When these classes have been used to extend the DSL,
import statements and fully qualified class names have to be adjusted.

## Migrating to 1.19

In version 1.19 all build wrapper elements have been moved from the job element to a wrappers sub-element. When
upgrading to 1.19 or later, the wrapper elements have to moved as shown below.

DSL prior to 1.19:

```groovy
job {
    ...
    runOnSameNodeAs 'other', true
    rvm 'ruby-1.9.2-p290'
    timeout 60
    allocatePorts('PORT_A', 'PORT_B')
    sshAgent 'deloy-key'
    ...
}
```

DSL since 1.19:

```groovy
job {
    ...
    wrappers {
        runOnSameNodeAs 'other', true
        rvm 'ruby-1.9.2-p290'
        timeout 60
        allocatePorts('PORT_A', 'PORT_B')
        sshAgent 'deloy-key'
    }
    ...
}
```
