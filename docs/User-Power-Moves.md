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

# Grab a shared, groovy file
If you have a file which you want to import into your script, but you can't put it in the location described in the "Importing Other Files" example on the [[Real-World-Examples]] page, you can do this:

```groovy
@GrabResolver('http://artifacts.netflix.com/build-local')
@Grab('com.netflix.build:dsl-conventions:1.2')
```

[Original  discussion on the newsgroup](https://groups.google.com/forum/#!msg/job-dsl-plugin/6zmau49-SJI/Msk9gMexs_0J)

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
