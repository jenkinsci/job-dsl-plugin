job('example') {
    publishers {
        cucumberTestResults {
            jsonReportFiles('files.json')
            ignoreBadSteps()
        }
    }
}
