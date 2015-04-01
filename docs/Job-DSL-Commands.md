**NOTE: See the [[Job Reference]] and [[View Reference]] pages for details about all options.**

# DSL Methods

The DSL execution engine exposes several methods to create Jenkins jobs, views, folders and config files. These
methods imply the creation of a Jenkins item and the closure to the method can be used to define the item's settings.
The only mandatory option is the item's name.

```groovy
job('my-job')
```

When defining jobs, views or folders the name is treated as absolute to the Jenkins root by default, but the seed job
can be configured to interpret names relative to the seed job. (since 1.24)

In the closure provided to the job methods there are a few top level methods, like `label` and `description`. Others are nested
deeper in blocks which represent their role in Jenkins, e.g. the `publishers` block contains all the publisher actions.

DSL methods can be cumulative or overriding, meaning that some methods will add nodes (e.g. `publishers` and `steps`)
and some will replace nodes (e.g. `disabled` will replace any existing disabled nodes). Some methods like `scm` and
`multiscm` are mutually exclusive. Likewise, when using the `scm` block, only one SCM can be specified.

**NOTE: when using these methods, remember that you need to use them in context. I.e. to use the `downstream` method,
it needs to be enclosed in a `publishers` context.**

The plugin tries to provide DSL methods to cover "common use case" scenarios as simple method calls. When these methods
fail you, you can always generate the underlying XML yourself via [[The Configure Block]]. Sometimes, a DSL
method will provide a configure block of its own, which will set the a good context to help modify a few fields.
This gives native access to the job config XML, which is typically very straight forward to understand.

(Note: The full XML can be found for any job, view or folder by taking the Jenkins URL and appending `/config.xml` to
it. We find that creating a job the way you like it, then viewing the XML is the best way to learn what fields you
need.)

# Job

The DSL exposes several methods to create jobs of different types.

```groovy
job(String name, Closure closure = null)          // since 1.30, an alias for freeStyleJob

freeStyleJob(String name, Closure closure = null) // since 1.30

buildFlowJob(String name, Closure closure = null) // since 1.30

matrixJob(String name, Closure closure = null)    // since 1.30

mavenJob(String name, Closure closure = null)     // since 1.30

multiJob(String name, Closure closure = null)     // since 1.30

workflowJob(String name, Closure closure = null)  // since 1.30
```

These methods will return a job object that can be re-used and passed around. E.g.

```groovy
def myJob = freeStyleJob('SimpleJob')
myJob.with {
    description 'A Simple Job'
}
```

See the [[Job Reference]] page for details about all job options.

For compatibility with previous releases, a generic `job` method exists which has an optional `type` attribute to
specify the type of job to be created. The `type` attribute can have a value of `Freeform`, `Maven`, `Multijob`,
`BuildFlow`, `Matrix` or `Workflow`. When no type is specified, a free-style job will be generated.

```groovy
job(Map<String, ?> arguments = [:], Closure closure) // deprecated since 1.30
```

# View

To create views, the DSL provides the following methods.

```groovy
listView(String name, Closure closure = null)             // since 1.30

sectionedView(String name, Closure closure = null)        // since 1.30

nestedView(String name, Closure closure = null)           // since 1.30

deliveryPipelineView(String name, Closure closure = null) // since 1.30

buildPipelineView(String name, Closure closure = null)    // since 1.30

buildMonitorView(String name, Closure closure = null)     // since 1.30

categorizedJobsView(String name, Closure closure = null)     // since 1.31
```

The view methods behaves like the [job](#job) methods and will return a view object.

See the [[View Reference]] pages for details about view options.

For compatibility with previous releases, a generic `view` method exists which has an optional `type` attribute to
specify the type of view to be created. The `type` attribute can have a value of `ListView`, `BuildPipelineView`,
`SectionedView`, `NestedView`, `DeliveryPipelineView` or `BuildMonitorView`. When no type is specified, a list view will
be generated.

```groovy
view(Map<String, Object> arguments = [:], Closure closure) // since 1.21, deprecated since 1.30
```

# Folder

When the [CloudBees Folders Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Folders+Plugin) has been
installed, the DSL can be used to create folders.

```groovy
folder(String name) { // since 1.30
    name(String name) // deprecated since 1.30

    // DSL specific methods
    configure(Closure configBlock)

    // common options
    displayName(String displayName)
}

folder(Closure folderClosure) // since 1.23, deprecated since 1.30
```

The `folder` methods behaves like the [job](#job) methods and will return a folder object.

Folders will be created before jobs and views to ensure that a folder exists before entries are created.

```groovy
folder('project-a') {
  displayName('Project A')
}
```

Items can be created within folders by using the full path as job name.

```groovy
folder('project-a')

freeStyleJob('project-a/compile')

listView('project-a/pipeline')

folder('project-a/testing')
```

# Config File

When the [Config File Provider Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Config+File+Provider+Plugin) has been
installed, the DSL can be used to create configuration files.

```groovy
customConfigFile(String name, Closure configFileClosure = null)        // since 1.30

mavenSettingsConfigFile(String name, Closure configFileClosure = null) // since 1.30
```

These methods behaves like the [job](#job) methods and will return a config file object.

Config files will be created before jobs to ensure that the file exists before it is referenced.

```groovy
customConfigFile('my-config') {
  comment('My important configuration')
  content('<some-xml/>')
}

mavenSettingsConfigFile('central-mirror') {
  content readFileFromWorkspace('maven-settings/central-mirror.xml')
}
```

For compatibility with previous releases, a generic `configFile` method exists which has an optional `type` attribute to
specify the type of configuration file to be created. The `type` attribute can have a value of `Custom` or
`MavenSettings`. When no type is specified, a custom config file will be generated.

```groovy
configFile(Map<String, Object> attributes = [:], Closure closure) // since 1.25, deprecated since 1.30
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
