mavenJob('example') {
    logRotator(-1, 10)
    jdk('Java 7')
    scm {
        github('jenkinsci/jenkins', 'master')
    }
    triggers {
        githubPush()
    }
    goals('clean verify')
}
