freeStyleJob('example') {
    logRotator(-1, 10)
    jdk('Java 8')
    scm {
        github('jenkinsci/job-dsl-plugin', 'master')
    }
    triggers {
        githubPush()
    }
    steps {
        gradle('clean build')
    }
    publishers {
        archiveArtifacts('job-dsl-plugin/build/libs/job-dsl.hpi')
    }
}
