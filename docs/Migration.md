## Migrating to 1.58

### Config Files

The syntax for creating config files is changing to allow new features. The methods `customConfigFile`,
`mavenSettingsConfigFile`, `globalMavenSettingsConfigFile` and `managedScriptConfigFile` are
[[deprecated|Deprecation-Policy]] and will be removed.

Finding config files by name is also [[deprecated|Deprecation-Policy]] and will be removed. Names must not be unique so
lookup by name can yield multiple results. Use the unique config ID instead.

DSL prior to 1.58
```groovy
customConfigFile('ACME Settings') {
    comment('Settings for ACME tools')
    content(readFileFromWorkspace('acme/settings.json'))
}

mavenSettingsConfigFile('Company Settings') {
    comment('Company Maven Settings')
    content(readFileFromWorkspace('maven/settings.xml'))
    replaceAll()
    serverCredentials('company-A', 'company-A-maven-repository-credentials')
    serverCredentials('company-B', 'company-B-maven-repository-credentials')
}

globalMavenSettingsConfigFile('Company Settings') {
    comment('Company Maven Settings')
    content(readFileFromWorkspace('maven/settings.xml'))
    replaceAll()
    serverCredentials('company-A', 'company-A-maven-repository-credentials')
    serverCredentials('company-B', 'company-B-maven-repository-credentials')
}

managedScriptConfigFile('Example') {
    comment('My script')
    content('echo Hello $1 and $2')
    arguments('NAME_1', 'NAME_2')
}

mavenJob('example-1') {
    providedSettings('Company Settings')
    providedGlobalSettings('Company Settings')
}

job('example-2') {
    wrappers {
        configFiles {
            file('ACME Settings') {
                variable('CONFIG_FILE')
            }
            mavenSettings('Company Settings') {
                targetLocation('settings.xml')
            }
            globalMavenSettings('Company Settings') {
                targetLocation('global-settings.xml')
            }
        }
    }
    steps {
        managedScript('Example') {
            arguments('foo', 'bar')
        }
        maven {
            providedSettings('Company Settings')
            providedGlobalSettings('Company Settings')
        }
    }
}
```

DSL since 1.58
```groovy
configFiles {
    customConfig
        id('acme-settings')
        name('ACME Settings') {
        comment('Settings for ACME tools')
        content(readFileFromWorkspace('acme/settings.json'))
    }
    mavenSettingsConfig
        id('company-settings')
        name('Company Settings')
        comment('Company Maven Settings')
        content(readFileFromWorkspace('maven/settings.xml'))
        isReplaceAll(true)
        serverCredentialMappings {
            serverCredentialMapping {
                serverId('company-A')
                credentialsId('company-A-maven-repository-credentials')
            }
        }
        serverCredentialMappings {
            serverCredentialMapping {
                serverId('company-B')
                credentialsId('company-B-maven-repository-credentials')
            }
        }
    }
    globalMavenSettingsConfig {
        id('global-company-settings')
        name('Company Settings') {
        comment('Company Maven Settings')
        content(readFileFromWorkspace('maven/settings.xml'))
        isReplaceAll()
        serverCredentialMappings {
            serverCredentialMapping {
                serverId('company-A')
                credentialsId('company-A-maven-repository-credentials')
            }
        }
        serverCredentialMappings {
            serverCredentialMapping {
                serverId('company-B')
                credentialsId('company-B-maven-repository-credentials')
            }
        }
    }
    scriptConfig {
        id('example')
        name('Example')
        comment('My script')
        content('echo Hello $1 and $2')
        args {
            arg {
                name('NAME_1')
            }
            arg {
                name('NAME_2')
            }
        }
    }
}

mavenJob('example-1') {
    providedSettings('company-settings')
    providedGlobalSettings('global-company-settings')
}

job('example-2') {
    wrappers {
        configFiles {
            file('acme-settings') {
                variable('CONFIG_FILE')
            }
            mavenSettings('company-settings') {
                targetLocation('settings.xml')
            }
            globalMavenSettings('global-company-settings') {
                targetLocation('global-settings.xml')
            }
        }
    }
    steps {
        managedScript('example') {
            arguments('foo', 'bar')
        }
        maven {
            providedSettings('company-settings')
            providedGlobalSettings('global-company-settings')
        }
    }
}
```

