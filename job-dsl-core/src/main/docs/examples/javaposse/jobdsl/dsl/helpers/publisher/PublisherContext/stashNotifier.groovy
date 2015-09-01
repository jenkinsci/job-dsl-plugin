// notify Stash using the global Jenkins settings
job('example-1') {
    publishers {
        stashNotifier()
    }
}

// notify Stash using the global Jenkins settings and sets keepRepeatedBuilds to true
job('example-2') {
    publishers {
        stashNotifier {
            keepRepeatedBuilds()
        }
    }
}
