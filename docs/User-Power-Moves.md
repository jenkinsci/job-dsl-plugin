When you get a little bit expert in your usage of the Job DSL and Plugin, you might want to try the following Power Moves:

# Run a DSL Script locally
Before you push a new DSL script to jenkins, it's helpful to run it locally and eyeball the resulting XML. To do this follow these steps:

1. git clone https://github.com/jenkinsci/job-dsl-plugin.git
1. cd job-dsl-plugin
1. ./gradlew :job-dsl-core:oneJar
1. DSL_JAR=$(find job-dsl-core -name '*standalone.jar'|tail -1)
1. java -jar $DSL_JAR sample.dsl.groovy

If you already have the source code checked out then you can ignore step 1.

What's going on here is that there's a static main method that can run the DSL, you just have to give it a filename. It'll output all the jobs' XML to the current directory. Likewise, if you use "using" (the templates-like feature) it'll look in the current directory for a file with the name of the job appended with ".xml" at the end of it.

By default the current directory is added to the classpath to be able to import classes. When using sub-directories for
scripts, the classpath differs compared to running in Jenkins where the DSL script's directory is added to the
classpath. Add the `-j` command line option to use the same behavior as when running in Jenkins:

    java -jar $DSL_JAR -j sample.dsl.groovy

# Generate a Job config.xml without having to fire up Jenkins
1. Add some job dsl content to a file, say job.dsl
1. Run the gradle command:  ./gradlew run -Pargs=job.dsl

   Note: the run task loads the file relative to the job-dsl-core directory, so I always just put my test files in there.
   Note2: if your dsl code contains a job named "myJob", the run task will generate myJob.xml.

