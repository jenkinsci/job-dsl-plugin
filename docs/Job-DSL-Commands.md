**NOTE: See the [API Viewer](https://jenkinsci.github.io/job-dsl-plugin/) for details about all options.**

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

ivyJob(String name, Closure closure = null)       // since 1.38

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

See the [API Viewer](https://jenkinsci.github.io/job-dsl-plugin/) page for details about all job options.

# View

To create views, the DSL provides the following methods.

```groovy
listView(String name, Closure closure = null)             // since 1.30

sectionedView(String name, Closure closure = null)        // since 1.30

nestedView(String name, Closure closure = null)           // since 1.30

deliveryPipelineView(String name, Closure closure = null) // since 1.30

buildPipelineView(String name, Closure closure = null)    // since 1.30

buildMonitorView(String name, Closure closure = null)     // since 1.30

categorizedJobsView(String name, Closure closure = null)  // since 1.31
```

The view methods behaves like the [job](#job) methods and will return a view object.

See the [API Viewer](https://jenkinsci.github.io/job-dsl-plugin/) page for details about view options.

# Folder

When the [CloudBees Folders Plugin](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Folders+Plugin) has been
installed, the DSL can be used to create folders.

```groovy
folder(String name, Closure closure = null) // since 1.30
```

The `folder` methods behaves like the [job](#job) methods and will return a folder object.

See the [API Viewer](https://jenkinsci.github.io/job-dsl-plugin/#path/folder) page for details about folder options.

Folders will be created before jobs and views to ensure that a folder exists before entries are created.

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

See the [API Viewer](https://jenkinsci.github.io/job-dsl-plugin/) page for details about config file options.

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
        shell(readFileFromWorkspace('build.sh'))
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

# Uploading User Content

Jenkins has a mechanism called [User Content](https://wiki.jenkins-ci.org/display/JENKINS/User+Content), which allows
arbitrary files to be served from `http://yourhost/jenkins/userContent`.

User content can be uploaded to Jenkins with the `userContent` DSL method.

```groovy
userContent(String path, InputStream content)
```

In conjunction with `streamFileFromWorkspace`, any content can be upload from the seed job's workspace.

```groovy
userContent('acme.png', streamFileFromWorkspace('images/acme.png'))
```

(since 1.33)

# Logging

Each DSL script provides a variable called `out` which points to a
[`PrintStream`](http://docs.groovy-lang.org/docs/groovy-1.8.9/html/groovy-jdk/java/io/PrintStream.html). It defines
methods like `println` which can be used to log messages to the build log (Console Output). This can be useful for
debugging complex DSL scripts.

```groovy
out.println('Hello from a Job DSL script!')
```

`out` is only available in scripts, but not in any classes used by a script, even if a class is defined
in a script. In this case, `out` must be passed into any method for logging.

```groovy
class Helper {
    static doSomething(def out) {
        out.println('Hello from a Job DSL helper class!')
    }
}

Helper.doSomething(out)
```

When used in a script directly, `out` can be omitted.

```groovy
println('Hello from a Job DSL script!')
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
BuildFramework.ant(this, 'my-ant-project', 'clean build')
```

Then the `BuildFramework` class has everything it needs to make `job` calls:

```groovy
class BuildFramework {
    static ant(dslFactory, jobName, antTargets) {
        dslFactory.job(jobName) {
            steps {
                ant(antTargets)
            }
        }
    }
}
```
