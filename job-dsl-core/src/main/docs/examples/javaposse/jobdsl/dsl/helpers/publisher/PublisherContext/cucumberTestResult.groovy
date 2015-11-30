job('example') {
    publishers {
        cucumberTestResult {
            jsonReportFiles('files.json')
            ignoreBadSteps(false)
        }
    }
}
