job('example') {
    wrappers {
        versionNumber {
            versionNumberString('1.0.0')
            projectStartDate()
            environmentVariableName('VERSION')
            environmentPrefixVariable('')
            oBuildsToday()
            oBuildsThisWeek()
            oBuildsThisMonth()
            oBuildsThisYear()
            oBuildsAllTime()
            skipFailedBuilds(false)
            useAsBuildDisplayName(true)
        }
    }
}
