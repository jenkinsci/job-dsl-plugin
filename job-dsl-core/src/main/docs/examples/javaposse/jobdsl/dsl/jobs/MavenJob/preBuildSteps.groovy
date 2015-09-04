mavenJob('example') {
    preBuildSteps {
        shell("echo 'run before Maven'")
    }
}
