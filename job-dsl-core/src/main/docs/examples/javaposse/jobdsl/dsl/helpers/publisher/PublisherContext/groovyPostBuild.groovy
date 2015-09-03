// run a groovy script and if that fails will mark the build as failed
job('example') {
    publishers {
        groovyPostBuild('println "hello, world"', Behavior.MarkFailed)
    }
}
