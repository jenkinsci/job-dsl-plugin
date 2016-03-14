job('example-1') {
    publishers {
        archiveGatling()
    }
}

job('example-2') {
    publishers {
        archiveGatling {
            enabled(false)
        }
    }
}
