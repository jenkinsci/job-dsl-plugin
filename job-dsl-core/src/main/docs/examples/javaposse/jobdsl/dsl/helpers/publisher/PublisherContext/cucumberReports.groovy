job('example') {
    publishers {
        cucumberReports {
            jsonReportPath('files.json')
            pluginUrlPath('url')
            fileIncludePattern('included')
            fileExcludePattern('excluded')
            skippedFails(false)
            pendingFails(false)
            undefinedFails(false)
            missingFails(false)
            turnOffFlashCharts(false)
            ignoreFailedTests(false)
            parallelTesting(false)
        }
    }
}
