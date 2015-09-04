// notify using the plugin defaults
job('example-1') {
    publishers {
        flowdock('a-flow-token')
    }
}

// notify on all build statuses
job('example-2') {
    publishers {
        flowdock('flow-token') {
            unstable()
            success()
            aborted()
            failure()
            fixed()
            notBuilt()
        }
    }
}

// notify multiple flows in their chat using the tags 'jenkins' and 'build'
job('example-3') {
    publishers {
        flowdock('first-flow-token, second-flow-token') {
            chat()
            tags('jenkins', 'build')
        }
    }
}
