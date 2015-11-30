job('example') {
    publishers {
        cucumberReports {
            jsonReportPath('files.json')
            pluginUrlPath('url')
            fileIncludePattern('included')
            fileExcludePattern('excluded')
            failOnSkipSteps()
            failOnPendingSteps()
            failOnUndefinedSteps()
            failOnMissingSteps()
            turnOffFlashCharts()
            ignoreFailedTests()
            parallelTesting()
        }
    }
}