[The original discussion about this on the Newsgroup](https://groups.google.com/forum/#!msg/job-dsl-plugin/lOYH7bL7AcM/70N1AEW219cJ)

# Access the Jenkins Environment Variables
To access the Jenkins Environment variables (such as BUILD_NUMBER) from within DSL scripts just wrap them in '${}'. E.g.:

`println " BUILD_NUMBER = ${BUILD_NUMBER}"`

Some of the available variables are as follows:

* BUILD_CAUSE
* BUILD_CAUSE_USERIDCAUSE
* BUILD_ID
* BUILD_NUMBER
* BUILD_TAG
* EXECUTOR_NUMBER
* HOME
* HUDSON_HOME
* HUDSON_SERVER_COOKIE
* JENKINS_HOME
* JENKINS_SERVER_COOKIE
* JOB_NAME
* LANG
* LOGNAME
* NODE_LABELS
* NODE_NAME
* OLDPWD
* PWD
* SHELL
* TERM
* TMPDIR
* USER

[Original  discussion on the newsgroup](https://groups.google.com/d/msg/job-dsl-plugin/ArgUBsLgumo/v77k5G6fllkJ)

# Parameterized Seed Job

Build parameters are exposed as environment variables in Jenkins. A seed job build parameter named `FOO` is available as
`FOO` variable in the DSL scripts. See the section about environment variables above.

# Reading Files from your Job Workspace
The job you create could be running on a slave, while the plugin runs on the master. Which means you shouldn't directly reference files on filesystem, since we're in a distributed system. The good news is that we added a method to help with this. See the docs for "Reading Files from Workspace" on https://github.com/jenkinsci/job-dsl-plugin/wiki/Job-DSL-Commands

[Original  discussion on the newsgroup](https://groups.google.com/forum/#!msg/job-dsl-plugin/wjrHEI7BLx8/zxW7j7xcWOcJ)

# Understanding config.xml Generation - Multiple Calls to the Same Command
Knowing when to overwrite or append to the XML is a fundamental problem with our approach. In the beginning of the project we though to append always, but we quickly learned that it takes a lot more work to intelligently append. We then took the approach to append when possible and easy, otherwise just overwrite.  Since then many users have found themselves just building up jobs from scratch and not worrying about existing values in templates, essentially taking a "we leave you alone if you leave us alone" approach.

I'd be interested in hearing from the community how often templates are used (aka using() syntax) and if the manipulations are additive (adding to exist structures) or constructive (creating new structures).

Users should also note that we're pretty bad about multiple calls being made in your DSL script to the same command, and we could be better. E.g. calling environmentVariables multiple times would leave the result of the last call as the winner. An alternative to this would be to defer its creation, accumulating the vars as we went. Once again, that takes more work and its something we can add later if needed. We should also document this better. So, if people see one behavior or other, please add it to the docs or bring it to our attention.

[Original  discussion on the newsgroup](https://groups.google.com/forum/#!msg/job-dsl-plugin/5YGgR8px7gE/fP6AL71BUrkJ)

# Have Multiple SVN Locations
If you want more than one SVN location in your SCM block:
```groovy
scm {
  svn('http://svn-mirror.xxx.lan/svn/internal/zzz/trunk', 'trunk') {
    it / locations << 'hudson.scm.SubversionSCM_-ModuleLocation' {
      remote 'http://svn-mirror.xxx.lan/svn/zzz/trunk'
      local 'trunk/community'
    }
  }
}
```

[Original  discussion on the newsgroup](https://groups.google.com/forum/#!msg/job-dsl-plugin/EWCaCYJgfsE/X_5ci3AX4pAJ)

# Using Libraries

Libraries can be used in a Job DSL script by adding them to the _Additional classpath_ option in the _Process Job DSLs_
build step. The library's JAR files must be available in the workspace of the seed job. For libraries without transitive
dependencies this can be achieved by using the _Artifact Resolver_ build step of the
[Repository Connector Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Repository+Connector+Plugin) prior to the
_Process Job DSLs_ build step. For more complex setups, an extra [Gradle](http://www.gradle.org/) build step
([Gradle Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Gradle+Plugin)) can be used.

For example, to use [Koshuke's GitHub API](http://github-api.kohsuke.org/), the following `build.gradle` will copy all
necessary libraries to a `lib` directory:

```groovy
defaultTasks 'libs'

repositories {
    jcenter()
}

configurations {
    libs
}

dependencies {
    libs 'org.kohsuke:github-api:1.70'
}

task clean(type: Delete) {
    delete 'lib'
}

task libs(type: Copy) {
    into 'lib'
    from configurations.libs
}

task wrapper(type: Wrapper) {
    gradleVersion = '2.2.1'
}
```

The _Additional classpath_ option in the _Process Job DSLs_ build step must be set to `lib/*.jar` to pick up all
libraries. And then the library can be used in a Job DSL script:

```groovy
import org.kohsuke.github.GitHub

def gh = GitHub.connectAnonymously()
gh.getOrganization('jenkinsci').listRepositories().each { repo ->
    job(repo.name) {
        scm {
            gitHub(repo.fullName)
        }
        steps {
            // ...
        }
    }
}
```

# List the Files in a Jenkins Jobs Workspace
Sometimes you want your DSL script to be able to grab a list of the files in the workspace.  Use the Hudson API to achieve this:

```groovy
hudson.FilePath workspace = hudson.model.Executor.currentExecutor().getCurrentWorkspace()
```

# Change the name of the root node
If you don't want the output job config.xml to start with a "project" node, you can use this little hack:

```groovy
configure { project ->
    project.name = 'com.cloudbees.plugins.flow.BuildFlow'
}
```
But note, this will only work if the BuildFlow project type uses the same sub-elements as the free style project type. If this is not the case, you need to modify the root node further and things will get even uglier. (Hint, you can use configure)

# Use DSL scripts in a Gradle project

Gradle provides a way to build and test your scripts and supporting classes. See [job-dsl-gradle-example](https://github.com/sheehan/job-dsl-gradle-example) for an example.

# Use Job DSL in Pipeline scripts

Starting with version 1.48, the Job DSL build step can be used in
[Pipeline](https://github.com/jenkinsci/pipeline-plugin) scripts (e.g. in `Jenkinsfile`). In version 1.49 Pipeline
support has been improved by enabling a more concise syntax when using _Pipeline: Groovy_ 2.10 or later.

Pipeline syntax with version 1.49 and _Pipeline: Groovy_ 2.10 or later:

```groovy
node {
    jobDsl scriptText: 'job("example-2")'

    jobDsl targets: ['jobs/projectA/*.groovy', 'jobs/common.groovy'].join('\n'),
           removedJobAction: 'DELETE',
           removedViewAction: 'DELETE',
           lookupStrategy: 'SEED_JOB',
           additionalClasspath: ['libA.jar', 'libB.jar'].join('\n')
}
```

Pipeline syntax with version 1.48 or _Pipeline: Groovy_ 2.9 and older:

```groovy
node {
    step([
        $class: 'ExecuteDslScripts',
        scriptText: 'job("example-2")'
    ])
    step([
        $class: 'ExecuteDslScripts',
        targets: ['jobs/projectA/*.groovy', 'jobs/common.groovy'].join('\n'),
        removedJobAction: 'DELETE',
        removedViewAction: 'DELETE',
        lookupStrategy: 'SEED_JOB',
        additionalClasspath: ['libA.jar', 'libB.jar'].join('\n')
    ])
}
```

Options:
* `targets`: optional, specifies Job DSL script files to execute, newline separated list of file names relative
             to the workspace
* `scriptText`: optional, specifies an inline Job DSL script
* `ignoreMissingFiles`: optional, defaults to `false`, set to `true` to ignore missing files or empty wildcards in
                        `targets`
* `ignoreExisting`: optional, defaults to `false`, set to `true` to not update existing jobs and views
* `removedJobAction`: optional, set to `'DELETE'` or `'DISABLE'` to delete or disable jobs that have been removed from
                      DSL scripts, defaults to `'IGNORE'`
* `removedViewAction`: optional, set to `'DELETE'` to delete views that have been removed from Job DSL scripts, defaults
                       to `'IGNORE'`
* `removedConfigFilesAction`: optional, set to `'DELETE'` to delete config files that have been removed from Job DSL
                              scripts, defaults to `'IGNORE'`
* `lookupStrategy`: optional, when set to `'SEED_JOB'` job names will be interpreted as relative to the pipeline job,
                    defaults to `'JENKINS_ROOT` which will treat all job names as absolute
* `additionalClasspath`: optional, newline separated list of additional classpath entries for Job DSL scripts, file
                         names must be relative to the workspace; this option will be ignored when script security for
                         Job DSL is enabled on the "Configure Global Security" page
* `sandbox`: optional, defaults to `false`, if `false` the DSL script needs to be approved by an administrator; set to
             `true` to run the DSL scripts in a sandbox with limited abilities (see [[Script Security]]); this option
              will be ignored when script security for Job DSL is disabled on the "Configure Global Security" page
