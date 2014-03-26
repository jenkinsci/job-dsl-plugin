***********************************
***********************************
**In Progress as of July 10, 2012**
***********************************
***********************************

This page capture the most common use cases, of which we'll try our best to make into actual DSL methods to greatly simplify use them. Please add your own uses cases, there is a high likelihood that if you provide an sample here, it'll get made into a DSL method.

First every job has to be encased in a job block with a name. Feel free to add variables, e.g. 
```
def jobName = 
job {
    name jobName
}
job {
    name 'SomeJob'
}
```

* Git
```
job {
    name 'GitJob'
    scm {
        git('git://github.com/JavaPosseRoundup/job-dsl-plugin')
    }
}
job {
    name 'GitJobForFeatureBranch'
    scm {
        git('git://github.com/JavaPosseRoundup/job-dsl-plugin', 'feature-branch')
    }
}
job {
    name 'GitJobXmlConfigured'
    scm {
        git('git://github.com/JavaPosseRoundup/job-dsl-plugin') { node ->
            // These names come straight from the xml, <scm class="hudson.plugins.git.GitSCM">
            node / authorOrCommitter << 'true'
            node / gitConfigName << 'Justin Ryan'
            node / gitConfigEmail << 'justin@halfempty.org'
        }
    }
}
```

* Set jobs for suites of tests

```groovy
def giturl = 'git://github.com/quidryan/aws-sdk-test.git'
for(i in 0..10) {   
    job {
        name "DSL-Tutorial-1-Test-${i}"
        scm {
            git(giturl)
        }
        steps {
            maven("test -Dtest.suite=${i}")
        }
    }
}
```

* Set Perforce View Spec

```
def viewspec = '''
//depot/Tools/build/... //jryan_car/Tools/build/...
//depot/commonlibraries/rest-server-utils/... //jryan_car/commonlibraries/rest-server-utils/...
//depot/webapplications/helloworld/... //jryan_car/webapplications/helloworld/...
'''

job {
    name 'PerforceJob'
    scm {
        perforce(viewspec)
    }
}
```

* Maintain Jenkins jobs for each branch of a project on Github - TBD
```
def project = 'Netflix/asgard'
def branchApi = new URL("https://api.github.com/repos/${project}/branches")
def branches = new groovy.json.JsonSlurper().parse(branchApi.newReader())
branches.each { 
    def branchName = it.name
    job {
        name "${project}-${branchName}".replaceAll('/','-')
        scm {
            git("git://github.com/${project}.git", branchName)
        }
    }
}
```

* Set up a chain of builds - compile & unit test, integration test, static analysis; each passing the build results of the former to the next step in the chain - TBD

* Add Gradle Build Step 
```
job {
    name 'GradleJob'
    steps {
        gradle('build')
    }
}
```

* Maven build
```
job {
    name 'MavenJob'
    steps {
        maven('clean build')
    }
}
```

* Import other files (i.e. with class definitions) into your script

1. Make a directory at the same level as the DSL called "utilities"
2. Make a file called MyUtilities.groovy in the utilities directory
3. Put the following contents in it:
```
  package utilities
  import javaposse.jobdsl.dsl.Job
  public class MyUtilities {
    def addEnterpriseFeature(Job job) {
        job.with {
          description('Arbitrary feature')
       }
    }
  }
```
4. Then from the DSL, add something like this:
```
  import utilities.MyUtilities
  MyUtilities.addEnterpriseFeature(job {})
```