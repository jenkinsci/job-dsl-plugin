// push a to branch if the job succeeded
job('example-1') {
    publishers {
        git {
            pushOnlyIfSuccess()
            branch('origin', 'staging')
        }
    }
}

// create and push a tag if the job succeeded, the tag name and message are parametrized.
job('example-2') {
    publishers {
        git {
            pushOnlyIfSuccess()
            tag('origin', 'foo-$PIPELINE_VERSION') {
                message('Release $PIPELINE_VERSION')
                create()
            }
        }
    }
}
