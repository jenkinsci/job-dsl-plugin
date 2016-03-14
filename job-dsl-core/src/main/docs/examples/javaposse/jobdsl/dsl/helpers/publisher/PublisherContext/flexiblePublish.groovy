job('example') {
    publishers {
        flexiblePublish {
            conditionalAction {
                condition {
                    status('ABORTED', 'FAILURE')
                }
                publishers {
                    wsCleanup()
                }
            }
            conditionalAction {
                condition {
                    and {
                        stringsMatch('foo', 'bar', false)
                    } {
                        status('SUCCESS', 'SUCCESS')
                    }
                }
                steps {
                    shell('echo hello!')
                }
            }
        }
    }
}
