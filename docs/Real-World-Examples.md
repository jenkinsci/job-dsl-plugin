This page shows how to use Groovy features in the DSL for advanced scripting.


Variables
---------

    def jobName = 'example'
    
    job(jobName) {
    }


Loops
-----

    def giturl = 'https://github.com/quidryan/aws-sdk-test.git'
    for(i in 0..10) {
        job("DSL-Tutorial-1-Test-${i}") {
            scm {
                git(giturl)
            }
            steps {
                maven("test -Dtest.suite=${i}")
            }
        }
    }

Be aware of
[[problems|Frequently-Asked-Questions#why-does-a-method-defined-in-an-outer-scope-takes-precedence-of-a-method-defined-in-an-inner-scope]]
with the Groovy SDK loop methods.


Multi-line strings
------------------

    def viewspec = '''
    //depot/Tools/build/... //jryan_car/Tools/build/...
    //depot/commonlibraries/utils/... //jryan_car/commonlibraries/utils/...
    //depot/helloworld/... //jryan_car/helloworld/...
    '''
    
    job('PerforceJob') {
        scm {
            p4(viewspec)
        }
    }
    

REST API calls
--------------

    def project = 'Netflix/asgard'
    def branchApi = new URL("https://api.github.com/repos/${project}/branches")
    def branches = new groovy.json.JsonSlurper().parse(branchApi.newReader())
    branches.each {
        def branchName = it.name
        def jobName = "${project}-${branchName}".replaceAll('/','-')
        job(jobName) {
            scm {
                git("https://github.com/${project}.git", branchName)
            }
        }
    }
    

Import other files (i.e. with class definitions) into your script
-----------------------------------------------------------------

> Importing Groovy classes from the workspace is not possible when script security is enabled since that would undermine
> the script approval process. As an alternative it is possible to package the classes into a JAR file and add that JAR
> to the classpath through the _Additional classpath_ option. Classpath entries are subject to the approval process. See
> the [Job DSL Gradle Example](https://github.com/sheehan/job-dsl-gradle-example) as starting point for building and
> packaging classes.

Make a directory at the same level as the DSL called `utilities` and create a file called `MyUtilities.groovy` in the
`utilities` directory with the following contents:

    package utilities

    class MyUtilities {
        static void addMyFeature(def job) {
            job.with {
                description('Arbitrary feature')
            }
        }
    }

Then from the DSL, add something like this:

    import utilities.MyUtilities

    def myJob = job('example')
    MyUtilities.addMyFeature(myJob)

Note that importing other files is not possible when [[Script Security]] is enabled.