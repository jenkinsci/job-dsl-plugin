job('example-1') {
    publishers {
        flexiblePublish {
            condition {
                status('ABORTED', 'FAILURE')
            }
            publisher {
                wsCleanup()
            }
        }
    }
}

job('example-2') {
    publishers {
        flexiblePublish {
            condition {
                and {
                    stringsMatch('foo', 'bar', false)
                } {
                    status('SUCCESS', 'SUCCESS')
                }
            }
            step {
                shell('echo hello!')
            }
        }
    }
}
