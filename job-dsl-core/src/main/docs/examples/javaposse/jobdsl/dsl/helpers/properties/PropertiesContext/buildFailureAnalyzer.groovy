job('example-1') {
    properties {
        buildFailureAnalyzer()
    }
}

job('example-2') {
    properties {
        buildFailureAnalyzer(false)
    }
}
