import javaposse.jobdsl.JenkinsJob

// A SAMPLE JENKINS JOB SCRIPT
myJob = JenkinsJob.createJob "My First Jenkins DSL Job", {  // TODO: I know this looks pretty ugly still...
    using './../../test/resources/template-lib.xml'
    configure {
        item    "My Birthday"
        keepDependencies    "false"
    }
}
