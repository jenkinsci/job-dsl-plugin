job('example1') {
    properties {
        buildFailureAnalyzer()
    }
}

job('example2') {
    properties {
        buildFailureAnalyzer(false)
    }
}
