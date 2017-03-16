job('example') {
    publishers {
        cucumberReports {
            jsonReportDirectory('')
            fileIncludePattern('**/*.json')
            fileExcludePattern('')
            failedStepsNumber()
            skippedStepsNumber()
            pendingStepsNumber()
            undefinedStepsNumber()
            failedScenariosNumber()
            failedFeaturesNumber()
            buildStatus()
        }
    }
}