The classes `javaposse.jobdsl.dsl.Config`, `javaposse.jobdsl.dsl.ConfigFile`,
`javaposse.jobdsl.dsl.MavenSettingsConfigFile`, `javaposse.jobdsl.dsl.ParametrizedConfigFile` and
`javaposse.jobdsl.plugin.ConfigFileProviderHelper` as well as the methods `createOrUpdateConfigFile` and
`getConfigFileId` in `javaposse.jobdsl.dsl.JobManagement` and it's implementing classes are
[[deprecated|Deprecation-Policy]] and will be removed.

### GitLab

Support for versions older than 1.4.0 of the [GitLab Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitLab+Plugin)
is [[deprecated|Deprecation-Policy]] and will be removed.

## Migrating to 1.57

### Rbenv

Support for versions older than 0.0.17 of the [Rbenv Plugin](https://wiki.jenkins-ci.org/display/JENKINS/rbenv+plugin)
is [[deprecated|Deprecation-Policy]] and will be removed.

## Migrating to 1.56

### SSH Agent

Support for versions older than 1.5 of the
[SSH Agent Plugin](https://wiki.jenkins-ci.org/display/JENKINS/SSH+Agent+Plugin) is [[deprecated|Deprecation-Policy]]
and will be removed.

## Migrating to 1.55

### ScriptRequest

Some constructors and the `location` property in `javaposse.jobdsl.dsl.ScriptRequest` are
[[deprecated|Deprecation-Policy]] and will be removed. The `body` and `scriptPath` properties should be used instead.
The class is part of the internal implementation and should not affect DSL scripts.

## Migrating to 1.54

### Embedded API Viewer

The short URL for the embedded API Viewer http://localhost:8080/plugin/job-dsl/api-viewer is
[[deprecated|Deprecation-Policy]] and will be removed. Use http://localhost:8080/plugin/job-dsl/api-viewer/index.html
instead.

### S3

Support for the [S3 Plugin](https://wiki.jenkins-ci.org/display/JENKINS/S3+Plugin) is [deprecated|Deprecation-Policy]]
and will be removed. Use the syntax provided by the [[Automatically Generated DSL]] instead.

DSL prior to 1.54
```groovy
job('example') {
    publishers {
        s3('myProfile') {
            entry('foo', 'bar', 'eu-west-1') {
                storageClass('REDUCED_REDUNDANCY')
                noUploadOnFailure()
                uploadFromSlave()
                managedArtifacts()
                useServerSideEncryption()
                flatten()
            }
            metadata('key', 'value')
        }
    }
}
```

DSL since 1.54
```groovy
job('example') {
    publishers {
        s3BucketPublisher {
            profileName('myProfile')
            entries {
                entry {
                    bucket('bar')
                    sourceFile('foo')
                    selectedRegion('eu-west-1')
                    storageClass('REDUCED_REDUNDANCY')
                    noUploadOnFailure(true)
                    uploadFromSlave(true)
                    managedArtifacts(true)
                    useServerSideEncryption(true)
                    flatten(true)
                }
            }
            userMetadata {
                metadataPair {
                    key('key')
                    value('value')
                }
            }
        }
    }
}
```

### Delivery Pipeline

Support for versions older than 0.10.0 of the
[Delivery Pipeline Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Delivery+Pipeline+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

### GitHub Branch Source

Support for versions older than 1.8 of the
[GitHub Branch Source Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+Branch+Source+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

### JobDslPlugin

The class `javaposse.jobdsl.plugin.JobDslPlugin` is [[deprecated|Deprecation-Policy]] and will be removed. It has been
deprecated because the constructor of the super class `hudson.Plugin` has been deprecated. See the documentation of
`hudson.Plugin` for details.

## Migrating to 1.53

### Overriding Job, Folder or View Names

The ability to override the name of a job, folder or view is deprecated [[deprecated|Deprecation-Policy]] and will be
removed. The name has to be set in the factory method that is creating the job, folder or view.

DSL prior to 1.53
```groovy
job('example-1') {
    name = 'other-1'
}

folder('example-2') {
    name = 'other-2'
}

listView('example-3') {
    name = 'other-3'
}
```

DSL since 1.53
```groovy
job('other-1') {
}

folder('other-2') {
}

listView('other-3') {
}
```

### GitHub Branch Source

The `ignoreOnPushNotifications` option in the GitHub branch source context is not needed. It has been
[[deprecated|Deprecation-Policy]] and will be removed.

DSL prior to 1.53
```groovy
multibranchPipelineJob('example') {
    branchSources {
        github {
            ignoreOnPushNotification()
        }
    }
}
```

DSL since 1.53
```groovy
multibranchPipelineJob('example') {
    branchSources {
        github {
        }
    }
}
```

## Migrating to 1.52

### Git

Support for versions older than 2.5.3 of the
[Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin) is [[deprecated|Deprecation-Policy]]
and will be removed.

### Multijob

Support for versions older than 1.22 of the
[Multijob Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Multijob+Plugin) is [[deprecated|Deprecation-Policy]] and
will be removed.

### Exclusion

Support for versions older than 0.12 of the
[Exclusion Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Exclusion-Plugin) is [[deprecated|Deprecation-Policy]]
and will be removed.

### Rundeck

Support for the [RunDeck Plugin](https://wiki.jenkins-ci.org/display/JENKINS/RunDeck+Plugin) is
[deprecated|Deprecation-Policy]] and will be removed. Use the syntax provided by the [[Automatically Generated DSL]]
instead.

DSL prior to 1.52
```groovy
job('example') {
    triggers {
        rundeck {
            jobIdentifiers(
                    '2027ce89-7924-4ecf-a963-30090ada834f',
                    'my-project-name:main-group/sub-group/my-job'
            )
            executionStatuses('FAILED', 'ABORTED')
        }
    }
    publishers {
        rundeck('13eba461-179d-40a1-8a08-bafee33fdc12') {
            rundeckInstance('prod')
            options(artifact: 'app', env: 'dev')
            option('version', '1.1')
            tag('#deploy')
            nodeFilters(hostname: 'dev(\\d+).company.net')
            nodeFilter('tags', 'www+dev')
            shouldWaitForRundeckJob()
            shouldFailTheBuild()
            includeRundeckLogs()
        }
    }
}
```

DSL since 1.52
```groovy
job('example') {
    triggers {
        rundeckTrigger {
            jobsIdentifiers([
                    '2027ce89-7924-4ecf-a963-30090ada834f',
                    'my-project-name:main-group/sub-group/my-job'
            ])
            executionStatuses(['FAILED', 'ABORTED'])
            filterJobs(true)
        }
    }
    publishers {
        rundeckNotifier {
            jobId('13eba461-179d-40a1-8a08-bafee33fdc12')
            rundeckInstance('prod')
            options(['artifact=app', 'env=dev', 'version=1.1'].join('\n'))
            tags('#deploy')
            nodeFilters(['hostname=dev(\\d+).company.net', 'tags=www+dev'].join('\n'))
            shouldWaitForRundeckJob(true)
            shouldFailTheBuild(true)
            includeRundeckLogs(true)
            tailLog(false)
        }
    }
}
```

## Migrating to 1.51

### Rundeck

Support for versions older than 3.5.4 of the
[RunDeck Plugin](https://wiki.jenkins-ci.org/display/JENKINS/RunDeck+Plugin) is [[deprecated|Deprecation-Policy]] and
will be removed.

### RVM

Support for versions older than 0.6 of the [RVM Plugin](https://wiki.jenkins-ci.org/display/JENKINS/RVM+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

### Ruby Runtime

Support for versions older than 0.12 of the
[Ruby Runtime Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Ruby+Runtime+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

## Migrating to 1.49

### ScriptLocation

The `scriptLocation` property of `javaposse.jobdsl.plugin.ExecuteDslScripts` and the
`javaposse.jobdsl.plugin.ExecuteDslScripts.ScriptLocation` class have been [[deprecated|Deprecation-Policy]] and will be
removed. The properties of `scriptLocation` can now be set directly on `ExecuteDslScripts`.

Pipeline syntax prior to 1.49
```groovy
node {
    step([
        $class: 'ExecuteDslScripts',
        scriptLocation: [scriptText: 'job("example-2")'],
    ])
    step([
        $class: 'ExecuteDslScripts',
        scriptLocation: [
            targets: ['jobs/projectA/*.groovy', 'jobs/common.groovy'].join('\n'),
            ignoreMissingFiles: true
        ]
    ])
}
```

Pipeline syntax since to 1.49
```groovy
node {
    step([
        $class: 'ExecuteDslScripts',
        scriptText: 'job("example-2")'
    ])
    step([
        $class: 'ExecuteDslScripts',
        targets: ['jobs/projectA/*.groovy', 'jobs/common.groovy'].join('\n'),
        ignoreMissingFiles: true
    ])
}
```

### JobManagement

The return type of the `getParameters()` method in `javaposse.jobdsl.dsl.JobManagement` changed from
`Map<String, String>` to `Map<String, Object>`.

## Migrating to 1.48

### Pipeline Compatibility

The classes `javaposse.jobdsl.plugin.WorkspaceProtocol`, `javaposse.jobdsl.plugin.WorkspaceUrlConnection` and
`javaposse.jobdsl.plugin.WorkspaceUrlHandler` as well as some constructors in
`javaposse.jobdsl.plugin.JenkinsJobManagement`, `javaposse.jobdsl.plugin.ExecuteDslScripts` and
`javaposse.jobdsl.plugin.ScriptRequestGenerator` are [[deprecated|Deprecation-Policy]] and will be removed. These
classes are part of the internal implementation and should not affect DSL scripts.

The classes `javaposse.jobdsl.plugin.actions.GeneratedObjectsBuildAction` and
`javaposse.jobdsl.plugin.actions.GeneratedObjectsBuildRunAction` are also deprecated and will be removed. Use
`javaposse.jobdsl.plugin.actions.GeneratedObjectsRunAction` instead.

## Migrating to 1.47

### Pipeline

The Workflow Plugin has been renamed to [Pipeline Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Pipeline+Plugin).
New `pipelineJob` and `multibranchPipelineJob` methods have been added as replacements for `workflowJob` and
`multibranchWorkflowJob` which are [[deprecated|Deprecation-Policy]] and will be removed.

DSL prior to 1.47
```groovy
workflowJob('example-1') {
    definition {
        cps {
            script(readFileFromWorkspace('project-a-workflow.groovy'))
            sandbox()
        }
    }
}

multibranchWorkflowJob('example-2') {
    branchSources {
        git {
            remote('https://github.com/jenkinsci/job-dsl-plugin.git')
            credentialsId('github-ci')
            includes('JENKINS-*')
        }
    }
    orphanedItemStrategy {
        discardOldItems {
            numToKeep(20)
        }
    }
}
```

DSL since 1.47
```groovy
pipelineJob('example-1') {
    definition {
        cps {
            script(readFileFromWorkspace('project-a-workflow.groovy'))
            sandbox()
        }
    }
}

multibranchPipelineJob('example-2') {
    branchSources {
        git {
            remote('https://github.com/jenkinsci/job-dsl-plugin.git')
            credentialsId('github-ci')
            includes('JENKINS-*')
        }
    }
    orphanedItemStrategy {
        discardOldItems {
            numToKeep(20)
        }
    }
}
```
 
### GitLab

Support for versions older than 1.2 of the [Gitlab Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitLab+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

### Slack

Support for the [Slack Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Slack+Plugin) is
[[deprecated|Deprecation-Policy]] because it is incompatible with newer versions of that plugin. It has been replaced by
the [[Automatically Generated DSL]].

DSL prior to 1.47
```groovy
job('example') {
    publishers {
        slackNotifications {
            projectChannel('Dev Team A')
            notifyAborted()
            notifyFailure()
            notifyNotBuilt()
            notifyUnstable()
            notifyBackToNormal()
        }
    }
}
```

DSL since 1.47
```groovy
job('example') {
  publishers {
    slackNotifier {
      room('Dev Team A')
      notifyAborted(true)
      notifyFailure(true)
      notifyNotBuilt(true)
      notifyUnstable(true)
      notifyBackToNormal(true)
      notifySuccess(false)
      notifyRepeatedFailure(false)
      startNotification(false)
      includeTestSummary(false)
      includeCustomMessage(false)
      customMessage(null)
      buildServerUrl(null)
      sendAs(null)
      commitInfoChoice('NONE')
      teamDomain(null)
      authToken(null)
    }
  }
}
```

### HipChat

Support for the [HipChat Plugin](https://wiki.jenkins-ci.org/display/JENKINS/HipChat+Plugin) is
[[deprecated|Deprecation-Policy]] because it is incompatible with newer versions of that plugin. It has been replaced by
the [[Automatically Generated DSL]].

DSL prior to 1.47
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

DSL since 1.47
```groovy
job('example') {
  publishers {
    hipChatNotifier {
      room('Dev Team A, QA')
      matrixTriggerMode('ONLY_PARENT')
      startJobMessage(null)
      completeJobMessage(null)
      token(null)
      notifications {
        notificationConfig {
          notifyEnabled(true)
          textFormat(true)
          notificationType('ABORTED')
          color('GRAY')
          messageTemplate(null)
        }
        notificationConfig {
          notifyEnabled(true)
          textFormat(true)
          notificationType('NOT_BUILT')
          color('GRAY')
          messageTemplate(null)
        }
        notificationConfig {
          notifyEnabled(true)
          textFormat(true)
          notificationType('UNSTABLE')
          color('YELLOW')
          messageTemplate(null)
        }
        notificationConfig {
          notifyEnabled(true)
          textFormat(true)
          notificationType('FAILURE')
          color('RED')
          messageTemplate(null)
        }
        notificationConfig {
          notifyEnabled(true)
          textFormat(true)
          notificationType('BACK_TO_NORMAL')
          color('GREEN')
          messageTemplate(null)
        }
      }
    }
  }
}
```

### Run Condition

The enum `javaposse.jobdsl.dsl.helpers.step.condition.FileExistsCondition.BaseDir` is [[deprecated|Deprecation-Policy]]
and will be removed. Use `javaposse.jobdsl.dsl.helpers.step.RunConditionContext.BaseDir` instead.

## Migrating to 1.46

### MultiJob

The behavior of the `currentJobParameters` method in the `phaseJob` context has changed. Prior to 1.46, the method
generated a `Current build parameters` job parameter. Since 1.46 the parameter is not generated when calling
`currentJobParameters` and must be created explicitly.

DSL prior to 1.46
```groovy
multiJob('example') {
    steps {
        phase('first') {
            phaseJob('job-a') {
                currentJobParameters()
            }
        }
    }
}
```

DSL since 1.46
```groovy
multiJob('example') {
    steps {
        phase('first') {
            phaseJob('job-a') {
                currentJobParameters()
                parameters {
                    currentBuild()
                }
            }
        }
    }
}
```

## Migrating to 1.45

### Docker Build and Publish

Support for versions older than 1.2 of the [CloudBees Docker Build and Publish
Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Docker+Build+and+Publish+plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

### DslScriptLoader

The `runDslEngine` methods in `DslScriptLoader` are [[deprecated|Deprecation-Policy]] and will be removed. Use the new
`runScripts` method instead.

### EnvInject

Support for the [EnvInject Plugin](https://wiki.jenkins-ci.org/display/JENKINS/EnvInject+Plugin) has changed to enable
masking passwords and enabling / disabling global passwords injection. Global passwords injection is disabled by default
to match the plugin's default configuration.

DSL prior to 1.45
```groovy
job('example') {
    wrappers {
        injectPasswords()
    }
}
```

DSL since 1.45
```groovy
job('example') {
    wrappers {
        injectPasswords {
            injectGlobalPasswords()
        }
    }
}
```

### Priority Sorter

Support for versions older than 3.4 of the
[Priority Sorter Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Priority+Sorter+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed. The top-level `priority` method is deprecated and only supports
plugin version prior to 3.0. A new `priority` method has been added to the `properties` context which will support
plugin versions starting from 3.4.

DSL prior to 1.45
```groovy
job('example') {
    priority(5)
}
```

DSL since 1.45
```groovy
job('example') {
    properties {
        priority(5)
    }
}
```

### Build Node Column

The [Build Node Column Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Node+Column+Plugin) is deprecated and
has been replaced by the [Extra Columns Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Extra+Columns+Plugin). DSL
support for the Build Node Column Plugin is [[deprecated|Deprecation-Policy]] as well and will be removed. Use the Extra
Columns Plugin instead, the DSL syntax stays the same.

## Migrating to 1.44

### Git

DSL support for the [Git Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Git+Plugin) has been changed to reflect the
configuration style of version 2.0 of the Git plugin.

DSL prior to 1.44
```groovy
job('example') {
    scm {
        git {
            strategy {
                inverse()
            }
            mergeOptions {
                remote('origin')
                branch('feature')
            }
            createTag()
            clean()
            wipeOutWorkspace()
            remotePoll(false)
            shallowClone()
            reference('/git/repo.git')
            cloneTimeout(10)
            recursiveSubmodules()
            trackingSubmodules()
            pruneBranches()
            localBranch('ci')
            relativeTargetDir('ws')
            ignoreNotifyCommit()
        }
    }
}
```

DSL since 1.44
```groovy
job('example') {
    scm {
        git {
            extensions {
                choosingStrategy {
                    inverse()
                }
                mergeOptions {
                    remote('origin')
                    branch('feature')
                }
                perBuildTag()
                cleanAfterCheckout()
                wipeOutWorkspace()
                disableRemotePoll()
                cloneOptions {
                    shallow()
                    reference('/git/repo.git')
                    timeout(10)
                }
                submoduleOptions {
                    recursive()
                    tracking()
                }
                pruneBranches()
                localBranch('ci')
                relativeTargetDirectory('ws')
                ignoreNotifyCommit()
            }
        }
    }
}
```

### Lockable Resources

Support for versions older than 1.7 of the
[Lockable Resources Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Lockable+Resources+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

### IRC

Support for versions older than 2.27 of the [IRC Plugin](https://wiki.jenkins-ci.org/display/JENKINS/IRC+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

### JobManagement

The method `JobManagement#createOrUpdateConfig(String path, String config, boolean ignoreExisting)` has been
[[deprecated|Deprecation-Policy]] since 1.33 and has been removed. Use
`JobManagement#createOrUpdateConfig(Item item, boolean ignoreExisting)` instead.

### Moved Classes

The classes `javaposse.jobdsl.dsl.helpers.WorkflowDefinitionContext` and `javaposse.jobdsl.dsl.helpers.CpsContext` have
been moved to the `javaposse.jobdsl.dsl.helpers.workflow` package.

### WithXmlAction

The class `javaposse.jobdsl.dsl.WithXmlAction` is [[deprecated|Deprecation-Policy]] and will be removed. Use
`javaposse.jobdsl.dsl.ContextHelper#executeConfigureBlock` to evaluate a configure block.

### Perforce

The method `p4(String viewSpec, Closure closure)` in the SCM context is [[deprecated|Deprecation-Policy]] and will be
removed.

DSL prior to 1.44
```groovy
job('example') {
    scm {
        p4('//depot/example/... //workspace/...')
    }
}
```

DSL since 1.44
```groovy
job('example') {
    scm {
        p4('//depot/example/... //workspace/...', 'rolem')
    }
}
```

## Migrating to 1.43

### Extended Email

The DSL support for the [Email-ext Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Email-ext+plugin) has changed to
address several issues.

DSL prior to 1.43
```groovy
job('example') {
    publishers {
        extendedEmail('me@halfempty.org', 'Oops', 'Something broken') {
            trigger('PreBuild')
            trigger(triggerName: 'StillUnstable', subject: 'Subject', body: 'Body', recipientList: 'RecipientList',
                    sendToDevelopers: true, sendToRequester: true, includeCulprits: true, sendToRecipientList: false)
            configure { node ->
                node / contentType << 'text/html'
            }
        }
    }
}
```

DSL since 1.43
```groovy
job('example') {
    publishers {
        extendedEmail {
            recipientList('me@halfempty.org')
            defaultSubject('Oops')
            defaultContent('Something broken')
            contentType('text/html')
            triggers {
                beforeBuild()
                stillUnstable {
                    subject('Subject')
                    content('Body')
                    sendTo {
                        developers()
                        requester()
                        culprits()
                    }
                }
            }
        }
    }
}
```

### GitHub Pull Request Builder

Built-in support for the
[GitHub Pull Request Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin) is
[[deprecated|Deprecation-Policy]] and will be removed. The GitHub Pull Request Builder Plugin implements the Job DSL
extension point and provides it's own Job DSL syntax since version 1.29.7.

DSL prior to 1.43
```groovy
job('example') {
    triggers {
        pullRequest {
        }
    }
    publishers {
        mergePullRequest {
        }
    }
}
```

DSL since 1.43
```groovy
job('example') {
    triggers {
        githubPullRequest {
        }
    }
    publishers {
        mergeGithubPullRequest {
        }
    }
}
```

### Docker Custom Build Environment

Support for versions older than 1.6.2 of the [CloudBees Docker Custom Build Environment
Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Docker+Custom+Build+Environment+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

### Rundeck

Support for versions older than 3.4 of the [RunDeck Plugin](https://wiki.jenkins-ci.org/display/JENKINS/RunDeck+Plugin)
is [[deprecated|Deprecation-Policy]] and will be removed.

### JUnit

Support for versions older than 1.10 of the [JUnit Plugin](https://wiki.jenkins-ci.org/display/JENKINS/JUnit+Plugin)
is [[deprecated|Deprecation-Policy]] and will be removed.

### Notification

Support for versions older than 1.8 of the
[Notification Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Notification+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

### JobManagement

Two methods in the `JobManagement` interface, `getPluginVersion` and `getJenkinsVersion`, are
[[deprecated|Deprecation-Policy]] and will be removed to get rid of the `org.jenkins-ci:version-number` dependency in
`job-dsl-core`. Use `isMinimumPluginVersionInstalled` as a replacement for `getPluginVersion`.

API prior to 1.43
```groovy
if (!jobManagement.getPluginVersion('git')?.isOlderThan(new VersionNumber('2.4.0'))) {
}
```

API since 1.43
```groovy
if (jobManagement.isMinimumPluginVersionInstalled('git', '2.4.0')) {
}
```

### ExtensibleContext

The classes `ExtensibleContext` and `AbstractExtensibleContext` have been moved from the `javaposse.jobdsl.dsl.helpers`
package to `javaposse.jobdsl.dsl`. This should not affect DSL scripts.

## Migrating to 1.42

### Task Scanner

Support for versions older than 4.41 of the
[Task Scanner Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Task+Scanner+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

### PostBuildScript

Support for versions older than 0.17 of the
[PostBuildScript Plugin](https://wiki.jenkins-ci.org/display/JENKINS/PostBuildScript+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

### Flexible Publish

The DSL syntax of the `flexiblePublish` context has been changed to fix
([JENKINS-30010](https://issues.jenkins-ci.org/browse/JENKINS-30010)).

DSL prior to 1.42
```groovy
job('example') {
    publishers {
        flexiblePublish {
            condition {
                status('ABORTED', 'FAILURE')
            }
            publisher {
                wsCleanup()
            }
            step {
                shell('echo hello!')
            }
        }
    }
}
```

DSL since 1.42
```groovy
job('example') {
    publishers {
        flexiblePublish {
            conditionalAction {
                condition {
                    status('ABORTED', 'FAILURE')
                }
                publishers {
                    wsCleanup()
                }
                steps {
                    shell('echo hello!')
                }
            }
        }
    }
}
```

## Migrating to 1.41

### Folders

Support for versions older than 5.0 of the
[CloudBees Folders Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Folders+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

### Jabber

Support for versions older than 1.35 of the [Jabber Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Jabber+Plugin)
is [[deprecated|Deprecation-Policy]] and will be removed.

## Migrating to 1.40

### Gradle

Support for versions older than 1.23 of the
[Gradle Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Gradle+Plugin) is [[deprecated|Deprecation-Policy]]
and will be removed.

### HTML Publisher

Support for versions older than 1.5 of the
[HTML Publisher Plugin](https://wiki.jenkins-ci.org/display/JENKINS/HTML+Publisher+Plugin) is
[[deprecated|Deprecation-Policy]] and will be removed.

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

### ConfigFileType

The implicit star import of `javaposse.jobdsl.dsl.ConfigFileType` in DSL scripts has been removed because the enum is
no longer used by any DSL method. If the values are used in scripts nevertheless, they must be used fully qualified or
imported explicitly.

DSL prior to 1.39
```groovy
Custom
MavenSettings
```

DSL since to 1.39
```groovy
javaposse.jobdsl.dsl.ConfigFileType.Custom
javaposse.jobdsl.dsl.ConfigFileType.MavenSettings
```

### DslScriptLoader

The signature of `DslScriptLoader.runDslEngineForParent` has changed and the method is no longer public. The change was
necessary to avoid a class loader leak and to fix ([JENKINS-30348](https://issues.jenkins-ci.org/browse/JENKINS-30348)).
Use `DslScriptLoader.runDslEngine` instead.

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
                    unstable('UNSTABLE')
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
customConfigFile('six') {
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
        publishJabber('one@example.org') {
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
