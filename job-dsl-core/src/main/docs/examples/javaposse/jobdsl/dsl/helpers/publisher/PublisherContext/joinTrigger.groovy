job('example-1') {
    publishers {
        joinTrigger {
            projects('upload-to-staging')
        }
    }
}

job('example-2') {
    publishers {
        joinTrigger {
            publishers {
                downstreamParameterized {
                    trigger('upload-to-staging') {
                        parameters {
                            currentBuild()
                        }
                    }
                }
            }
        }
    }
}
